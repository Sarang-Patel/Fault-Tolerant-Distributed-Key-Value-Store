import java.io.Serializable;

/**
 * Class represents paxos request message.
 */
public class PaxosMessage implements Serializable {

  private final int proposalNumber;
  private final String value;

  public PaxosMessage(int proposalNumber, String value) {
    this.proposalNumber = proposalNumber;
    this.value = value;
  }

  public int getProposalNumber() {
    return proposalNumber;
  }

  public String getValue() {
    return value;
  }
}
