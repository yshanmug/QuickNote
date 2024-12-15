package com.yuva.notetakingapp.screens
import android.content.ClipData.Item
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.modifier.EmptyMap.set
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yuva.notetakingapp.Note
import com.yuva.notetakingapp.NoteWithToDoItems
import com.yuva.notetakingapp.ToDoItem
import com.yuva.notetakingapp.viewmodels.NotesViewModel
import dagger.Lazy
import kotlinx.coroutines.flow.forEach

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NoteTakingScreen(
    notesViewModel: NotesViewModel = hiltViewModel(),
    note: NoteWithToDoItems,
    isToDoList: Boolean,
    onBackPressed:(isToDoList: Boolean) -> Unit,
    onTitleValueChange: ( String) -> Unit,
    onDescriptionValueChange: (String) -> Unit,
) {
    val toDoListItems by notesViewModel.toDoListItems.collectAsState()
    val focusRequesterForText = remember { FocusRequester() }
    val focusRequester = remember { mutableStateListOf<FocusRequester>() }
    BackHandler()
    {
        onBackPressed.invoke(isToDoList)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .imePadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 0.dp)
        ) {
            IconButton(onClick = {
                onBackPressed.invoke(isToDoList)

            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "back"
                )
            }
        }

            TextField(
                modifier = Modifier
                    .padding(vertical = 0.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedTrailingIconColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,

                ),
                value = note.note.title,
                onValueChange = {
                    onTitleValueChange(it)
                },
                placeholder = {
                    Text(
                        "Title..",
                        fontSize = 20.sp,
                        color = if(isSystemInDarkTheme())
                            MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onTertiary
                    )
                },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                )



            if (isToDoList) {
                    LaunchedEffect(toDoListItems.size) {
                        while (focusRequester.size < toDoListItems.size) {

                            focusRequester.add(FocusRequester())
                            Log.d("Block1 executed", "{${focusRequester.size}}, {${toDoListItems.size}}")
                        }
                        while (focusRequester.size > toDoListItems.size) {
                            focusRequester.removeLast()
                            Log.d("Block2 executed", "{${focusRequester.size}}, {${toDoListItems.size}}")
                        }
                    }

                val unCheckedItems = toDoListItems.filter { !it.isChecked  }
                val checkedItems = toDoListItems.filter { it.isChecked }
                val combinedItems = unCheckedItems + checkedItems

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(combinedItems, key = {it.id }){ item->
                        DynamicRow(
                            toDoItem = item,
                            onDelete = { notesViewModel.deletedToDoItem(item.id)
//                                if(index> 0 && focusRequester.size > index - 1){
//                                    focusRequester[index-1].requestFocus()
//                                }
                            },
                            onValueChange = {
                                             toDoItem -> notesViewModel.updateToDoItem(toDoItem)
                                             Log.d("TO-DO ITEM", toDoItem.toString())
                                            },
                            focusRequester = focusRequester.getOrNull(item.id) ?: FocusRequester(),
                            modifier = Modifier.animateItemPlacement(
                                )

                            )

                    }



                    item {
                        Row(
                            Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clickable {
                                    notesViewModel.addToDoItem(
                                        noteId = 0,
                                        toDoText = "", isChecked = false
                                    )
                                    focusRequester.add(FocusRequester())
                                },
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        )
                        {
                            Icon(
                                Icons.Filled.Add,
                                tint = if(isSystemInDarkTheme())
                                    MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.onTertiary,
                                contentDescription = "Add Icon",
                            )

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(), text = "Add Item",
                                color = if(isSystemInDarkTheme())
                                    MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                }
                Log.d("ToDoListItems1", toDoListItems.toList().toString())

            }
            else {

                TextField(
                    value = note.note.description,
                    onValueChange = {
                        onDescriptionValueChange(it)
                    },
                    placeholder = {
                        Text(
                            "Description..",
                            fontSize = 16.sp,
                            color = if(isSystemInDarkTheme())
                                MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                            else
                                MaterialTheme.colorScheme.onTertiary

                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequesterForText)
                        .fillMaxWidth()
                        .padding(vertical = 0.dp)
                        ,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onSecondary,
                        unfocusedTrailingIconColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    maxLines = Int.MAX_VALUE,
                    keyboardOptions = KeyboardOptions(),
                )

            }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DynamicRow(
    toDoItem: ToDoItem,
    onValueChange: ( ToDoItem) -> Unit,
    onDelete: (Int) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    LaunchedEffect (Unit){
        focusRequester.requestFocus()
    }
    val (checkedState, onStateChange) = remember { mutableStateOf(toDoItem.isChecked) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center

    ) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                onStateChange(it)
                onValueChange(
                 toDoItem.copy(isChecked = it)
                )
            },
            modifier = Modifier.padding(top = 4.dp),
            colors = CheckboxDefaults.colors( checkedColor = if(isSystemInDarkTheme())
            { MaterialTheme.colorScheme.onSecondary}
                    else{ MaterialTheme.colorScheme.primary},
                uncheckedColor = if(isSystemInDarkTheme())
                    {MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)}
                    else
                { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)},

               // Semi-transparent onSurface color for unchecked state
                checkmarkColor = MaterialTheme.colorScheme.onPrimary )
        )
        TextField(
            value = toDoItem.toDoItem,
            onValueChange = {
                onValueChange(
                    toDoItem.copy(toDoItem = it)
                )
            },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .align(Alignment.Top),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
            ),
            maxLines = Int.MAX_VALUE,
            keyboardOptions = KeyboardOptions.Default,
            textStyle = if (checkedState) {
                TextStyle(textDecoration = TextDecoration.LineThrough)
            } else {
                TextStyle.Default
            }
        )

        IconButton(onClick = {
            focusRequester.requestFocus()
            onDelete(toDoItem.id) },modifier = Modifier.padding(top = 4.dp))

        {
            Icon(
                Icons.Filled.Clear,
                tint= if(isSystemInDarkTheme())
                    MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.onTertiary,
                contentDescription = "Clear"
            )
        }
    }
}
