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

    /**
     * public key getter
     * @return the public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * private key getter
     * @return the private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * address getter
     * @return the address
     */
    public String getAddress() {
        String publicKeyHash = hashPubKey(publicKey);
        return Coder.decodeMD5(publicKeyHash);
    }

    /**
     * address getter
     * @return the address
     */
    public static String getAddress(String publicKey) {
        String publicKeyHash = hashPubKey(publicKey);
        return Coder.decodeMD5(publicKeyHash);
    }

    /**
     * hash key getter
     * @return the hash key
     */
    public String getHashPubKey() {
        return Coder.applySha256(publicKey);
    }

    /**
     * hash key getter
     * @return the hash key
     */
    public static String hashPubKey(String publicKey) {
        return Coder.applySha256(publicKey);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }

    /**
     * pubclic key setter
     * @param publicKey the public key
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * private key setter
     * @param privateKey private key
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
