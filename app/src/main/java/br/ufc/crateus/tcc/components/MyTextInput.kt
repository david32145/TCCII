package br.ufc.crateus.tcc.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import br.ufc.crateus.tcc.BuildConfig
import br.ufc.crateus.tcc.services.MyAccessibilityService
import br.ufc.crateus.tcc.utils.AccessibilityUtils
import com.google.android.material.textfield.TextInputEditText

class MyTextInput : TextInputEditText {
    private val textToSpeech: TextToSpeech

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        this.textToSpeech = TextToSpeech(context) {}
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(AccessibilityUtils.isInAccessibilityMode(context)) {
            return false
        }
        return super.onTouchEvent(event)
    }

    private inner class MyFocusListener: OnFocusChangeListener {
        override fun onFocusChange(p0: View?, focused: Boolean) {
            Log.d("MyFocusListener", focused.toString())
            if( focused) {
                var suffix = "com o valor \"${text?.trim().toString()}\""
                if(text?.trim()?.isBlank()!!) {
                    suffix = "sem valor preenchido"
                }
                textToSpeech.speak("Campo de texto para $contentDescription, $suffix", TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }
}