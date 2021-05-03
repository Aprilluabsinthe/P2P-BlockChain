package TransLib;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * RemoteController -- implementation of RemoteControllerInterface, which
 * includes APIs for MessageHandling interface in the Raft node
 */
public class RemoteController extends UnicastRemoteObject implements RemoteControllerInterface{
    public final MessageHandling message_callback;

    private static final long serialVersionUID = 1L;

    public RemoteController(MessageHandling mh) throws RemoteException {
        this.message_callback = mh;
    }

    public Message deliverMessage(Message message) throws RemoteException {
       return message_callback.deliverMessage(message);
    }

    public GetStateReply getState() throws RemoteException{
        return message_callback.getState();
    }

    @Override
    public byte[] mineNewBlock(byte[] data) throws RemoteException {
        return message_callback.mineNewBlock(data);
    }

    @Override
    public void broadcastNewBlock() throws RemoteException {
        message_callback.broadcastNewBlock();
    }

    @Override
    public void downloadChain() throws RemoteException {
        message_callback.consensusBlockchain();
    }

}
