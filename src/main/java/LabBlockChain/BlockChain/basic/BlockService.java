package LabBlockChain.BlockChain.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Transaction.*;
import LabBlockChain.BlockChain.Helper.CoderHelper;


public class BlockService implements BlockServiceInterface {
	private List<Block> blockChain = new ArrayList<Block>();
	private Map<String, Wallet> myWalletMap = new HashMap<>();
	private Map<String, Wallet> otherWalletMap = new HashMap<>();
	private List<Transaction> allTransactions = new ArrayList<>();
	private List<Transaction> packedTransactions = new ArrayList<>();

	public BlockService() {
		Block genesisBlock = new Block(
				1, System.currentTimeMillis(), new ArrayList<Transaction>(),
				1, "1", "1");
		blockChain.add(genesisBlock);
		System.out.println("Genesis Block：" + JSON.toJSONString(genesisBlock));
	}

	/**
	 * get LastBlock
	 * @return
	 */
	@Override
	public Block getLatestBlock() {
		return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null;
	}

	/**
	 * add new Block
	 * @param newBlock
	 */
	@Override
	public boolean addBlock(Block newBlock) {
		if (isValidBlock(newBlock, getLatestBlock())) {
			blockChain.add(newBlock);
			packedTransactions.addAll(newBlock.getTransactions());
			return true;
		}
		return false;
	}

