package LabBlockChain.BlockChain.Transaction;

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
}
