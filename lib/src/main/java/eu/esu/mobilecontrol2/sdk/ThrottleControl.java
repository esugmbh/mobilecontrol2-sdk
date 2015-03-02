package eu.esu.mobilecontrol2.sdk;

/**
 * Interface to control the MobileControl II throttle.
 */
public interface ThrottleControl {
    /**
     * Moves the throttle to a position.
     */
    void moveThrottle(int position);

    /**
     * Sets the number of throttle steps.
     *
     * @param count The number of steps.
     */
    void setThrottleStepCount(int count);

    /**
     * Sets the on Throttle listener to receive callbacks.
     */
    void setOnThrottleListener(OnThrottleListener listener);
}
