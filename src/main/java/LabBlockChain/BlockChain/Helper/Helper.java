//package LabBlockChain.BlockChain.Helper;
//import java.security.*;
//import java.security.spec.ECGenParameterSpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.*;
//import java.util.logging.Logger;
//
//import LabBlockChain.BlockChain.Transaction.Transaction;
//import LabBlockChain.BlockChain.basic.Block;
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.binary.StringUtils;
//import org.apache.commons.codec.digest.DigestUtils;
//
//public class Helper {
//    private static final String EMPTY_STRING = "";
//    private static final Logger LOGGER = Logger.getLogger(Helper.class.getName());
//
//
//    public static String getHexSha256(String string) {
//        MessageDigest digest = DigestUtils.getSha256Digest();
//        byte[] hash = digest.digest(StringUtils.getBytesUtf8(string));
//        return Hex.encodeHexString(hash);
//    }
//
//    public static String getDificultyTarget(int difficulty) {
//        char[] c = new char[difficulty];
//        Arrays.fill(c, '0');
//        String result = new String(c);
//        return result;
////        return new String(new char[difficulty]).replace('\0', '0');
//    }
//
//    public static String keyToString(Key key){
//        return Base64.getEncoder().encodeToString(key.getEncoded());
//    }
//
//    /**
//     * https://stackoverflow.com/questions/11339788/tutorial-of-ecdsa-algorithm-to-sign-a-string
//     * https://en.bitcoin.it/wiki/Elliptic_Curve_Digital_Signature_Algorithm
//     * @param privateKey
//     * @param contentToSign
//     * @return
//     * @throws NoSuchProviderException
//     * @throws NoSuchAlgorithmException
//     */
//    public static byte[] generateSignature(PrivateKey privateKey, String contentToSign){
//        Signature sig = null;
//        byte[] sigByte = new byte[0];
//        try {
//            sig = Signature.getInstance("ECDSA", "BC");
//            sig.initSign(privateKey);
//            sig.update(contentToSign.getBytes());
//            sigByte = sig.sign();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchAlgorithmException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchAlgorithmException");
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchProviderException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchProviderException");
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-InvalidKeyException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-generateSignature-InvalidKeyException");
//        } catch (SignatureException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-SignatureException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-generateSignature-SignatureException");
//        }
//        return sigByte;
//    }
//
//    public static boolean verifySignature(PublicKey publicKey, String contentToVerify, byte[] signatureToVerify) {
//        Signature sig = null;
//        try {
//            sig = Signature.getInstance("ECDSA", "BC");
//            sig.initVerify(publicKey);
//            sig.update(contentToVerify.getBytes());
//            return sig.verify(signatureToVerify);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchAlgorithmException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-verifySignature-NoSuchAlgorithmException");
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-NoSuchProviderException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-verifySignature-NoSuchProviderException");
//        } catch (SignatureException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-SignatureException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-verifySignature-SignatureException");
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//            LOGGER.fine("LabBlockChain.basic.BlockChain.Helper-generateSignature-InvalidKeyException");
//            System.out.println("LabBlockChain.basic.BlockChain.Helper-verifySignature-InvalidKeyException");
//        }
//        return false;
//    }
//
//    public static String getMerkleroot(List<Transaction> transactionList) {
//        int size = transactionList.size();
//        List<String> copiedList = new ArrayList<String>();
//        for (Transaction tx: transactionList){
//            copiedList.add(tx.transactionId);
//        }
//
//        List<String> merkleTree = null;
//        while( size > 1){
//            merkleTree = new ArrayList<String>();
//            for(int i = 1; i < copiedList.size() ; i += 2){
//                String child = copiedList.get(i-1) + copiedList.get(i);
//                merkleTree.add(getHexSha256(child));
//            }
//            if (size%2 != 0){
//                String left = copiedList.get(copiedList.size()-1);
//                merkleTree.add(left);
//            }
//            size = merkleTree.size();
//            copiedList = merkleTree;
//        }
//
//        if(size==1){
//            return merkleTree.get(0);
//        }
//        else{
//            return EMPTY_STRING;
//        }
//    }
//
//    public static boolean possiblyUnderRoot(Transaction tx, String merkleRoot ){
//        return merkleRoot.contains(tx.transactionId);
//    }
//
//    public static KeyPair generateKey(){
//        try{
//            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            KeyPairGenerator keysGenerator = KeyPairGenerator.getInstance("ECDSA","BC");
//            SecureRandom r1 = SecureRandom.getInstance("SHA1PRNG");
//            ECGenParameterSpec ecGen = new ECGenParameterSpec("secp256k1");
//            keysGenerator.initialize(ecGen,r1);
//            KeyPair keyPair = keysGenerator.generateKeyPair();
//            return keyPair;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static PublicKey genEcPubKey(byte[] bytePubKey) throws Exception {
//        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
//        java.security.PublicKey ecPublicKey = (PublicKey) factory
//                .generatePublic(new X509EncodedKeySpec(bytePubKey)); // LabBlockChain.basic.BlockChain.Helper.toByte(ecRemotePubKey)) is java.LabBlockChain.basic.BlockChain.security.PublicKey#getEncoded()
//        return (PublicKey) ecPublicKey;
//    }
//
//    public static byte[] blockToBytes(Block block){
//        return block.toString().getBytes();
//    }
//
//    public static Block bytesToBlocks(byte[] bytes){
//        String json = new String(bytes);
//        return Block.fromJson(json);
//    }
//
//    public static byte[] blockchainToBytes(List<Block> blockChains){
//        String blocksString = String.join(";", blockChains.toString());
//        return blocksString.getBytes();
//    }
//
//    public static List<Block> bytesToBlockchain(byte[] bytes){
//        String json = new String(bytes);
//        String[] blocksString = json.split(";");
//        List<Block> blockchain = new ArrayList<>();
//        for(String str:blocksString){
//            Block thisBlock = Block.fromJson(str);
//            blockchain.add(thisBlock);
//        }
//        return blockchain;
//    }
//

//        public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
//                byte[] keyBytes = decryptBASE64(key);
//                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//                KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//                Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//                Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//                cipher.init(Cipher.DECRYPT_MODE, privateKey);
//                return cipher.doFinal(data);
//                }
//
//
//        public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
//                byte[] keyBytes = decryptBASE64(key);
//
//
//                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//                KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//                Key publicKey = keyFactory.generatePublic(x509KeySpec);
//
//                Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//                cipher.init(Cipher.DECRYPT_MODE, publicKey);
//
//                return cipher.doFinal(data);
//                }
//
//        public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
//                byte[] keyBytes = decryptBASE64(key);
//
//                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
//                KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//                Key publicKey = keyFactory.generatePublic(x509KeySpec);
//
//                Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//
//                return cipher.doFinal(data);
//                }
//
//        public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
//                // 对密钥解密
//                byte[] keyBytes = decryptBASE64(key);
//
//                // 取得私钥
//                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//                KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//                Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//
//                // 对数据加密
//                Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//
//                return cipher.doFinal(data);
//                }

//}
