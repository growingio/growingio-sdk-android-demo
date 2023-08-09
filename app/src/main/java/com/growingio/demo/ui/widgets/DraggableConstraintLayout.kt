/*
 *   Copyright (c) - 2023 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.growingio.demo.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.customview.widget.ViewDragHelper

/**
 * <p>
 *
 * @author cpacm 2023/4/23
 */
class DraggableConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val viewDragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(
            this,
            0.5f,
            dragCallback,
        )
    }

    private val draggableChildren = mutableListOf<View>()
    private var dragListener: ViewDragListener? = null

    fun addDraggableChild(child: View) {
        if (child.parent != this) {
            throw IllegalArgumentException("child must be a direct child of this layout")
        }
        draggableChildren.add(child)
    }

    fun removeDraggableChild(child: View) {
        if (child.parent != this) {
            throw IllegalArgumentException("child must be a direct child of this layout")
        }
        draggableChildren.remove(child)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child.visibility == View.VISIBLE && viewIsDraggableChild(child)
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            dragListener?.onViewCaptured(capturedChild, activePointerId)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            dragListener?.onViewReleased(releasedChild, xvel, yvel)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return child.height
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }
    }

    private fun viewIsDraggableChild(view: View): Boolean {
        return draggableChildren.isNotEmpty() && draggableChildren.contains(view)
    }

    interface ViewDragListener {
        fun onViewCaptured(view: View, pointerId: Int)
        fun onViewReleased(view: View, xvel: Float, yvel: Float)
    }
}
