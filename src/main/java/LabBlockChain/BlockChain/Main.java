package LabBlockChain.BlockChain;

import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
import LabBlockChain.BlockChain.http.HTTPService;
import LabBlockChain.BlockChain.p2p.P2PClient;
import LabBlockChain.BlockChain.p2p.P2PServer;
import LabBlockChain.BlockChain.p2p.P2PService;
import LabBlockChain.BlockChain.p2p.P2PServiceInterface;


/**
 * Main function
 * we can pass
 * 1 argument: the port to start http service
 * 2 arguments: the port to start p2p service
 * 3 arguments: the websocket address to connect to (the peer to connect to)
 */
public class Main {
	public static void main(String[] args) {
		if (args != null && (args.length == 1 || args.length == 2 || args.length == 3)) {
			try {
				BlockChainImplement blockChainOpr = new BlockChainImplement();
				P2PServiceInterface p2PServiceInterface = new P2PService(blockChainOpr);

				// start and init p2p service
				P2PServerInit(args, p2PServiceInterface);

				// start and init HTTP service
				HTTPServerInit(args,blockChainOpr,p2PServiceInterface);
//				HTTPService httpService = new HTTPService(blockChainOpr, p2PServiceInterface);
//				int httpPort = Integer.valueOf(args[0]);
//				httpService.initHTTPServer(httpPort);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("startup is error:" + e.getMessage());
			}
		}
		else {
			System.out.println("usage: java -jar labBlockChain-1.0-SNAPSHOT.jar 1234 5678");
		}
	}

	private static void P2PServerInit(String[] args, P2PServiceInterface p2PServiceInterface) {
		P2PServer p2pServer = new P2PServer(p2PServiceInterface);
		P2PClient p2pClient = new P2PClient(p2PServiceInterface);
		int p2pPort = Integer.parseInt(args[1]);
		p2pServer.initP2PServer(p2pPort);
		// optional arguments
		if (args.length == 3 && args[2] != null) {
			p2pClient.connectToPeer(args[2]);
		}
	}

	private static void HTTPServerInit(String[] args,BlockChainImplement blockChainOpr,P2PServiceInterface p2PServiceInterface) {
		HTTPService httpService = new HTTPService(blockChainOpr, p2PServiceInterface);
		int httpPort = Integer.parseInt(args[0]);
		httpService.initHTTPServer(httpPort, false);
	}
}
