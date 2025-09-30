package com.nishant.tapnshare

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.nishant.tapnshare.ui.theme.TapNShareTheme // Assuming you have a Theme file

class MainActivity : ComponentActivity() {

    private val PREFS_NAME = "TapNSharePrefs"
    private val ONBOARDING_COMPLETED_KEY = "is_onboarding_completed"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply your application theme
            TapNShareTheme {
                AppNavigator()
            }
        }
    }

    // Composable that handles the primary navigation logic
    @Composable
    fun AppNavigator() {
        val context = LocalContext.current

        // 1. Get SharedPreferences instance (Remembered to avoid re-creation on recomposition)
        val sharedPrefs = remember {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        // 2. State variable to hold whether onboarding is complete
        // Load the stored state. Defaults to false (Onboarding needed).
        var isOnboardingCompleted by remember {
            mutableStateOf(sharedPrefs.getBoolean(ONBOARDING_COMPLETED_KEY, false))
        }

        // 3. Define the action to complete onboarding
        val completeOnboarding: () -> Unit = {
            // Persist the state change
            sharedPrefs.edit().putBoolean(ONBOARDING_COMPLETED_KEY, true).apply()
            // Update the state variable to trigger recomposition and switch screens
            isOnboardingCompleted = true
        }

        // 4. Conditional UI Logic
        if (isOnboardingCompleted) {
            // Show the main application screen
            MainScreen()
        } else {
            // Show the onboarding flow, passing the completion function
            OnboardingScreen(onGetStarted = completeOnboarding)
        }
    }
}