package LabBlockChain.BlockChain.p2p;

import LabBlockChain.BlockChain.basic.Block;
import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.basic.BlockService;
import LabBlockChain.BlockChain.basic.Wallet;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class P2PService implements P2PServiceInterface {
	private List<WebSocket> sockets;
	private BlockService blockService;
	MessageType type;

	public P2PService(BlockService blockService) {
		this.blockService = blockService;
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
			System.out.println("Received from" + webSocket.getRemoteSocketAddress().getPort() + "P2P Message"
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
		Block latestBlock = blockService.getLatestBlock();
		if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
			if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
				System.out.println("Add new block to chain");
				if (blockService.addBlock(latestBlockReceived)) {
					broatcast(responseLatestBlockMsg());
				}
			} else if (receiveBlockchain.size() == 1) {
				System.out.println("query for all blockchain");
				broatcast(queryBlockChainMsg());
			} else {
				blockService.replaceChain(receiveBlockchain);
			}
		} else {
			System.out.println("Peer Blockchain no longer than local, not adopt.");
		}
	}

	@Override
	public void handleWalletResponse(String message) {
		List<Wallet> wallets = JSON.parseArray(message, Wallet.class);
		wallets.forEach(wallet -> {
			blockService.getOtherWalletMap().put(wallet.getAddress(), wallet);
		});
	}

	@Override
	public void handleTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockService.getAllTransactions().addAll(txs);
	}
	
	@Override
	public void handlePackedTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockService.getPackedTransactions().addAll(txs);
	}

	@Override
	public void write(WebSocket ws, String message) {
		System.out.println("Send to" + ws.getRemoteSocketAddress().getPort() + "P2P Message" + message);
		ws.send(message);
	}

	@Override
	public void broatcast(String message) {
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
		return JSON.toJSONString(new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blockService.getBlockChain())));
	}

	@Override
	public String responseLatestBlockMsg() {
		Block[] blocks = { blockService.getLatestBlock() };
		return JSON.toJSONString(new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blocks)));
	}
	
	@Override
	public String responseTransactions() {
		return JSON.toJSONString(new Message(MessageType.RESPONSE_TRANSACTION.value, JSON.toJSONString(blockService.getAllTransactions())));
	}
	
	@Override
	public String responsePackedTransactions() {
		return JSON.toJSONString(new Message(MessageType.RESPONSE_PACKED_TRANSACTION.value, JSON.toJSONString(blockService.getPackedTransactions())));
	}
	
	@Override
	public String responseWallets() {
		List<Wallet> wallets = new ArrayList<Wallet>();
		blockService.getMyWalletMap().forEach((address,wallet) -> {
			wallets.add(new Wallet(wallet.getPublicKey()));
		});
		blockService.getOtherWalletMap().forEach((address,wallet) -> {
			wallets.add(wallet);
		});
		return JSON.toJSONString(new Message(MessageType.RESPONSE_WALLET.value, JSON.toJSONString(wallets)));
	}
	
}
