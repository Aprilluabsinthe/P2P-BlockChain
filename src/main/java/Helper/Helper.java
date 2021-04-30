package Helper;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.*;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.GsonBuilder;

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
}
