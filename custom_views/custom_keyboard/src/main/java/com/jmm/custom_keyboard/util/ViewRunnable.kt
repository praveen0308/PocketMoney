package com.jmm.custom_keyboard.util


import android.view.View

/**
 * Created by Evgeny Eliseyev on 11/02/2018.
 */

internal abstract class ViewRunnable : Runnable {
    var view: View? = null
}