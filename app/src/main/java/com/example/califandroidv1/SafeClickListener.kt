package com.example.califandroidv1

import android.os.SystemClock
import android.view.View

//Clase q creamos "SafeClickListener" q evita 2 clicks seguidos en 1 seg

class SafeClickListener(
    private var defaultInterval: Int = 2000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }

}

