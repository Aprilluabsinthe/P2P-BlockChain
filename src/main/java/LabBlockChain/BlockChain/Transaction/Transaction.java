package LabBlockChain.BlockChain.Transaction;


import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Helper.Coder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * tx0-
 * input output
 *
 * tx1-                  tx3
 * input output          input output
 *
 * tx2-
 * input output
 */
public class Transaction {
	private String id;
	private TransactionInput txIn;
	private TransactionOutput txOut;

	public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public Transaction() {
		super();
	}

	public Transaction(String id, TransactionInput txIn, TransactionOutput txOut) {
		super();
		this.id = id;
		this.txIn = txIn;
		this.txOut = txOut;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TransactionInput getTxIn() {
		return txIn;
	}

	public void setTxIn(TransactionInput txIn) {
		this.txIn = txIn;
	}

	public TransactionOutput getTxOut() {
		return txOut;
	}

	public void setTxOut(TransactionOutput txOut) {
		this.txOut = txOut;
	}

	public boolean isBaseTx() {
		return txIn.getTxId().equals("0") && getTxIn().getValue() == -1;
	}


	public void sign(String privateKey, Transaction prevTx) {
		if (isBaseTx()) {
			return;
		}

		if (!prevTx.getId().equals(txIn.getTxId())) {
			System.err.println("Transaction Failed, previous Sign not match");
		}

		Transaction txClone = cloneTx();
		txClone.getTxIn().setPublicKey(prevTx.getTxOut().getPublicKeyHash());
		String sign = "";
		try {
			sign = Coder.MD5RSAsign(txClone.hash().getBytes(), privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		txIn.setSignature(sign);
	}

	public Transaction cloneTx() {
		TransactionInput transactionInput = new TransactionInput(txIn.getTxId(), txIn.getValue(), null, null);
		TransactionOutput transactionOutput = new TransactionOutput(txOut.getValue(), txOut.getPublicKeyHash());
		return new Transaction(id, transactionInput, transactionOutput);
	}

	public boolean verify(Transaction prevTx) {
		if (isBaseTx()) {
			return true;
		}

		if (!prevTx.getId().equals(txIn.getTxId())) {
			System.err.println("Transaction verification Failed, previous Sign not match");
		}

		Transaction txClone = cloneTx();
		txClone.getTxIn().setPublicKey(prevTx.getTxOut().getPublicKeyHash());

		boolean result = false;
		try {
			result = Coder.verify(txClone.hash().getBytes(), txIn.getPublicKey(), txIn.getSignature());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String hash() {
		return Coder.applySha256(JSON.toJSONString(this));
	}
//	public String hash() {
//		return CoderHelper.applySha256(new Gson().toJson(this));
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toJson();
	}

	private class TransactionData{
		private String tid;
		private String ttxIn;
		private String ttxOut;

		public TransactionData() {
			this.tid = id;
			this.ttxIn = txIn.toString();
			this.ttxOut = txOut.toString();
		}
	}

	public String toJson() {
		TransactionData copy = new TransactionData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	public static Transaction fromString(String json){
		Gson gson = new Gson();
		TransactionData obj = gson.fromJson(json, TransactionData.class);
		String txId = obj.tid;
		TransactionInput txIn = TransactionInput.fromString(obj.ttxIn);
		TransactionOutput txOut = TransactionOutput.fromString(obj.ttxOut);
		return new Transaction(txId,txIn,txOut);
	}
}
