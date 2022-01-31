package main.java.ui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import main.java.dws.Schedule;
import main.java.dws.Scheduler;
import main.java.util.Persistence;

public class SchedulesView extends JFrame {

  private final Scheduler scheduler;
  private JButton addJobBtn;
  private JButton settingsBtn;
  private JList jobList;
  private JPanel mainPanel;
  private JButton deleteJobBtn;
  private JButton editJobBtn;
  private DefaultListModel jobModel;


  public SchedulesView(String title, Scheduler scheduler) {
    super(title);
    this.scheduler = scheduler;
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setContentPane(mainPanel);
    this.pack();
    jobModel = new DefaultListModel();
    jobList.setModel(jobModel);

    addJobBtn.addActionListener(e -> {
      scheduler.getAddJobFrame().resetView();
      scheduler.getAddJobFrame().setVisible(true);
    });

    deleteJobBtn.addActionListener(e -> {
      scheduler.deleteJob(jobList.getSelectedIndex());
    });

    jobList.addMouseListener(new MouseAdapter() {

      public void mouseClicked(MouseEvent evt) {
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
          // Double-click detected
          int index = list.locationToIndex(evt.getPoint());
          Schedule schedule = scheduler.getSchedules().get(index);
          if (schedule.getLastResult() != null) {
            ScheduleResult scheduleResultFrame = new ScheduleResult("Result", schedule);
            scheduleResultFrame.setLocationRelativeTo(null);
            scheduleResultFrame.setResizable(false);
            scheduleResultFrame.setMinimumSize(new Dimension(550, 300));
            scheduleResultFrame.setVisible(true);
          }
        }
      }
    });

    settingsBtn.addActionListener(e -> {
      Persistence.openConfigWithOSHandler();
    });
  }

  public JButton getAddJobBtn() {
    return addJobBtn;
  }

  public JButton getDeleteJobBtn() {
    return settingsBtn;
  }

  public JList getJobList() {
    return jobList;
  }

  // TODO refactor this mess, maybe show last status code?
  public void syncJobs() {
    jobModel.removeAllElements();
    for (Schedule s : scheduler.getSchedules()) {
      String resChar;
      if (s.getLastResult() == null) {
        resChar = "?";
      } else {
        resChar = s.getLastResult().wasSuccess() ? new String(Character.toChars(10003)) : "X";
      }

      jobModel.addElement(resChar + "  - " + s.getUrl());
    }
  }

  public void updateJobs() {
    int i = 0;
    for (Schedule s : scheduler.getSchedules()) {
      String resChar;
      if (s.getLastResult() == null) {
        resChar = "?";
      } else {
        resChar = s.getLastResult().wasSuccess() ? new String(Character.toChars(10003)) : "X";
      }
      jobModel.setElementAt(resChar + "  - " + s.getUrl(), i++);
    }
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public JButton getEditJobBtn() {
    return editJobBtn;
  }
}
