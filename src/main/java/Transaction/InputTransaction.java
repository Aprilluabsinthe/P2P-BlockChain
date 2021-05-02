package Transaction;

import LabCoin.LabCoin;
import com.google.gson.Gson;
import Helper.*;

import java.security.KeyPair;
import java.security.PublicKey;

public class InputTransaction {
    public String outputId;
    public OutputTransaction UTXO;

    public InputTransaction(String outputId) {
        this.outputId = outputId;
        this.UTXO = LabCoin.UTXOMap.get(this.outputId);
    }

    @Override
    public String toString() {
        try {
            return toJson();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return "INPUT TO STRING ERROR";
    }

    private class inData{
        public String outId;
        public OutputTransaction inUTXO;

        public inData() {
            this.outId = outputId;
            this.inUTXO = UTXO;
        }
    }

    public String toJson() throws CloneNotSupportedException {
        inData copy = new inData();
        Gson gson = new Gson();
        String json = gson.toJson(copy);
        return json;
    }

    public static InputTransaction fromString(String json){
        //{"id":"28763c0be94a5804afc5b55e6f856edc114128b5a0b04a6dca64fcbf490ad42c","receiver":"EC Public Key [fc:3c:4a:5d:16:a0:63:60:5f:03:71:a2:ec:54:16:b2:88:51:77:a9]\n            X: 72e97c5010e2042ab5c5256f080e93c5fb625e3768dc6bdef37a8294482dc1f3\n            Y: af9a5c1b6cdc0f45544f4c505d5aa8c23c4552f8876d5f34d2728ace9f69a6d7\n","asset":100.01,"parent":"4050302"}
        Gson gson = new Gson();
        inData obj = gson.fromJson(json,inData.class);
        String outputId = obj.outId;
        OutputTransaction UTXO = obj.inUTXO;
        return new InputTransaction(outputId);
    }



    public InputTransaction(OutputTransaction UTXO) {
        this.outputId = UTXO.outputId;
        this.UTXO = UTXO;
    }


    public static void main(String[] args) {
        KeyPair kp = Helper.generateKey();
        OutputTransaction outputtx = new OutputTransaction(kp.getPublic(), (float)100.01,"4050302");
        System.out.println(outputtx.toString());
        InputTransaction intx = new InputTransaction(outputtx);
        String in = intx.toString();
        System.out.println(in);
    }
}
