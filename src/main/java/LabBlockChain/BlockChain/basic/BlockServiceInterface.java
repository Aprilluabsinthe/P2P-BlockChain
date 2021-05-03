package LabBlockChain.BlockChain.basic;

import LabBlockChain.BlockChain.Transaction.Transaction;

import java.util.List;
import java.util.Map;

public interface BlockServiceInterface {
    Block getLatestBlock();

    boolean addBlock(Block newBlock);

    boolean isValidNewBlock(Block newBlock, Block previousBlock);

    void replaceChain(List<Block> newBlocks);

    Block mine(String toAddress);

    Transaction newCoinbaseTx(String toAddress);

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
