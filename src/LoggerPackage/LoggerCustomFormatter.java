package LoggerPackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is a custom formatter for the logs.
 */

public class LoggerCustomFormatter extends Formatter {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss:SSS");

  /**
   * Format the given log record and return the formatted string.
   * <p>
   * The resulting formatted String will normally include a localized and formatted version of the
   * LogRecord's message field. It is recommended to use the {@link Formatter#formatMessage}
   * convenience method to localize and format the message field.
   *
   * @param record the log record to be formatted.
   * @return the formatted log record
   */
  @Override
  public String format(LogRecord record) {
    return dateFormat.format(new Date(record.getMillis())) +
        " -> " + formatMessage(record) + "\n";
  }
}
