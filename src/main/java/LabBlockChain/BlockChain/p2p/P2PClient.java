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
		try {
			final WebSocketClient socketClient = new WebSocketClient(new URI(peer)) {
				@Override
				public void onOpen(ServerHandshake serverHandshake) {
					p2PServiceInterface.write(this, p2PServiceInterface.queryLatestBlockMsg());
					p2PServiceInterface.write(this, p2PServiceInterface.queryTransactionMsg());
					p2PServiceInterface.write(this, p2PServiceInterface.queryPackedTransactionMsg());
					p2PServiceInterface.write(this, p2PServiceInterface.queryWalletMsg());
					p2PServiceInterface.getSockets().add(this);
				}

				@Override
				public void onMessage(String msg) {
					p2PServiceInterface.handleMessage(this, msg, p2PServiceInterface.getSockets());
				}

				@Override
				public void onClose(int i, String msg, boolean b) {
					System.out.println("connection failed");
					p2PServiceInterface.getSockets().remove(this);
				}

				@Override
				public void onError(Exception e) {
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
