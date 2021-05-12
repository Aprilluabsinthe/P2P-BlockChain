package LabBlockChain.BlockChain.p2p;

import org.java_websocket.WebSocket;

import java.util.List;

/**
 * the abstract interface for p2p service
 */
public interface P2PServiceInterface {
    List<WebSocket> getSockets();

    void messageHandler(WebSocket webSocket, String msg, List<WebSocket> sockets);

    void handleBlockChainResponse(String message, List<WebSocket> sockets);

    void walletResponse(String message);

    void transactionResponse(String message);

    void packedTransactionResponse(String message);

    void printAndSend(WebSocket ws, String message);

    void writeToFile(WebSocket ws, String message);

    void broadcast(String message);

    String generateBlockChainQuery();

    String generateLatestBlockQuery();

    String GenerateTransactionQuery();

    String GeneratePackedTransactionQuery();

    String generateWalletQuery();

    String generateBlockChainResponse();

    String generateLatestBlockResponse();

    String generateTransactionsResponse();

    String generatePackedTransactionsresponse();

    String generateWalletsResponse();
}
