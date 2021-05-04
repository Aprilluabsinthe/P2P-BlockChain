package LabBlockChain.BlockChain.Transaction;

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
}
