package Transaction;

import labCoin.LabCoin;

public class InputTransaction {
    public String outputId;
    public OutputTransaction UTXO;


    public InputTransaction(String outputId) {
        this.outputId = outputId;
        this.UTXO = LabCoin.UTXOMap.get(this.outputId);
    }
}
