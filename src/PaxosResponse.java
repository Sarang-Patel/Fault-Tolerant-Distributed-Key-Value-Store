import java.io.Serializable;

/**
 * Class represents paxos response message.
 */
public class PaxosResponse implements Serializable {
  private final int receivedToken;
  private final int highestToken;
  private final boolean success;

  public PaxosResponse(int receivedToken, int highestToken, boolean success) {
    this.receivedToken = receivedToken;
    this.highestToken = highestToken;
    this.success = success;
  }

  public int getReceivedToken() {
    return receivedToken;
  }

  public int getHighestToken() {
    return highestToken;
  }

  public boolean isSuccess() {
    return success;
  }
}
