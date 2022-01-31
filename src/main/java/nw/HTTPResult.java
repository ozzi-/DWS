package main.java.nw;

import java.time.Instant;

public class HTTPResult {

  private int statusCode = -1;
  private String body;
  private String errorString = "";
  private Instant finished;

  HTTPResult() {
  }

  HTTPResult(int statusCode, String body, Instant finished) {
    this.statusCode = statusCode;
    this.body = body;
    this.finished = finished;
  }

  public HTTPResult setStatusCode(int statusCode) {
    this.statusCode = statusCode;
    return this;
  }


  public HTTPResult setBody(String body) {
    this.body = body;
    return this;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public boolean wasSuccess() {
    if (statusCode == -1) {
      return false;
    }
    return statusCode >= 200 && statusCode <= 299;
  }

  public String getBody() {
    return body;
  }

  public void setErrorString(String errorString) {
    this.errorString = errorString;
  }

  public String getErrorString() {
    return this.errorString;
  }

  public void setFinished(Instant finished) {
    this.finished = finished;
  }

  public Instant getFinished() {
    return finished;
  }
}
