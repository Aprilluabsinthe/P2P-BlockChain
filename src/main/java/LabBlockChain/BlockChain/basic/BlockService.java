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
import com.google.gson.Gson;

/***
 * generate transaction
 * signature
 * broadcast
 * verified and accepted by all peers
 * verify minned Node and add to blockchain
 * be verified by enough blocks
 */
public class BlockService implements BlockServiceInterface {
	private final static int SYS_REWARD = 10;
	private List<Block> blockChain = new ArrayList<Block>();
	private Map<String, Wallet> myWalletMap = new HashMap<>();
	private Map<String, Wallet> otherWalletMap = new HashMap<>();
	private List<Transaction> allTransactions = new ArrayList<>();
	private List<Transaction> packedTransactions = new ArrayList<>();
	private List<Transaction> UTXOs = new ArrayList<>();
//	public Gson gson;

	/**
	 * generate genesisBlock
	 * The genesis Block's index starts from 1,
	 * timestamp is current time,
	 * transaction is null,
	 * nouce is 1,
	 * previous hash string ans hash string equals to 1
	 */
	public BlockService() {
		Block genesisBlock = new Block(
				1, System.currentTimeMillis(), new ArrayList<Transaction>(),
				1, "1", "1");
		blockChain.add(genesisBlock);
		System.out.println("Genesis Block：" + JSON.toJSONString(genesisBlock));
//		System.out.println("Genesis Block：" + new Gson().toJson(genesisBlock));

	}

	/**
	 * get LastBlock
	 * @return the last block
	 */
	@Override
	public Block getLatestBlock() {
		return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null;
	}

	/**
	 * add new Block to blockchain, add all transactions to the packed list
	 * @param newBlock the new block
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
	 * the new Block is valid or not
	 * the current <code>prevHash</code> should equals to the <code>hash</code> of the previous block
	 * the current <code>index</code> should be the previous <code>index+1</code>
	 * the current hash should
	 * 1) match hash calculation: equals to the SHA256 hash of PreviousHash, Transactions, Nonce
	 * 2) match Proof-of-work: start with [0] * DIFFICULTY
	 * @param curBlock current block
	 * @param prevBlock previous block
	 * @return
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

	/**
	 * if the chain is valid or not
	 * @param chain the block chain
	 *      //  outerindex     0            1       2      3       4        5
	 * 		//  innerindex     1            2       3      4       5        6
	 * 		//           genesis block | block | block | block | block | block |
	 * 		//             prev           cur
	 * @return True if valid, False if not
	 */
	private boolean isValidChain(List<Block> chain) {
		Block curBlock;
		Block prevBlock = chain.get(0);
		int iter = 1;
		// validate a block with the previous block
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
	 * check if hash starts with [0]*difficulty
	 * @param hash the hash String
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
		if (isValidChain(newBlocks)) {
			if( newBlocks.size() > blockChain.size() ||
					(newBlocks.size() == blockChain.size() &&
					newBlocks.get(newBlocks.size()-1).getTimestamp() < getLatestBlock().getTimestamp()) ){
				blockChain = newBlocks;
				packedTransactions.clear();
				for(Block block:blockChain){
					packedTransactions.addAll(block.getTransactions());
				}
			}
		}
		else {
			System.out.println("Not a Valid Chain, Peer Blockchain not adopted;");
		}
	}

	/**
	 * Create a New Block
	 * @param transactions A list of Transactions
	 * @param nonce random number
	 * @param previousHash Hash of the previous Block
	 * @param hash hash of the current block
	 * @return
	 */
	private Block createNewBlock(List<Transaction> transactions,int nonce, String previousHash, String hash) {
		Block block = new Block(blockChain.size() + 1, System.currentTimeMillis(), transactions, nonce, previousHash, hash);
		if (addBlock(block)) {
			return block;
		}
		return null;
	}


	/**
	 * Calculate the Block SHA256 Hash according to <code>previousHash,currentTransactions,nonce</code>
	 * @param previousHash
	 * @param currentTransactions
	 * @param nonce
	 * @return
	 */
	private String calculateHash(String previousHash, List<Transaction> currentTransactions, int nonce) {
		return CoderHelper.applySha256(
				previousHash + JSON.toJSONString(currentTransactions) + nonce
		);
	}

	/**
	 * Calculate the Block SHA256 Hash according to <code>getIndex,getTimestamp,previousHash,Transactions,nonce</code>
	 * @param block
	 * @return
	 */
	private String calculateFullHash(Block block) {
		String toHash = block.getIndex() + block.getTimestamp() + JSON.toJSONString(block.getTransactions()) + block.getPreviousHash() + block.getNonce();
		return CoderHelper.applySha256(toHash);
	}

