/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;

/**
 * Provides simple access the throttle.
 * <p>
 * This fragment handles the communication with the throttle service. If the ESU Input Services package is not installed
 * all methods will do nothing so that the fragment just works if running on another device.
 * </p>
 * <h3>Usage:</h3>
 * Add the fragment to the activity and set the {@link eu.esu.mobilecontrol2.sdk.ThrottleFragment.OnThrottleListener}.
 * <pre> {@code
 * protected void onCreate(Bundle savedInstanceState) {
 *     ...
 *     mThrottleFragment = ThrottleFragment.newInstance(1);
 *     mThrottleFragment.setOnThrottleListener(mOnThrottleListener);
 *     getSupportFragmentManager().beginTransaction()
 *             .add(mThrottleFragment, "mc2:throttle")
 *             .commit();
 * }
 * }
 * </pre>
 */
public class ThrottleFragment extends MessageServiceFragment {

    /**
     * Key event used to wake up the device.
     * <p>
     * Since {@link android.view.KeyEvent#KEYCODE_WAKEUP} is not available before api level 20 (KITKAT),
     * {@link android.view.KeyEvent#KEYCODE_BUTTON_16} is used. To avoid unexpected input events ignore this key in your
     * activity.
     * </p>
     *
     * Example:
     * <pre> {@code
     * public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
     *     if (keyEvent == ThrottleFragment.KEYCODE_THROTTLE_WAKEUP) {
     *         return true;
     *     }
     *     return super.onKeyDown(keyCode, keyEvent);
     * }}
     * </pre>
     */
    public static final int KEYCODE_THROTTLE_WAKEUP = KeyEvent.KEYCODE_BUTTON_16;

    /**
     * Message to change the throttle position. Set {@link Message#arg1} to the position. Range: 0 - 255.
     */
    private static final int MSG_MOVE_TO = 3;

    /**
     * Message to set the zero position of the throttle. Set {@link Message#arg1} to the position. Range: 0 - 255.
     */
    private static final int MSG_SET_ZERO_POSITION = 4;

    /**
     * Callback message when the position has changed by user input,
     * {@link Message#arg1} contains the new throttle position. Range: 0 - 126
     */
    private static final int MSG_POSITION_CHANGED = 5;

    /**
     * Callback message when the button is pressed.
     */
    private static final int MSG_BUTTON_DOWN = 6;

    /**
     * Callback message when the button is released.
     */
    private static final int MSG_BUTTON_UP = 7;

    /**
     * Callback when the phisical slider's position has changed by user input,
     * {@link Message#arg1} contains the new slider position.
     */
    public static final int MSG_PHYSICAL_SLIDER_POSITION_CHANGED = 8;

    private int mZeroPosition;
    private int mLastPosition;
    private OnThrottleListener mOnThrottleListener;

    /**
     * Creates a new instance of the {@link eu.esu.mobilecontrol2.sdk.ThrottleFragment} class.
     * <p>
     * The {@code zeroPosition} defines the lowest position where the throttle may stop. If the current position is
     * lower than {@code zeroPosition} the throttle will try to move to the position.
     * </p>
     *
     * @param zeroPosition The zeroPosition.
     * @return A new throttle fragment instance.
     */
    public static ThrottleFragment newInstance(int zeroPosition) {
        Bundle args = new Bundle();
        args.putInt("zeroPosition", checkPosition(zeroPosition));

        ThrottleFragment fragment = new ThrottleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static int checkPosition(int position) {
        if (position < 0 || position > 255) {
            throw new IllegalArgumentException("position must be >= 0 and <= 255");
        }

        return position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mZeroPosition = getArguments().getInt("zeroPosition");
    }

    /**
     * Returns the last known position.
     *
     * @return The last known position.
     */
    public int getLastPosition() {
        return mLastPosition;
    }

    /**
     * Moves the throttle.
     *
     * @param position The new throttle position, range 0 - 255.
     * @throws java.lang.IllegalArgumentException "position" is out of range.
     */
    public void moveThrottle(int position) {
        if (isServiceBound()) {
            final Message msg = Message.obtain(null, MSG_MOVE_TO, checkPosition(position), 0);
            sendMessage(msg);
            mLastPosition = position;
        }
    }

    /**
     * Sets the listener to receive callbacks from the fragment.
     *
     * @param listener The listener.
     */
    public void setOnThrottleListener(OnThrottleListener listener) {
        mOnThrottleListener = listener;
    }

    /**
     * Return the current zero position.
     *
     * @return The current zero position.
     */
    public int getZeroPosition() {
        return mZeroPosition;
    }

    /**
     * Sets the current zero position.
     *
     * @param position The new zero position.
     */
    public void setZeroPosition(int position) {
        mZeroPosition = checkPosition(position);
        if (isServiceBound()) {
            sendMessage(Message.obtain(null, MSG_SET_ZERO_POSITION, position, 0));
        }
    }

    @Override
    protected void onServiceConnected() {
        setZeroPosition(mZeroPosition);
    }

    @Override
    protected void onMessageReceived(Message message) {
        if (mOnThrottleListener != null) {
            switch (message.what) {
                case MSG_BUTTON_DOWN:
                    onButtonDown();
                    break;
                case MSG_BUTTON_UP:
                    onButtonUp();
                    break;
                case MSG_POSITION_CHANGED:
                    onPositionChanged(message.arg1);
                    break;
                case MSG_PHYSICAL_SLIDER_POSITION_CHANGED:
                    onPhysicalSliderPositionChanged(message.arg1);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected Intent getServiceIntent() {
        Intent intent = new Intent("eu.esu.mobilecontrol2.input.THROTTLE_SERVICE");
        intent.setPackage(InputServices.SERVICE_PACKAGE);
        return intent;
    }

    private void onButtonDown() {
        if (mOnThrottleListener != null) {
            mOnThrottleListener.onButtonDown();
        }
    }

    private void onButtonUp() {
        if (mOnThrottleListener != null) {
            mOnThrottleListener.onButtonUp();
        }
    }

    private void onPositionChanged(int position) {
        if (mOnThrottleListener != null) {
            mLastPosition = position;
            mOnThrottleListener.onPositionChanged(position);
        }
    }

    private void onPhysicalSliderPositionChanged(int pos) {
        if (mOnThrottleListener != null) {
            mOnThrottleListener.onPhysicalSliderPositionChanged(pos);
        }
    }

    /**
     * Listener interface for throttle callbacks.
     */
    public interface OnThrottleListener {

        /**
         * Invoked after the button has been pressed.
         */
        void onButtonDown();

        /**
         * Invoked after the button has been released.
         */
        void onButtonUp();

        /**
         * Invoked after the throttle position has changed.
         *
         * @param position The new position.
         */
        void onPositionChanged(int position);

        /**
         * Invoked after the physical slider's position has changed by the user.
         *
         * @param position The new position.
         */
        void onPhysicalSliderPositionChanged(int position);
    }
}