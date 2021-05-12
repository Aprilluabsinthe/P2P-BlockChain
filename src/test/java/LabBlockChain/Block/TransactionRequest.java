package LabBlockChain.Block;

import LabBlockChain.BlockChain.basic.Wallet;

public class TransactionRequest {
    public String sender;
    public String recipient;
    public int amount;

    public TransactionRequest(String sender, String recipient, int amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "sender address: " + this.sender + ", " + "receiver address: " + this.recipient + ", " + "amount: " + this.amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TransactionRequest)) return false;
        TransactionRequest tr = (TransactionRequest) obj;
        return this.sender.equals(tr.sender) && this.recipient == tr.recipient && this.amount == tr.amount;
    }
}