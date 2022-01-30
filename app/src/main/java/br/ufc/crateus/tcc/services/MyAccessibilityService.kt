package br.ufc.crateus.tcc.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "conected")
    }

    override fun onInterrupt() {

    }

    companion object {
        const val TAG = "MyAccessibilityService"
    }
}