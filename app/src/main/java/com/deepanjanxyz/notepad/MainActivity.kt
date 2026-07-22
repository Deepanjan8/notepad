package com.deepanjanxyz.notepad

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.deepanjanxyz.notepad.core.security.SecurityManager
import com.deepanjanxyz.notepad.core.ui.theme.EliteMemoTheme
import com.deepanjanxyz.notepad.features.lock.LockScreen
import com.deepanjanxyz.notepad.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var securityManager: SecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EliteMemoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val isUnlocked by securityManager.isUnlocked.collectAsState()
                    val navController = rememberNavController()

                    if (isUnlocked) {
                        AppNavigation(navController = navController)
                    } else {
                        LockScreen(
                            onUnlocked = { securityManager.unlock() }
                        )
                    }
                }
            }
        }
    }
}
