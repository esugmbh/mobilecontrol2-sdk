/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE.txt file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.ref.WeakReference;

import static eu.esu.mobilecontrol2.sdk.Throttle.INTENT_BIND_SERVICE;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_BUTTON_DOWN;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_BUTTON_UP;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_MOVE_TO;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_POSITION_CHANGED;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_REGISTER_CLIENT;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_SET_ZERO_POSITION;
import static eu.esu.mobilecontrol2.sdk.Throttle.MSG_UNREGISTER_CLIENT;

/**
 * Fragment which provides access the Mobile Control II Throttle service.
 * <p/>
 * This fragment handles the communication with the Mobile Control II throttle service. If throttle service has been
 * detected all methods will do nothing so that the fragment just works if not running on another device.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ThrottleFragment extends Fragment {

    /**
     * Key event used to wake up the device.
     * <p/>
     * Since {@link android.view.KeyEvent#KEYCODE_WAKEUP} is not available before api level 20 (KITKAT),
     * {@link android.view.KeyEvent#KEYCODE_BUTTON_16} is used. To avoid unexpected input events ignore this key in your
     * activity, for example:
     * <pre> {@code
     * public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
     *     if (keyEvent == ThrottleFragment.KEYCODE_THROTTLE_WAKEUP) {
     *         return true;
     *     }
     * <p/>
     *     return super.onKeyDown(keyCode, keyEvent);
     * }
     * </pre>
     */
    public static final int KEYCODE_THROTTLE_WAKEUP = KeyEvent.KEYCODE_BUTTON_16;

    private final static String TAG = "Mobile Control II Throttle";

    private final Messenger mReceiver = new Messenger(new IncomingMessageHandler(new WeakReference<>(this)));
    private Messenger mSender;
    private boolean mThrottleBound;
    private int mZeroPosition;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSender = new Messenger(service);
            final Message msgRegister = Message.obtain(null, MSG_REGISTER_CLIENT);
            msgRegister.replyTo = mReceiver;
            sendMessage(msgRegister);

            sendMessage(Message.obtain(null, MSG_SET_ZERO_POSITION, mZeroPosition, 0));
            mThrottleBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "Throttle service disconnected");
            mThrottleBound = false;
        }
    };

    private OnThrottleListener mOnThrottleListener;

    /**
     * Creates a new instance of the {@link eu.esu.mobilecontrol2.sdk.ThrottleFragment} class.
     * <p/>
     * The {@code zeroPosition} defines the lowest position where the throttle may stop. If the current position is
     * lower than {@code zeroPosition} the throttle will try to move to the position.
     *
     * @param zeroPosition The zeroPosition.
     * @return A new throttle fragment instance.
     */
    public static ThrottleFragment newInstance(int zeroPosition) {
        Bundle args = new Bundle();
        args.putInt("zeroPosition", checkPosition(zeroPosition));

        ThrottleFragment fragment =  new ThrottleFragment();
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

        mZeroPosition = getArguments().getInt("zeroPosition", 0);

        if (Throttle.isInstalled(getActivity())) {
            Log.d(TAG, "Found Mobile Control II Throttle, binding service.");
            final Intent intent = new Intent(INTENT_BIND_SERVICE);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        if (mThrottleBound) {
            Log.d(TAG, "Mobile Control II Throttle no longer needed, unbinding service.");
            final Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
            msg.replyTo = mReceiver;
            sendMessage(msg);

            getActivity().unbindService(mConnection);
        }

        super.onDestroy();
    }

    /**
     * Moves the throttle.
     *
     * @param position The new throttle position, range 0 - 255.
     * @throws java.lang.IllegalArgumentException "position" is out of range.
     */
    public void moveThrottle(int position) {
        if (mThrottleBound) {
            final Message msg = Message.obtain(null, MSG_MOVE_TO, checkPosition(position), 0);
            sendMessage(msg);
        }
    }

    public void setOnThrottleListener(OnThrottleListener listener) {
        mOnThrottleListener = listener;
    }

    private void sendMessage(Message msg) {
        try {
            mSender.send(msg);
        } catch (final RemoteException ex) {
            Log.e(TAG, "Message not sent to throttle.", ex);
        }
    }

    private static class IncomingMessageHandler extends Handler {
        private final WeakReference<ThrottleFragment> mParent;

        public IncomingMessageHandler(WeakReference<ThrottleFragment> parent) {
            mParent = parent;
        }

        @Override
        public void handleMessage(Message msg) {
            final ThrottleFragment parent = mParent.get();
            if (parent == null) {
                throw new NullPointerException();
            }

            final OnThrottleListener listener = parent.mOnThrottleListener;
            if (listener != null) {
                switch (msg.what) {
                    case MSG_BUTTON_DOWN:
                        listener.onButtonDown();
                        break;
                    case MSG_BUTTON_UP:
                        listener.onButtonUp();
                        break;
                    case MSG_POSITION_CHANGED:
                        listener.onPositionChanged(msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }
}
