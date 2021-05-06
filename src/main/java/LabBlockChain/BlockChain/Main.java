package LabBlockChain.BlockChain;

import LabBlockChain.BlockChain.basic.BlockService;
import LabBlockChain.BlockChain.http.HTTPService;
import LabBlockChain.BlockChain.p2p.P2PClient;
import LabBlockChain.BlockChain.p2p.P2PServer;
import LabBlockChain.BlockChain.p2p.P2PService;
import LabBlockChain.BlockChain.p2p.P2PServiceInterface;


public class Main {
	public static void main(String[] args) {
		if (args != null && (args.length == 1 || args.length == 2 || args.length == 3)) {
			try {
				BlockService blockService = new BlockService();
				P2PServiceInterface p2PServiceInterface = new P2PService(blockService);
				startP2PServer(args, p2PServiceInterface);
				HTTPService httpService = new HTTPService(blockService, p2PServiceInterface);
				int httpPort = Integer.valueOf(args[0]);
				httpService.initHTTPServer(httpPort);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("startup is error:" + e.getMessage());
			}
		} else {
			System.out.println("usage: java -jar labBlockChain-1.0-SNAPSHOT.jar 1234 5678");
		}
	}

	private static void startP2PServer(String[] args, P2PServiceInterface p2PServiceInterface) {
		P2PServer p2pServer = new P2PServer(p2PServiceInterface);
		P2PClient p2pClient = new P2PClient(p2PServiceInterface);
		int p2pPort = Integer.valueOf(args[1]);
		p2pServer.initP2PServer(p2pPort);
		if (args.length == 3 && args[2] != null) {
			p2pClient.connectToPeer(args[2]);
		}
	}
}
