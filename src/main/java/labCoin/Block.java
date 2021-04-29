package labCoin;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Block {
    private int index;
    private String timestamp;
    private int asset;
    private String Hash;
    private String preHash;
    private int difficulty;
    private String nouce;


}
