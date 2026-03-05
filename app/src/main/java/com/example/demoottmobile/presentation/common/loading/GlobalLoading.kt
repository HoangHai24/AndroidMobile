package com.example.demoottmobile.presentation.common.loading

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.demoottmobile.R

object GlobalLoading {

    fun show(activity: FragmentActivity) {
        activity.runOnUiThread {
            activity.findViewById<View>(R.id.loading_overlay)?.visibility = View.VISIBLE
        }
    }

    fun hide(activity: FragmentActivity) {
        activity.runOnUiThread {
            activity.findViewById<View>(R.id.loading_overlay)?.visibility = View.GONE
        }
    }
}
