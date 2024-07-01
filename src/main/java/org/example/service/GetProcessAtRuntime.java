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

    private static final String TEST_JAR = "test.jar";

    private static final String[] SEARCH_JAVAW_PROCESS_NAME_USING_WMIC = {"cmd", "/c", "wmic", "Path", "win32_process", "Where", "\"CommandLine Like '%test.jar%'\""};

    private static final String[] SEARCH_JAVAW_PROCESS_NAME_USING_PS_ELF = {"/bin/sh", "-c", "ps -elf | grep -v grep | grep 'test.jar'"};

    public void execute() {
        final GetOperatingSystem osInfo = GetOperatingSystem.getOsInfo();

        if (osInfo == GetOperatingSystem.WINDOWS) {

            this.detectTheJavaProcessByName(line -> line.startsWith("javaw.exe") && line.contains(TEST_JAR), SEARCH_JAVAW_PROCESS_NAME_USING_WMIC);

        } else if (osInfo == GetOperatingSystem.LINUX) {

            this.detectTheJavaProcessByName(line -> line.contains(TEST_JAR), SEARCH_JAVAW_PROCESS_NAME_USING_PS_ELF);
        } else if (osInfo == GetOperatingSystem.MAC) {

            this.detectMacProcess();
        } else if (osInfo == GetOperatingSystem.FREEBSD) {

            this.detectFreeBSDProcess();
        } else {
            this.otherOs();
        }

    }

    private void detectTheJavaProcessByName(Predicate<String> predicate, String... command) {

        try (final InputStream inputStream = Runtime.getRuntime().exec(command).getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            boolean isTestJarExists = bufferedReader.lines()
                    .peek(System.out::println)
                    .filter(predicate)
                    .count() > 1;

            if (!isTestJarExists) {
                new Thread(ClickMe::new).start();
            } else {
                JOptionPane.showMessageDialog(null, "El proceso existe", "Error", JOptionPane.ERROR_MESSAGE);
            }


        } catch (IOException e) {
            e.printStackTrace();
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
