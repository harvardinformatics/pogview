package pogvue.util;

import java.io.PrintStream;

public final class ErrorLog {
  private static final PrintStream errStream = System.err;
  static final PrintStream teeFile = null;

  private ErrorLog() {}

  private static void println(String str) {
    errStream.println("Error Logger: " + str);
  }

  public static void main(String [] argv) {
    ErrorLog.println("ERROR");
  }
}
