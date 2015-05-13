/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Message;

/**
 * Utility class for the ESU Input Services.
 */
class InputServices {
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
     * The Mobile Control II input service package ID.
     */
    public static final String SERVICE_PACKAGE = "eu.esu.mobilecontrol2.input";

    /**
     * Returns if the service is installed on the current device.
     *
     * @param context The current application context.
     */
    public static boolean isInstalled(Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(InputServices.SERVICE_PACKAGE, 0);
            return true;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
