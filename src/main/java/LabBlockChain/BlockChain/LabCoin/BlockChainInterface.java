package LabBlockChain.BlockChain.LabCoin;

import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.basic.Wallet;

import java.util.List;
import java.util.Map;

/**
 * the interface for block chain service
 */
public interface BlockChainInterface {
    Block getLatestBlock();

    boolean addBlock(Block newBlock);

    boolean isValidBlock(Block newBlock, Block previousBlock);

    void replaceChain(List<Block> newBlocks);

    Block mine(String toAddress);

    Transaction generateBaseTx(String toAddress);

    Transaction createTransaction(Wallet senderWallet, Wallet recipientWallet, int amount);

    Wallet createWallet();

    int getWalletBalance(String address);

    List<Block> getBlockChain();

    void setBlockChain(List<Block> blockChain);

    Map<String, Wallet> getMyWalletMap();

    void setMyWalletMap(Map<String, Wallet> myWalletMap);

    Map<String, Wallet> getOtherWalletMap();

    void setOtherWalletMap(Map<String, Wallet> otherWalletMap);

    List<Transaction> getAllTransactions();

    void setAllTransactions(List<Transaction> allTransactions);

    List<Transaction> getPackedTransactions();

    void setPackedTransactions(List<Transaction> packedTransactions);
}
