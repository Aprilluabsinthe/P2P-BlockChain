package BlockChain;

import Helper.Helper;
import basic.Block;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class blockchainImplementation implements blockchainInterface{
    private int NodeId;
    private Node Node;
    private int difficulty;
    private Queue<Block> mindedQueue;
    private List<Block> blockChains;
    private static final Logger LOGGER = Logger.getLogger(blockchainImplementation.class.getName());


    public blockchainImplementation(int nodeId, BlockChain.Node node, int difficulty) {
        this.NodeId = nodeId;
        this.Node = node;
        this.difficulty = difficulty;
        this.mindedQueue = new LinkedList<>();
        this.blockChains = new ArrayList<>();
        this.blockChains.add(GenesisBlock());
    }

    @Override
    public boolean addBlock(Block block) {
        return false;
    }

    @Override
    public Block GenesisBlock() {
        String genesisHash = "GENESIS_BLOCK";
        String genesisPreHash = "GENESIS_PREVIOUS_HASH";
        String data = "GENESIS_RANDOM_DATA";
        long time_stamp = System.currentTimeMillis();

        Block genesisBlock = new Block();
        genesisBlock.setIndex(0);
        genesisBlock.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        genesisBlock.setAsset(0);
        genesisBlock.setPreHash("GENESIS_PREVIOUS_HASH");
        genesisBlock.setDifficulty(this.difficulty);
        String hash = Block.calculateNewHash(genesisBlock);
        genesisBlock.setHash(hash);
        LOGGER.info(genesisBlock.toJson());
        return genesisBlock;
    }



    @Override
    public boolean broadcastNewBlock() {
        int peer_num = this.Node.getPeerNumber();
        if (mindedQueue.peek() == null) {
            System.err.println("No Valid Mined Block");
        }

        Block newBlock = this.mindedQueue.poll();
        LOGGER.info("Node" + this.NodeId + " BroadCast Mined Block: " + newBlock.getIndex());

        for (int i = 0; i < peer_num; i++) {
            if (this.Node.broadcastToPeer(i, Helper.blockToBytes(newBlock))) {
                LOGGER.info("Node" + i + " Accept Mined Block [" + newBlock.getIndex() + "] From Node" + this.NodeId);
            } else {
                LOGGER.info("Node" + i + " Rejected Mined Block [" + newBlock.getIndex() + "] From Node" + this.NodeId);
                return false;
            }
        }
        return true;
    }

    @Override
    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    @Override
    public byte[] getBlockchainData() {
        return Helper.blockchainToBytes(blockChains);
    }

    @Override
    public void ConsensusBlockchain() {
        /* Calling 'getBlockChainDataFromPeer' method by using the Node Instance */
        List<Block> potentialChains = new ArrayList<>();
        int num_peers = Node.getPeerNumber();

        for(int i=0; i<num_peers; i++){
            if(i != this.NodeId){
                try {
                    byte[] downloaded = this.Node.getPeerBlockChain(i);
                    List<Block> peerChain = Helper.bytesToBlockchain(downloaded);
                    if(peerChain.size() < potentialChains.size()){
                        continue;
                    }
                    else if(peerChain.size() > potentialChains.size()){
                        System.out.println("Node"+this.NodeId+" Switch to apply Chains From Node"+i);
                        potentialChains = peerChain;
                    }
                    else{
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date peer_timestamp = format.parse(peerChain.get(peerChain.size()-1).getTimestamp());
                        Date ptntl_timestamp = format.parse(potentialChains.get(getBlockChainLength()-1).getTimestamp());

                        if(peer_timestamp.compareTo(ptntl_timestamp) <= 0){ // eailier
                            System.out.println("Node"+this.NodeId+" Switch to apply Chains From Node"+i);
                            potentialChains = peerChain;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(potentialChains.size() == 0){
            System.err.println("Error: Node"+this.NodeId+" Failed to Download BloackChain From Peers");
            System.exit(-1);
        }

        this.blockChains = potentialChains;
    }

    @Override
    public Block createNewBlock(byte[] data) {
        Block preBlock = getLastBlock();
        String sdata = new String(data);
        float asset = Float.parseFloat(sdata);
        Block newBlock = Block.buildCurrentBlock(preBlock,asset,difficulty);
        this.mindedQueue.offer(newBlock);
        return newBlock;
    }

    @Override
    public void setNode(Node node) {
        this.Node = node;
    }

    @Override
    public boolean isValidNewBlock(Block newBlock, Block prevBlock) {
        return Block.isValidBlock(newBlock,prevBlock);
    }

    @Override
    public Block getLastBlock() {
        Block lastBlock = this.blockChains.get(this.blockChains.size()-1);
        if(lastBlock == null){
            System.out.println("Node"+this.NodeId+" contains Empty Block Chains");
            LOGGER.finest("Node"+this.NodeId+" contains Empty Block Chains");

        }
        return lastBlock;
    }

    @Override
    public int getBlockChainLength() {
        if(blockChains==null){
            System.err.println("Node"+this.NodeId+" contains Empty Block Chains");
            LOGGER.finest("Node"+this.NodeId+" contains Empty Block Chains");
        }
        return this.blockChains.size();
    }

    @Override
    public List<Block> replaceChain(List<Block> oldBlocks, List<Block> newBlocks) {
        return (newBlocks.size() > oldBlocks.size())?newBlocks:oldBlocks;
    }
}
