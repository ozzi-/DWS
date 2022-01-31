package main.java.dws;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.nw.HTTPResult;
import main.java.ui.AddJobView;
import main.java.ui.CTrayIcon;
import main.java.ui.SchedulesView;
import main.java.util.Output;
import main.java.util.Persistence;

public class Scheduler implements Runnable {

  private SchedulesView jobsFrame;
  private AddJobView addJobFrame;
  private CTrayIcon trayIcon;
  public volatile boolean shutdown = false;
  final List<Schedule> schedules;
  private Duration clock = Duration.parse("PT1S");

  public Scheduler() {
    schedules = Collections.synchronizedList(new ArrayList<>());
  }

  public List<Schedule> getSchedules() {
    return schedules;
  }

  public void setUI(SchedulesView jobsFrame, AddJobView addJobFrame, CTrayIcon trayIcon) {
    this.jobsFrame = jobsFrame;
    this.addJobFrame = addJobFrame;
    this.trayIcon = trayIcon;
  }

  public void addSchedule(Schedule scheduleToAdd, boolean persist) {
    schedules.add(scheduleToAdd);
    jobsFrame.syncJobs();
    if (persist) {
      Persistence.save(this);
    }
  }


  public AddJobView getAddJobFrame() {
    return addJobFrame;
  }


  @Override
  public void run() {
    while (!shutdown) {
      sleep((int) clock.toMillis());
      Output.output("dws.Scheduler running . . ." + Instant.now() + " - " + schedules.size());
      synchronized (schedules) {
        jobsFrame.updateJobs();
        schedules.stream().filter(Schedule::needsToRun).forEach(Schedule::run);
        schedules.stream().filter(Schedule::hasNewResult).forEach(this::handle);
        int failCount = 0;
        // TODO mess
        for (int i = 0; i < schedules.size(); i++) {
          if (schedules.get(i).getLastResult() != null) {
            if (!schedules.get(i).getLastResult().wasSuccess()) {
              failCount++;
            }
          }
        }
        if (failCount > 0) {
          if (failCount == schedules.size()) {
            trayIcon.setImage(CTrayIcon.getTrayIconNOK());
          } else {
            trayIcon.setImage(CTrayIcon.getTrayIconWARN());
          }
        } else {
          trayIcon.setImage(CTrayIcon.getTrayIconOK());
        }
      }
    }
  }


  // TODO evaluate results!
  private void handle(Schedule schedule) {
    HTTPResult lastResult = schedule.getLastResult();
    schedule.markLastResultHandled();
    trayIcon.setToolTip("Last job: " + schedule.getUrl() + " - " + lastResult.getStatusCode());
    Output.output("Status code=" + lastResult.getStatusCode());
    if (lastResult.getStatusCode() < 200 || lastResult.getStatusCode() >= 299) {
      if (!schedule.hasReportedError()) {
        DWS.toastError("Response Code " + lastResult.getStatusCode() + " for " + schedule.getUrl());
        schedule.setReportedError(true);
      }
    } else {
      schedule.setReportedError(false);
    }
  }

  private void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignored) {
    }
  }

  public void setTrayIcon(CTrayIcon trayIcon) {
    this.trayIcon = trayIcon;
  }


  public void deleteJob(int selectedIndex) {
    synchronized (schedules) {
      if (selectedIndex >= 0 && selectedIndex <= schedules.size()) {
        schedules.remove(selectedIndex);
        jobsFrame.syncJobs();
        Persistence.save(this);
      } else {
        Output.outputError("Can't delete job with unknown ID = " + selectedIndex);
      }
    }
  }

  public void setClock(Duration clock) {
    this.clock = clock;
  }

  public Duration getClock() {
    return clock;
  }


}
