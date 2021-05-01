package labCoin;
import Helper.*;
import Transaction.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Block {
    private int index;
    private String timestamp;

    private String hash;
    private String preHash;
    private int difficulty;
    private int nonce;
    private String merkleRoot;

    private transient List<Transaction> transactionList = new ArrayList<>();

    private static final Logger LOGGER = Logger.getLogger(Block.class.getName());

    public Block(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block() { }

    /***
     * https://mkyong.com/java/how-to-get-current-timestamps-in-java/#java-timestamp-examples
     * @param preBlock
     * @param difficulty
     * @return
     */
    public static Block buildCurrentBlock(Block preBlock, int difficulty){
        Block curBlock = new Block(difficulty);
        curBlock.setIndex(preBlock.getIndex() + 1);
        SimpleDateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        curBlock.setTimestamp(stringFormat.format(new Date()));
        curBlock.setPreHash(preBlock.getHash());
        curBlock.setHash(curBlock.blockMining());
        return curBlock;
    }



    public static String areaToHash(Block block){
        return block.getIndex() + block.getTimestamp() + block.getNonce() + block.getPreHash();
    }

    /***
     * https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/binary/StringUtils.html
     * @param block
     * @return
     */
    public static String calculateNewHash(Block block){
        String hashRecord = areaToHash(block);
        return Helper.getHexSha256(hashRecord);
    }

    public String blockMining(){
        this.merkleRoot = Helper.getMerkleroot(this.transactionList);
        String targetHash = Helper.getDificultyTarget(difficulty);
        this.hash = calculateNewHash(this);
        while(!this.hash.substring(0,difficulty).equals(targetHash)){
            this.nonce++;
            this.hash = calculateNewHash(this);
        }
        return this.hash;
    }
    //************************************************************************
    // Block Validation
    //************************************************************************


    public static boolean isValidBlock(Block newBlock, Block oldBlock){
        return (oldBlock.getIndex() + 1 == newBlock.getIndex())
                && (oldBlock.getHash().equals(newBlock.getPreHash()))
                && (calculateNewHash(newBlock).equals(newBlock.getHash()));
    }



    //************************************************************************
    // Transaction operation
    //************************************************************************


    public boolean appendTransaction(Transaction tx){
        if(tx==null){
            return false;
        }

        if(this.preHash.equals("0")){
            LOGGER.info("the first block");
            this.transactionList.add(tx);
            return true;
        }
        else{
            boolean addSuccess = tx.forwardTransaction();
            if(!addSuccess){
                System.out.println("appendTransaction error");
                LOGGER.info("appendTransaction error");
                return false;
            }
            this.transactionList.add(tx);
            return false;
        }
    }



    //************************************************************************
    // Getter and Setter
    //************************************************************************

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNouce(int nonce) {
        this.nonce = nonce;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public List<Transaction> getTransactionList() {
        return (List<Transaction>) transactionList;
    }
}
