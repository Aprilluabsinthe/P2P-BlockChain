package LabBlockChain.BlockChain.http;

import LabBlockChain.BlockChain.Transaction.*;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.p2p.MessageType;
import LabBlockChain.BlockChain.p2p.P2PServiceInterface;
import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
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


/**
 * ref:
 * https://www.baeldung.com/jetty-embedded
 * https://stackoverflow.com/questions/2717294/create-a-simple-http-server-with-java
 * http://avro.apache.org/docs/current/api/java/org/apache/avro/ipc/jetty/HttpServer.html
 */
public class HTTPService {
	private BlockChainImplement blockChainOpr;
	private P2PServiceInterface p2PServiceInterface;

	public HTTPService(BlockChainImplement blockChainOpr, P2PServiceInterface p2PServiceInterface) {
		this.blockChainOpr = blockChainOpr;
		this.p2PServiceInterface = p2PServiceInterface;
	}

	public void initHTTPServer(int port, boolean runInBackground) {
		try {
			Server server = new Server(port);
			System.out.println("listening LabBlockChain.http port on: " + port);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			// register url and apis
			context.addServlet(new ServletHolder(new ChainServlet()), "/chain");
			context.addServlet(new ServletHolder(new CreateWalletServlet()), "/wallet/create");
			context.addServlet(new ServletHolder(new GetWalletsServlet()), "/wallet/get");
			context.addServlet(new ServletHolder(new GetOthersWalletsServlet()), "/wallet/get/other");
			context.addServlet(new ServletHolder(new GetWalletBalanceServlet()), "/wallet/get/balance");
			context.addServlet(new ServletHolder(new MineServlet()), "/mine");
			context.addServlet(new ServletHolder(new NewTransactionArgsServlet()), "/transactions/build");
			context.addServlet(new ServletHolder(new NewTransactionServlet()), "/transactions/new");
			context.addServlet(new ServletHolder(new GetPackedTransactionServlet()), "/transactions/get/packed");
			context.addServlet(new ServletHolder(new GetUnpackedTransactionServlet()), "/transactions/get/unpacked");
			context.addServlet(new ServletHolder(new GetAllTransactionServlet()), "/transactions/get/all");
			context.addServlet(new ServletHolder(new PeersServlet()), "/peers");

			server.start();
			if (!runInBackground) {
				server.join();
			}
		} catch (Exception e) {
			System.out.println("init LabBlockChain.basic.BlockChain.http server is error:" + e.getMessage());
		}
	}

