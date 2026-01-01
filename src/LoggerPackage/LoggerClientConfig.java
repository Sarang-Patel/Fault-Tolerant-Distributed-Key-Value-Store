package LoggerPackage;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * This class represents the custom log config for the client. This removes the default handlers and
 * adds custom ones.
 */
public class LoggerClientConfig {

  /**
   * Logger sets the custom logging configuration for the client side.
   *
   * @throws IOException exception.
   */
  public void setLogger() throws IOException {
    Logger logger = Logger.getLogger("ClientLog");

    logger.setUseParentHandlers(false);

    for (var handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }

    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new LoggerCustomFormatter());
    logger.addHandler(consoleHandler);

    FileHandler fileHandler = new FileHandler("clientLogFile.log", true);
    fileHandler.setFormatter(new LoggerCustomFormatter());
    logger.addHandler(fileHandler);
  }

}
