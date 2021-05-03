package basic;
import Helper.*;
import Transaction.*;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Block {
    private int index;
    private String timestamp;
    private float asset;
    private String hash;
    private String preHash;
    private static int difficulty;
    private int nonce;
    private String merkleRoot;
    private transient List<Transaction> transactionList = new ArrayList<>();


    private static final Logger LOGGER = Logger.getLogger(Block.class.getName());
    public Block(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block() { }

    public Block(int index, String timestamp, float asset,String hash, String preHash, int difficulty, int nonce, String merkleRoot, List<Transaction> transactionList) {
        this.index = index;
        this.timestamp = timestamp;
        this.asset = asset;
        this.hash = hash;
        this.preHash = preHash;

        this.difficulty = difficulty;
        this.nonce = nonce;
        this.merkleRoot = merkleRoot;
        this.transactionList = transactionList;
    }

    /***
     * https://mkyong.com/java/how-to-get-current-timestamps-in-java/#java-timestamp-examples
     * @param preBlock
     * @param difficulty
     * @return
     */
    public static Block buildCurrentBlock(Block preBlock, float asset, int difficulty){
        Block curBlock = new Block(difficulty);
        curBlock.setIndex(preBlock.getIndex() + 1);
        SimpleDateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        curBlock.setAsset(asset);
        curBlock.setTimestamp(stringFormat.format(new Date()));
        curBlock.setPreHash(preBlock.getHash());
        curBlock.setHash(curBlock.blockMining());
        return curBlock;
    }



    public static String areaToHash(Block block){
        return block.getIndex() + block.getTimestamp() + block.getAsset() + block.getPreHash();
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


    public static String targetString(){
        return Helper.getDificultyTarget(difficulty);
    }

    public static boolean isValidBlock(Block newBlock, Block oldBlock){
        return (oldBlock.getIndex() + 1 == newBlock.getIndex())
                && (oldBlock.getHash().equals(newBlock.getPreHash()))
                && (calculateNewHash(newBlock).equals(newBlock.getHash()))
                && (newBlock.getHash().startsWith(targetString()));
    }



    //************************************************************************
    // Transaction operation
    //************************************************************************


    public boolean appendTransaction(Transaction tx){
        if(tx==null){
            return false;
        }

        if(this.preHash.equals("0")){
            LOGGER.info("block Discarded");
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

    public float getAsset() {
        return asset;
    }

    public void setAsset(float asset) {
        this.asset = asset;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }



    @Override
    public String toString() {
        return toJson();
    }

    public class blockData{
        private int blockIndex;
        private String blockTimestamp;
        private float blockAsset;
        private String blockHash;
        private String blockPreHash;
        private int blockDifficulty;
        private int blockNonce;
        private String blockMerkleRoot;
        private List<String> blockTransactionList = new ArrayList<>();


        public blockData() {
            this.blockIndex = index;
            this.blockTimestamp = timestamp;
            this.blockAsset = asset;
            this.blockHash = hash;
            this.blockPreHash = preHash;
            this.blockDifficulty = difficulty;
            this.blockNonce = nonce;
            this.blockMerkleRoot = merkleRoot;
            for(Transaction tx : transactionList){
                this.blockTransactionList.add(tx.toString());
            }
        }
    }

    public String toJson(){
        blockData copy = new blockData();
        Gson gson = new Gson();
        String json = gson.toJson(copy);
        return json;
    }

    public static Block fromJson(String json){
        Gson gson = new Gson();
        blockData obj = gson.fromJson(json, blockData.class);
        int index = obj.blockIndex;
        String timestamp = obj.blockTimestamp;
        float asset = obj.blockAsset;
        String hash = obj.blockHash;
        String preHash = obj.blockPreHash;
        int difficulty = obj.blockDifficulty;
        int nonce = obj.blockNonce;
        String merkleRoot = obj.blockMerkleRoot;

        List<String> transactionStrList = obj.blockTransactionList;
        List<Transaction> transactionList = new ArrayList<Transaction>();

        for(String txString : transactionStrList){
            Transaction transaction = Transaction.fromString(txString);
            transactionList.add(transaction);
        }
        return new Block(index,timestamp,asset,hash,preHash,difficulty,nonce,merkleRoot,transactionList);
    }

}
