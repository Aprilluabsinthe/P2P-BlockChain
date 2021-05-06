package LabBlockChain.Block;

import LabBlockChain.BlockChain.Helper.CoderHelper;
import LabBlockChain.BlockChain.Transaction.Transaction;
import LabBlockChain.BlockChain.Transaction.TransactionInput;
import LabBlockChain.BlockChain.Transaction.TransactionOutput;
import LabBlockChain.BlockChain.basic.Block;
import LabBlockChain.BlockChain.basic.Wallet;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class BlockOperationTest {
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void MiningTest() throws Exception{
        // new blockchain
        List<Block> blockchain = new ArrayList<Block>();
        // genesis block
        Block genesis = new Block(1,System.currentTimeMillis(),new ArrayList<Transaction>(),1,"1","1");
        blockchain.add(genesis);
        System.out.println(gson.toJson(blockchain));
        // be like:
        // [{"index":1,"hash":"1","previousHash":"1","timestamp":1620250554479,"transactions":[],"nonce":1}]

        List<Transaction> txlist = new ArrayList<Transaction>();
        Transaction localtx = new Transaction();
        txlist.add(localtx);

        Wallet sender = Wallet.generateWallet();
        Wallet receiver = Wallet.generateWallet();

        // generate the first base transaction
        int amount = 10;
        TransactionInput Intx = null;
        TransactionOutput Outtx = new TransactionOutput(amount,sender.getHashPubKey());
//        String forHashId = Intx.toString() + Outtx.toString();
        Transaction tx = new Transaction(CoderHelper.UUID(),Intx,Outtx);
        txlist.add(tx);

        // generate following transactions
        TransactionInput Intx_test = new TransactionInput(tx.getId(),amount,null,sender.getPublicKey());
        TransactionOutput Outtx_test = new TransactionOutput(amount,receiver.getHashPubKey());
        String tx_testId = CoderHelper.applySha256(Intx_test.toString()+Outtx_test.toString());
        Transaction tx_test = new Transaction(CoderHelper.UUID(),Intx_test,Outtx_test);
        tx_test.sign(sender.getPrivateKey(),tx);
        txlist.add(tx_test);

        // obtain previous last block
        Block lastBlock = blockchain.get(blockchain.size()-1);
        int count = 0;
        int nonce = 1;
        String hash;
        while(true){
            count += 1;
            hash = CoderHelper.applySha256(
                    lastBlock.getPreviousHash() + gson.toJson(lastBlock.getTransactions()) + nonce
            );
            System.out.println("nonce: "+ nonce +" hash: " +hash);
            if(hash.startsWith("0000")){
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println("End of calculating hash after "+count+" times of try : ");
                System.out.println("the correct hash is: " +hash);
                break;
            }
            nonce += 1;
        }

        Block newBlock = new Block(lastBlock.getIndex()+1,System.currentTimeMillis(),txlist,nonce,lastBlock.getHash(),hash);
        blockchain.add(newBlock);
        System.out.println("Block chain after mining>>>>>>>>>>>>>>>>>");
        System.out.println(gson.toJson(blockchain));
    }

}