	/**
	 * getBlockChain method HttpService
	 */
	private class ChainServlet extends HttpServlet {
		/**
		 * should be GET method,
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print("Current BlockChain: " + JSON.toJSONString(blockChainOpr.getBlockChain()));
		}
	}

	/**
	 * mine a new block, method HttpService
	 */
	private class MineServlet extends HttpServlet {
		/**
		 * Should be a POST method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			String address = req.getParameter("address");
			Wallet myWallet = blockChainOpr.getMyWalletMap().get(address);
			System.out.println(myWallet);
			if (myWallet == null) {
				resp.getWriter().print("Wallet for Mining not exist");
				return;
			}
			Block newBlock = blockChainOpr.mine(address);
			if (newBlock == null) {
				resp.getWriter().print("Mining Failed;");
				return;
			}
			Block[] blocks = {newBlock};
			String msg = JSON.toJSONString(
					new Message(MessageType.RESPONSE_BLOCKCHAIN.value, JSON.toJSONString(blocks)));
			p2PServiceInterface.broadcast(msg);
			resp.getWriter().print("New Block from Mining???" + JSON.toJSONString(newBlock));
		}
	}

	/**
	 * create a new block, method HttpService
	 */
	private class CreateWalletServlet extends HttpServlet {
		/**
		 * should be POST method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			Wallet wallet = blockChainOpr.createWallet();
			Wallet[] wallets = {new Wallet(wallet.getPublicKey())};
			String msg = JSON.toJSONString(
					new Message(MessageType.RESPONSE_WALLET.value, JSON.toJSONString(wallets)));
			p2PServiceInterface.broadcast(msg);
			resp.getWriter().print("Wallet Created! Wallet Address??? " + wallet.getAddress());
		}
	}

	/**
	 * query for wallet method http service
	 */
	private class GetWalletsServlet extends HttpServlet {
		/**
		 * should be GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print("Current Wallet:" + JSON.toJSONString(blockChainOpr.getMyWalletMap().values()));
		}
	}

	/**
	 * get others' wallet http service
	 */
	private class GetOthersWalletsServlet extends HttpServlet {
		/**
		 * SHould be GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print("Others' Wallets:" + JSON.toJSONString(blockChainOpr.getOtherWalletMap().values()));
		}
	}

	/**
	 * generate new transaction in a block
	 */
	private class NewTransactionArgsServlet extends HttpServlet {
		/**
		 * redirect Get method
		 * @param req
		 * @param resp
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			doPost(req,resp);
		}

		/**
		 * POST method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			String sender = req.getParameter("sender");
			String recipient = req.getParameter("recipient");
			int amount = Integer.parseInt(req.getParameter("amount"));

			Wallet senderWallet = blockChainOpr.getMyWalletMap().get(sender);
			// wallet can be mine or others
			resp.getWriter().print("my Wallet ");
			Wallet recipientWallet = blockChainOpr.getMyWalletMap().get(recipient);
			if (recipientWallet == null) {
				resp.getWriter().print("others' Wallet ");
				recipientWallet = blockChainOpr.getOtherWalletMap().get(recipient);
			}

			resp.getWriter().print("Sender Wallet is: " + JSON.toJSONString(senderWallet) + " Receiver Wallet is:" + JSON.toJSONString(recipientWallet) + " amount is:" + amount + "\n");
			if (senderWallet == null || recipientWallet == null) {
				resp.getWriter().print("Wallet Not Exist");
				return;
			}

			Transaction newTransaction = blockChainOpr.createTransaction(senderWallet, recipientWallet, amount);
			if (newTransaction == null) {
				resp.getWriter().print(
						"Wallet " + sender + " Do not have enough balance (require " + amount + " LabCoin UTXO)");
			} else {
				resp.getWriter().print("New Transaction:" + JSON.toJSONString(newTransaction));
				Transaction[] txs = {newTransaction};
				String msg = JSON.toJSONString(new Message(MessageType.RESPONSE_TRANSACTION.value, JSON
						.toJSONString(txs)));
				p2PServiceInterface.broadcast(msg);
			}
		}
	}

	/**
	 * create new transaction method
	 */
	private class NewTransactionServlet extends HttpServlet {
		/**
		 * POST method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			TransactionParam txParam = JSON.parseObject(getReqBody(req), TransactionParam.class);
			resp.getWriter().print("txParam is:" + JSON.toJSONString(txParam));
			int amount = txParam.getAmount();

			Wallet senderWallet = blockChainOpr.getMyWalletMap().get(txParam.getSender());
			// wallet can be mine or others
			resp.getWriter().print("my Wallet");
			Wallet recipientWallet = blockChainOpr.getMyWalletMap().get(txParam.getRecipient());
			if (recipientWallet == null) {
				resp.getWriter().print("others' Wallet");
				recipientWallet = blockChainOpr.getOtherWalletMap().get(txParam.getRecipient());
			}

			resp.getWriter().print("Sender Wallet is:" + JSON.toJSONString(senderWallet) + " Receiver Wallet is:" + JSON.toJSONString(recipientWallet) + " amount is:" + amount);
			if (senderWallet == null || recipientWallet == null) {
				resp.getWriter().print("Wallet Not Exist");
				return;
			}

			Transaction newTransaction = blockChainOpr.createTransaction(senderWallet, recipientWallet, amount);
			if (newTransaction == null) {
				resp.getWriter().print(
						"Wallet " + txParam.getSender() + " Do not have enough balance (require " + txParam.getAmount() + " LabCoin UTXO)");
			} else {
				resp.getWriter().print("New Transaction:" + JSON.toJSONString(newTransaction));
				Transaction[] txs = {newTransaction};
				String msg = JSON.toJSONString(new Message(MessageType.RESPONSE_TRANSACTION.value, JSON
						.toJSONString(txs)));
				p2PServiceInterface.broadcast(msg);
			}
		}
	}

	/**
	 * query for balance method http service
	 */
	private class GetWalletBalanceServlet extends HttpServlet {
		/**
		 * GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			String address = req.getParameter("address");
			resp.getWriter().print("wallet address is:" + address + "\n");
			resp.getWriter().print("Balance in Wallet is:" + blockChainOpr.getWalletBalance(address) + " LabCoin");
		}
	}

	/**
	 * Get Unpacked Transaction
	 */
	private class GetUnpackedTransactionServlet extends HttpServlet {
		/**
		 * should be GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			List<Transaction> transactions = new ArrayList<>(blockChainOpr.getAllTransactions());
			transactions.removeAll(blockChainOpr.getPackedTransactions());
			resp.getWriter().print("UTXOs in the Node???" + JSON.toJSONString(transactions));
		}
	}

	/**
	 * Get Packed Transaction
	 */
	private class GetPackedTransactionServlet extends HttpServlet {
		/**
		 * should be GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			List<Transaction> packed = new ArrayList<>(blockChainOpr.getPackedTransactions());
			resp.getWriter().print("packet transaction in the Node???" + JSON.toJSONString(packed));
		}
	}

	/**
	 * Get All Transaction
	 */
	private class GetAllTransactionServlet extends HttpServlet{
		/**
		 * should be GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			List<Transaction> transactions = new ArrayList<>(blockChainOpr.getAllTransactions());
			resp.getWriter().print("all Transactions in the Node???" + JSON.toJSONString(transactions));
		}
	}

	/**
	 * get peers http service
	 */
	private class PeersServlet extends HttpServlet {
		/**
		 * GET method
		 * @param req Http Request
		 * @param resp Http Response
		 * @throws ServletException
		 * @throws IOException
		 */
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setCharacterEncoding("UTF-8");
			for (WebSocket socket : p2PServiceInterface.getSockets()) {
				InetSocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
				resp.getWriter().print(remoteSocketAddress.getHostName() + ":" + remoteSocketAddress.getPort() + "\n");
			}
		}
	}

	/**
	 * Reads a line of text from request
	 * https://www.tutorialspoint.com/java/io/bufferedreader_readline.htm
	 * @param req
	 * @return
	 * @throws IOException
	 */
	private String getReqBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		return body;
	}
}
