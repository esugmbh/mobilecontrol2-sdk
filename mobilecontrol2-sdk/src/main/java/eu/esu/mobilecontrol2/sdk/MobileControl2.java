/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides key constant mapping and methods to access to the LEDs.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public final class MobileControl2 {
    /**
     * The stop / go button.
     */
    public final static int KEYCODE_STOP = KeyEvent.KEYCODE_BUTTON_1;
    /**
     * The top right button.
     */
    public final static int KEYCODE_TOP_RIGHT = KeyEvent.KEYCODE_BUTTON_2;
    /**
     * The bottom right button.
     */
    public final static int KEYCODE_BOTTOM_RIGHT = KeyEvent.KEYCODE_BUTTON_3;
    /**
     * The top left button.
     */
    public final static int KEYCODE_TOP_LEFT = KeyEvent.KEYCODE_VOLUME_UP;
    /**
     * The bottom left button.
     */
    public final static int KEYCODE_BOTTOM_LEFT = KeyEvent.KEYCODE_VOLUME_DOWN;
    /**
     * The red LED.
     */
    public final static int LED_RED = 1;
    /**
     * The greed LED.
     */
    public final static int LED_GREEN = 2;

    private final static String TAG = "Mobile Control II Input";
    private final static String ROOT_RED_LED = "/sys/class/leds/mc2:red:led1";
    private final static String ROOT_GREEN_LED = "/sys/class/leds/mc2:green:led2";

    private final static boolean RUNS_ON_MOBILECONTROL2 =
            Build.MODEL.equalsIgnoreCase("MobileControlII")
                    || Build.MODEL.equalsIgnoreCase("Mobile Control II")
                    || Build.MODEL.equalsIgnoreCase("SmartControl");

    private MobileControl2() {
    }

    /**
     * Returns if the app is running on a Mobile Control II or a compatible device.
     *
     * @return {@code true} if running on a Mobile Control II else {@code false}.
     */
    public static boolean isMobileControl2() {
        return RUNS_ON_MOBILECONTROL2;
    }

    /**
     * Turns a LED on or off.
     * <p/>
     * If not running on a Mobile Control II device this code does nothing.
     *
     * @param which The LED, {@link #LED_RED} or {@link #LED_GREEN}.
     * @param on    {@code true} for on, else {@code false}.
     */
    public static void setLedState(int which, boolean on) {
        if (!RUNS_ON_MOBILECONTROL2) {
            return;
        }

        try {
            writeText(ledPath(which, "trigger"), "none");
            writeText(ledPath(which, "brightness"), on ? "1" : "0");
        } catch (IOException ex) {
            Log.e(TAG, "Set brightness failed", ex);
        }
    }

    /**
     * Turns a LED on for flashing.
     * <p/>
     * If not running on a Mobile Control II device this code does nothing.
     *
     * @param which The LED, {@link #LED_RED} or {@link #LED_GREEN}.
     * @param onMillis  The number of milliseconds for the LED to be on while it's flashing.
     * @param offMillis The number of milliseconds for the LED to be off while it's flashing.
     */
    public static void setLedState(int which, int onMillis, int offMillis) {
        if (!RUNS_ON_MOBILECONTROL2) {
            return;
        }

        try {
            writeText(ledPath(which, "trigger"), "timer");
            writeText(ledPath(which, "delay_on"), Integer.toString(onMillis));
            writeText(ledPath(which, "delay_off"), Integer.toString(offMillis));
        } catch (IOException ex) {
            Log.e(TAG, "Set brightness failed", ex);
        }
    }

    private static String ledPath(int which, String fileName) {
        String root;

        switch (which) {
            case LED_RED:
                root = ROOT_RED_LED;
                break;
            case LED_GREEN:
                root = ROOT_GREEN_LED;
                break;
            default:
                throw new IllegalArgumentException("\"which\" must be LED_RED or LED_GREEN.");
        }

        return root + "/" + fileName;
    }

    private static void writeText(String fileName, String text) throws IOException {
        FileOutputStream fs = null;

        try {
            fs = new FileOutputStream(fileName);
            fs.write(text.getBytes());
            fs.flush();
        } catch (IOException ex) {
            Log.e(TAG, "Write to file failed", ex);
            throw new IOException("Write to file failed.", ex);
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
