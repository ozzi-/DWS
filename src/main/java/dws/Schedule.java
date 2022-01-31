package main.java.dws;

import java.time.Duration;
import java.time.Instant;

import com.eclipsesource.json.JsonObject;

import main.java.nw.HTTP;
import main.java.nw.HTTPResult;

public class Schedule {

  private String url;
  private Duration interval;
  private Instant lastRun;
  private HTTPResult lastResult;
  private Boolean hasNewResult = false;
  private Boolean isRunning = false;
  private Boolean reportedError = false;

  public Schedule(String url, Duration interval) {
    this.url = url;
    this.interval = interval;
  }

  public boolean needsToRun() {
    if (lastRun == null && !isRunning) {
      return true;
    }
    return !isRunning && Instant.now().isAfter(lastRun.plus(interval));
  }

  public void setLastRunNow() {
    lastRun = Instant.now();
    isRunning = false;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void run() {
    isRunning = true;
    Thread thread = new Thread(() -> {
      HTTP.doGet(this);
      hasNewResult = true;
    });
    thread.start();
  }

  public Duration getInterval() {
    return interval;
  }

  public void setInterval(Duration interval) {
    this.interval = interval;
  }

  @Override
  public String toString() {
    return url;
  }

  public void setLastResult(HTTPResult lastResult) {
    this.lastResult = lastResult;
  }

  public HTTPResult getLastResult() {
    return lastResult;
  }

  public void markLastResultHandled() {
    hasNewResult = false;
  }

  public Boolean hasNewResult() {
    return hasNewResult;
  }

  public static Schedule fromJSON(JsonObject jo) {
    String url = jo.get("url").asString();
    Duration interval = Duration.parse(jo.get("interval").asString());
    // TODO error handling
    return new Schedule(url, interval);
  }

  public JsonObject toJSON() {
    JsonObject jo = new JsonObject();
    jo.add("url", url);
    jo.add("interval", getInterval().toString());
    return jo;
  }

  public Boolean hasReportedError() {
    return reportedError;
  }

  public void setReportedError(Boolean reportedError) {
    this.reportedError = reportedError;
  }
}
