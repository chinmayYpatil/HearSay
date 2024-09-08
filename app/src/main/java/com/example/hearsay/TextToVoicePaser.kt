package com.example.hearsay

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel: ViewModel() {

    private val _state = mutableStateOf(MainScreenState())
    val state: State<MainScreenState> = _state
    private var textToSpeech: TextToSpeech? = null

    fun onTextFieldValueChange(text: String) {
        _state.value = state.value.copy(
            text = text
        )
    }

    fun textToSpeech(context: Context, text: String) {
        _state.value = state.value.copy(
            isButtonEnabled = false
        )
        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    // Set language to Hindi
                    val result = txtToSpeech.setLanguage(Locale("en"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle case where Hindi is not supported or data is missing
                        // You can show a toast or log an error message
                    } else {
                        txtToSpeech.setSpeechRate(1.0f)
                        txtToSpeech.speak(
                            _state.value.text,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }
            }
            _state.value = state.value.copy(
                isButtonEnabled = true
            )
        }
    }
}

data class MainScreenState(
    val isButtonEnabled:Boolean = true,
    val text:String = ""
)