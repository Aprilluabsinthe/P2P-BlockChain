package LabBlockChain.BlockChain.basic;
import LabBlockChain.BlockChain.Transaction.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class Block {
    private final static int DIFFICULTY = 3;

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

    public String contentToHash(Block block){
        return block.index + block.timestamp + JSON.toJSONString(block.transactions) + block.previousHash + block.nonce;
    }

    public String calculateHash(Block block) {
        String record = contentToHash(block);
        MessageDigest digest = DigestUtils.getSha256Digest();
        byte[] hash = digest.digest(StringUtils.getBytesUtf8(record));
        return Hex.encodeHexString(hash);
    }
}
