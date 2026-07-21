package com.deepanjanxyz.notepad.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.deepanjanxyz.notepad.features.editor.EditorScreen
import com.deepanjanxyz.notepad.features.editor.EditorViewModel
import com.deepanjanxyz.notepad.features.notes.NotesScreen
import com.deepanjanxyz.notepad.features.notes.NotesViewModel
import com.deepanjanxyz.notepad.features.settings.SettingsScreen
import com.deepanjanxyz.notepad.features.settings.SettingsViewModel

object NavRoutes {
    const val NOTES = "notes"
    const val EDITOR = "editor/{noteId}"
    const val SETTINGS = "settings"

    fun createEditorRoute(noteId: Long): String = "editor/$noteId"
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.NOTES
    ) {
        composable(NavRoutes.NOTES) {
            val viewModel: NotesViewModel = hiltViewModel()
            NotesScreen(
                viewModel = viewModel,
                onNavigateToEditor = { noteId ->
                    navController.navigate(NavRoutes.createEditorRoute(noteId))
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }

        composable(
            route = NavRoutes.EDITOR,
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType; defaultValue = 0L }
            )
        ) {
            val viewModel: EditorViewModel = hiltViewModel()
            EditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
