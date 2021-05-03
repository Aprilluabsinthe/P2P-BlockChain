package TransLib;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RemoteControllerInterface -- defines the remote callback functions that will
 * provide APIs for a MessageHandling interface for your Raft node
 */
public interface RemoteControllerInterface extends Remote {
    public Message deliverMessage(Message message) throws RemoteException;
    public GetStateReply getState() throws RemoteException;
    byte[] mineNewBlock(byte[] data) throws RemoteException;
    void broadcastNewBlock() throws RemoteException;
    void downloadChain() throws RemoteException;
}
