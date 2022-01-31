package main.java.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Output {

  private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

  public static void output(String out) {
    out = formatOutput(out);
    System.out.println(out);
  }

  public static void outputError(String out) {
    out = formatOutput(out);
    System.err.println(out);
  }

  private static String formatOutput(String out) {
    Date cal = Calendar.getInstance(TimeZone.getDefault()).getTime();
    out = sdf.format(cal.getTime()) + " | " + out;
    return out;
  }
}
