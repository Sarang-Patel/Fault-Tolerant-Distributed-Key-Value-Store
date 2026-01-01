import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Below class executes the paxos protocol on the commands given by user to maintain consistency and
 * provide fault tolerance.
 */
public class IDataStorageImpl extends UnicastRemoteObject implements IDataStorage, Serializable {

  private static Logger logger;

  private final KeyValueStore hashmap;

  private int highestProposal = -1;
  private String acceptedValue = null;
  private final ExecutorService executorService;

  public IDataStorageImpl(Logger logger) throws RemoteException {
    hashmap = new KeyValueStore();
    IDataStorageImpl.logger = logger;
    executorService = Executors.newCachedThreadPool();
  }

  @Override
  public String command(String cmd, String[] ports) throws RemoteException {
    logger.info("Command received: " + cmd);
    return startPaxos(new PaxosMessage(generateProposalNumber(), cmd), ports);
  }

  /**
   * Proposer that executes the entire paxos process.
   * @param paxosMessage paxos request packet.
   * @param ports port numbers of servers.
   * @return returns response.
   */
  private String startPaxos(PaxosMessage paxosMessage, String[] ports) {
    String response = "";
    boolean success = false;
    ExecutorService executorService = Executors.newCachedThreadPool();

    while (!success) {
      int cTrue = 0;
      int cFalse = 0;

      // Prepare phase
      for (String port : ports) {
        PaxosMessage finalPaxosMessage = paxosMessage;
        Future<PaxosResponse> future = executorService.submit(() -> {
          try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", Integer.parseInt(port));
            IDataStorage server = (IDataStorage) registry.lookup("Storage");
            return server.promise(finalPaxosMessage.getProposalNumber(), finalPaxosMessage.getValue());
          } catch (RemoteException | NotBoundException e) {
            logger.severe(e.getMessage());
            return new PaxosResponse(finalPaxosMessage.getProposalNumber(), -1, false);
          }
        });

        try {
          PaxosResponse responseObj = future.get(1, TimeUnit.SECONDS);
          if (responseObj.isSuccess()) {
            cTrue++;
          } else {
            cFalse++;
          }
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
          future.cancel(true);
          cFalse++;
          logger.severe("Timeout or exception: " + e.getMessage());
        }
      }

      simulateRandomFailure("PROPOSER");

      if (cTrue > cFalse) {
        cTrue = 0;
        cFalse = 0;

        // Accept phase
        for (String port : ports) {
          PaxosMessage finalPaxosMessage1 = paxosMessage;
          Future<PaxosResponse> future = executorService.submit(() -> {
            try {
              Registry registry = LocateRegistry.getRegistry("127.0.0.1", Integer.parseInt(port));
              IDataStorage server = (IDataStorage) registry.lookup("Storage");
              return server.accept(finalPaxosMessage1.getProposalNumber(), finalPaxosMessage1.getValue());
            } catch (RemoteException | NotBoundException e) {
              logger.severe(e.getMessage());
              return new PaxosResponse(finalPaxosMessage1.getProposalNumber(), -1, false);
            }
          });

          try {
            PaxosResponse responseObj = future.get(1, TimeUnit.SECONDS);
            if (responseObj.isSuccess()) {
              cTrue++;
            } else {
              cFalse++;
            }
          } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true);
            cFalse++;
            logger.severe("Timeout or exception: " + e.getMessage());
          }
        }

        if (cTrue > cFalse) {
          String finalValue;
          for (String port : ports) {
            Future<String> future = executorService.submit(() -> {
              try {
                Registry registry = LocateRegistry.getRegistry("127.0.0.1", Integer.parseInt(port));
                IDataStorage server = (IDataStorage) registry.lookup("Storage");
                return server.learn();
              } catch (RemoteException | NotBoundException e) {
                logger.severe(e.getMessage());
                return null;
              }
            });

            try {
              finalValue = future.get(1, TimeUnit.SECONDS);
              if (finalValue != null) {
                response = applyLearnedValue(finalValue, port);
              }
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
              future.cancel(true);
              logger.severe("Timeout or exception: " + e.getMessage());
            }
          }
          success = true;
        }
      }

      paxosMessage = new PaxosMessage(generateProposalNumber(), paxosMessage.getValue());
    }

    executorService.shutdown();
    return response;
  }


  /**
   * applies the learned value to all the servers.
   * @param finalValue final value learned by all the learners.
   * @param port port for a server.
   * @return string of response.
   */
  private String applyLearnedValue(String finalValue, String port) {
    String response = "";
    if (finalValue != null) {
      String[] parts = finalValue.split(" ");
      String operation = parts[0];
      String key = parts[1];
      String value = (parts.length > 2) ? parts[2] : null;

      Registry registry = null;
      try {
        registry = LocateRegistry.getRegistry("127.0.0.1", Integer.parseInt(port));
        IDataStorage server = (IDataStorage) registry.lookup("Storage");
        if ("DELETE".equals(operation)) {
          response = server.delete(key);
        } else {
          response = server.put(key, value);
        }
      } catch (RemoteException | NotBoundException e) {
        throw new RuntimeException(e);
      }
    }

    return response;
  }

  @Override
  public synchronized PaxosResponse promise(int tokenID, String cmd) throws RemoteException {
    simulateRandomFailure("ACCEPTOR during promise phase");
    boolean success = false;
    if (tokenID > highestProposal) {
      highestProposal = tokenID;
      success = true;
    }
    return new PaxosResponse(tokenID, highestProposal, success);
  }

  @Override
  public synchronized PaxosResponse accept(int tokenID, String cmd) throws RemoteException {
    simulateRandomFailure("ACCEPTOR during accept phase");
    boolean success = false;
    if (tokenID >= highestProposal) {
      highestProposal = tokenID;
      acceptedValue = cmd;
      success = true;
    }
    return new PaxosResponse(tokenID, highestProposal, success);
  }


  /**
   * simulates failure on acceptors. Chances of failure is 10%.
   */
  private void simulateRandomFailure(String type) {
    Random random = new Random();
    if (random.nextInt(30) + 1 > 29) {
      try {
        logger.info("Simulating failure on " + type + ". Thread sleeping for 5 seconds...");
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        logger.severe(e.getMessage());
      }
      logger.info(type + " restarted after failure simulation.");
    }
  }

  @Override
  public synchronized String learn() throws RemoteException {
    simulateRandomFailure("LEARNER");
    return acceptedValue;
  }

  private int generateProposalNumber() {
    return highestProposal + 1;
  }

  @Override
  public String get(String key) throws RemoteException {
    return hashmap.get(key);
  }

  @Override
  public String put(String key, String value) throws RemoteException {
    return hashmap.put(key, value);
  }

  @Override
  public String delete(String key) throws RemoteException {
    return hashmap.delete(key);
  }

}
