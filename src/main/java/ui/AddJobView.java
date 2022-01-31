package main.java.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Duration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.java.dws.Schedule;
import main.java.dws.Scheduler;
import main.java.util.Persistence;


public class AddJobView extends JFrame {

  private final Scheduler scheduler;
  private JTextField urlField;
  private JLabel urlLabel;
  private JPanel mainPanel;
  private JButton addJobBtn;
  private JTextField intervalField;
  private JLabel intervalLabel;
  private boolean intervalValid = false;

  public AddJobView(String title, Scheduler scheduler) {
    super(title);
    this.scheduler = scheduler;
    this.setContentPane(mainPanel);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.pack();

    urlField.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (urlField.getText().isEmpty()) {
          urlField.setBackground(Color.RED);
        } else {
          urlField.setBackground(Color.white);
        }
      }
    });

    intervalField.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyReleased(KeyEvent e) {
        checkIfIntervalValid();
      }
    });

    addJobBtn.addActionListener(e -> {
      checkIfIntervalValid();
      if (urlField.getText().isEmpty()) {
        urlField.setBackground(Color.RED);
      } else {
        if (intervalValid) {
          String durationStrng = "PT" + intervalField.getText();
          Schedule add = new Schedule(urlField.getText(), Duration.parse(durationStrng));
          scheduler.addSchedule(add, true);
          Persistence.save(scheduler);
          this.dispose();
        }
      }
    });
  }

  private void checkIfIntervalValid() {
    boolean isValid = true;
    String durationStrng = "PT" + intervalField.getText();
    try {
      Duration.parse(durationStrng);
    } catch (Exception ignored) {
      isValid = false;
    }
    intervalValid = isValid;
    intervalField.setBackground(intervalValid ? Color.white : Color.RED);
  }

  public void resetView() {
    this.urlField.setText("");
    this.intervalField.setText("");
    this.urlField.setBackground(Color.WHITE);
    this.intervalField.setBackground(Color.WHITE);
  }


}