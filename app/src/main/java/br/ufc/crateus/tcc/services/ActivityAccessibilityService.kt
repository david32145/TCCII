package br.ufc.crateus.tcc.services

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

import android.content.ClipData
import br.ufc.crateus.tcc.utils.AccessibilityUtils


class FastAction(
    val code: Int,
    val description: String
) {
    companion object {
        const val COPY = 0
        const val PAST = 1
    }
}


class ActivityAccessibilityService(
    private val activity: AppCompatActivity,
    @IdRes private val rootId: Int
) {
    companion object {
        const val TEXT_INPUT_LAYOUT_CLASS_NAME =
            "br.ufc.crateus.tcc.components.MyTextInputLayout"

        private val ACCESSIBILITY_CLASS_NAMES = listOf(
            "br.ufc.crateus.tcc.components.MyButton",
            "br.ufc.crateus.tcc.components.MyAppCompatImageView",
            "br.ufc.crateus.tcc.components.MyTextInputLayout",
            "br.ufc.crateus.tcc.components.MyTextInput",
            "br.ufc.crateus.tcc.components.MyMaterialTextView",
            "br.ufc.crateus.tcc.components.MyImageButton"
        )
        const val TAG = "br.ufc.crateus.tcc.services"
    }

    private var speeching: Boolean = false
    private val textToSpeech = TextToSpeech(activity) {
        Log.d(TAG, "TextToSpeech initialized ${it == 0}")
    }
    private val actions = listOf(
        FastAction(FastAction.COPY, "Copiar texto atual"),
        FastAction(FastAction.PAST, "Colar seleção atual")
    )
    private var lastActionData = LocalDateTime.now()
    private var currentAction = -1

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        .apply {
            setRecognitionListener(MyRecognitionListener())
        }
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
    }
    private var clipboardManager: ClipboardManager? =
        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?


    private val vibrationManager = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val accessibilityViews = mutableListOf<View>()
    private var currentFocused = -1
    private lateinit var onAccessibilityTouchListener: OnAccessibilityTouchListener
    private lateinit var rootView: View

    fun onViewsChange() {
        if (AccessibilityUtils.isInAccessibilityMode(activity)) {
            accessibilityViews.clear()
            currentFocused = -1
            rootView = activity.findViewById(rootId)
            onAccessibilityTouchListener = createOnTouchListener()
            rootView.setOnTouchListener(onAccessibilityTouchListener)
            bfSearch(rootView)
        }
    }

    private fun bfSearch(view: View) {
        if (ACCESSIBILITY_CLASS_NAMES.contains(view.className)) {
            Log.d(TAG, "Element ${view.className} added")
            accessibilityViews.add(view)
        }
        if (view.className == TEXT_INPUT_LAYOUT_CLASS_NAME) {
            return
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                bfSearch(view.getChildAt((i)))
            }
        }
    }

    private fun vibrate() {
        vibrationManager?.vibrate(120)
    }

    private fun moveNext() {
        vibrate()
        if (currentFocused != -1) {
            accessibilityViews[currentFocused].clearFocus()
        }
        currentFocused = (currentFocused + 1) % accessibilityViews.size
        textToSpeech.speakText(accessibilityViews[currentFocused].accessibilityText)
    }

    private fun movePrevious() {
        vibrate()
        if (currentFocused != -1) {
            accessibilityViews[currentFocused].clearFocus()
        } else {
            currentFocused = 0
        }
        currentFocused = (currentFocused - 1 + accessibilityViews.size) % accessibilityViews.size

        textToSpeech.speakText(accessibilityViews[currentFocused].accessibilityText)
    }

    private fun createOnTouchListener(): OnAccessibilityTouchListener {
        return object : OnAccessibilityTouchListener(activity) {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onSwipeLeft() {
                moveNext()
            }

            override fun onSwipeRight() {
                movePrevious()
            }

            override fun onSwipeTop() {
                vibrate()
                currentAction = (currentAction + 1) % actions.size
                textToSpeech.speakText(actions[currentAction].description)
                lastActionData = LocalDateTime.now()
            }

            override fun onSwipeBottom() {
                vibrate()
                if (currentAction == -1) {
                    currentAction = 0
                }
                currentAction = (currentAction - 1 + actions.size) % actions.size
                textToSpeech.speakText(actions[currentAction].description)
                lastActionData = LocalDateTime.now()
            }

            override fun onDoubleTap(): Boolean {
                vibrate()
                accessibilityViews[currentFocused].performClick()
                accessibilityViews[currentFocused].requestFocus()
                return true
            }

            override fun onSingleTapConfirmed(): Boolean {
                vibrate()
                if (shouldProcessAction()) {
                    processAction()
                    return true
                }
                textToSpeech.speakText(accessibilityViews[currentFocused].accessibilityText)
                return true
            }

            override fun onTouchDown() {
                if (currentFocused >= 0 && accessibilityViews[currentFocused].textInputLayout) {
                    Log.d(TAG, "Start listening")
                    vibrate()
                    speechRecognizer.startListening(speechRecognizerIntent)
                }
            }

            override fun onTouchUp() {
                if (speeching && currentFocused >= 0 && accessibilityViews[currentFocused].textInputLayout) {
                    Log.d(TAG, "Stop listening")
                    vibrate()
                    speechRecognizer.stopListening()
                }
            }
        }
    }

    private fun shouldProcessAction(): Boolean {
        return abs(ChronoUnit.MILLIS.between(LocalDateTime.now(), lastActionData)) < 4000
    }

    private fun processAction() {
        when (actions[currentAction].code) {
            FastAction.COPY -> {
                val view = accessibilityViews[currentFocused]
                if (view != null && view is TextView) {
                    val clip = ClipData.newPlainText("Accessibility", view.text)
                    clipboardManager?.setPrimaryClip(clip)
                    textToSpeech.speakText("O texto \"${view.text}\" foi copiado")
                }
            }

            FastAction.PAST -> {
                val view = accessibilityViews[currentFocused]
                Log.d(TAG, "pasting")
                if (view != null && view is TextInputLayout) {
                    clipboardManager?.primaryClip.let {
                        val textClip = it?.getItemAt(0)?.text.toString()
                        view.editText?.setText(textClip)
                        textToSpeech.speakText("O texto \"${textClip}\" foi colado na caixa de texto")
                    }
                }
            }
        }
    }

    fun onDetach() {
        if (AccessibilityUtils.isInAccessibilityMode(activity)) {
            textToSpeech.shutdown()
            speechRecognizer.destroy()
        }
    }

    private inner class MyRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
            speeching = true
        }

        override fun onRmsChanged(p0: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(p0: ByteArray?) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech")
            speeching = false
        }

        override fun onError(p0: Int) {
            Log.d(TAG, "onError")
            textToSpeech.speakText("Não foi possível entender o que você disse, tente novamente")
        }

        override fun onResults(p0: Bundle?) {
            Log.d(TAG, "onResults")
            val data = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = data?.get(0).toString()
            val node = accessibilityViews[currentFocused]
            if (node is TextInputLayout) {
                node.editText?.setText(text)
            }
            textToSpeech.speakText("O valor do campo de texto foi alterado para: $text")
        }

        override fun onPartialResults(p0: Bundle?) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onEvent(p0: Int, p1: Bundle?) {
            Log.d(TAG, "onEvent")
        }

    }
}

