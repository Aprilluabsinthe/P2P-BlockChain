package BlockChain;

import Helper.Helper;
import LabCoin.LabCoin;
import LabCoin.coinWallet;
import basic.Block;
import lib.*;

import java.rmi.RemoteException;
import java.util.logging.Logger;

public class Node implements MessageHandling {
    public final int DIFFICULTY = 5;
    private static final Logger LOGGER = Logger.getLogger(LabCoin.class.getName());
    private int id;
    private static TransportLib lib;
    private int num_peers;
    private coinWallet wallet = new coinWallet();
    blockchainInterface interfaces;
    private final Object lock = new Object();

    public Node(int port, int id, int num_peers) {
        this.id = id;
        this.lib = new TransportLib(port, id, this);
        this.num_peers = num_peers;
        this.wallet = new coinWallet();
        this.interfaces = new blockchainImplementation(id,this, DIFFICULTY);
    }

    @Override
    public Message deliverMessage(Message message) {
        byte[] data = null;
        MessageType typeInfo = null;
        Block newBlock = null;
        switch (message.getType()) {
            // for blockchain to use
            case BROADCAST_NEW_BLOCK:
                data = message.getBody();
                String json = new String(data);
                newBlock = Block.fromJson(json);
                boolean success = interfaces.addBlock(newBlock);
                if(success) {
                    typeInfo = MessageType.AGREE;
                }
                else
                    typeInfo = MessageType.DISAGREE;
                break;
            case GET_BLOCKCHAIN:
                typeInfo = MessageType.GET_BLOCKCHAIN;
                data = interfaces.getBlockchainData();
                break;
            default:
        }

        Message reply = new Message(typeInfo, message.getDest(),message.getSrc(), data);
        return reply;
    }

    @Override
    public GetStateReply getState() {
        synchronized (lock) {
            int length = interfaces.getBlockChainLength();
            Block last = interfaces.getLastBlock();
            return new GetStateReply(length, last.getHash());
        }
    }

    @Override
    public byte[] mineNewBlock(byte[] data) {
        return Helper.blockToBytes(
                interfaces.createNewBlock(data)
        );
    }

    @Override
    public void consensusBlockchain() {
        interfaces.ConsensusBlockchain();
    }

    public int getPeerNumber() {
        return num_peers;
    }


    @Override
    public void broadcastNewBlock() {
        if(interfaces.broadcastNewBlock()){
//            updateWallet(blockToBytes);
        };
    }

    public void updateWallet(byte[] blockToBytes){
        float value = Float.parseFloat(new String(blockToBytes));
        coinWallet.makeTransaction(wallet.publicKey,value);
        float balance = coinWallet.walletBalance();
        LOGGER.info("coinWallet updated"+value+",now balance : "+balance);
    }

    public boolean broadcastToPeer(int peerAddr, byte[] blockToBytes){
        Message message = new Message(MessageType.BROADCAST_NEW_BLOCK, id, peerAddr, blockToBytes);
        Message reply = null;
        try{
            reply = lib.sendMessage(message);
        }catch (RemoteException e) {
            e.printStackTrace();
        }

        if(reply == null)
            return true;

        if(reply.getType() == MessageType.AGREE)
            return true;
        else if(reply.getType() == MessageType.DISAGREE) {
            return false;
        }
        else{
            System.out.println("wrong reply type!");
            return false;
        }
    }

    public byte[] getPeerBlockChain(int peerAddr) {
        Message message = new Message(MessageType.GET_BLOCKCHAIN, id, peerAddr, null);
        Message reply = null;
        try {
            reply = lib.sendMessage(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return reply.getBody();
    }
}
