package com.example.hearsay

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class VoiceLanguage(val name: String, val code: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceToTextScreen(
    navController: NavController,
    application: Context,
    voiceToTextParser: VoiceToTextParser = remember { VoiceToTextParser(application) }
) {
    var displayedText by remember { mutableStateOf("Speak now...") }
    val parserState by voiceToTextParser.state.collectAsState()

    val languages = listOf(
        VoiceLanguage("English", "en"),
        VoiceLanguage("Hindi", "hi"),
        VoiceLanguage("Bengali", "bn"),
        VoiceLanguage("Telugu", "te"),
        VoiceLanguage("Tamil", "ta"),
        VoiceLanguage("Marathi", "mr"),
        VoiceLanguage("Gujarati", "gu"),
        VoiceLanguage("Kannada", "kn"),
        VoiceLanguage("Malayalam", "ml")
    )
    var selectedLanguage by remember { mutableStateOf(languages[0]) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(parserState) {
        displayedText = when {
            parserState.spokenText.isNotEmpty() -> parserState.spokenText
            parserState.error != null -> "Error: ${parserState.error}"
            else -> "Speak now..."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                IconButton(onClick = { /* TODO: Handle menu click */ }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = displayedText,
            color = Color.Gray,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedLanguage.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Dropdown arrow", tint = Color.White) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF424242),
                        focusedContainerColor = Color(0xFF424242),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF424242))
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language.name, color = Color.White) },
                            onClick = {
                                selectedLanguage = language
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF4285F4), CircleShape)
                .align(Alignment.CenterHorizontally)
                .clickable { voiceToTextParser.startListening(selectedLanguage.code) }
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color.Black)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}