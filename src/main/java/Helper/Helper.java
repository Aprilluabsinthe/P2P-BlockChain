package Helper;
import java.security.*;
import java.util.*;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Helper {

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
            System.out.println("Helper-generateSignature-NoSuchAlgorithmException");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("Helper-generateSignature-NoSuchProviderException");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("Helper-generateSignature-InvalidKeyException");
        } catch (SignatureException e) {
            e.printStackTrace();
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
            System.out.println("Helper-verifySignature-NoSuchAlgorithmException");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("Helper-verifySignature-NoSuchProviderException");
        } catch (SignatureException e) {
            e.printStackTrace();
            System.out.println("Helper-verifySignature-SignatureException");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("Helper-verifySignature-InvalidKeyException");
        }
        return false;
    }
}
