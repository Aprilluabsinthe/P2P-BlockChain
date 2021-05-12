package LabBlockChain.Block;

import LabBlockChain.BlockChain.Helper.Coder;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.Transaction.TransactionInput;
import LabBlockChain.BlockChain.Transaction.TransactionOutput;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.basic.Wallet;
import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
import LabBlockChain.BlockChain.LabCoin.BlockChainInterface;
import LabBlockChain.BlockChain.p2p.P2PServiceInterface;
import LabBlockChain.BlockChain.p2p.P2PClient;
import LabBlockChain.BlockChain.p2p.P2PServer;
import LabBlockChain.BlockChain.p2p.P2PService;
import LabBlockChain.BlockChain.http.HTTPService;

import com.google.gson.Gson;
import org.junit.*;
import org.json.*;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class TransactionTest {
    Gson gson = new Gson();
    private static final int TRANS_AMOUNT = 1;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void transactionAPITest() throws IOException, InterruptedException {
        BlockChainImplement blockChainImplement = new BlockChainImplement();

        // start the server in the background for testing
        try {
            P2PServiceInterface p2PServiceInterface = new P2PService(blockChainImplement);

            P2PServer p2pServer = new P2PServer(p2PServiceInterface);
            P2PClient p2pClient = new P2PClient(p2PServiceInterface);
            int p2pPort = 5678;
            p2pServer.initP2PServer(p2pPort);
            
            HTTPService httpService = new HTTPService(blockChainImplement, p2PServiceInterface);
            int httpPort = 1234;
            httpService.initHTTPServer(httpPort, true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("startup is error:" + e.getMessage());
        }
        Wallet sender = blockChainImplement.createWallet();

        blockChainImplement.mine(sender.getAddress());

        Wallet receiver = blockChainImplement.createWallet();

        TransactionRequest transactionRequest = new TransactionRequest(sender.getAddress(), receiver.getAddress(), TRANS_AMOUNT);

        HttpClient httpClient = HttpClient.newHttpClient();


        // issue a post request to a new transaction from the sender wallet to the receiver wallet
        // now we have mined 1 LabCoin in the sender wallet, so we should be able to make a 
        // transaction of 1 LabCoin to the receiver
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:1234/transactions/new"))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(transactionRequest)))
                    .build();

        HttpResponse<String> response;
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String respText = response.body();
        String searchFor = "New Transaction:";
        int idx = respText.indexOf(searchFor);
        if (idx != -1) {
            String tranString = respText.substring(idx + searchFor.length());

            try {
                JSONObject jsonObj = new JSONObject(tranString);
                boolean isBaseTx = jsonObj.getBoolean("baseTx");
                JSONObject txOut = jsonObj.getJSONObject("txOut");
                int tranAmount = txOut.getInt("value");

                // the amount should be the amount we set, and the transaction should NOT a base transaction, 
                // which means that the transaction is valid
                Assert.assertFalse(isBaseTx);
                Assert.assertTrue(tranAmount == TRANS_AMOUNT);
                System.out.println("Transaction test complete!");
            } catch (JSONException e) {
                return;
            }
        }
    }
}