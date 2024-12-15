package com.yuva.notetakingapp
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

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
                        HomeScreen(notesViewModel = notesViewModel,
                            onNewNoteClicked = { isToDoList ->
                                navController.navigate("NoteTakingScreen/$isToDoList")
                            },
                            onNoteClicked = { noteWithToDoItems ->
                                notesViewModel.setNote(noteWithToDoItems)
                                val isToDoList = noteWithToDoItems.toDoItems.isNotEmpty()
                                navController.navigate("NoteTakingScreen/$isToDoList")
                            } ,
                            onBackPress = {
                                finish()
                            },
                            onSortByTimePress = {isAscendingByTime ->
                                notesViewModel.sortByTimeCreated(isAscendingByTime)
                            },
                            onSortByTitlePress = {isAscendingByTitle ->
                                notesViewModel.sortNoteByTitle(isAscendingByTitle)
                            },
                        )
                    }
                    composable(route = "NoteTakingScreen/{isToDoList}", arguments = listOf(navArgument("isToDoList") {
                        type = NavType.BoolType
                    } ) ) { it ->
                        val isToDo = it.arguments?.getBoolean("isToDoList") ?: false
                        val note by notesViewModel.note.collectAsState()
                        NoteTakingScreen(notesViewModel = notesViewModel, note,
                            isToDoList = isToDo,
                            onTitleValueChange = {
                               title ->
                                notesViewModel.updateTitle(title) },
                            onDescriptionValueChange = {
                                notesViewModel.updateDescription(it) },
                            onBackPressed = {isToDoList ->
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

