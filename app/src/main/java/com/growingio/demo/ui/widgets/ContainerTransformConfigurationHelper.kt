/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.growingio.demo.ui.widgets

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform

/**
 * A helper class which manages all configuration UI presented.
 */
class ContainerTransformConfigurationHelper {
    /**
     * Whether or not to a custom container transform should use [ ].
     */
    var isArcMotionEnabled = false
        private set

    /**
     * The enter duration to be used by a custom container transform.
     */
    var enterDuration: Long = 0

    /**
     * The return duration to be used by a custom container transform.
     */
    var returnDuration: Long = 0

    /**
     * The interpolator to be used by a custom container transform.
     */
    var interpolator: Interpolator? = null
        private set
    private var fadeMode = 0

    /**
     * Whether or not the custom transform should draw debugging lines.
     */
    var isDrawDebugEnabled = false
        private set

    init {
        setUpDefaultValues()
    }

    /**
     * Show configuration chooser associated with a container transform
     */
    fun showConfigurationChooser(
        context: Context?,
        contentView: View?,
        onDismissListener: DialogInterface.OnDismissListener?
    ) {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        bottomSheetDialog.setContentView(contentView!!)
        bottomSheetDialog.setOnDismissListener(onDismissListener)
        bottomSheetDialog.show()
    }

    /**
     * Set up the androidx transition according to the config helper's parameters.
     */
    fun configure(transform: MaterialContainerTransform, entering: Boolean) {
        val duration = if (entering) enterDuration else returnDuration
        if (duration != NO_DURATION) {
            transform.duration = duration
        }
        if (interpolator != null) {
            transform.interpolator = interpolator
        }
        if (isArcMotionEnabled) {
            transform.setPathMotion(MaterialArcMotion())
        }
        transform.fadeMode = fadeMode
        transform.isDrawDebugEnabled = isDrawDebugEnabled
    }

    /**
     * Set up the platform transition according to the config helper's parameters.
     */
    fun configure(
        transform: com.google.android.material.transition.platform.MaterialContainerTransform,
        entering: Boolean
    ) {
        val duration = if (entering) enterDuration else returnDuration
        if (duration != NO_DURATION) {
            transform.duration = duration
        }
        if (interpolator != null) {
            transform.interpolator = interpolator
        }
        if (isArcMotionEnabled) {
            transform.pathMotion = com.google.android.material.transition.platform.MaterialArcMotion()
        }
        transform.fadeMode = fadeMode
        transform.isDrawDebugEnabled = isDrawDebugEnabled
    }

    private fun setUpDefaultValues() {
        isArcMotionEnabled = false
        enterDuration = NO_DURATION
        returnDuration = NO_DURATION
        interpolator = null
        fadeMode = MaterialContainerTransform.FADE_MODE_IN
        isDrawDebugEnabled = false
    }

    /**
     * Create a bottom sheet dialog that displays controls to configure a container transform.
     */
    fun serArcMotionEnabled(arcMotionEnabled: Boolean) {
        isArcMotionEnabled = arcMotionEnabled
    }

    /**
     * Update the fade mode
     */
    fun setFadeMode(@MaterialContainerTransform.FadeMode fadeMode: Int) {
        this.fadeMode = fadeMode
    }

    /**
     * Set up whether or not to draw debugging paint
     */
    private fun setDrawDebugging(drawDebugEnabled: Boolean) {
        isDrawDebugEnabled = drawDebugEnabled
    }

    fun setCustomCubicBezierInterpolator(x1: Float, y1: Float, x2: Float, y2: Float) {
        if (areValidCubicBezierControls(x1, y1, x2, y2)) {
            interpolator = CustomCubicBezier(x1, y1, x2, y2)
        }
    }

    fun setCustomOvershootInterpolator() {
        interpolator = CustomOvershootInterpolator()
    }

    fun setCustomOvershootInterpolator(tension: Float) {
        interpolator = CustomOvershootInterpolator(tension)
    }

    fun setCustomAnticipateOvershootInterpolator() {
        interpolator = CustomAnticipateOvershootInterpolator()
    }

    fun setCustomAnticipateOvershootInterpolator(tension: Float) {
        interpolator = CustomAnticipateOvershootInterpolator(tension)
    }

    fun setBounceInterpolator() {
        interpolator = BounceInterpolator()
    }

    fun setFastOutSlowInInterpolator() {
        interpolator = FastOutSlowInInterpolator()
    }

    fun clearInterpolator() {
        interpolator = null
    }

    /**
     * A custom overshoot interpolator which exposes its tension.
     */
    private class CustomOvershootInterpolator @JvmOverloads internal constructor(val tension: Float = DEFAULT_TENSION) :
        OvershootInterpolator(tension) {
        companion object {
            // This is the default tension value in OvershootInterpolator
            const val DEFAULT_TENSION = 2.0f
        }
    }

    /**
     * A custom anticipate overshoot interpolator which exposes its tension.
     */
    private class CustomAnticipateOvershootInterpolator @JvmOverloads internal constructor(val tension: Float = DEFAULT_TENSION) :
        AnticipateOvershootInterpolator(tension) {
        companion object {
            // This is the default tension value in AnticipateOvershootInterpolator
            const val DEFAULT_TENSION = 2.0f
        }
    }

    /**
     * A custom cubic bezier interpolator which exposes its control points.
     */
    private class CustomCubicBezier(
        controlX1: Float,
        controlY1: Float,
        controlX2: Float,
        controlY2: Float
    ) : Interpolator {
        private val interpolator: Interpolator

        init {
            interpolator = PathInterpolatorCompat.create(controlX1, controlY1, controlX2, controlY2)
        }

        override fun getInterpolation(input: Float): Float {
            return interpolator.getInterpolation(input)
        }
    }

    companion object {
        private const val CUBIC_CONTROL_FORMAT = "%.3f"
        private const val DURATION_FORMAT = "%.0f"
        private const val NO_DURATION: Long = -1
        private fun isValidCubicBezierControlValue(value: Float?): Boolean {
            return value != null && value >= 0 && value <= 1
        }

        private fun areValidCubicBezierControls(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
            var isValid = true
            if (!isValidCubicBezierControlValue(x1)) {
                isValid = false
            }
            if (!isValidCubicBezierControlValue(y1)) {
                isValid = false
            }
            if (!isValidCubicBezierControlValue(x2)) {
                isValid = false
            }
            if (!isValidCubicBezierControlValue(y2)) {
                isValid = false
            }
            return isValid
        }
    }
}