private fun TextToSpeech.speakText(text: String) {
    this.speak(text, TextToSpeech.QUEUE_FLUSH, null)
}

private val View.className: String
    get() {
        return this.javaClass.name
    }

private val View.textInputLayout: Boolean
    get() {
        return this is TextInputLayout
    }


private val View.accessibilityText: String
    get() {
        if (this is TextInputLayout) {
            return if (editText != null && editText?.contentDescription?.isNotBlank() == true) {
                val textInput = editText!!
                var suffix = "com o valor \"${textInput.text?.trim().toString()}\""
                if (textInput.text.isBlank()) {
                    suffix = "sem valor preenchido"
                }
                "Campo de texto para ${textInput.contentDescription.trim()}, $suffix"
            } else {
                "Campo de texto sem conteúdo acessível"
            }
        }

        if (this is MaterialButton) {
            return if (this.contentDescription.isNotBlank()) {
                "Botão para ${this.contentDescription.trim()}"
            } else {
                "Botão ${this.text}"
            }
        }

        if (this is ImageButton) {
            return if (this.contentDescription.isNotBlank()) {
                "Botão para ${this.contentDescription.trim()}"
            } else {
                "Botão sem conteúdo acessível"
            }
        }

        if (this is TextView) {
            if (this.contentDescription?.trim()?.isNotBlank() == true) {
                return "Texto ${this.contentDescription.trim()}"
            }
            return "Texto ${this.text}"
        }

        if (this is ImageView) {
            if (this.contentDescription.isNotBlank()) {
                return "Imagem ${this.contentDescription.trim()}"
            }
            return "Imagem sem conteúdo acessível"
        }

        return "Elemento sem conteúdo acessível"
    }