	/**
	 * validate new Block
	 */
	@Override
	public boolean isValidBlock(Block curBlock, Block prevBlock) {
		if (!prevBlock.getHash().equals(curBlock.getPreviousHash())) {
			System.out.println("prevHash not equal to the Hash of previous Block");
			return false;
		}
		else if((prevBlock.getIndex() + 1) != curBlock.getIndex()){
			System.out.println("prev index and current index mot match");
			return false;
		}
		else {
			String hash = calculateHash(curBlock.getPreviousHash(), curBlock.getTransactions(), curBlock.getNonce());
//			String hash = calculateFullHash(curBlock);
			if (!hash.equals(curBlock.getHash())) {
				System.out.println("New hash " + hash + " not match " + curBlock.getHash());
				return false;
			}
			if (!isValidHash(curBlock.getHash())) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidChain(List<Block> chain) {
		//  outerindex     0            1       2      3       4        5
		//  innerindex     1            2       3      4       5        6
		//           genesis block | block | block | block | block | block |
		//             prev           cur
		Block curBlock = null;
		Block prevBlock = chain.get(0);
		int iter = 1;

		while (iter < chain.size()) {
			curBlock = chain.get(iter);
			if (!isValidBlock(curBlock, prevBlock)) {
				return false;
			}
			prevBlock = curBlock;
			iter++;
		}
		return true;
	}

	/**
	 * check if hash starts with difficulty
	 *
	 * @param hash
	 * @return
	 */
	private boolean isValidHash(String hash) {
		String pow = Block.provOfWorkZeros();
		return hash.startsWith(pow);
	}

	/**
	 * peer blockchain is Longer or older, upload local blockchain
	 * @param newBlocks
	 */
	@Override
	public void replaceChain(List<Block> newBlocks) {
		if (isValidChain(newBlocks) && newBlocks.size() > blockChain.size()) {
			blockChain = newBlocks;
			packedTransactions.clear();
			for(Block block:blockChain){
				packedTransactions.addAll(block.getTransactions());
			}
		}
		else {
			System.out.println("Peer Blockchain not adopted;");
		}
	}

	private Block createNewBlock(int nonce, String previousHash, String hash, List<Transaction> transactions) {
		Block block = new Block(blockChain.size() + 1, System.currentTimeMillis(), transactions, nonce, previousHash, hash);
		if (addBlock(block)) {
			return block;
		}
		return null;
	}


	private String calculateHash(String previousHash, List<Transaction> currentTransactions, int nonce) {
		return CoderHelper.applySha256(
				previousHash + JSON.toJSONString(currentTransactions) + nonce
		);
	}

	private String calculateFullHash(Block block) {
		String toHash = block.getIndex() + block.getTimestamp() + JSON.toJSONString(block.getTransactions()) + block.getPreviousHash() + block.getNonce();
		return CoderHelper.applySha256(toHash);
	}

	/**
	 * Mine a new block
	 * @param toAddress
	 * @return
	 */
	public Block mine(String toAddress) {
		allTransactions.add(generateBaseTx(toAddress));
		List<Transaction> blockTxs = new ArrayList<Transaction>(allTransactions);
		blockTxs.removeAll(packedTransactions);
		verifyAllTransactions(blockTxs);

		String newBlockHash = "";
		int nonce = 0;
		long start = System.currentTimeMillis();
		System.out.println("Start Mining");
		while (true) {
//			newBlockHash = calculateFullHash(curBlock);
			newBlockHash = calculateHash(getLatestBlock().getHash(), blockTxs, nonce);
			if (isValidHash(newBlockHash)) {
				System.out.println("Mine Complete，correct hash is：" + newBlockHash);
				System.out.println("time comsumption" + (System.currentTimeMillis() - start) + "ms");
				break;
			}
			System.out.println("error hash：" + newBlockHash);
			nonce++;
		}

		Block block = createNewBlock(nonce, getLatestBlock().getHash(), newBlockHash, blockTxs);
		return block;
	}


	private void verifyAllTransactions(List<Transaction> blockTxs) {
		List<Transaction> invalidTxs = new ArrayList<>();
		for (Transaction tx : blockTxs) {
			if (!verifyTransaction(tx)) {
				invalidTxs.add(tx);
			}
		}
		blockTxs.removeAll(invalidTxs);
		allTransactions.removeAll(invalidTxs);
	}

	@Override
	public Transaction generateBaseTx(String receiverAddr) {
		TransactionInput txIn = new TransactionInput("0", -1, null, null);
		System.out.println("generateBaseTx txIn：" + txIn.toString());
		Wallet wallet = myWalletMap.get(receiverAddr);
		System.out.println("generateBaseTx myWalletMap：" + myWalletMap.toString());
		TransactionOutput txOut = new TransactionOutput(10, wallet.getHashPubKey());
		System.out.println("generateBaseTx txOut：" + txOut.toString());
		String forHashId = txIn.toString() + txOut.toString();
//		String id = CoderHelper.applySha256(forHashId);
//		return new Transaction(CoderHelper.UUID(), txIn, txOut);
		return new Transaction(CoderHelper.applySha256(forHashId), txIn, txOut);
	}

	@Override
	public Transaction createTransaction(Wallet senderWallet, Wallet recipientWallet, int amount) {

		List<Transaction> unspentTxs = findUnspentTransactions(senderWallet.getAddress());
		Transaction prevTx = null;
		if( unspentTxs != null){
			for (Transaction transaction : unspentTxs) {
				//TODO exchage
				if (transaction.getTxOut().getValue() == amount) {
					prevTx = transaction;
					break;
				}
			}
			if (prevTx == null) {
				return null;
			}
		}

		TransactionInput txIn = new TransactionInput(prevTx.getId(), amount, null, senderWallet.getPublicKey());
		TransactionOutput txOut = new TransactionOutput(amount, recipientWallet.getHashPubKey());
		String forHashId = txIn.toString() + txOut.toString();
//		Transaction transaction = new Transaction(CoderHelper.UUID(), txIn, txOut);
		Transaction transaction = new Transaction(CoderHelper.applySha256(forHashId), txIn, txOut);
		transaction.sign(senderWallet.getPrivateKey(), prevTx);
		allTransactions.add(transaction);
		return transaction;
	}


	private List<Transaction> findUnspentTransactions(String address) {
		List<Transaction> unspentTxs = new ArrayList<Transaction>();
		Set<String> spentTxs = new HashSet<String>();
		for (Transaction tx : allTransactions) {
			if (tx.coinbaseTx()) {
				continue;
			}
			if (address.equals(Wallet.getAddress(tx.getTxIn().getPublicKey()))) {
				spentTxs.add(tx.getTxIn().getTxId());
			}
		}

		for (Block block : blockChain) {
			List<Transaction> transactions = block.getTransactions();
			for (Transaction tx : transactions) {
				if (address.equals(CoderHelper.MD5(tx.getTxOut().getPublicKeyHash()))) {
					if (!spentTxs.contains(tx.getId())) {
						unspentTxs.add(tx);
					}
				}
			}
		}

		return unspentTxs;
	}

	private Transaction findTransaction(String id) {
		for (Transaction tx : allTransactions) {
			if (id.equals(tx.getId())) {
				return tx;
			}
		}
		return null;
	}

	private boolean verifyTransaction(Transaction tx) {
		if (tx.coinbaseTx()) {
			return true;
		}
		Transaction prevTx = findTransaction(tx.getTxIn().getTxId());
		return tx.verify(prevTx);
	}


	public Wallet createWallet() {
		Wallet wallet = Wallet.generateWallet();
		String address = wallet.getAddress();
		myWalletMap.put(address, wallet);
		System.out.println(myWalletMap);
		return wallet;
	}


	@Override
	public int getWalletBalance(String address) {
		List<Transaction> unspentTxs = findUnspentTransactions(address);
		int balance = 0;
		for (Transaction transaction : unspentTxs) {
			balance += transaction.getTxOut().getValue();
		}
		return balance;
	}
	@Override
	public List<Block> getBlockChain() {
		return blockChain;
	}

	@Override
	public void setBlockChain(List<Block> blockChain) {
		this.blockChain = blockChain;
	}

	@Override
	public Map<String, Wallet> getMyWalletMap() {
		return myWalletMap;
	}

	@Override
	public void setMyWalletMap(Map<String, Wallet> myWalletMap) {
		this.myWalletMap = myWalletMap;
	}

	@Override
	public Map<String, Wallet> getOtherWalletMap() {
		return otherWalletMap;
	}

	@Override
	public void setOtherWalletMap(Map<String, Wallet> otherWalletMap) {
		this.otherWalletMap = otherWalletMap;
	}

	@Override
	public List<Transaction> getAllTransactions() {
		return allTransactions;
	}

	@Override
	public void setAllTransactions(List<Transaction> allTransactions) {
		this.allTransactions = allTransactions;
	}

	@Override
	public List<Transaction> getPackedTransactions() {
		return packedTransactions;
	}

	@Override
	public void setPackedTransactions(List<Transaction> packedTransactions) {
		this.packedTransactions = packedTransactions;
	}
}

