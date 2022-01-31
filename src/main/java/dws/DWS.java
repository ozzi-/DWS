package main.java.dws;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main.java.ui.AddJobView;
import main.java.ui.CTrayIcon;
import main.java.ui.SchedulesView;
import main.java.util.Output;
import main.java.util.Persistence;
 
public class DWS {

  public static final String APP_NAME = "dws";
  private static Scheduler scheduler;
  private static SchedulesView jobsFrame;
  private static CTrayIcon trayIcon;
  private static AddJobView addJobFrame;

  public static void main(String[] args) {


    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    UIManager.put("swing.boldMetal", Boolean.FALSE);
    scheduler = new Scheduler();

    initView();
    initSchedulesView();
    SwingUtilities.invokeLater(DWS::createAndShowTray);
    scheduler.setUI(jobsFrame, addJobFrame, trayIcon);
    scheduler.run();
  }

  public static void initView() {
    addJobFrame = new AddJobView("Add Job", scheduler);
    addJobFrame.setLocationRelativeTo(null);
    addJobFrame.setResizable(false);
    addJobFrame.setMinimumSize(new Dimension(350, 300));
    Image trayIconOK = CTrayIcon.getTrayIconOK();
    int trayIconWidth = new TrayIcon(trayIconOK).getSize().width;
    trayIcon = new CTrayIcon(trayIconOK.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
  }


  private static void initSchedulesView() {
    jobsFrame = new SchedulesView(APP_NAME, scheduler);
    jobsFrame.setLocationRelativeTo(null);
    jobsFrame.setResizable(false);
    jobsFrame.setIconImage(CTrayIcon.getTrayIconOK());
    jobsFrame.setMinimumSize(new Dimension(350, 300));
    jobsFrame.setVisible(false);
  }

  private static void createAndShowTray() {
    String sysTraySupportMsg = "SystemTray is not supported";
    if (!SystemTray.isSupported()) {
      JOptionPane.showMessageDialog(null, sysTraySupportMsg, "Error", JOptionPane.ERROR_MESSAGE);
      Output.outputError(sysTraySupportMsg);
      return;
    }
    final PopupMenu popup = new PopupMenu();
    final SystemTray tray = SystemTray.getSystemTray();
    trayIcon.setToolTip(APP_NAME);
    MenuItem schedules = new MenuItem("Schedules");
    MenuItem exitItem = new MenuItem("Exit");

    popup.add(schedules);
    popup.addSeparator();
    popup.add(exitItem);

    trayIcon.setPopupMenu(popup);
    trayIcon.setImageAutoSize(true);
    trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {

      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
          jobsFrame.setVisible(true);
        }
      }
    });

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      Output.outputError("TrayIcon could not be added.");
      return;
    }

    exitItem.addActionListener(e -> {
      tray.remove(trayIcon);
      scheduler.shutdown = true;
      Persistence.save(scheduler);
      System.exit(0);
    });

    schedules.addActionListener(e -> {
      jobsFrame.setVisible(true);
    });

    Persistence.loadConfig(scheduler);
  }

  public static void toastError(String message) {
    trayIcon.displayMessage(APP_NAME, message, TrayIcon.MessageType.ERROR);
  }

}