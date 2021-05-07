package LabBlockChain.BlockChain.Helper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * KEY_ALGORITHM = "RSA"
 * SIGNATURE_ALGORITHM = "MD5withRSA";
 */
public abstract class Coder {
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	public static byte[] decryptBASE64(String key) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] buffer = decoder.decode(key);
		return buffer;
	}


	public static String encryptBASE64(byte[] key) throws Exception {
		Base64.Encoder encoder = Base64.getEncoder();
		String encode = encoder.encodeToString(key);
		return encode;
	}

	public static String MD5RSAsign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = decryptBASE64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initSign(priKey);
		signature.update(data);
		return encryptBASE64(signature.sign());
	}

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

	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = decryptBASE64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(decryptBASE64(sign));
	}


	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return encryptBASE64(key.getEncoded());
	}


	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return encryptBASE64(key.getEncoded());
	}

	public static Map<String, Object> generateInitKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	public void generateECDSAKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random); //256
			KeyPair keyPair = keyGen.generateKeyPair();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		String text = encryptBASE64("1".getBytes("UTF-8"));
		System.out.println(new String(decryptBASE64(text)));
	}

	public static String applySha256(String str) {
//		MessageDigest digest = DigestUtils.getSha256Digest();
//		String encodeStr = "";
//		byte[] hash = new byte[0];
//		digest = DigestUtils.getSha256Digest();
//		byte[] hash = digest.digest(StringUtils.getBytesUtf8(str));
//		encodeStr = Hex.encodeHexString(hash);

//		MessageDigest digest = DigestUtils.getSha256Digest();
//		byte[] hash = digest.digest(StringUtils.getBytesUtf8(input));
//		return Hex.encodeHexString(hash);

//		try {
//			digest = DigestUtils.getSha256Digest();
//		}catch (Exception e) {
//			System.out.println("getSHA256 is error getSha256Digest");
//		}
//		try {
//			hash = digest.digest(StringUtils.getBytesUtf8(str));
//		}catch (Exception e) {
//			System.out.println("str:"+str);
//			e.printStackTrace();
//			System.out.println("getSHA256 is error digest getBytesUtf8");
//		}
//		try{
//			encodeStr = Hex.encodeHexString(hash);
//		} catch (Exception e) {
//			System.out.println("getSHA256 is error encodeHexString");
//		}
//		return encodeStr;
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getSHA256 is error" + e.getMessage());
		}
		return encodeStr;
	}

	private static String byte2Hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		String temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				builder.append("0");
			}
			builder.append(temp);
		}
		return builder.toString();
	}

	public static String decodeMD5(String str) {
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