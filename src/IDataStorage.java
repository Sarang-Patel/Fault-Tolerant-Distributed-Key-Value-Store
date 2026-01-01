import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface below represents the public methods available for a data storage.
 */
public interface IDataStorage extends Remote {

  /**
   * Below method executes command based on
   * @param cmd PUT/DELETE
   * @param ports port numbers of all the servers
   * @return response to the command execution
   * @throws RemoteException remote exception
   */
  String command(String cmd, String[] ports) throws RemoteException;

  /**
   * Below method performs prepare phase in paxos protocol.
   * @param tokenID the token for a particular request by the proposer.
   * @param cmd the command to execute as value
   * @return True/false based on whether it excepts or not.
   * @throws RemoteException remote exception
   */
  PaxosResponse promise(int tokenID, String cmd) throws RemoteException;

  /**
   * Below method accept prepare phase in paxos protocol.
   * @param tokenID the token for a particular request by the proposer.
   * @param cmd the command to execute as value
   * @return True/false based on whether it excepts or not.
   * @throws RemoteException remote exception
   */
  PaxosResponse accept(int tokenID, String cmd) throws RemoteException;

  /**
   * Makes all learners return their learned value;
   * @return returns string.
   * @throws RemoteException remote exception.
   */
  String learn() throws RemoteException;

  /**
   * returns value associated with a key.
   * @param key key.
   * @return string.
   * @throws RemoteException remote exception
   */
  String get(String key) throws RemoteException;

  /**
   * adds value in the key value store.
   * @param key key.
   * @param value value.
   * @return returns response.
   * @throws RemoteException remote exception.
   */
  String put(String key, String value) throws RemoteException;

  /**
   * deletes value from key value store.
   * @param key key to delete.
   * @return returns response.
   * @throws RemoteException remote exception.
   */
  String delete(String key) throws RemoteException;
}
