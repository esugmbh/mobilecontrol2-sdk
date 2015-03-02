package eu.esu.mobilecontrol2.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Message;

/**
 * Utility class for the Mobile Control II Throttle.
 */
public final class Throttle {

    /**
     * Command to the service to register a client. Callbacks are delivered to
     * the messenger defined in {@link Message#replyTo}.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to remove a registered client from the service.
     * The {@link Message#replyTo} field must be set to the messenger that shall
     * be removed.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to change the throttle position. Set {@link Message#arg1} to the
     * position. Range: 0 - 126.
     */
    public static final int MSG_MOVE_TO = 3;

    /**
     * Command to set the zero position of the throttle. Set
     * {@link Message#arg1} to the position. Range: 0 - 255.
     * <p/>
     * If the throttle ensures that the current position is greater that the
     * zero position.
     */
    public static final int MSG_SET_ZERO_POSITION = 4;

    /**
     * Sets the number of steps including 0. Range 0 - 127.
     */
    public static final int MSG_SET_STEP_COUNT = 8;

    /**
     * Callback that is invoked when the position has changed by user input,
     * {@link Message#arg1} contains the new throttle position. Range: 0 - 126
     */
    public static final int MSG_POSITION_CHANGED = 5;

    /**
     * Callback that is invoked when the button is pressed.
     */
    public static final int MSG_BUTTON_DOWN = 6;

    /**
     * Callback that is invoked when the button is released.
     */
    public static final int MSG_BUTTON_UP = 7;

    /**
     * The package mobile control II package URI.
     */
    public static final String SERVICE_PACKAGE = "eu.esu.mobilecontrol2.throttle";

    /**
     * Intent used to bind the throttle service.
     */
    public static final String INTENT_BIND_SERVICE = "eu.esu.mobilecontrol2.throttle.SERVICE";

    private Throttle() {
    }

    /**
     * Returns if the service is installed on the current device.
     *
     * @param context The current application context.
     */
    public static boolean isInstalled(Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(SERVICE_PACKAGE, 0);
            return true;
        } catch (final NameNotFoundException e) {
            return false;
        }
    }

}
