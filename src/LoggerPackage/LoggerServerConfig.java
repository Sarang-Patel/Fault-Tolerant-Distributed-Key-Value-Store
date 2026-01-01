package LoggerPackage;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a log config file for the server. This removes the default handlers * and
 * adds custom ones.
 */
public class LoggerServerConfig {

  /**
   * Logger sets the custom logging configuration for the server side.
   *
   * @throws IOException exception.
   */
  public void setLogger() throws IOException {
    Logger logger = Logger.getLogger("ServerLog");
    logger.setLevel(Level.INFO);

    logger.setUseParentHandlers(false);

    for (var handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }

    ConsoleHandler consoleHandler = new ConsoleHandler();
    logger.setLevel(Level.INFO);
    consoleHandler.setFormatter(new LoggerCustomFormatter());
    logger.addHandler(consoleHandler);

    FileHandler fileHandler = new FileHandler("serverLogFile.log", true);
    logger.setLevel(Level.INFO);
    fileHandler.setFormatter(new LoggerCustomFormatter());
    logger.addHandler(fileHandler);
  }
}
