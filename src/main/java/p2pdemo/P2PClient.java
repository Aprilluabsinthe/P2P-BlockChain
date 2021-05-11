package p2pdemo;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class P2PClient {
	
	private List<WebSocket> sockets = new ArrayList<WebSocket>();

	public List<WebSocket> getSockets() {
		return sockets;
	}

	public void connectToPeer(String peer) { 
		try {
			final WebSocketClient socketClient = new WebSocketClient(new URI(peer)) {
				@Override
				public void onOpen(ServerHandshake serverHandshake) {
					write(this, "Client Connect Success");
					sockets.add(this);
				}

				@Override
				public void onMessage(String msg) {
					System.out.println("Received Message from Server" + msg);
				}

				@Override
				public void onClose(int i, String msg, boolean b) {
					System.out.println("connection closed");
					sockets.remove(this);
				}

				@Override
				public void onError(Exception e) {
					System.out.println("connection failed");
					sockets.remove(this);
				}
			};
			socketClient.connect();
		} catch (URISyntaxException e) {
			System.out.println("LabBlockChain.basic.BlockChain.p2p connect is error:" + e.getMessage());
		}
	}
	
	public void write(WebSocket ws, String message) {
		System.out.println("Send to" + ws.getRemoteSocketAddress().getPort() + " p2p Message: " + message);
		ws.send(message);
	}
	
	public void broadcast(String message) {
		if (sockets.size() == 0) {
			return;
		}
		System.out.println("======Start Broadcast：");
		for (WebSocket socket : sockets) {
			this.write(socket, message);
		}
		System.out.println("======End Broadcast");
	}

}
