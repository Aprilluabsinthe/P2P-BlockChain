package LabBlockChain.BlockChain.p2p;

import org.java_websocket.WebSocket;

import java.util.List;

public interface P2PServiceInterface {
    List<WebSocket> getSockets();

    void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets);

    void handleBlockChainResponse(String message, List<WebSocket> sockets);

    void handleWalletResponse(String message);

    void handleTransactionResponse(String message);

    void handlePackedTransactionResponse(String message);

    void write(WebSocket ws, String message);

    void broadcast(String message);

    String queryBlockChainMsg();

    String queryLatestBlockMsg();

    String queryTransactionMsg();

    String queryPackedTransactionMsg();

    String queryWalletMsg();

    String responseBlockChainMsg();

    String responseLatestBlockMsg();

    String responseTransactions();

    String responsePackedTransactions();

    String responseWallets();
}
