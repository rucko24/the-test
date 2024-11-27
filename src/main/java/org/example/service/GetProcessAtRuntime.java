package org.example.service;

import org.example.ClickMe;
import org.example.util.GetOperatingSystem;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

/**
 * Here the logic applies depending on the operating system.
 */
public class GetProcessAtRuntime {

    public static final String TEST_JAR = "test.jar";

    public void execute() {
        final GetOperatingSystem osInfo = GetOperatingSystem.getOsInfo();

        if (osInfo == GetOperatingSystem.WINDOWS) {

            final ProcessContext processContext = new ProcessContext(new WindowsProcessStrategyImpl());
            processContext.execute();

        } else if (osInfo == GetOperatingSystem.LINUX) {

            final ProcessContext processContext = new ProcessContext(new LinuxProcessStrategyImpl());
            processContext.execute();

        } else if (osInfo == GetOperatingSystem.MAC) {

            this.detectMacProcess();

        } else if (osInfo == GetOperatingSystem.FREEBSD) {

            this.detectFreeBSDProcess();
        } else {
            this.otherOs();
        }

    }

    private void detectMacProcess() {
        JOptionPane.showMessageDialog(null, "Not implemented yet", "Information message", JOptionPane.INFORMATION_MESSAGE);
    }

    private void detectFreeBSDProcess() {
        JOptionPane.showMessageDialog(null, "Not implemented yet", "Information message", JOptionPane.INFORMATION_MESSAGE);
    }

    private void otherOs() {
        JOptionPane.showMessageDialog(null, "Os not found", "Error", JOptionPane.ERROR_MESSAGE);
    }

}
