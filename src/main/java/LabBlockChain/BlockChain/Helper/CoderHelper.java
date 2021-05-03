package LabBlockChain.BlockChain.Helper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.UUID;


public class CoderHelper {
	private CoderHelper() {
	}

	public static String applySha256(String str) {
		MessageDigest digest;
		String encodeStr = "";
		try {
			digest = DigestUtils.getSha256Digest();
			byte[] hash = digest.digest(StringUtils.getBytesUtf8(str));
			encodeStr = Hex.encodeHexString(hash);
		} catch (Exception e) {
			System.out.println("getSHA256 is error" + e.getMessage());
		}
		return encodeStr;
	}

	// Applies ECDSA Signature and returns the result ( as bytes ).
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	public static String MD5(String str) {  
        try {  
            StringBuffer buffer = new StringBuffer();  
            char[] chars = new char[]{'0','1','2','3',  
                    '4','5','6','7','8','9','A','B','C','D','E','F'};  
            byte [] bytes = str.getBytes();  
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");  
            byte[] targ = messageDigest.digest(bytes);  
            for(byte b:targ) {  
                buffer.append(chars[(b>>4)&0x0F]);  
                buffer.append(chars[b&0x0F]);  
            }  
            return buffer.toString();  
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  
        }  
        return null;  
    } 

	public static String UUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}

	
}
