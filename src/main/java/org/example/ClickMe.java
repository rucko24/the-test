package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author rubn
 */
public class ClickMe extends JFrame {

    private static final ScheduledExecutorService SCHEDULED = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private final JButton initButton = new JButton("Init");
    private final JButton stopButton = new JButton("Stop");
    private final JLabel jLabel = new JLabel("Delay");
    private final JComboBox<String> jComboBox = new JComboBox<>();
    private final JPanel jPanel = new JPanel();
    private static final int HEIGHT = 50;
    private static final int WIDTH = 250;
    private static final int TWO = 2;
    private int delay; //default delay
    private static FileLock fileLock;

    private TrayIcon trayIcon;

    public ClickMe() {
        this.initComponents();
    }

    private void initComponents() {
        this.initButton();
        this.fillComboBox();
        this.stopButton();

        this.jPanel.add(this.jLabel);
        this.jPanel.add(this.jComboBox);
        this.jPanel.add(this.initButton);
        this.jPanel.add(this.stopButton);

        this.jPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.jPanel.setToolTipText("ESCAPE to minimize with gray icon");
        jPanel.setBorder(BorderFactory.createEtchedBorder());

        super.setAlwaysOnTop(true);
        super.add(this.jPanel);
        super.setTitle("v1.7.2-1_SNAPSHOT");
        super.setSize(WIDTH, HEIGHT);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(true);
        super.pack();
        super.setLocation(0,0);
        super.setVisible(true);
        this.onWindowClosing();

        this.setupSystemTrayAndMinimize();
    }

    private void setupSystemTrayAndMinimize() {
        KeyStroke minimizeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(e -> {
            this.setExtendedState(JFrame.ICONIFIED);
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
                this.setVisible(true);
                this.setExtendedState(JFrame.NORMAL);
            });

            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Close");
            exitItem.addActionListener(e -> {
                SCHEDULED.shutdown();
                System.exit(0);
            });
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                JOptionPane.showMessageDialog(null, "The icon could not be added to the system tray.", "Error", JOptionPane.ERROR_MESSAGE);

            }

            this.addWindowStateListener(e -> {
                if (e.getNewState() == JFrame.ICONIFIED) {
                    this.setVisible(false);
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "System Tray is not supported on this system.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void initButton() {
        this.initButton.setEnabled(false);
        Robot robot = null;
        final AtomicReference<Robot> robotAtomicReference = new AtomicReference<>();
        try {
            robot = new Robot();
            robotAtomicReference.set(robot);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        initButton.setFocusPainted(false);
        initButton.addActionListener(e -> {
            final String item = jComboBox.getSelectedItem().toString();
            if(!item.equals(" ")) {
                this.initButton.setEnabled(false);
                this.stopButton.setEnabled(true);
                this.scheduledFuture = SCHEDULED.scheduleAtFixedRate(() -> {
                    final int[] center = this.center();
                    robotAtomicReference.get().mouseMove(center[0], center[1]);
                    robotAtomicReference.get().mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robotAtomicReference.get().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    robotAtomicReference.get().delay(delay);
                    final int[] leftMiddleCenter = this.leftMiddleCenter();
                    robotAtomicReference.get().mouseMove(0, leftMiddleCenter[0]);
                    robotAtomicReference.get().mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robotAtomicReference.get().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    robotAtomicReference.get().delay(delay);
                }, 0, delay, TimeUnit.MILLISECONDS);
            } else {
                JOptionPane.showMessageDialog(null, "Select delay", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void fillComboBox() {
        jComboBox.setToolTipText("Delay in seconds");
        Arrays.stream(new String[]{" ", "2", "3", "4", "5", "6"})
                .forEach(jComboBox::addItem);

        jComboBox.addItemListener(event -> {
            if(!event.getItem().toString().equals(" ")) {
                if(event.getItem().toString().equals("2")) {
                    delay = 2000;
                } else if(event.getItem().toString().equals("3")) {
                    delay = 3000;
                } else if(event.getItem().toString().equals("4")) {
                    delay = 4000;
                } else if(event.getItem().toString().equals("5")) {
                    delay = 5000;
                } else if(event.getItem().toString().equals("6")) {
                    delay = 6000;
                }
                this.stopButton.setEnabled(true);
                this.initButton.setEnabled(true);
            } else {
                this.stopButton.setEnabled(false);
                this.initButton.setEnabled(false);
            }
        });

    }

    private void stopButton() {
        this.stopButton.setFocusPainted(false);
        final Action performStop = new AbstractAction("stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                initButton.setEnabled(true);
                stopButton.setEnabled(false);
                if (Objects.nonNull(scheduledFuture)) {
                    scheduledFuture.cancel(true);
                }
            }
        };
        this.stopButton.setAction(performStop);
        this.stopButton.getActionMap().put("stop", stopButton.getAction());
        final InputMap inputMap = this.stopButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), "stop");
        this.stopButton.setToolTipText("You can use Ctrl + A to stop quickly.");

    }

    private int[] center() {
        final int ancho = Toolkit.getDefaultToolkit().getScreenSize().width / TWO;
        final int altura = Toolkit.getDefaultToolkit().getScreenSize().height / TWO;
        return new int[]{ancho, altura};
    }

    private int[] leftMiddleCenter() {
        return new int[]{Toolkit.getDefaultToolkit().getScreenSize().height / TWO};
    }

    private void onWindowClosing() {
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                SCHEDULED.shutdown();
                super.windowClosing(event);
                System.exit(0);
            }
        });
    }

    public static void main(String... agrs) throws IOException {
        try {
            UIManager.getSystemLookAndFeelClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileChannel fileChannel = FileChannel.open(
                Paths.get(System.getProperty("java.io.tmpdir"), "test.lock"),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        );
        fileLock  = fileChannel.tryLock();
        if(fileLock == null) {
            JOptionPane.showMessageDialog(null, "The application is running.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Thread(ClickMe::new).start();

    }
}