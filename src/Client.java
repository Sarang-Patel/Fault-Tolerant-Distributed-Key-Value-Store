import LoggerPackage.LoggerClientConfig;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Below represents the client instance that gets the object from the rmi registry and executes the
 * methods it contains.
 */
public class Client {

  static final Logger logger = Logger.getLogger("ClientLog");
  static String[] ports;

  /**
   * Main method to run the server.
   *
   * @param args args.
   */
  public static void main(String[] args) {
    ports = args;
    final Logger logger = Logger.getLogger("ClientLog");
    final LoggerClientConfig loggerClientConfig = new LoggerClientConfig();
    try {
      loggerClientConfig.setLogger();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Scanner scanner = new Scanner(System.in);
    try {
      int portnumber = getPortNumber(args);
      Registry registry = LocateRegistry.getRegistry("127.0.0.1", portnumber);
      System.out.println(portnumber);
      IDataStorage hashmap = (IDataStorage) registry.lookup("Storage");

      prepopulateStorage(hashmap);
      Thread.sleep(300);
      System.out.println("Please perform one of the following commands:");
      System.out.println("  GET <key>     - Retrieve the value associated with the specified key.");
      System.out.println("  PUT <key> <value> - Store the specified key-value pair.");
      System.out.println("  DELETE <key>  - Remove the value associated with the specified key.");
      System.out.println("Usage examples:");
      System.out.println("  GET myKey");
      System.out.println("  PUT myKey myValue");
      System.out.println("  DELETE myKey");

      while (true) {

        System.out.println("Input Command -> ");
        String userInput = scanner.nextLine();
        if (userInput.isEmpty()) {
          System.out.println("Command cannot be empty. Please try again.");
          continue;
        }
        String[] parts = userInput.split(" ");
        if (parts.length < 2) {
          System.out.println("Invalid command format. Please follow the instructions.");
          continue;
        }
        String operation = parts[0];
        String key = parts[1];
        if (key.isEmpty()) {
          System.out.println("Key cannot be empty. Please try again.");
          continue;
        }
        String value;
        if (parts.length > 2) {
          value = parts[2];
        } else {
          value = null;
        }
        executeCommand("User Input", hashmap, operation, key, value, userInput);
      }

    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
  }

  private static int getPortNumber(String[] args) {
    Random rand = new Random();

    return Integer.parseInt(args[rand.nextInt(args.length)]);
  }

  private static void prepopulateStorage(IDataStorage hashmap) {

    List<String> commands = Arrays.asList(
        "PUT MATHEW 1990-5-5",
        "PUT JAMES 1989-12-23",
        "PUT TINA 1999-7-15",
        "GET MATHEW",
        "PUT AMANDA 2002-9-19",
        "DELETE JAMES",
        "GET JAMES",
        "PUT JOHN 2001-5-8",
        "GET AMANDA",
        "GET JOHN",
        "DELETE MAX",
        "PUT RILEY 2006-9-18",
        "PUT TYLER 1985-7-7",
        "GET RILEY",
        "DELETE TYLER",
        "GET TYLER",
        "PUT NICOLE 2000-6-19",
        "GET NICOLE",
        "DELETE NICOLE",
        "PUT SAM 1993-4-4",
        "DELETE SAM",
        "GET SAM",
        "PUT BRIAN 1992-7-8",
        "GET BRIAN",
        "DELETE JAMES"
    );

    for (String command : commands) {
      String[] parts = command.split(" ");
      String operation = parts[0];
      String key = parts[1];
      String value;
      if (parts.length > 2) {
        value = parts[2];
      } else {
        value = null;
      }

      logger.info("Prepopulate Command: " + command);

      try {
        executeCommand("Prepopulate", hashmap, operation, key, value, command);
      } catch (RemoteException e) {
        throw new RuntimeException(e);
      }

    }
  }

  private static void executeCommand(String cmdType, IDataStorage hashmap, String operation,
      String key, String value, String cmd) throws RemoteException {
    switch (operation) {
      case "GET":
        String response = null;
        try {
          response = hashmap.get(key);
        } catch (RemoteException e) {
          throw new RuntimeException(e);
        }
        logger.info(cmdType + " Response: " + response);
        if (Objects.equals(cmdType, "User Input")) {
          System.out.println(response);
        }
        break;

      case "PUT":

        if (value == null) {
          System.out.println("Value cannot be empty. Please try again.");
          break;
        }
        response = hashmap.command(cmd, ports);

        logger.info(cmdType + " Response: " + response);
        if (Objects.equals(cmdType, "User Input")) {
          System.out.println(response);
        }
        break;

      case "DELETE":

        response = hashmap.command(cmd, ports);
        logger.info(cmdType + " Response: " + response);
        if (Objects.equals(cmdType, "User Input")) {
          System.out.println(response);
        }
        break;

    }
  }

}
