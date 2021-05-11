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
 * ref: https://www.devglan.com/java8/rsa-encryption-decryption-java
 */
public abstract class Coder {
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

	/**
	 * ref: https://www.geeksforgeeks.org/md5-hash-in-java/
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
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
		Key key = (Key) keyMap.get("RSAPrivateKey");

		return encryptBASE64(key.getEncoded());
	}


	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get("RSAPublicKey");

		return encryptBASE64(key.getEncoded());
	}

	public static Map<String, Object> generateInitKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		java.security.Security.addProvider(
				new org.bouncycastle.jce.provider.BouncyCastleProvider()
		);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put("RSAPublicKey", publicKey);
		keyMap.put("RSAPrivateKey", privateKey);
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

	public static String applySha256(String str) {
		MessageDigest digest = DigestUtils.getSha256Digest();
		byte[] hash = digest.digest(StringUtils.getBytesUtf8(str));
		return Hex.encodeHexString(hash);
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

	/**
	 * https://stackoverflow.com/questions/5470219/get-md5-string-from-message-digest
	 * @param str
	 * @return
	 */
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

	public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
		java.security.Security.addProvider(
				new org.bouncycastle.jce.provider.BouncyCastleProvider()
		);
		byte[] keyBytes = decryptBASE64(key);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
		java.security.Security.addProvider(
				new org.bouncycastle.jce.provider.BouncyCastleProvider()
		);
		byte[] keyBytes = decryptBASE64(key);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key publicKey = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}
}