package com.nishant.tapnshare


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nishant.tapnshare.ui.theme.TapNShareTheme

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapNShareTheme {
                OnboardingScreen {
                    // 👇 Go to MainActivity once splash is done
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // destroy splash so back press won’t return here
                }
            }
        }
    }
}
