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

    @Test
    public void transactionTest() {
        BlockChainImplement blockChainImplement = new BlockChainImplement();
        Wallet sender = blockChainImplement.createWallet();

        Wallet receiver = blockChainImplement.createWallet();
        blockChainImplement.createTransaction(sender, receiver, 20);
    }

}