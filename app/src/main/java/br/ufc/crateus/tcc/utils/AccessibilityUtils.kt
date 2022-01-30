package br.ufc.crateus.tcc.utils

import android.content.Context
import br.ufc.crateus.tcc.services.MyAccessibilityService

class AccessibilityUtils {
    companion object {
        private const val DEV = false
        fun isInAccessibilityMode(context: Context): Boolean {
            if (DEV) {
                return true
            }
            return context.getSystemServiceName(MyAccessibilityService::class.java) != null
        }
    }
}