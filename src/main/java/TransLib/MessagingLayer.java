package TransLib;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessagingLayer extends Remote {
    public void register(int id, RemoteControllerInterface remoteController) throws RemoteException;
    public Message send(Message message) throws RemoteException;
}
