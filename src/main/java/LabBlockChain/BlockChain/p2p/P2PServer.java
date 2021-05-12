package LabBlockChain.BlockChain.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * p2pServer
 *
 */
public class P2PServer {

	// command interface
	private P2PServiceInterface p2PServiceInterface;

	/**
	 * constructor for P2PServer
	 * @param p2PServiceInterface
	 */
	public P2PServer(P2PServiceInterface p2PServiceInterface) {

		this.p2PServiceInterface = p2PServiceInterface;
	}

	/**
	 * init server and start service
	 * @param port the port to listen to
	 */
	public void initP2PServer(int port) {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			/**
			 * Callback hook for open
			 * @param webSocket  Web Socket
			 * @param clientHandshake Client Handshake
			 */
			@Override
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				p2PServiceInterface.getSockets().add(webSocket);
			}

			/**
			 * Callback hook for close
			 * @param webSocket web Socket
			 * @param i
			 * @param msg
			 * @param b
			 */
			@Override
			public void onClose(WebSocket webSocket, int i, String msg, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2PServiceInterface.getSockets().remove(webSocket);
			}

			/**
			 * Callback hook for sending message
			 * @param webSocket
			 * @param msg reason for message
			 */
			@Override
			public void onMessage(WebSocket webSocket, String msg) {
				p2PServiceInterface.messageHandler(
						webSocket, msg, p2PServiceInterface.getSockets());
			}

			/**
			 * Callback hook for error
			 * @param webSocket web Socket
			 * @param e
			 */
			@Override
			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2PServiceInterface.getSockets().remove(webSocket);
			}

			public void onStart() { }
		};
		// start socket
		socketServer.start();
		System.out.println("listening websocket LabBlockChain.basic.BlockChain.p2p port on: " + port);
	}

}
