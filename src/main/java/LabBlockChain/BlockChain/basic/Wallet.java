package LabBlockChain.BlockChain.basic;
import java.util.List;
import java.util.Map;
import LabBlockChain.BlockChain.Helper.CoderHelper;
import LabBlockChain.BlockChain.Helper.RSACoder;
import LabBlockChain.BlockChain.Transaction.Transaction;


public class Wallet {

    private String publicKey;
    private String privateKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Wallet() {
    }


    public Wallet(String publicKey) {
        this.publicKey = publicKey;
    }

    public Wallet(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public static Wallet generateWallet() {
        Map<String, Object> initKey;
        try {
            initKey = RSACoder.initKey();
            String publicKey = RSACoder.getPublicKey(initKey);
            String privateKey = RSACoder.getPrivateKey(initKey);
            return new Wallet(publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAddress() {
        String publicKeyHash = hashPubKey(publicKey);
        return CoderHelper.MD5(publicKeyHash);
    }

    public static String getAddress(String publicKey) {
        String publicKeyHash = hashPubKey(publicKey);
        return CoderHelper.MD5(publicKeyHash);
    }

    public String getHashPubKey() {
        return CoderHelper.applySha256(publicKey);
    }
    public static String hashPubKey(String publicKey) {
        return CoderHelper.applySha256(publicKey);
    }

}
