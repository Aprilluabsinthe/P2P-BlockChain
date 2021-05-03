package Helper;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.logging.Logger;

import Transaction.Transaction;
import basic.Block;
import basic.trial;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Helper {
    private static final String EMPTY_STRING = "";
    private static final Logger LOGGER = Logger.getLogger(Helper.class.getName());


    public static String getHexSha256(String string) {
        MessageDigest digest = DigestUtils.getSha256Digest();
        byte[] hash = digest.digest(StringUtils.getBytesUtf8(string));
        return Hex.encodeHexString(hash);
    }

    public static String getDificultyTarget(int difficulty) {
        char[] c = new char[difficulty];
        Arrays.fill(c, '0');
        String result = new String(c);
        return result;
//        return new String(new char[difficulty]).replace('\0', '0');
    }

    public static String keyToString(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * https://stackoverflow.com/questions/11339788/tutorial-of-ecdsa-algorithm-to-sign-a-string
     * https://en.bitcoin.it/wiki/Elliptic_Curve_Digital_Signature_Algorithm
     * @param privateKey
     * @param contentToSign
     * @return
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateSignature(PrivateKey privateKey, String contentToSign){
        Signature sig = null;
        byte[] sigByte = new byte[0];
        try {
            sig = Signature.getInstance("ECDSA", "BC");
            sig.initSign(privateKey);
            sig.update(contentToSign.getBytes());
            sigByte = sig.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-NoSuchAlgorithmException");
            System.out.println("Helper-generateSignature-NoSuchAlgorithmException");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-NoSuchProviderException");
            System.out.println("Helper-generateSignature-NoSuchProviderException");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-InvalidKeyException");
            System.out.println("Helper-generateSignature-InvalidKeyException");
        } catch (SignatureException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-SignatureException");
            System.out.println("Helper-generateSignature-SignatureException");
        }
        return sigByte;
    }

    public static boolean verifySignature(PublicKey publicKey, String contentToVerify, byte[] signatureToVerify) {
        Signature sig = null;
        try {
            sig = Signature.getInstance("ECDSA", "BC");
            sig.initVerify(publicKey);
            sig.update(contentToVerify.getBytes());
            return sig.verify(signatureToVerify);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-NoSuchAlgorithmException");
            System.out.println("Helper-verifySignature-NoSuchAlgorithmException");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-NoSuchProviderException");
            System.out.println("Helper-verifySignature-NoSuchProviderException");
        } catch (SignatureException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-SignatureException");
            System.out.println("Helper-verifySignature-SignatureException");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            LOGGER.fine("Helper-generateSignature-InvalidKeyException");
            System.out.println("Helper-verifySignature-InvalidKeyException");
        }
        return false;
    }

    public static String getMerkleroot(List<Transaction> transactionList) {
        int size = transactionList.size();
        List<String> copiedList = new ArrayList<String>();
        for (Transaction tx: transactionList){
            copiedList.add(tx.transactionId);
        }
        
        List<String> merkleTree = null;
        while( size > 1){
            merkleTree = new ArrayList<String>();
            for(int i = 1; i < copiedList.size() ; i += 2){
                String child = copiedList.get(i-1) + copiedList.get(i);
                merkleTree.add(getHexSha256(child));
            }
            if (size%2 != 0){
                String left = copiedList.get(copiedList.size()-1);
                merkleTree.add(left);
            }
            size = merkleTree.size();
            copiedList = merkleTree;
        }
        
        if(size==1){
            return merkleTree.get(0);
        }
        else{
            return EMPTY_STRING;
        }
    }

    public static boolean possiblyUnderRoot(Transaction tx, String merkleRoot ){
        return merkleRoot.contains(tx.transactionId);
    }

    public static KeyPair generateKey(){
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator keysGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGen = new ECGenParameterSpec("secp256k1");
            keysGenerator.initialize(ecGen,r1);
            KeyPair keyPair = keysGenerator.generateKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PublicKey genEcPubKey(byte[] bytePubKey) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
        java.security.PublicKey ecPublicKey = (PublicKey) factory
                .generatePublic(new X509EncodedKeySpec(bytePubKey)); // Helper.toByte(ecRemotePubKey)) is java.security.PublicKey#getEncoded()
        return (PublicKey) ecPublicKey;
    }

    public static byte[] blockToBytes(Block block){
        return block.toString().getBytes();
    }

    public static Block bytesToBlocks(byte[] bytes){
        String json = new String(bytes);
        return Block.fromJson(json);
    }

    public static byte[] blockchainToBytes(List<Block> blockChains){
        String blocksString = String.join(";", blockChains.toString());
        return blocksString.getBytes();
    }

    public static List<Block> bytesToBlockchain(byte[] bytes){
        String json = new String(bytes);
        String[] blocksString = json.split(";");
        List<Block> blockchain = new ArrayList<>();
        for(String str:blocksString){
            Block thisBlock = Block.fromJson(str);
            blockchain.add(thisBlock);
        }
        return blockchain;
    }

}
