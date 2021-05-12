package LabBlockChain.BlockChain.Transaction;


import com.alibaba.fastjson.JSON;
import LabBlockChain.BlockChain.Helper.Coder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Transactions that occur on a blockchain must be validated by a number of the network's participants,
 * A transaction is only valid once the participants verify the transaction and a consensus is
 * reached about its validity.
 * The transaction details are then recorded on the block and distributed to the network's participants.
 *
 * tx-
 * Unique ID
 * input  - > output
 *
 * for simplicity, here only one-to-one relationship
 */
public class Transaction {
	/**
	 * the globally globally unique ID, AKA UUID
	 */
	private String id;
	/**
	 * transaction input, indicate the input source of this transaction
	 */
	private TransactionInput txIn;
	/**
	 * transaction output, indicate the UTXO of this transaction
	 */
	private TransactionOutput txOut;

	// for futher improve the transactions
	public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public Transaction() {
		super();
	}

	/**
	 * contructor for Transaction
	 * @param id globally unique ID
	 * @param txIn transaction input
	 * @param txOut transaction output
	 */
	public Transaction(String id, TransactionInput txIn, TransactionOutput txOut) {
		super();
		this.id = id;
		this.txIn = txIn;
		this.txOut = txOut;
	}

	/**
	 * Getter for ID
	 * @return String UUID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter for ID
	 * this setter will not be called, but is required for json parse
	 * @param id the id string
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * getter for  transaction input
	 * @return TransactionInput
	 */
	public TransactionInput getTxIn() {
		return txIn;
	}

	/**
	 * Setter for TransactionInput
	 * this setter will not be called, but is required for json parse
	 * @param txIn TransactionInput
	 */
	public void setTxIn(TransactionInput txIn) {
		this.txIn = txIn;
	}

	/**
	 * Getter for TransactionOutput
	 * @return TransactionOutput
	 */
	public TransactionOutput getTxOut() {
		return txOut;
	}

	/**
	 * Setter for TransactionOutput
	 * this setter will not be called, but is required for json parse
	 * @param txOut TransactionOutput
	 */
	public void setTxOut(TransactionOutput txOut) {
		this.txOut = txOut;
	}

	/**
	 * is the transaction the base generated one
	 * @return true if is base
	 */
	public boolean isBaseTx() {
		return txIn.getTxId().equals("0") && getTxIn().getValue() == -1;
	}


	/**
	 * sign the transaction with MD5 algorithm
	 * @param privateKey privateKey
	 * @param prevTx the previous transaction for integrity
	 */
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
			sign = Coder.MD5RSAsign(txClone.calculateHash().getBytes(), privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		txIn.setSignature(sign);
	}

	/**
	 * clone the transaction, helper function to keep original Transaction itegrity
	 * @return Transaction
	 */
	public Transaction cloneTx() {
		TransactionInput transactionInput = new TransactionInput(txIn.getTxId(), txIn.getValue(), null, null);
		TransactionOutput transactionOutput = new TransactionOutput(txOut.getValue(), txOut.getPublicKeyHash());
		return new Transaction(id, transactionInput, transactionOutput);
	}

	/**
	 * verify signature
	 * @param prevTx the previous transaction for verification
	 * @return true if signatue is valid
	 */
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
			result = Coder.MD5RSAverify(txClone.calculateHash().getBytes(), txIn.getPublicKey(), txIn.getSignature());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * helper function
	 * @return
	 */
	public String calculateHash() {
		return Coder.applySha256(JSON.toJSONString(this));
	}


	/**
	 * override string function
	 * @return String
	 */
	@Override
	public String toString() {
		return toJson();
	}

	/**
	 * override equals function
	 * @param o object to compare to
	 * @return true if is equal, false if not
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Transaction)) return false;
		Transaction that = (Transaction) o;
		return id.equals(that.id) && txIn.equals(that.txIn) && txOut.equals(that.txOut) && inputs.equals(that.inputs) && outputs.equals(that.outputs);
	}

	/**
	 * override hash code function
	 * @return
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, txIn, txOut, inputs, outputs);
	}

	/**
	 * inner class for json parse
	 */
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

	/**
	 * gson parse
	 * @return json string
	 */
	public String toJson() {
		TransactionData copy = new TransactionData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	/**
	 * reverse parse from string
	 * @param json
	 * @return
	 */
	public static Transaction fromString(String json){
		Gson gson = new Gson();
		TransactionData obj = gson.fromJson(json, TransactionData.class);
		String txId = obj.tid;
		TransactionInput txIn = TransactionInput.fromString(obj.ttxIn);
		TransactionOutput txOut = TransactionOutput.fromString(obj.ttxOut);
		return new Transaction(txId,txIn,txOut);
	}
}
