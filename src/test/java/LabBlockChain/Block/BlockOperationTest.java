package LabBlockChain.Block;

import LabBlockChain.BlockChain.Helper.Coder;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.Transaction.TransactionInput;
import LabBlockChain.BlockChain.Transaction.TransactionOutput;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.basic.Wallet;
import LabBlockChain.BlockChain.LabCoin.BlockChainImplement;
import LabBlockChain.BlockChain.LabCoin.BlockChainInterface;
import com.google.gson.Gson;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class BlockOperationTest {
    Gson gson = new Gson();

    private static final int STRESS_TEST_TIMES = 100;

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test operations on the local blockchain data structures
     */
    @Test
    public void localBlockOperationTest() throws Exception {
        BlockChainImplement blockChainImplement = new BlockChainImplement();
        Wallet wallet = blockChainImplement.createWallet();
        Block block = blockChainImplement.mine(wallet.getAddress());
        blockChainImplement.addBlock(block);

        List<Block> blockChain = blockChainImplement.getBlockChain();
        Assert.assertEquals(2, blockChain.size());
        Block lastBlock = blockChainImplement.getLatestBlock();
        String hash = lastBlock.getHash();

        // The default difficulty level is 3
        Assert.assertTrue(hash.startsWith("000"));

        Block block2 = blockChainImplement.mine(wallet.getAddress());
        blockChainImplement.addBlock(block2);

        List<Block> newBlockChain = blockChainImplement.getBlockChain();
        Assert.assertEquals(3, blockChain.size());
        Block lastBlock2 = blockChainImplement.getLatestBlock();
        String prevHash = lastBlock2.getPreviousHash();

        // make sure the chain is valid
        Assert.assertTrue(prevHash.equals(hash));
    }

    @Test
    public void stressTestLocalOperation() throws Exception {
        BlockChainImplement blockChainImplement = new BlockChainImplement();
        Wallet wallet = blockChainImplement.createWallet();

        int expectedCnt = 1;

        Block prevBlock = blockChainImplement.getLatestBlock();

        for (int i = 0; i < STRESS_TEST_TIMES; ++i) {
            Block block = blockChainImplement.mine(wallet.getAddress());
            blockChainImplement.addBlock(block);
            ++expectedCnt;

            List<Block> blockChain = blockChainImplement.getBlockChain();
            Assert.assertEquals(expectedCnt, blockChain.size());
            Block lastBlock = blockChainImplement.getLatestBlock();
            String hash = lastBlock.getHash();

            // The default difficulty level is 3
            Assert.assertTrue(hash.startsWith("000"));

            String prevHash = block.getPreviousHash();
            String prevBlockHash = prevBlock.getHash();

            // make sure the chain is valid
            Assert.assertTrue(prevHash.equals(prevBlockHash));
            prevBlock = block;
        }
    }

    @Test
    public void transactionTest() {
        BlockChainImplement blockChainImplement = new BlockChainImplement();
        Wallet sender = blockChainImplement.createWallet();

        Wallet receiver = blockChainImplement.createWallet();
        blockChainImplement.createTransaction(sender, receiver, 20);
    }

}
