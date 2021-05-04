package LabBlockChain.BlockChain.Transaction;

import com.google.gson.Gson;

public class TransactionInput {

	private String txId;
	private int value;
	private String signature;
	private String publicKey;

	public TransactionInput() {
		super();
	}

	public TransactionInput(String txId, int value, String signature, String publicKey) {
		super();
		this.txId = txId;
		this.value = value;
		this.signature = signature;
		this.publicKey = publicKey;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return toJson();
	}

	private class inData{
		private String intxId;
		private int invalue;
		private String insignature;
		private String inpublicKey;

		public inData() {
			this.intxId = txId;
			this.invalue = value;
			this.insignature = signature;
			this.inpublicKey = publicKey;
		}
	}

	public String toJson() {
		inData copy = new inData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	public static TransactionInput fromString(String json){
		Gson gson = new Gson();
		inData obj = gson.fromJson(json,inData.class);
		String txId = obj.intxId;
		int value = obj.invalue;
		String signature = obj.insignature;
		String publicKey = obj.inpublicKey;
		return new TransactionInput(txId,value,signature,publicKey);
	}

}
