package LabBlockChain.BlockChain.p2p;

import LabBlockChain.BlockChain.basic.Block;
import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
import LabBlockChain.BlockChain.basic.Wallet;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * receive message and operate according to message type
 */
public class P2PService implements P2PServiceInterface {
	private List<WebSocket> sockets;
	private BlockChainImplement blockChainOpr;
	MessageType type;

	/**
	 * The constrcution for the service
	 * @param blockChainOpr
	 */
	public P2PService(BlockChainImplement blockChainOpr) {
		this.blockChainOpr = blockChainOpr;
		this.sockets = new ArrayList<WebSocket>();
	}

	/**
	 * get current connected peers
	 * @return a list of peer sockets
	 */
	@Override
	public List<WebSocket> getSockets() {
		return sockets;
	}

	/**
	 * a wrap function to deal with received messages
	 * @param webSocket web Socket
	 * @param msg the String raw message received
	 * @param sockets all peer node sockets
	 */
	@Override
	public void messageHandler(WebSocket webSocket, String msg, List<WebSocket> sockets) {
		try {
			Message message = JSON.parseObject(msg, Message.class);
			System.out.println("Received from " + webSocket.getRemoteSocketAddress().getPort() + " P2P Message "
					+ JSON.toJSONString(message));
			switch (message.getType()) {
				case 0: // get_Latest_Block
					printAndSend(webSocket, generateLatestBlockResponse());
					writeToFile(webSocket, generateLatestBlockResponse());
					break;
				case 1:// QUERY_BLOCKCHAIN
					printAndSend(webSocket, generateBlockChainResponse());
					writeToFile(webSocket, generateBlockChainResponse());
					break;
				case 2:// QUERY TRANSACTION
					printAndSend(webSocket, generateTransactionsResponse());
					writeToFile(webSocket, generateTransactionsResponse());
					break;
				case 3:// response Packed Transactions
					printAndSend(webSocket, generatePackedTransactionsresponse());
					writeToFile(webSocket, generatePackedTransactionsresponse());
					break;
				case 4:// response Wallets
					printAndSend(webSocket, generateWalletsResponse());
					writeToFile(webSocket, generateWalletsResponse());
					break;
				case 5:// post BlockChain
					handleBlockChainResponse(message.getData(), sockets);
					break;
				case 6:// post transaction
					transactionResponse(message.getData());
					break;
				case 7:// post Packed Transaction
					packedTransactionResponse(message.getData());
					break;
				case 8:// Wallet Response
					walletResponse(message.getData());
					break;
			}
		} catch (Exception e) {
			System.out.println("P2P Message Handling Error:" + e.getMessage());
		}
	}

