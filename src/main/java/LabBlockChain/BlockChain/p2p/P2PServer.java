package LabBlockChain.BlockChain.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * p2pServer
 * 
 * @author aaron
 *
 */
public class P2PServer {
	
	private P2PServiceInterface p2PServiceInterface;
	
	public P2PServer(P2PServiceInterface p2PServiceInterface) {
	    this.p2PServiceInterface = p2PServiceInterface;
    }

	public void initP2PServer(int port) {
		final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
			public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
				p2PServiceInterface.getSockets().add(webSocket);
			}

			public void onClose(WebSocket webSocket, int i, String s, boolean b) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2PServiceInterface.getSockets().remove(webSocket);
			}

			public void onMessage(WebSocket webSocket, String msg) {
				p2PServiceInterface.handleMessage(webSocket, msg, p2PServiceInterface.getSockets());
			}

			public void onError(WebSocket webSocket, Exception e) {
				System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
				p2PServiceInterface.getSockets().remove(webSocket);
			}

			public void onStart() {

			}
		};
		socketServer.start();
		System.out.println("listening websocket LabBlockChain.basic.BlockChain.p2p port on: " + port);
	}

}