	/**
	 * Mine a new block
	 * @param toAddress the Address of the Wallet
	 * @return
	 */
	public Block mine(String toAddress) {
		// add base coin(system reward for mining)
		allTransactions.add(generateBaseTx(toAddress));
		// get UTXO blocks
		List<Transaction> UTXOBlocks = new ArrayList<Transaction>(allTransactions);
		UTXOBlocks.removeAll(packedTransactions);
		// ensure UTXOs are valid
		verifyAllTransactions(UTXOBlocks);

		String newBlockHash = "";
		int nonce = 0;
		long start = System.currentTimeMillis();
		System.out.println("Start Mining>>>>>>");

		// mine until hash meets the requirements of PoW
		while (true) {
			newBlockHash = calculateHash(getLatestBlock().getHash(), UTXOBlocks, nonce);
			if (isValidHash(newBlockHash)) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println("Mine Complete，correct hash is：" + newBlockHash);
				System.out.println("time comsumption" + (System.currentTimeMillis() - start) + "ms");
				break;
			}
			System.out.println("error hash：" + newBlockHash);
			nonce++;
		}

		Block block = createNewBlock(UTXOBlocks,nonce, getLatestBlock().getHash(), newBlockHash);
		System.out.println("New Block: "+new Gson().toJson(block));
		return block;
	}


	/**
	 * Verify each transaction
	 * @param transactionList
	 */
	private void verifyAllTransactions(List<Transaction> transactionList) {
		for (Transaction tx : transactionList) {
			if (!verifyTransaction(tx)) {
				transactionList.remove(tx);
				allTransactions.remove(tx);
			}
		}
	}

	/**
	 * verify Transaction, match the recordof previous transaction
	 * @param tx the transaction
	 * @return true if is valid
	 */
	private boolean verifyTransaction(Transaction tx) {
		if (tx.isBaseTx()) {
			return true;
		}
		Transaction prevTx = getTransaction(tx.getTxIn().getTxId());
		return tx.verify(prevTx);
	}

	/**
	 * system base transaction, have only output, no input, no signaturexs
	 * @param receiverWallet
	 * @return system reward base transaction
	 */
	@Override
	public Transaction generateBaseTx(String receiverWallet) {
		TransactionInput txIn = new TransactionInput("0", -1, null, null);
		System.out.println("generateBaseTx txIn：" + txIn.toString());

		Wallet receiver = myWalletMap.get(receiverWallet);
		System.out.println("generateBaseTx Wallet：" + myWalletMap.toString());
		// mine reword is SYS_REWARD
		TransactionOutput txOut = new TransactionOutput(SYS_REWARD, receiver.getHashPubKey());
		System.out.println("generateBaseTx txOut：" + txOut.toString());
		String forHashId = txIn.toString() + txOut.toString();
		return new Transaction(CoderHelper.UUID(), txIn, txOut);
	}

	/**
	 * Create new Transaction
	 * @param sender the wallet of the sender
	 * @param receiver the wallet of the receiver
	 * @param amount the transaction output amount
	 * @return
	 */
	@Override
	public Transaction createTransaction(Wallet sender, Wallet receiver, int amount) {
		if(getWalletBalance(sender.getAddress()) < amount){
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}

		List<Transaction> senderUTXOs = findUTXOs(sender.getAddress());
		Transaction prevTx = null;
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		int total = 0;
		if( senderUTXOs != null){
			for (Transaction transaction : senderUTXOs) {
				total +=  transaction.getTxOut().getValue();
//
//				if (transaction.getTxOut().getValue() == amount) {
//					prevTx = transaction;
//					break;
//				}
//				// exchange
				if(total >= amount){
					prevTx = transaction;
					break;
				}
			}
			if (prevTx == null) {
				return null;
			}
		}

		// transaction of amount
		TransactionInput txIn = new TransactionInput(prevTx.getId(), amount, null, sender.getPublicKey());
		TransactionOutput txOut = new TransactionOutput(amount, receiver.getHashPubKey());
		Transaction transaction = new Transaction(CoderHelper.UUID(), txIn, txOut);
		transaction.sign(sender.getPrivateKey(), prevTx);
		allTransactions.add(transaction);

		// transaction of exchange
		// sennder send the exchange to it self
//		int exchange = total - amount;
//		TransactionInput txIn_exchange = new TransactionInput(transaction.getId(), exchange, null, sender.getPublicKey());
//		TransactionOutput txOut_exchange = new TransactionOutput(exchange, sender.getHashPubKey());
////		String forHashId_exchange = txIn.toString() + txOut.toString();
//		Transaction transaction_exchange = new Transaction(CoderHelper.UUID(), txIn_exchange, txOut_exchange);
////		Transaction transaction = new Transaction(CoderHelper.applySha256(forHashId), txIn, txOut);
//		transaction_exchange.sign(sender.getPrivateKey(), transaction);
//		allTransactions.add(transaction_exchange);

		return transaction;
	}


	/**
	 * Find unpacked transactions
	 * @param address
	 * @return
	 */
	public List<Transaction> findUTXOs(String address) {
		List<Transaction> unspentTxs = new ArrayList<Transaction>();
		Set<String> spentTxs = new HashSet<String>();

		// find all transactions that was made by myself
		for (Transaction tx : allTransactions) {
			if (tx.isBaseTx()) {
				continue;
			}
			String inTXpublickey = tx.getTxIn().getPublicKey();
			if (address.equals(Wallet.getAddress( inTXpublickey ))) {
				spentTxs.add(tx.getTxIn().getTxId());
			}
		}

		// find all transactiosn that was sent to me
		// and not included in my spend transactions
		for (Block block : blockChain) {
			List<Transaction> transactions = block.getTransactions();
			for (Transaction tx : transactions) {
				String outTXpublickeyHash = tx.getTxOut().getPublicKeyHash();
				String outTXAddress = CoderHelper.MD5(outTXpublickeyHash);
				if (address.equals(outTXAddress)) {
					if (!spentTxs.contains(tx.getId())) {
						unspentTxs.add(tx);
					}
				}
			}
		}
		this.UTXOs = unspentTxs;
		return unspentTxs;
	}

	/**
	 * Get transactio by ID
	 * @param id the id to find the transaction
	 * @return
	 */
	private Transaction getTransaction(String id) {
		for (Transaction tx : allTransactions) {
			if (id.equals(tx.getId())) {
				return tx;
			}
		}
		return null;
	}


	/**
	 * Create a new Wallet, put mapping into myWalletMap
	 * @return Wallet
	 */
	public Wallet createWallet() {
		Wallet wallet = Wallet.generateWallet();
		String address = wallet.getAddress();
		myWalletMap.put(address, wallet);
		System.out.println(myWalletMap);
		return wallet;
	}





	//********************************************************************************************************************************************
	// Getter and Setter
	//********************************************************************************************************************************************

	/**
	 * get Wallet Balance by address, find map in <code>myWalletMap</code> and calculate unspend outputs
	 * @param address the Wallet address
	 * @return balance in the wallet
	 */
	@Override
	public int getWalletBalance(String address) {
		List<Transaction> unspentTxs = findUTXOs(address);
		int balance = 0;
		for (Transaction transaction : unspentTxs) {
			balance += transaction.getTxOut().getValue();
		}
		return balance;
	}

	/**
	 * Getter for blockchain
	 * @return blockchain
	 */
	@Override
	public List<Block> getBlockChain() {
		return blockChain;
	}

	/**
	 * Setter for blockchain
	 * @param blockChain the new blockchain
	 */
	@Override
	public void setBlockChain(List<Block> blockChain) {
		this.blockChain = blockChain;
	}

	/**
	 * The getter for myWalletMap
	 * @return myWalletMap
	 */
	@Override
	public Map<String, Wallet> getMyWalletMap() {
		return myWalletMap;
	}

	/**
	 * The setter for myWalletMap
	 * @param myWalletMap new myWalletMap
	 */
	@Override
	public void setMyWalletMap(Map<String, Wallet> myWalletMap) {
		this.myWalletMap = myWalletMap;
	}


	/**
	 * The getter for Other's Wallet Map
	 * @return Other's Wallet Map
	 */
	@Override
	public Map<String, Wallet> getOtherWalletMap() {
		return otherWalletMap;
	}

	/**
	 * The setter for Other's Wallet Map
	 * @param otherWalletMap Other's Wallet Map
	 */
	@Override
	public void setOtherWalletMap(Map<String, Wallet> otherWalletMap) {
		this.otherWalletMap = otherWalletMap;
	}

	/**
	 * Getter for allTransactions
	 * @return allTransactions
	 */
	@Override
	public List<Transaction> getAllTransactions() {
		return allTransactions;
	}

	/**
	 * Setter for allTransactions
	 * @param allTransactions allTransactions
	 */
	@Override
	public void setAllTransactions(List<Transaction> allTransactions) {
		this.allTransactions = allTransactions;
	}

	/**
	 * Getter for packedTransactions
	 * @return packedTransactions
	 */
	@Override
	public List<Transaction> getPackedTransactions() {
		return packedTransactions;
	}

	/**
	 * Setter for packedTransactions
	 * @param packedTransactions packedTransactions
	 */
	@Override
	public void setPackedTransactions(List<Transaction> packedTransactions) {
		this.packedTransactions = packedTransactions;
	}
}

