package com.example.hearsay

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.*

data class Language(val name: String, val code: String)

val supportedLanguages = listOf(
    Language("Hindi", "hi"),
    Language("Bengali", "bn"),
    Language("Telugu", "te"),
    Language("Marathi", "mr"),
    Language("Tamil", "ta"),
    Language("Urdu", "ur"),
    Language("Gujarati", "gu"),
    Language("Kannada", "kn"),
    Language("Odia", "or"),
    Language("Malayalam", "ml"),
    Language("Punjabi", "pa"),
    Language("Assamese", "as")
)

class TextToSpeechManager(context: Context) {
    private val tts: TextToSpeech = TextToSpeech(context, OnInitListener { status ->
        if (status != TextToSpeech.SUCCESS) {
            println("TextToSpeech initialization failed")
        }
    })

    fun speak(text: String, locale: Locale) {
        if (text.isNotEmpty()) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Language not supported or missing data for language: $locale")
                return
            }
            tts.speak(text, QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(navController: NavController) {
    val context = LocalContext.current
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var displayedText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages[0]) }

    // Create an instance of TextToSpeechManager
    val ttsManager = remember { TextToSpeechManager(context) }

    // Dispose the TextToSpeechManager when the composable is removed from the composition
    DisposableEffect(context) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.White)) {
                                append("Hear")
                            }
                            withStyle(style = SpanStyle(color = Color.White)) {
                                append("Say")
                            }
                        },
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Enter text and listen button
        TranslationField(
            value = textState,
            onValueChange = { textState = it },
            onListenClick = {
                ttsManager.speak(displayedText, Locale(selectedLanguage.code))
            },
            onSendClick = {
                displayedText = textState.text.uppercase(Locale.getDefault())
                textState = TextFieldValue("")
            },
            displayedText = displayedText,
            placeholder = "Enter text in ${selectedLanguage.name}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection box
        LanguageSelection(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        ActionButtons(navController, displayedText)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onListenClick: () -> Unit,
    onSendClick: () -> Unit,
    displayedText: String,
    placeholder: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), // Increase the height
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        placeholder,
                        color = Color.Gray,
                        fontSize = 24.sp // Increase placeholder font size
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), // Ensure the text field fills the height
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 30.sp, // Set the entered text to be larger
                    color = Color.White
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
            ) {
                Text("Send", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayedText,
            color = Color.White,
            fontSize = 30.sp, // Increase the displayed text size
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center // Center the text
        )

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = onListenClick,
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF4285F4), CircleShape)
                .align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Listen",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelection(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedLanguage.name,
            onValueChange = { },
            label = { Text("Language") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF333333),
                unfocusedContainerColor = Color(0xFF333333),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            supportedLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.name) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun ActionButtons(navController: NavController, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            label = "Speak",
            icon = Icons.Filled.PlayArrow,
            isMain = true,
            onClick = {
                navController.navigate("voice_to_text_screen")  // Redirect to voice-to-text screen
            }
        )
        ActionButton(
            label = "Text",
            icon = Icons.Filled.Send,
            isMain = true,
            onClick = {
                navController.navigate("chat_screen/${text}")  // Redirect to chat screen
            }
        )
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    isMain: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(if (isMain) 64.dp else 48.dp)
                .background(if (isMain) Color(0xFF4285F4) else Color.Gray, CircleShape)
        ) {
            Icon(icon, contentDescription = label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White)
    }
}
