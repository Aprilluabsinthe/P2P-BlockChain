package LabBlockChain.BlockChain.basic;
import LabBlockChain.BlockChain.Transaction.*;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 *     //******************************************************
 *     // ***************______________________*****************
 *     // ***************|        index       |*****************
 *     // ***************|        hash        |*****************
 *     // ***************|    previousHash    |*****************
 *     // ***************|       timestamp    |*****************
 *     // ***************|        nonce       |*****************
 *     // ***************|____________________|*****************
 *     // ***************|____transactions____|*****************
 *     // ***************|   Transaction1->   |*****************
 *     // ***************|    ->Transaction2->|*****************
 *     // ***************|    ->Transaction3  |*****************
 *     // ***************|____________________|*****************
 */
public class Block {
    private final static int DIFFICULTY = 3;
    private int index;
    private String hash;
    private String previousHash;
    private long timestamp;
    private List<Transaction> transactions;
    private int nonce;

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

    public static int getDIFFICULTY() {
        return DIFFICULTY;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    /**
     * Getter for index
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for timestamp
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for transactions
     * @return transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Getter for nonce
     * @return nonce
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Getter for previousHash
     * @return previousHash
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Getter for hash
     * @return hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Generete target string of PoW
     * @return String of PoW
     */
    public static String provOfWorkZeros() {
        char[] c = new char[DIFFICULTY];
        Arrays.fill(c, '0');
        String result = new String(c);
        return result;
    }

    /**
     * the String to calculate Hash
     * @param block the block to be calculated
     * @return
     */
    public String contentToHash(Block block){
        Gson json = new Gson();
        return block.index + block.timestamp + JSON.toJSONString(block.transactions) + block.previousHash + block.nonce;
    }

    /**
     * SHA256 Hash for block
     * @param block block to calculate hash
     * @return String of Hash
     */
    public String calculateHash(Block block) {
        String record = contentToHash(block);
        MessageDigest digest = DigestUtils.getSha256Digest();
        byte[] hash = digest.digest(StringUtils.getBytesUtf8(record));
        return Hex.encodeHexString(hash);
    }
}
