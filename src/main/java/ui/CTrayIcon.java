package main.java.ui;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.net.URL;

import javax.swing.ImageIcon;

import main.java.util.Output;


public class CTrayIcon extends TrayIcon {

  public CTrayIcon(Image image) {
    super(image);
  }

  public CTrayIcon(Image image, String tooltip) {
    super(image, tooltip);
  }

  public CTrayIcon(Image image, String tooltip, PopupMenu popup) {
    super(image, tooltip, popup);
  }

  public static Image getTrayIconOK() {
    return createImage("images/green.gif", "tray icon green");
  }

  public static Image getTrayIconWARN() {
    return createImage("images/yellow.gif", "tray icon yellow");
  }

  public static Image getTrayIconNOK() {
    return createImage("images/red.gif", "tray icon red");
  }

  protected static Image createImage(String path, String description) {
    URL imageURL = CTrayIcon.class.getClassLoader().getResource(path);

    if (imageURL == null) {
      Output.outputError("Resource not found: " + path);
      return null;
    } else {
      return (new ImageIcon(imageURL, description)).getImage();
    }
  }
}
