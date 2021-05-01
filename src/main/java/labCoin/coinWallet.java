package labCoin;

import Transaction.OutputTransaction;
import Transaction.InputTransaction;
import Transaction.Transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.*;
import java.util.logging.Logger;

public class coinWallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public static Map<String, OutputTransaction> walletUTXOMap = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(coinWallet.class.getName());


    public coinWallet(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * https://www.cryptosys.net/pki/eccrypto.html
     * https://www.codota.com/web/assistant/code/rs/5c7888c1df79be0001ea7af2#L55
     */
    public coinWallet() {
        try{
            KeyPairGenerator keysGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGen = new ECGenParameterSpec("secp256k1");
            keysGenerator.initialize(ecGen,r1);
            KeyPair keyPair = keysGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LOGGER.fine("coinWallet-construction-NoSuchAlgorithmException");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            LOGGER.fine("coinWallet-construction-NoSuchProviderException");
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            LOGGER.fine("coinWallet-construction-InvalidAlgorithmParameterException");
        }
    }

    public float walletBalance(){
        float balance = 0;
        for (Map.Entry<String, OutputTransaction> entry : LabCoin.UTXOMap.entrySet()){
            OutputTransaction unPackcoin = entry.getValue();
            if(coinForMe(unPackcoin)){
                this.walletUTXOMap.put(unPackcoin.outputId, unPackcoin);
                balance += unPackcoin.value;
            }
        }
        return balance;
    }

    public boolean coinForMe(OutputTransaction coin){
        return coin.recipient.equals(this.publicKey);
    }

    public Transaction makeTransaction(PublicKey receiver,float value){
        if(value > walletBalance()){
            System.out.println("coinWallet do not have enough asset");
            LOGGER.fine("coinWallet do not have enough asset");
            return null;
        }
        List<InputTransaction> inputTransactionList = new ArrayList<InputTransaction>();

        float assetSum = 0;
        for (Map.Entry<String, OutputTransaction> entry : this.walletUTXOMap.entrySet()){
            OutputTransaction unspentCoin = entry.getValue();
            assetSum += unspentCoin.value;
            inputTransactionList.add(new InputTransaction(unspentCoin.outputId));
            if(assetSum > value){
                break;
            }
        }

        // generate new transaction and sign
        Transaction newTransaction = new Transaction(this.publicKey,receiver,value,inputTransactionList);
        newTransaction.yieldSignature(this.privateKey);

        // move from wallet
        for(InputTransaction inputHasPacked : inputTransactionList){
            this.walletUTXOMap.remove(inputHasPacked.outputId);
        }

        return newTransaction;
    }
}
