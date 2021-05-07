package LabBlockChain.BlockChain.basic;
import java.util.Map;

import LabBlockChain.BlockChain.Helper.Coder;

/**
 * The LabCoin Wallet
 */
public class Wallet {
    private String publicKey;
    private String privateKey;

    /**
     * Super constructor for wallet
     */
    public Wallet() {
    }

    /**
     * Constructor for wallet
     * @param publicKey the public Key
     */
    public Wallet(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Constructor for wallet
     * @param publicKey the public Key
     * @param privateKey the private Key
     */
    public Wallet(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Genrate a new Wallet
     * @return
     */
    public static Wallet generateWallet() {
        Map<String, Object> initKey;
        try {
            initKey = Coder.generateInitKey();
            String publicKey = Coder.getPublicKey(initKey);
            String privateKey = Coder.getPrivateKey(initKey);
            return new Wallet(publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getAddress() {
        String publicKeyHash = hashPubKey(publicKey);
        return Coder.decodeMD5(publicKeyHash);
    }

    public static String getAddress(String publicKey) {
        String publicKeyHash = hashPubKey(publicKey);
        return Coder.decodeMD5(publicKeyHash);
    }

    public String getHashPubKey() {
        return Coder.applySha256(publicKey);
    }
    public static String hashPubKey(String publicKey) {
        return Coder.applySha256(publicKey);
    }

}
