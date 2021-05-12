package LabBlockChain.BlockChain.Transaction;

import com.google.gson.Gson;

/***
 * A transfer of coins from one address to another creates a transaction
 * in which the outputs can be claimed by the recipient in the input of another transaction.
 * Imagine a transaction as being a module which is wired up to others,
 * the inputs of one have to be wired to the outputs of another.
 * The exceptions are coinbase transactions, which create new coins.
 */
public class TransactionInput {
	/**
	 * the previous transaction's ID
	 */
	private String txId;
	/**
	 * the value of the input
	 */
	private int value;
	/**
	 * the signature of the sender
	 */
	private String signature;
	/**
	 * the public key of the receiver
	 */
	private String publicKey;


	/**
	 * super constructor
	 */
	public TransactionInput() {
		super();
	}

	/**
	 * constructor
	 * @param txId the previous transaction's ID
	 * @param value the value of the input
	 * @param signature the signature of the sender
	 * @param publicKey the public key of the receiver
	 */
	public TransactionInput(String txId, int value, String signature, String publicKey) {
		super();
		this.txId = txId;
		this.value = value;
		this.signature = signature;
		this.publicKey = publicKey;
	}

	/**
	 * Getter for previous transaction ID
	 * @return String ID
	 */
	public String getTxId() {
		return txId;
	}

	/**
	 * Setter for previous transaction ID
	 * this setter will not be called, but is required for json parse
	 * @param txId
	 */
	public void setTxId(String txId) {
		this.txId = txId;
	}

	/**
	 * Getter for value
	 * @return the value of the inout
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Setter for the value
	 * this setter will not be called, but is required for json parse
	 * @param value the value asset
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Getter for signature
	 * @return the signature of the inout
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Setter for Signature
	 * @param signature the Signature to be set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Getter for publicKey
	 * @return the publicKey of the inout
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * Setter for publicKey
	 * @param publicKey the publicKey to be set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * override to string
	 * @return
	 */
	@Override
	public String toString() {
		return toJson();
	}

	/**
	 * inner data type for json parse
	 */
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

	/**
	 * parse to json string
	 * @return json String
	 */
	public String toJson() {
		inData copy = new inData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	/**
	 * parse reversely from json sting to TransactionInput
	 * @param json the json to parse
	 * @return
	 */
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
