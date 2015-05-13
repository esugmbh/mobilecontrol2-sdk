/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.content.Intent;
import android.os.Message;

/**
 * A fragment which provides access to the Mobile Control II Stop key.
 * <p/>
 * This fragment handles the communication with the Stop Button Service. If the ESU Input Services package is not installed
 * all methods will not perform any action so that the fragment just works if running on another device.
 * <p/>
 * <h3>Usage:</h3>
 * Add the fragment to the activity and set the {@see OnStopButtonListener}.
 * <pre> {@code
 * protected void onCreate(Bundle savedInstanceState) {
 *     ...
 *     mStopButtonFragment = StopButtonFragment.newInstance();
 *     mStopButtonFragment.setOnStopButtonListener(mOnStopButtonListener);
 *     getSupportFragmentManager().beginTransaction()
 *         .add(mStopButtonFragment, "mc2:stopButton")
 *         .commit();
 * }
 * }
 * <p/>
 * </pre>
 */
public class StopButtonFragment extends MessageServiceFragment {

    /**
     * Callback message  when the stop button is pressed.
     */
    private static final int MSG_STOP_BUTTON_DOWN = 3;
    /**
     * Callback message when the stop button is released.
     */
    private static final int MSG_STOP_BUTTON_UP = 4;

    private OnStopButtonListener mListener;

    /**
     * Creates a new instance of the {@see StopButtonFragment} class.
     */
    public static StopButtonFragment newInstance() {
        return new StopButtonFragment();
    }

    /**
     * Sets the {@see OnStopButtonListener} for the fragment.
     *
     * @param listener The listener.
     */
    public void setOnStopButtonListener(OnStopButtonListener listener) {
        mListener = listener;
    }

    @Override
    protected Intent getServiceIntent() {
        return new Intent("eu.esu.mobilecontrol2.input.STOP_BUTTON_SERVICE");
    }

    @Override
    protected void onMessageReceived(Message message) {
        if (mListener != null) {
            switch (message.what) {
                case MSG_STOP_BUTTON_DOWN:
                    mListener.onStopButtonDown();
                    break;
                case MSG_STOP_BUTTON_UP:
                    mListener.onStopButtonUp();
                    break;
            }
        }
    }

    /**
     * Listener interface for the stop button fragment.
     */
    public interface OnStopButtonListener {
        /**
         * Invoked when the stop button is pressed.
         */
        void onStopButtonDown();

        /**
         * Invoked when the stop button is released.
         */
        void onStopButtonUp();
    }
}
