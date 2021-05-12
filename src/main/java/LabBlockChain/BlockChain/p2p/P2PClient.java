package LabBlockChain.BlockChain.p2p;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * p2pClient
 *
 */
public class P2PClient {

	private P2PServiceInterface p2PServiceInterface;

	public P2PClient(P2PServiceInterface p2PServiceInterface) {

		this.p2PServiceInterface = p2PServiceInterface;
	}

	public void connectToPeer(String peer) {
		// peer be like ws://localhost:port
		// build new Web Socket Client
		try {
			// open websocket
			final WebSocketClient socketClient = new WebSocketClient(new URI(peer)) {
				/**
				 * Callback hook for Connection open events.
				 * @param serverHandshake serverHandshake
				 */
				@Override
				public void onOpen(ServerHandshake serverHandshake) {
					// on open operation
					p2PServiceInterface.printAndSend(this, p2PServiceInterface.generateLatestBlockQuery());
					p2PServiceInterface.printAndSend(this, p2PServiceInterface.GenerateTransactionQuery());
					p2PServiceInterface.printAndSend(this, p2PServiceInterface.GeneratePackedTransactionQuery());
					p2PServiceInterface.printAndSend(this, p2PServiceInterface.generateWalletQuery());
					p2PServiceInterface.getSockets().add(this);
				}

				/**
				 * Callback hook for Message Events. This method will be invoked when a client send a message.
				 * send message to web socket
				 * @param msg reason for message
				 */
				@Override
				public void onMessage(String msg) {
					p2PServiceInterface.messageHandler(
							this, msg, p2PServiceInterface.getSockets());
				}

				/**
				 * Callback hook for Connection close events.
				 * @param i
				 * @param msg the reason for close
				 * @param b
				 */
				@Override
				public void onClose(int i, String msg, boolean b) {
					System.out.println("connection failed");
					p2PServiceInterface.getSockets().remove(this);
				}

				/**
				 * Callback hook for Connection error events.
				 * @param exc exception
				 */
				@Override
				public void onError(Exception exc) {
					System.out.println("connection failed");
					p2PServiceInterface.getSockets().remove(this);
				}
			};
			socketClient.connect();
		} catch (URISyntaxException e) {
			System.out.println("LabBlockChain.basic.BlockChain.p2p connect is error:" + e.getMessage());
		}
	}

}
