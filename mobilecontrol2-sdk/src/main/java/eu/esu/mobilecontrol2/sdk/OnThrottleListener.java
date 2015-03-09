/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

/**
 * Listener interface for throttle callbacks.
 */
public interface OnThrottleListener {

    /**
     * Invoked after the button changed its state to down.
     */
    void onButtonDown();

    /**
     * Invoked after the button changed its state to up.
     */
    void onButtonUp();

    /**
     * Invoked after the throttle position has changed.
     *
     * @param position The new position.
     */
    void onPositionChanged(int position);
}