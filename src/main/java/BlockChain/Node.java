package BlockChain;

import LabCoin.LabCoin;
import LabCoin.coinWallet;
import lib.GetStateReply;
import lib.Message;
import lib.MessageHandling;
import lib.TransportLib;

import java.util.logging.Logger;

public class Node implements MessageHandling {
    private static final Logger LOGGER = Logger.getLogger(LabCoin.class.getName());
    private int id;
    private static TransportLib lib;
    private int num_peers;
    private coinWallet wallet;

    @Override
    public Message deliverMessage(Message message) {
        return null;
    }

    @Override
    public GetStateReply getState() {
        return null;
    }

    @Override
    public byte[] mineNewBlock(byte[] data) {
        return new byte[0];
    }

    @Override
    public void broadcastNewBlock() {

    }

    @Override
    public void downloadChain() {

    }
}
