package Transaction;

import Helper.Helper;

import java.security.PublicKey;

public class OutputTransaction {
    public String outputId;
    public PublicKey recipient;
    public float value;
    public String parentId;

    public OutputTransaction(PublicKey recipient, float value, String parentId) {
        this.recipient = recipient;
        this.value = value;
        this.parentId = parentId;
        String digest =  Helper.keyToString(recipient) + value + parentId;
        this.outputId = Helper.getHexSha256(digest);
    }

    public boolean coinIsMine(PublicKey PublicKey){
        return PublicKey.equals(recipient);
    }
}
