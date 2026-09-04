package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupGlobalHotKeysService {

    public void setupGlobalHotkeys(Runnable stopAction) {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            JOptionPane.showMessageDialog(null, "Problem registering JNativeHook.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                if (e.getKeyCode() == NativeKeyEvent.VC_A && (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
                    stopAction.run();
                }
            }
        });
    }

}
