package com.nishant.tapnshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nishant.tapnshare.ui.theme.TapNShareTheme
import com.nishant.tapnshare.viewmodel.PermissionsViewModel
import com.nishant.tapnshare.viewmodel.UserProfileViewModel

class SettingsActivity : ComponentActivity() {

    // ViewModels
    private val userProfileViewModel: UserProfileViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private val permissionsViewModel: PermissionsViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TapNShareTheme {
                SettingsScreen(
                    userProfileViewModel = userProfileViewModel,
                    permissionsViewModel = permissionsViewModel,
                    onBack = { finish() },
                    restartMain = { restartMainActivity() }
                )
            }
        }
    }

    // Restart MainActivity when permissions change
    private fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }
}
