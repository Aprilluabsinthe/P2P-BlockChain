package LabBlockChain.BlockChain.p2p;

import java.io.Serializable;

/**
 * The simplified message class for communication
 */
public class Message implements Serializable {
	private int type;
	private String data;
	public Message() {
	}

	/**
	 * The construction for Message
	 * @param type
	 */
	public Message(int type) {
		this.type = type;
	}

	/**
	 * The construction for Message
	 * @param type
	 * @param data
	 */
	public Message(int type, String data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Getter for type
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setter for tyoe
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Getter for dada
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * setter for data
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}
}
