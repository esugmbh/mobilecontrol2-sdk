/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.ref.WeakReference;

import static eu.esu.mobilecontrol2.sdk.InputServices.MSG_REGISTER_CLIENT;
import static eu.esu.mobilecontrol2.sdk.InputServices.MSG_UNREGISTER_CLIENT;

/**
 * Base class for fragments that communicate with a message-based bindable service.
 */
abstract class MessageServiceFragment extends Fragment {
    private Messenger mSender;
    private Messenger mReceiver;
    private boolean mServiceBound;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSender = new Messenger(service);
            final Message register = Message.obtain(null, MSG_REGISTER_CLIENT);
            register.replyTo = mReceiver;
            sendMessage(register);

            mServiceBound = true;
            MessageServiceFragment.this.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new Messenger(new IncomingMessageHandler(new WeakReference<>(this)));

        if (InputServices.isInstalled(getActivity())) {
            getActivity().bindService(
                    getServiceIntent(),
                    mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        if (mServiceBound) {
            final Message message = Message.obtain(null, MSG_UNREGISTER_CLIENT);
            message.replyTo = mReceiver;
            sendMessage(message);

            getActivity().unbindService(mConnection);
        }
        super.onDestroy();
    }

    protected boolean isServiceBound() {
        return mServiceBound;
    }

    /**
     * Returns the service intent.
     */
    protected abstract Intent getServiceIntent();

    /**
     * Sends a message to the service.
     *
     * @param message The message.
     */
    protected void sendMessage(Message message) {
        try {
            mSender.send(message);
        } catch (final RemoteException ex) {
            Log.e("EsuInputServices", "Failed to send message", ex);
        }
    }

    /**
     * Invoked after the service is connected.
     */
    protected void onServiceConnected() {
    }

    /**
     * Invoked after a message has been received from the service.
     *
     * @param message The message.
     */
    protected void onMessageReceived(Message message) {
    }

    private static class IncomingMessageHandler extends Handler {
        private final WeakReference<MessageServiceFragment> mParent;

        public IncomingMessageHandler(WeakReference<MessageServiceFragment> parent) {
            mParent = parent;
        }

        @Override
        public void handleMessage(Message msg) {
            MessageServiceFragment parent = mParent.get();
            if (parent == null) {
                throw new AssertionError("parent is null");
            }

            if (parent.isResumed()) {
                parent.onMessageReceived(msg);
            }
        }
    }
}
