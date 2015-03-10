/*
 * Copyright (c) 2015 ESU electronic solutions ulm GmbH & Co KG
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package eu.esu.mobilecontrol2.sdk;

/**
 * Helper class to convert throttle positions to steps and vice versa.
 */
public class ThrottleScale {

    private int mZeroOffset;
    private int mStepCount;
    private int[] mLookup;

    /**
     * Creates a new {@link eu.esu.mobilecontrol2.sdk.ThrottleScale} instance.
     * <p/>
     * Positions between 0 and {@code zeroOffset} will return 0. Use zeroOffset to avoid unwanted
     *
     * @param zeroOffset The last position of the zero range.
     * @param stepCount  The number of steps including zero. Must be lower than (255 - {@code zeroOffset}).
     */
    public ThrottleScale(int zeroOffset, int stepCount) {
        if (zeroOffset < 0 || zeroOffset > 255) {
            throw new IllegalArgumentException("zeroOffset must be >= 0 and <= 255");
        }

        if (stepCount > (255 - zeroOffset)) {
            throw new IllegalArgumentException("More steps than available positions in range");
        }

        mZeroOffset = zeroOffset;
        mStepCount = stepCount;
        createLookup();
    }

    /**
     * Returns the corresponding throttle position of a step.
     *
     * @param step The step.
     * @return The throttle position.
     */
    public int stepToPosition(int step) {
        if (step >= mStepCount) {
            throw new IllegalArgumentException("step must be <= stepCount");
        }

        return mLookup[step];
    }

    /**
     * Returns the corresponding step of a throttle position.
     *
     * @param position The position.
     * @return The step.
     */
    public int positionToStep(int position) {
        for (int i = mStepCount - 1; i > 0; --i) {
            if (position > mLookup[i]) {
                return i;
            }
        }

        return 0;
    }

    private void createLookup() {
        mLookup = new int[mStepCount];
        final double chunkSize = (255.0 - mZeroOffset) / mStepCount;

        mLookup[0] = 0;
        for (int i = 1; i < mStepCount; ++i) {
            mLookup[i] = (int) Math.round(mZeroOffset + (i * chunkSize));
        }
    }
}
