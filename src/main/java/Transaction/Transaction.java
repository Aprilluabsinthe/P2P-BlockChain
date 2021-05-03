package Transaction;
import Helper.Helper;
import LabCoin.LabCoin;
import basic.trial;
import com.google.gson.Gson;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;
	
	public List<InputTransaction> inputs = new ArrayList<InputTransaction>();
	public List<OutputTransaction> outputs = new ArrayList<OutputTransaction>();
	
	private static int sequence = 0; //A rough count of how many transactions have been generated 
	
	// Constructor: 
	public Transaction(PublicKey sender, PublicKey reciepient, float value, List<InputTransaction> inputs) {
		this.sender = sender;
		this.reciepient = reciepient;
		this.value = value;
		this.inputs = inputs;
	}
	
	public boolean forwardTransaction() {
		if(verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// merge all input transaction
		for(InputTransaction in : inputs) {
			in.UTXO = LabCoin.UTXOMap.get(in.outputId);
		}

		// operation bigger than coin min value
		float inputSum = InputValueSum();
		if(inputSum < LabCoin.MIN_TRANSACTION) {
			return false;
		}
		
		//Generate transaction outputs, update receiver asset and sender asset
		transactionId = identicalHash();
		outputs.add(new OutputTransaction( this.reciepient, this.value,this.transactionId)); //send value to recipient
		outputs.add(new OutputTransaction( this.sender, inputSum - this.value, this.transactionId)); //send the left over 'change' back to sender
				
		// Map outputs to Unspent record
		for(OutputTransaction out : outputs) {
			LabCoin.UTXOMap.put(out.outputId , out);
		}
		
		// Remove transaction inputs from unspent map
		for(InputTransaction in : inputs) {
			if(in.UTXO == null) continue; //if Transaction can't be found skip it
			LabCoin.UTXOMap.remove(in.UTXO.outputId);
		}
		return true;
	}

	public String contentToBeSigned(){
		return Helper.keyToString(sender) + Helper.keyToString(reciepient) + value;
	}

	// ******************************************************
	// signature generation and verification
	// ******************************************************
	public void yieldSignature(PrivateKey privateKey) {
		String contentToSign = contentToBeSigned();
		signature = Helper.generateSignature(privateKey,contentToSign);
	}
	
	public boolean verifySignature() {
		String contentToVerify = contentToBeSigned();
		return Helper.verifySignature(sender,contentToVerify,signature);
	}

	// ******************************************************
	// Inflow and outflow value Sum
	// ******************************************************
	public float InputValueSum() {
		float insum = 0;
		for(InputTransaction in : inputs) {
			if(in.UTXO == null){
				System.out.println("Can not find this input transaction");
			} //if Transaction can't be found skip it, This behavior may not be optimal.
			insum += in.UTXO.value;
		}
		return insum;
	}

	
	private String identicalHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		String newContent = contentToBeSigned() + sequence;
		return Helper.getHexSha256(newContent);
	}

	@Override
	public String toString() {
		return toJson();
	}

	public class tranData{
		public byte[] tranSender;
		public byte[] tranReciepient;
		public float tranValue;
		public List<String> tranInputs = new ArrayList<String>();

		public tranData() {
			this.tranSender = sender.getEncoded();
			this.tranReciepient = reciepient.getEncoded();
			this.tranValue = value;
			for(InputTransaction input : inputs){
				this.tranInputs.add(input.toString());
			}
		}
	}

	public String toJson() {
		tranData copy = new tranData();
		Gson gson = new Gson();
		String json = gson.toJson(copy);
		return json;
	}

	public static Transaction fromString(String json){
		Gson gson = new Gson();
		tranData obj = gson.fromJson(json, tranData.class);
		PublicKey sender = null;
		PublicKey reciepient = null;
		try {
			sender = Helper.genEcPubKey(obj.tranSender);
			reciepient = Helper.genEcPubKey(obj.tranReciepient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		float value = obj.tranValue;
		List<String> inputs = obj.tranInputs;

		List<InputTransaction> inputTXs = new ArrayList<>();
		for(String input : inputs){
			InputTransaction inTX = InputTransaction.fromString(input);
			inputTXs.add(inTX);
		}

		return new Transaction(sender,reciepient,value,inputTXs);
	}
}
