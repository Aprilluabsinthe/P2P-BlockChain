package BlockChain;

import basic.Block;

public interface blockchainInterface {
    boolean addBlock(Block block);
    Block createGenesisBlock();
    byte[] createNewBlock(String data);
    boolean broadcastNewBlock();
    void setDifficulty(int difficulty);
    byte[] getBlockchainData();
    void downloadBlockchain();
    void setNode(Node node);
    boolean isValidNewBlock(Block newBlock, Block prevBlock);
    Block getLastBlock();
    int getBlockChainLength();
}
