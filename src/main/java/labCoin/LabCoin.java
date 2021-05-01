package labCoin;

// logging
import Transaction.OutputTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LabCoin {
    public static final float MIN_TRANSACTION = (float) 0.01;
    private static List<Block> localBlockChain = new ArrayList<Block>();
    private static Block blockToPack = new Block();
    public static Map<String, OutputTransaction> UTXOMap = new HashMap<>();


    private static final Logger LOGGER = Logger.getLogger(LabCoin.class.getName());

}
