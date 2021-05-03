package BlockChain;

import basic.Block;

import java.util.List;

public interface blockchainInterface {
    boolean addBlock(Block block);
    Block GenesisBlock();
    Block createNewBlock(byte[] data);
    boolean broadcastNewBlock();
    void setDifficulty(int difficulty);
    byte[] getBlockchainData();
    void ConsensusBlockchain();
    void setNode(Node node);
    boolean isValidNewBlock(Block newBlock, Block prevBlock);
    Block getLastBlock();
    int getBlockChainLength();
    List<Block> replaceChain(List<Block> oldBlocks,List<Block> newBlocks) ;
}
