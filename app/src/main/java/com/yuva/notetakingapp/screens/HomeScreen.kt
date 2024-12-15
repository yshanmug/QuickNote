package com.yuva.notetakingapp.screens

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.yuva.notetakingapp.Note
import com.yuva.notetakingapp.NoteWithToDoItems
import com.yuva.notetakingapp.R
import com.yuva.notetakingapp.ui.theme.ranchoFamily
import com.yuva.notetakingapp.viewmodels.NotesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    notesViewModel: NotesViewModel = hiltViewModel(),
    onNewNoteClicked: (isToDoList: Boolean) -> Unit,
    onNoteClicked: (NoteWithToDoItems) -> Unit,
    onBackPress: () -> Unit,
    onSortByTimePress: (isAscending: Boolean) -> Unit,
    onSortByTitlePress: (isAscending: Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedNotes by remember { mutableStateOf<List<Note>>(emptyList()) }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isAscendingByTitle by remember { mutableStateOf(true) }
    var isAscendingByTime by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val notesWithToDoItems by notesViewModel.allNotesList.collectAsState()
    val isDataLoaded by notesViewModel.isDataReady.collectAsState()
    val focusManager = LocalFocusManager.current
    var showNotes by remember { mutableStateOf(false) }
    LaunchedEffect(isDataLoaded) {
        if (!isDataLoaded) {
            delay(500)
            showNotes = true
        }
    }
    BackHandler()
    {
        onBackPress.invoke()
    }

    Scaffold(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState, modifier = Modifier.size(300.dp, 100.dp))
        },

        topBar = {
            TopAppBar(
                title = {
                    GreetingText()
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSortByTitlePress(!isAscendingByTitle)
                            isAscendingByTitle = !isAscendingByTitle
                        },
                        colors =  IconButtonDefaults.iconButtonColors( contentColor = MaterialTheme.colorScheme.onPrimary),
                        interactionSource = interactionSource,
                      ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sortbyascdsc),
                            contentDescription = "SortByTime", modifier = Modifier.size(25.dp),
                            )
                    }

                    IconButton(onClick = {
                        onSortByTimePress(!isAscendingByTime)
                        isAscendingByTime = !isAscendingByTime
                    },
                        modifier = Modifier
                        ,
                        colors =  IconButtonDefaults.iconButtonColors( contentColor = MaterialTheme.colorScheme.onPrimary),)

                    {
                        Icon(
                            painter = painterResource(id = R.drawable.sortbytime),
                            contentDescription = "Search", modifier = Modifier.size(25.dp)
                        )

                    }

                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "More"
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary)
                                .wrapContentHeight()
                                .width(150.dp),
                            content = {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Delete Note",
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    },
                                    onClick = {
                                        if (selectedNotes.isNotEmpty()) {
                                            selectedNotes.forEach {
                                                notesViewModel.deleteNote(it.id)
                                            }
                                            selectedNotes = emptyList()
                                            expanded = false

                                        } else {
                                            scope.launch {
                                                snackBarHostState.showSnackbar("Please Select a note to delete")
                                            }
                                        }
                                    },
                                    )
                            },
                            properties = PopupProperties(focusable = true),
                            offset = DpOffset(x = 0.dp, y = 15.dp)
                        )
                    }
                },
                colors = topAppBarColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier,
            )
        },
        bottomBar = {
            BottomAppBar(

                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                    ){

                      Checkbox(
                          checked = true,
                          onCheckedChange = {onNewNoteClicked(true)},
                          colors = CheckboxDefaults.colors( checkedColor = if(isSystemInDarkTheme())
                          { MaterialTheme.colorScheme.onPrimary}
                          else{ MaterialTheme.colorScheme.primary},
                              uncheckedColor = if(isSystemInDarkTheme())
                              {MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)}
                              else
                              { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)},
                              checkmarkColor = if(isSystemInDarkTheme())
                              {
                                  MaterialTheme.colorScheme.primary}
                                      else{
                        MaterialTheme.colorScheme.onPrimary })

                      )
                        Text(
                            text = "To-Do List",
                            modifier = Modifier
                                .clickable { onNewNoteClicked(true) })
                        }
                },

                containerColor = Color.Transparent,
                modifier = Modifier
                    .focusProperties { canFocus = false }
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f) // Semi-transparent background
                    )
                    .navigationBarsPadding(),


                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onNewNoteClicked(false) },
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                    {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },

    )
    { innerPadding ->

        Log.d("Noteswithtodoitem", notesWithToDoItems.toString())
        val isNoteEmpty = notesWithToDoItems.isEmpty()
        Log.d("isNoteReady", isDataLoaded.toString())

        when {
            isDataLoaded -> {
                Column(
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier,
                        color = if(isSystemInDarkTheme()){ MaterialTheme.colorScheme.onPrimary}
                                else{ MaterialTheme.colorScheme.primary},
                        strokeWidth = 4.dp,
                        strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
                    )
                }
            }
            notesWithToDoItems.isEmpty() && !isDataLoaded -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Take your first note...!",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Normal
                    )
                }
            }

            else -> {

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    )
                    {

                        items(
                            items = notesWithToDoItems.reversed(),
                            key = { it.note.id!! },
                        ) { noteWithToDoItems ->
                            val note = noteWithToDoItems.note
                            val toDoItems = noteWithToDoItems.toDoItems
                            val isSelected = selectedNotes.contains(note)
                            Column(
                                modifier = Modifier
                                    .widthIn(min = 0.dp, max = 200.dp)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.05.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (selectedNotes.isNotEmpty()) {
                                                selectedNotes = if (isSelected) {
                                                    selectedNotes - note
                                                } else {
                                                    selectedNotes + note
                                                }
                                            } else {
                                                onNoteClicked(noteWithToDoItems)
                                            }
                                        },
                                        onLongClick = {
                                            selectedNotes = if (isSelected) {
                                                selectedNotes - note
                                            } else {
                                                selectedNotes + note
                                            }
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .heightIn(min = 0.dp, max = 200.dp),

                                ) {
                                if (note.title.isNotEmpty()) {
                                    Text(
                                        text = note.title,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        textAlign = TextAlign.Start,
                                        fontWeight = FontWeight.W500
                                    )
                                }

                                if (note.description.isNotEmpty()) {
                                    Text(
                                        text = note.description,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Start
                                    )
                                }

                                val maxItems = 7
                                val maxUnchecked = 5
                                val unCheckedItems = toDoItems.filter { !it.isChecked }
                                Log.d("unCheckedItems", unCheckedItems.toString())
                                val checkedItems = toDoItems.filter { it.isChecked }
                                val uncheckedToTake = if (checkedItems.isEmpty()) maxItems else maxUnchecked
                                val displayedUncheckedItems = unCheckedItems.take(uncheckedToTake)
                                val remainingSlots = maxItems - displayedUncheckedItems.size
                                val displayedCheckedItems = checkedItems.take(remainingSlots)
                                val combinedItems = displayedUncheckedItems + displayedCheckedItems
                                if (combinedItems.isNotEmpty()) {
                                    combinedItems.forEach { toDoItem ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                        ) {

                                            Checkbox(
                                                checked = toDoItem.isChecked,
                                                onCheckedChange = null,
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = if (isSystemInDarkTheme()) {
                                                        MaterialTheme.colorScheme.onSecondary
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    },
                                                    uncheckedColor = if (isSystemInDarkTheme()) {
                                                        MaterialTheme.colorScheme.onSecondary.copy(
                                                            alpha = 0.6f
                                                        )
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.6f
                                                        )
                                                    },
                                                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                                ),
                                                modifier = Modifier.scale(0.60f)
                                            )
                                            Text(
                                                text = toDoItem.toDoItem,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.padding(start = 4.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(0.dp))
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GreetingText() {
        Column {
        TypingTextEffect(text = "{ Quick Note. . . }")
    }
}

@Composable
fun TypingTextEffect(text: String, typingSpeed: Long = 100L) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, char ->
            delay(typingSpeed)
            displayedText += char
        }
    }

    Text(
        text = displayedText,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        fontFamily = ranchoFamily,
        fontStyle = FontStyle.Normal   )
}

