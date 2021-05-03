package LabBlockChain.BlockChain.http;

import LabBlockChain.BlockChain.Transaction.*;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.p2p.MessageType;
import LabBlockChain.BlockChain.p2p.P2PServiceInterface;
import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.basic.BlockService;
import LabBlockChain.BlockChain.basic.Wallet;
import LabBlockChain.BlockChain.p2p.Message;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class HTTPService {
	private BlockService blockService;
	private P2PServiceInterface p2PServiceInterface;

	public HTTPService(BlockService blockService, P2PServiceInterface p2PServiceInterface) {
		this.blockService = blockService;
		this.p2PServiceInterface = p2PServiceInterface;
	}

	public void initHTTPServer(int port) {
		try {
			Server server = new Server(port);
			System.out.println("listening LabBlockChain.http port on: " + port);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);

			// 查询区块链
			context.addServlet(new ServletHolder(new ChainServlet()), "/chain");
			// 创建钱包
			context.addServlet(new ServletHolder(new CreateWalletServlet()), "/wallet/create");
			// 查询钱包
			context.addServlet(new ServletHolder(new GetWalletsServlet()), "/wallet/get");
			// 挖矿
			context.addServlet(new ServletHolder(new MineServlet()), "/mine");
			// 转账交易
			context.addServlet(new ServletHolder(new NewTransactionServlet()), "/transactions/new");
			// 查询未打包交易
			context.addServlet(new ServletHolder(new GetUnpackedTransactionServlet()), "/transactions/unpacked/get");
			// 查询钱包余额
			context.addServlet(new ServletHolder(new GetWalletBalanceServlet()), "/wallet/balance/get");
			// 查询所有socket节点
			context.addServlet(new ServletHolder(new PeersServlet()), "/peers");

			server.start();
			server.join();
		} catch (Exception e) {
			System.out.println("init LabBlockChain.basic.BlockChain.http server is error:" + e.getMessage());
		}
	}

	private class ChainServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print("Current BlockChain：" + JSON.toJSONString(blockService.getBlockChain()));
		}
	}

	private class MineServlet extends HttpServlet {
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			String address = req.getParameter("address");
			Wallet myWallet = blockService.getMyWalletMap().get(address);
			System.out.println(myWallet);
			if (myWallet == null) {
				resp.getWriter().print("Wallet for Mining not exist");
				return;
			}
			Block newBlock = blockService.mine(address);
			if (newBlock == null) {
				resp.getWriter().print("Mining Failed;");
				return;
			}
			Block[] blocks = {newBlock};
			String msg = JSON.toJSONString(new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blocks)));
			p2PServiceInterface.broatcast(msg);
			resp.getWriter().print("New Block from Mining：" + JSON.toJSONString(newBlock));
		}
	}

	private class CreateWalletServlet extends HttpServlet {
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			Wallet wallet = blockService.createWallet();
			Wallet[] wallets = {new Wallet(wallet.getPublicKey())}; 
			String msg = JSON.toJSONString(new Message(MessageType.RESPONSE_WALLET.value, JSON.toJSONString(wallets)));
			p2PServiceInterface.broatcast(msg);
			resp.getWriter().print("Wallet Created! Wallet Address： " + wallet.getAddress());
		}
	}

	private class GetWalletsServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print("Current Wallet:" + JSON.toJSONString(blockService.getMyWalletMap().values()));
		}
	}

	private class NewTransactionServlet extends HttpServlet {
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			TransactionParam txParam = JSON.parseObject(getReqBody(req), TransactionParam.class);

			Wallet senderWallet = blockService.getMyWalletMap().get(txParam.getSender());
			Wallet recipientWallet = blockService.getMyWalletMap().get(txParam.getRecipient());
			if (recipientWallet == null) {
				recipientWallet = blockService.getOtherWalletMap().get(txParam.getRecipient());
			}
			if (senderWallet == null || recipientWallet == null) {
				resp.getWriter().print("Wallet Not Exist");
				return;
			}

			Transaction newTransaction = blockService.createTransaction(senderWallet, recipientWallet,
			        txParam.getAmount());
			if (newTransaction == null) {
				resp.getWriter().print(
				        "Wallet" + txParam.getSender() + "Do not have enough balance (require" + txParam.getAmount() + "LabCoin UTXO)");
			} else {
				resp.getWriter().print("New Transaction:" + JSON.toJSONString(newTransaction));
				Transaction[] txs = {newTransaction}; 
				String msg = JSON.toJSONString(new Message(MessageType.RESPONSE_TRANSACTION.value, JSON
				        .toJSONString(txs)));
				p2PServiceInterface.broatcast(msg);
			}
		}
	}

	private class GetWalletBalanceServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			String address = req.getParameter("address");
			resp.getWriter().print("Balance in Wallet is:" + blockService.getWalletBalance(address) + " LabCoin");
		}
	}

	private class GetUnpackedTransactionServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			List<Transaction> transactions = new ArrayList<>(blockService.getAllTransactions());
			transactions.removeAll(blockService.getPackedTransactions());
			resp.getWriter().print("UTXOs in the Node：" + JSON.toJSONString(transactions));
		}
	}
	
    private class PeersServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");
            for (WebSocket socket : p2PServiceInterface.getSockets()) {
                InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
                resp.getWriter().print(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort() + "  ");
            }
        }
    }
    
	private String getReqBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		return body;
	}
}
