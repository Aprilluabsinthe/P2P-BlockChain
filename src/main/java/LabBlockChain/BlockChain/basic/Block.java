package LabBlockChain.BlockChain.basic;
import LabBlockChain.BlockChain.Transaction.*;

import java.util.Arrays;
import java.util.List;

public class Block {
    private final static int DIFFICULTY = 5;

    //******************************************************
    // ***************______________________*****************
    // ***************|        index       |*****************
    // ***************|        hash        |*****************
    // ***************|    previousHash    |*****************
    // ***************|       timestamp    |*****************
    // ***************|        nonce       |*****************
    // ***************|____________________|*****************
    // ***************|____transactions____|*****************
    // ***************|   Transaction1->   |*****************
    // ***************|    ->Transaction2->|*****************
    // ***************|    ->Transaction3  |*****************
    // ***************|____________________|*****************

    //******************************************************
    private int index;
    private String hash;
    private String previousHash;
    private long timestamp;
    private List<Transaction> transactions;
    private int nonce;

    public Block() {
        super();
    }

    public Block(int index, long timestamp, List<Transaction> transactions, int nonce, String previousHash, String hash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    //******************************************************
    // ***************  Getter and Setter  *****************
    //******************************************************
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static String provOfWorkZeros() {
        char[] c = new char[DIFFICULTY];
        Arrays.fill(c, '0');
        String result = new String(c);
        return result;
    }
}
