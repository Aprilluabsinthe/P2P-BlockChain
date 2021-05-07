package LabBlockChain.BlockChain.p2p;

import LabBlockChain.BlockChain.basic.Block;
import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
import LabBlockChain.BlockChain.basic.Wallet;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class P2PService implements P2PServiceInterface {
	private List<WebSocket> sockets;
	private BlockChainImplement blockChainOpr;
	MessageType type;

	public P2PService(BlockChainImplement blockChainOpr) {
		this.blockChainOpr = blockChainOpr;
		this.sockets = new ArrayList<WebSocket>();
	}
	
	@Override
	public List<WebSocket> getSockets() {
		return sockets;
	}

	@Override
	public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
		try {
			Message message = JSON.parseObject(msg, Message.class);
			System.out.println("Received from " + webSocket.getRemoteSocketAddress().getPort() + " P2P Message "
			        + JSON.toJSONString(message));
			switch (message.getType()) {
			case 0:
				write(webSocket, responseLatestBlockMsg());
				break;
			case 1:
				write(webSocket, responseBlockChainMsg());
				break;
			case 2:
				write(webSocket, responseTransactions());
				break;
			case 3:
				write(webSocket, responsePackedTransactions());
				break;
			case 4:
				write(webSocket, responseWallets());
				break;
			case 5:
				handleBlockChainResponse(message.getData(), sockets);
				break;
			case 6:
				handleTransactionResponse(message.getData());
				break;
			case 7:
				handlePackedTransactionResponse(message.getData());
				break;
			case 8:
				handleWalletResponse(message.getData());
				break;
			}
		} catch (Exception e) {
			System.out.println("P2P Message Handling Error:" + e.getMessage());
		}
	}

	@Override
	public synchronized void handleBlockChainResponse(String message, List<WebSocket> sockets) {
		List<Block> receiveBlockchain = JSON.parseArray(message, Block.class);
		Collections.sort(receiveBlockchain, new Comparator<Block>() {
			public int compare(Block block1, Block block2) {
				return block1.getIndex() - block2.getIndex();
			}
		});

		Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
		Block latestBlock = blockChainOpr.getLatestBlock();
		if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
			if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
				System.out.println("Add new block to chain");
				if (blockChainOpr.addBlock(latestBlockReceived)) {
					broadcast(responseLatestBlockMsg());
				}
			} else if (receiveBlockchain.size() == 1) {
				System.out.println("query for all blockchain");
				broadcast(queryBlockChainMsg());
			} else {
				blockChainOpr.replaceChain(receiveBlockchain);
			}
		} else {
			System.out.println("Peer Blockchain no longer than local, not adopt.");
		}
	}

	@Override
	public void handleWalletResponse(String message) {
		List<Wallet> wallets = JSON.parseArray(message, Wallet.class);
		System.out.println("wallets wallets:" + wallets.toString() + "\n");
		wallets.forEach(wallet -> {
			System.out.println("wallet json:" + wallet+ "\n");
			System.out.println("wallet getAddress:" + wallet.getAddress()+ "\n");
			System.out.println("wallet getPublicKey:" + wallet.getPublicKey()+ "\n");
			System.out.println("wallet getHashPubKey:" + wallet.getHashPubKey()+ "\n");
			System.out.println("wallet toString:" + wallet.toString()+ "\n");
			blockChainOpr.getOtherWalletMap().put(wallet.getAddress(), wallet);
		});
	}

	@Override
	public void handleTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockChainOpr.getAllTransactions().addAll(txs);
	}
	
	@Override
	public void handlePackedTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockChainOpr.getPackedTransactions().addAll(txs);
	}

	@Override
	public void write(WebSocket ws, String message) {
		System.out.println("Send to" + ws.getRemoteSocketAddress().getPort() + "P2P Message" + message);
		ws.send(message);
	}

	@Override
	public void broadcast(String message) {
		if (sockets.size() == 0) {
			return;
		}
		System.out.println("======Start Broadcasting：");
		for (WebSocket socket : sockets) {
			this.write(socket, message);
		}
		System.out.println("======End Broadcasting");
	}

	@Override
	public String queryBlockChainMsg() {
		return JSON.toJSONString(new Message(MessageType.QUERY_BLOCKCHAIN.value));
	}

	@Override
	public String queryLatestBlockMsg() {
		return JSON.toJSONString(new Message(MessageType.QUERY_LATEST_BLOCK.value));
	}
	
	@Override
	public String queryTransactionMsg() {
		return JSON.toJSONString(new Message(MessageType.QUERY_TRANSACTION.value));
	}
	
	@Override
	public String queryPackedTransactionMsg() {
		return JSON.toJSONString(new Message(MessageType.QUERY_PACKED_TRANSACTION.value));
	}
	
	@Override
	public String queryWalletMsg() {
		return JSON.toJSONString(new Message(MessageType.QUERY_WALLET.value));
	}

	@Override
	public String responseBlockChainMsg() {
		return JSON.toJSONString(new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blockChainOpr.getBlockChain())));
	}

	@Override
	public String responseLatestBlockMsg() {
		Block[] blocks = { blockChainOpr.getLatestBlock() };
		return JSON.toJSONString(new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blocks)));
	}
	
	@Override
	public String responseTransactions() {
		return JSON.toJSONString(new Message(MessageType.RESPONSE_TRANSACTION.value, JSON.toJSONString(blockChainOpr.getAllTransactions())));
	}
	
	@Override
	public String responsePackedTransactions() {
		return JSON.toJSONString(new Message(MessageType.RESPONSE_PACKED_TRANSACTION.value, JSON.toJSONString(blockChainOpr.getPackedTransactions())));
	}
	
	@Override
	public String responseWallets() {
		List<Wallet> wallets = new ArrayList<Wallet>();
		blockChainOpr.getMyWalletMap().forEach((address, wallet) -> {
			wallets.add(new Wallet(wallet.getPublicKey()));
		});
		blockChainOpr.getOtherWalletMap().forEach((address, wallet) -> {
			wallets.add(wallet);
		});
		return JSON.toJSONString(new Message(MessageType.RESPONSE_WALLET.value, JSON.toJSONString(wallets)));
	}
	
}
