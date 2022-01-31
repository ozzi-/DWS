package main.java.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import main.java.dws.Schedule;
import main.java.dws.Scheduler;
import main.java.nw.HTTP;

public class Persistence {

  public static String schedulesFileName = "schedules.json";
  public static String configFolderName = ".dws";
  public static String schedulesFilePath =
      System.getProperty("user.home") + File.separator + configFolderName + File.separator + schedulesFileName;


  public static void loadConfig(Scheduler scheduler) {
    File configFile = new File(schedulesFilePath);

    if (!configFile.getParentFile().exists()) {
      Output.output("Config file directory '" + configFolderName + "' does not exist - creating it");
      boolean mkdir = configFile.getParentFile().mkdir();
      if (!mkdir) {
        Output.outputError("Could not create directory '" + configFile.getParentFile().toString() + "'");
      }
    }

    if (!configFile.exists()) {
      Output.output("Config file '" + schedulesFilePath + "' does not exist - creating it");
      try {
        boolean configFileCreated = configFile.createNewFile();
        if (!configFileCreated) {
          throw new IOException("Create new file failed for config file");
        }
      } catch (IOException e) {
        Output.outputError(
            "Error creating config file '" + schedulesFilePath + "' - message:" + e.getMessage() + " - cause:"
                + e.getCause().toString());
        return;
      }
      String defaultConfigJSONRaw = generateDefaultConfigFile();
      try {
        Files.write(Paths.get(schedulesFilePath), defaultConfigJSONRaw.getBytes());
      } catch (IOException e) {
        Output.outputError(
            "Error writing config file '" + schedulesFilePath + "' - message:" + e.getMessage() + " - cause:"
                + e.getCause().toString());
      }
    }

    try {
      String configRaw = new String(Files.readAllBytes(Paths.get(schedulesFilePath)));
      JsonObject configJO = Json.parse(configRaw).asObject();

      JsonArray schedulesJA = configJO.get("schedules").asArray();
      for (JsonValue schedule : schedulesJA) {
        Schedule scheduleObj = Schedule.fromJSON(schedule.asObject());
        scheduler.addSchedule(scheduleObj, false);
      }

      String clock = configJO.get("clock").asString();
      scheduler.setClock(Duration.parse(clock));

      String useragent = configJO.get("useragent").asString();
      HTTP.setUserAgent(useragent);

    } catch (IOException e) {
      // TODO handle json parse errors if conf file is messed up
      e.printStackTrace();
      Output.outputError(e.getMessage());
    }
  }

  private static String generateDefaultConfigFile() {
    JsonObject configJO = new JsonObject();
    JsonArray schedules = new JsonArray();
    configJO.add("schedules", schedules);
    configJO.add("clock", "PT1S");
    configJO.add("useragent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return configJO.toString(WriterConfig.PRETTY_PRINT);
  }

  public static void save(Scheduler scheduler) {
    Output.output("Saving");
    JsonObject conf = new JsonObject();
    List<Schedule> schedules = scheduler.getSchedules();
    JsonArray schedulesJA = new JsonArray();
    schedules.stream().forEach(s -> schedulesJA.add(s.toJSON()));
    conf.add("schedules", schedulesJA);
    conf.add("clock", scheduler.getClock().toString());
    conf.add("useragent", HTTP.getUserAgent());
    String s = conf.toString(WriterConfig.PRETTY_PRINT);
    try {
      Files.write(Paths.get(schedulesFilePath), s.getBytes());
    } catch (IOException e) {
      Output.outputError(
          "Error writing config file '" + schedulesFilePath + "' - message:" + e.getMessage() + " - cause:"
              + e.getCause().toString());
    }
  }

  public static void openConfigWithOSHandler() {
    File file = new File(schedulesFilePath);
    Desktop desktop = Desktop.getDesktop();
    try {
      desktop.open(file);
    } catch (IOException e) {
      Output.outputError("Error using Desktop API - " + e.getMessage() + " - " + e.getCause());
    }
  }
}
