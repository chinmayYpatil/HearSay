package com.example.hearsay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission
            } else {
                // Handle the case where the user denies the permission
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            AppNavigation()
        }
    }

    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()
        val context = LocalContext.current

        NavHost(navController, startDestination = "translator_screen") {
            composable("translator_screen") { TranslatorScreen(navController) }
            composable("voice_to_text_screen") {
                VoiceToTextScreen(navController, context)
            }
            composable(
                "chat_screen/{text}",
                arguments = listOf(navArgument("text") { type = NavType.StringType })
            ) { backStackEntry ->
                val text = backStackEntry.arguments?.getString("text") ?: ""
                ChatScreen(navController, text)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, proceed with functionality that requires this permission
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Explain to the user why the permission is needed and request permission again
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // Directly request for permission
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}