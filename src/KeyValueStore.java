import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class represents a key value store object.
 */
public class KeyValueStore {

  private final ConcurrentHashMap<String, String> hashMap;
  private final ExecutorService executorService;

  public KeyValueStore() throws RemoteException {
    hashMap = new ConcurrentHashMap<>();
    executorService = Executors.newFixedThreadPool(10);
  }

  /**
   * Adds the key and its corresponding value in the data structure.
   *
   * @param key   key
   * @param value value
   * @return String indication result of operation
   * @throws RemoteException exception
   */
  public String put(String key, String value) throws RemoteException {
    Future<?> future = executorService.submit(() -> hashMap.put(key, value));
    try {
      future.get();  // Wait for the task to complete
      return "Value successfully added in storage.";
    } catch (Exception e) {
      throw new RemoteException("Failed to add value to storage", e);
    }
  }

  /**
   * Retrieves the value corresponding to a key from the data structure.
   *
   * @param key key
   * @return String indication result of operation
   * @throws RemoteException exception
   */
  public String get(String key) throws RemoteException {
    return hashMap.getOrDefault(key, "Key not found.");
  }

  /**
   * Deletes the value corresponding to a key from the data structure.
   *
   * @param key key
   * @return String indication result of operation
   * @throws RemoteException exception
   */
  public String delete(String key) throws RemoteException {
    Future<?> future = executorService.submit(() -> hashMap.remove(key));
    try {
      future.get();  // Wait for the task to complete
      return "Key successfully deleted.";
    } catch (Exception e) {
      throw new RemoteException("Failed to delete key from storage", e);
    }
  }
}

