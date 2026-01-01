import LoggerPackage.LoggerServerConfig;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Below class represents a server instance creating the registry and binding the hashmap object to
 * it.
 */
public class Server {

  /**
   * Main method to run the server.
   *
   * @param args args
   */
  public static void main(String[] args) {
    final Logger logger = Logger.getLogger("ServerLog");
    final LoggerServerConfig loggerServerConfig = new LoggerServerConfig();
    try {
      loggerServerConfig.setLogger();
    } catch (IOException e) {
      System.out.println(e.getMessage());
      System.out.println("Pls try restarting or checking that logger folder is correctly placed.");
    }

    try {
      IDataStorage hashmap = new IDataStorageImpl(logger);
      Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
      registry.bind("Storage", hashmap);
      System.out.println("Server started the registry on port " + args[0]);
      logger.info("Server started the registry on port " + args[0]);
      new CountDownLatch(1).await();

    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

}
