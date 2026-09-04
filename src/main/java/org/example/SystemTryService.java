package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static org.example.ClickMe.SCHEDULED;

public class SystemTryService {

    private TrayIcon trayIcon;
    private final JFrame jFrame;

    public SystemTryService(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    public void setupSystemTrayAndMinimize() {
        KeyStroke minimizeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.jFrame.getRootPane().registerKeyboardAction(e -> {
            this.jFrame.setExtendedState(JFrame.ICONIFIED);
        }, minimizeKey, JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 16, 16);
            g.dispose();

            trayIcon = new TrayIcon(image, "");
            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(e -> {
                this.jFrame.setVisible(true);
                this.jFrame.setExtendedState(JFrame.NORMAL);
            });

            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Close");
            exitItem.addActionListener(e -> {
                SCHEDULED.shutdown();
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            });
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                JOptionPane.showMessageDialog(null, "The icon could not be added to the system tray.", "Error", JOptionPane.ERROR_MESSAGE);

            }

            this.jFrame.addWindowStateListener(e -> {
                if (e.getNewState() == JFrame.ICONIFIED) {
                    this.jFrame.setVisible(false);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "System Tray is not supported on this system.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
