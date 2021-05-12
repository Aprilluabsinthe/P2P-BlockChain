package LabBlockChain.BlockChain.Transaction;

import LabBlockChain.BlockChain.Helper.Coder;
import LabBlockChain.BlockChain.basic.Wallet;
import com.google.gson.Gson;

/**
 * A TransactionOutput contains value and a scriptPubKey that controls who is able to spend its value.
 */
public class TransactionOutput {
	private int value;
	private String publicKeyHash;

	/**
	 * default constructor for TransactionOutput
	 */
	public TransactionOutput() {
		super();
	}

	/**
	 * Getter for value
	 * @return value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Setter fro Value
	 * @param value the virtual asset
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * getter for the scriptPubKey
	 * @return String
	 */
	public String getPublicKeyHash() {
		return publicKeyHash;
	}

	/**
	 * Setter for public key hash
	 * this setter will not be called, but is required for json parse
	 * @param publicKeyHash
	 */
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
