package LabBlockChain.BlockChain.Transaction;

import LabBlockChain.BlockChain.Helper.Coder;
import LabBlockChain.BlockChain.basic.Wallet;
import com.google.gson.Gson;

public class TransactionOutput {

	private int value;
	private String publicKeyHash;

	public TransactionOutput() {
		super();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getPublicKeyHash() {
		return publicKeyHash;
	}

	public void setPublicKeyHash(String publicKeyHash) {
		this.publicKeyHash = publicKeyHash;
	}

	public TransactionOutput(int value, String publicKeyHash) {
		this.value = value;
		this.publicKeyHash = publicKeyHash;
	}

	public boolean belongsToUser(String publicKey){
		String hashPubKey = Coder.applySha256(publicKey);
		return hashPubKey == publicKeyHash;
	}

	public boolean belongsToWallet(Wallet wallet){
		String hashPubKey = wallet.getHashPubKey();
		return hashPubKey == publicKeyHash;
	}

	@Override
	public String toString() {
		return toJson();
	}

	private class outData{
		private int outvalue;
		private String outpublicKeyHash;

		public outData() {
			this.outvalue = value;
			this.outpublicKeyHash = publicKeyHash;
		}
	}

	public String toJson() {
		outData copy = new TransactionOutput.outData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	public static TransactionOutput fromString(String json){
		Gson gson = new Gson();
		outData obj = gson.fromJson(json, outData.class);
		int value = obj.outvalue;
		String publicKeyHash = obj.outpublicKeyHash;
		return new TransactionOutput(value, publicKeyHash);
	}
}
