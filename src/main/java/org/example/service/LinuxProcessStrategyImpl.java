package org.example.service;

import org.example.ClickMe;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

import static org.example.service.GetProcessAtRuntime.TEST_JAR;

/**
 * @author rubn
 */
public class LinuxProcessStrategyImpl implements ProcessStrategy {

    private static final String[] SEARCH_JAVAW_PROCESS_NAME_USING_PS_ELF = {"/bin/sh", "-c", "ps -elf | grep -v grep | grep 'test.jar'"};

    @Override
    public void execute() {

        this.detectTheJavaProcessByName(line -> line.contains(TEST_JAR), SEARCH_JAVAW_PROCESS_NAME_USING_PS_ELF);
    }

    private void detectTheJavaProcessByName(Predicate<String> predicate, String... command) {

        try (final InputStream inputStream = Runtime.getRuntime().exec(command).getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            boolean isTestJarExists = bufferedReader.lines()
                    .filter(predicate)
                    .peek(System.out::println)
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

}
