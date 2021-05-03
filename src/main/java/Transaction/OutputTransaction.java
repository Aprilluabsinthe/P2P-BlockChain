package Transaction;

import Helper.Helper;
import com.google.gson.Gson;

import java.security.*;
import java.util.Objects;

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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutputTransaction)) return false;
        OutputTransaction that = (OutputTransaction) o;
        return Float.compare(that.value, value) == 0 && outputId.equals(that.outputId) && recipient.equals(that.recipient) && parentId.equals(that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputId, recipient, value, parentId);
    }

    // parser for String
    // Stirn
    // {"outId":"a696c9e2a56c979c3e6793a93d9a1bdf364ece3728038fb71c76a17b63b332cf",
    // "receiver":[48,86,48,16,6,7,42,-122,72,-50,61,2,1,6,5,43,-127,4,0,10,3,66,0,4,83,-11,83,69,123,0,-1,74,29,63,30,123,-30,-23,-22,-13,12,-86,-53,-4,23,66,-85,80,-70,49,28,125,-22,-26,74,-101,121,-124,0,-102,28,9,-100,38,-121,-56,-61,-23,21,-90,-75,-110,51,0,105,11,-30,0,-118,-14,41,-98,-83,-84,72,-88,35,127],
    // "asset":100.01,
    // "parent":"4050302"}
    @Override
    public String toString() {
        return toJson();
    }

    public class outData{
        public String outId;
        public byte[] receiver;
        public float asset;
        public String parent;


        public outData() {
            this.outId = outputId;
            this.receiver = recipient.getEncoded();
            this.asset = value;
            this.parent = parentId;
        }
    }

    public String toJson() {
        outData copy = new outData();
        Gson gson = new Gson();
        String json = gson.toJson(copy);
        return json;
    }

    public static OutputTransaction fromString(String json){
        //{"id":"28763c0be94a5804afc5b55e6f856edc114128b5a0b04a6dca64fcbf490ad42c","receiver":"EC Public Key [fc:3c:4a:5d:16:a0:63:60:5f:03:71:a2:ec:54:16:b2:88:51:77:a9]\n            X: 72e97c5010e2042ab5c5256f080e93c5fb625e3768dc6bdef37a8294482dc1f3\n            Y: af9a5c1b6cdc0f45544f4c505d5aa8c23c4552f8876d5f34d2728ace9f69a6d7\n","asset":100.01,"parent":"4050302"}
        Gson gson = new Gson();
        outData obj = gson.fromJson(json, outData.class);
        PublicKey pubKep = null;
        try {
            pubKep = Helper.genEcPubKey(obj.receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String parentId = obj.parent;
        float value = obj.asset;
        return new OutputTransaction(pubKep,value,parentId);
    }



    public static void main(String[] args) {
        KeyPair kp = Helper.generateKey();
        OutputTransaction outputtx = new OutputTransaction(kp.getPublic(), (float)100.01,"4050302");
        System.out.println(outputtx.toString());
        OutputTransaction out = fromString(outputtx.toString());
        System.out.println(out.equals(outputtx));
        System.out.println(out.toString());
    }

}
