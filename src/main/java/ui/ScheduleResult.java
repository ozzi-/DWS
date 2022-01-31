package main.java.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.java.dws.Schedule;

public class ScheduleResult extends JFrame {

  public static final int BODY_TRIM_LENGTH = 1000;
  private JTextField statusCodeField;
  private JTextField urlField;
  private JTextArea bodyField;
  private JPanel mainPanel;
  private JTextField finishedField;
  private JTextField exceptionField;

  public ScheduleResult(String title, Schedule schedule) {
    super(title);
    this.setContentPane(mainPanel);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.pack();

    int statusCode = schedule.getLastResult().getStatusCode();
    statusCodeField.setText(String.valueOf(statusCode));
    statusCodeField.setEnabled(false);

    String errorString = schedule.getLastResult().getErrorString();
    exceptionField.setText(errorString);
    exceptionField.setEnabled(false);

    if (schedule.getLastResult().getBody() != null) {
      String body = schedule.getLastResult().getBody();
      body = body.length() > BODY_TRIM_LENGTH ? body.substring(0, BODY_TRIM_LENGTH) : body;
      bodyField.setText(body);
    }
    bodyField.setEnabled(false);

    urlField.setText(schedule.getUrl());
    urlField.setEnabled(false);

    finishedField.setText(schedule.getLastResult().getFinished().toString());
    finishedField.setEnabled(false);
  }
}
