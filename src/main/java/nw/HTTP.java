package main.java.nw;

import java.time.Instant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import main.java.dws.Schedule;
import main.java.util.Output;

public class HTTP {

  private static String userAgent;

  private HTTP() {
  }

  public static void doGet(Schedule schedule) {
    Output.output("GET " + schedule.getUrl());
    String url = schedule.getUrl();
    if (!schedule.getUrl().toLowerCase().startsWith("https://") && !schedule.getUrl()
        .toLowerCase()
        .startsWith("http://")) {
      url = "https://" + schedule.getUrl();
    }
    HTTPResult hpres = new HTTPResult();
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(url);
      request.addHeader(HttpHeaders.USER_AGENT, userAgent);

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        hpres.setStatusCode(response.getStatusLine().getStatusCode());

        HttpEntity entity = response.getEntity();
        if (entity != null) {
          String result = EntityUtils.toString(entity);
          hpres.setBody(result);
        }
      }
    } catch (Exception e) {
      Output.outputError(e.getMessage());
      hpres.setErrorString(e.getMessage());
    } finally {
      hpres.setFinished(Instant.now());
      schedule.setLastResult(hpres);
      schedule.setLastRunNow();
    }
  }

  public static String getUserAgent() {
    return userAgent;
  }

  public static void setUserAgent(String userAgent) {
    HTTP.userAgent = userAgent;
  }

}