	/**
	 * Consensus
	 * If two peers mine a block successfully, keep the two blockchains locally until one is longer than the other one. The longer one will overwrite the other’s local records until all achieve the same record.
	 * When a peer wants to participate in the system, it will need to download all the information. When a peer has been disconnected for some time, it will need to update it’s local cache.
	 * Compare the  global blockchain and the local blockchain.
	 * If the last block of the received blockchain has a higher index than the local blockchain:
	 * If the last one is exactly what the peer misses, directed add the block to local blockchain
	 * If the peer has missed more than one block, it should broadcast and ask all peers for the longest chain. Then pull down the longest chain.
	 * @param message raw string message to be deal with
	 * @param sockets socket list
	 */
	@Override
	public synchronized void handleBlockChainResponse(String message, List<WebSocket> sockets) {
		List<Block> receivedChain = JSON.parseArray(message, Block.class);

		// self defined block compartion
		Collections.sort(receivedChain, new Comparator<Block>() {
			public int compare(Block block1, Block block2) {
				return block1.getIndex() - block2.getIndex();
			}
		});

		Block latestBlockReceived = receivedChain.get(receivedChain.size() - 1);
		Block latestBlock = blockChainOpr.getLatestBlock();

		// latest Block Received index bigger than current last, should update
		if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
			// the receiced is exactly the last block we need
			if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
				System.out.println("Add new block to chain");
				if (blockChainOpr.addBlock(latestBlockReceived)) {
					broadcast(generateLatestBlockResponse());
				}
			}
			//
			else if (receivedChain.size() == 1) {
				System.out.println("query for all blockchain");
				broadcast(generateBlockChainQuery());
			}
			// more than If the peer has missed more than one block,
			// it should broadcast and ask all peers for the longest chain. Then pull down the longest chain.
			else {
				System.out.println("Peer Blockchain longer than local, adopt.");
				blockChainOpr.replaceChain(receivedChain);
			}
		} else {
			System.out.println("Peer Blockchain no longer than local, not adopt.");
		}
	}

	/**
	 *
	 * @param message
	 */
	@Override
	public void walletResponse(String message) {
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

	/**
	 * get all transactions, includes packed and unpacked
	 * @param message raw string message to ge deal with
	 */
	@Override
	public void transactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockChainOpr.getAllTransactions().addAll(txs);
	}

	/**
	 * get packed transactions, only include packed txs
	 * @param message raw string message to ge deal with
	 */
	@Override
	public void packedTransactionResponse(String message) {
		List<Transaction> txs = JSON.parseArray(message, Transaction.class);
		blockChainOpr.getPackedTransactions().addAll(txs);
	}

	/**
	 * printAndSend message to console
	 * @param ws websocket
	 * @param message the message to printAndSend
	 */
	@Override
	public void printAndSend(WebSocket ws, String message) {
		System.out.println("P2P Message" + message + "Send to port" + ws.getRemoteSocketAddress().getPort());
		ws.send(message);
	}

	/**
	 * write to file for write and debug
	 * @param ws
	 * @param message
	 */
	public void writeToFile(WebSocket ws, String message) {
		try {
			Path filepath = Paths.get("record.txt");
			if(filepath == null){
				File myObj = new File("record.txt");
				myObj.createNewFile();
				filepath = Paths.get("record.txt");
			}

			Files.write(filepath,"".getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		ws.send(message);
	}


	/**
	 * broadcast current message to all peers
	 * in essense is <code>ws.send(message)</code>
	 * @param message the message to broad cast
	 */
	@Override
	public void broadcast(String message) {
		if (sockets.size() == 0) {
			return;
		}
		System.out.println("\n>>>>>>>>>>>>>>Start Broadcasting：>>>>>>>>>>>>>>");
		for (WebSocket socket : sockets) {
			this.printAndSend(socket, message);
		}
		System.out.println(">>>>>>>>>>>>>>End Broadcasting>>>>>>>>>>>>>>\n");
	}

	/**
	 * response to blockchain query
	 * @return Message containing message type
	 */
	@Override
	public String generateBlockChainQuery() {

		return JSON.toJSONString(new Message(MessageType.QUERY_BLOCKCHAIN.value));
	}

	/**
	 * response to last block query
	 * @return Message containing message type
	 */
	@Override
	public String generateLatestBlockQuery() {

		return JSON.toJSONString(new Message(MessageType.QUERY_LATEST_BLOCK.value));
	}
	/**
	 * response to Transaction query
	 * @return Message containing message type
	 */
	@Override
	public String GenerateTransactionQuery() {

		return JSON.toJSONString(new Message(MessageType.QUERY_TRANSACTION.value));
	}
	/**
	 * response to Packed Transaction query
	 * @return Message containing message type
	 */
	@Override
	public String GeneratePackedTransactionQuery() {
		return JSON.toJSONString(new Message(MessageType.QUERY_PACKED_TRANSACTION.value));
	}
	/**
	 * response to Wallet query
	 * @return Message containing message type
	 */
	@Override
	public String generateWalletQuery() {

		return JSON.toJSONString(new Message(MessageType.QUERY_WALLET.value));
	}
	/**
	 * response to blockchain
	 * @return Message containing message type
	 */
	@Override
	public String generateBlockChainResponse() {
		return JSON.toJSONString(
				new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blockChainOpr.getBlockChain())));
	}
	/**
	 * response to LatestBlock
	 * @return Message containing message type
	 */
	@Override
	public String generateLatestBlockResponse() {
		Block[] blocks = { blockChainOpr.getLatestBlock() };
		return JSON.toJSONString(
				new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blocks)));
	}
	/**
	 * response to Transactions
	 * @return Message containing message type
	 */
	@Override
	public String generateTransactionsResponse() {
		return JSON.toJSONString(
				new Message(MessageType.RESPONSE_TRANSACTION.value, JSON.toJSONString(blockChainOpr.getAllTransactions())));
	}
	/**
	 * response to Packed Transactions
	 * @return Message containing message type
	 */
	@Override
	public String generatePackedTransactionsresponse() {
		return JSON.toJSONString(
				new Message(MessageType.RESPONSE_PACKED_TRANSACTION.value, JSON.toJSONString(blockChainOpr.getPackedTransactions())));
	}

	/**
	 * response to Wallets
	 * @return Message containing message type
	 */
	@Override
	public String generateWalletsResponse() {
		List<Wallet> wallets = new ArrayList<Wallet>();
		// lambda expressions
		// https://stackoverflow.com/questions/46898/how-do-i-efficiently-iterate-over-each-entry-in-a-java-map
		blockChainOpr.getMyWalletMap().forEach((address, wallet) -> {
			wallets.add(new Wallet(wallet.getPublicKey()));
		});
		blockChainOpr.getOtherWalletMap().forEach((address, wallet) -> {
			wallets.add(wallet);
		});
		return JSON.toJSONString(
				new Message(MessageType.RESPONSE_WALLET.value, JSON.toJSONString(wallets)));
	}

}
