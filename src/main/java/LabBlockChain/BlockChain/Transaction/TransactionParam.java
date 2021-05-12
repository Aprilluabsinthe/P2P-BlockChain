package LabBlockChain.BlockChain.Transaction;

/**
 * for parse json strings from http request
 * if use JSON: JSON.parseArray(message,TransactionParam.class)
 * if use GSON: Gson.fromJson((message,TransactionParam.class)
 */
public class TransactionParam {
	private String sender;
	private String recipient;
	private int amount;

	/**
	 * Getter for Sender
	 * @return String, sender's wallet address
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * setter for sender
	 * @param sender sender's wallet address
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * Getter for Recipient
	 * @return String, Recipient's wallet address
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Setter for Recipient
	 * @param recipient String, Recipient's wallet address
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Getter for Amount
	 * @return String, the amount transfered
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Setter for Amount
	 * @param amount the amount to transfer
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
