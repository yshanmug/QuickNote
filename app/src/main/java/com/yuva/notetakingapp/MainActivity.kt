package com.yuva.notetakingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yuva.notetakingapp.screens.HomeScreen
import com.yuva.notetakingapp.screens.NoteTakingScreen
import com.yuva.notetakingapp.ui.theme.NoteTakingAppTheme
import com.yuva.notetakingapp.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            onBackPressedDispatcher
            NoteTakingAppTheme {
                val notesViewModel = hiltViewModel<NotesViewModel>()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "AllNoteScreen") {
                    composable("AllNoteScreen") {
                        HomeScreen(
                            notesViewModel = notesViewModel,
                            onNewNoteClicked = { isToDoList ->
                                navController.navigate("NoteTakingScreen/$isToDoList")
                            },
                            onNoteClicked = { noteWithToDoItems ->
                                notesViewModel.setNote(noteWithToDoItems)
                                val isToDoList = noteWithToDoItems.toDoItems.isNotEmpty()
                                navController.navigate("NoteTakingScreen/$isToDoList")
                            },
                            onBackPress = {
                                finish()
                            },
                            onSortByTimePress = { isAscendingByTime ->
                                notesViewModel.sortByTimeCreated(isAscendingByTime)
                            },
                            onSortByTitlePress = { isAscendingByTitle ->
                                notesViewModel.sortNoteByTitle(isAscendingByTitle)
                            },
                        )
                    }
                    composable(
                        route = "NoteTakingScreen/{isToDoList}",
                        arguments = listOf(navArgument("isToDoList") {
                            type = NavType.BoolType
                        })
                    ) { it ->
                        val isToDo = it.arguments?.getBoolean("isToDoList") ?: false
                        val note by notesViewModel.note.collectAsState()
                        NoteTakingScreen(
                            notesViewModel = notesViewModel, note,
                            isToDoList = isToDo,
                            onTitleValueChange = { title ->
                                notesViewModel.updateTitle(title)
                            },
                            onDescriptionValueChange = {
                                notesViewModel.updateDescription(it)
                            },
                            onBackPressed = { isToDoList ->
                                navController.popBackStack()
                                notesViewModel.insertNote(isToDoList)
                            },
                        )
                    }
                }
            }
        }
    }
}

