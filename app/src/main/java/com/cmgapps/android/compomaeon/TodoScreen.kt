/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalMaterialApi::class)

package com.cmgapps.android.compomaeon

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.android.compomaeon.data.TodoIcon
import com.cmgapps.android.compomaeon.data.TodoItem
import com.cmgapps.android.compomaeon.infra.Resource
import com.cmgapps.android.compomaeon.infra.Resource.Loading
import com.cmgapps.android.compomaeon.infra.Resource.Success
import com.cmgapps.android.compomaeon.ui.TodoTheme
import com.cmgapps.android.compomaeon.util.generateRandomTodoItem

@Composable
fun TodoScreen(
    items: Resource<List<TodoItem>>,
    currentlyEditing: TodoItem?,
    onAddItem: (TodoItem) -> Unit,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    if (items is Resource.Error) {
        val message = stringResource(R.string.whoops)
        LaunchedEffect(scaffoldState) {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = { TodoFloatingActionButton(onAddItem = onAddItem) },
    ) {
        TodoContent(
            items = items,
            currentlyEditing = currentlyEditing,
            onAddItem = onAddItem,
            onRemoveItem = onRemoveItem,
            onStartEdit = onStartEdit,
            onEditItemChange = onEditItemChange,
            onEditDone = onEditDone
        )
    }
}

@Composable
private fun TodoFloatingActionButton(onAddItem: (TodoItem) -> Unit) {
    FloatingActionButton(
        modifier = Modifier.testTag("AddTodoFAB"),
        onClick = { onAddItem(generateRandomTodoItem()) }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.cd_add_item)
        )
    }
}

@Composable
private fun TodoContent(
    items: Resource<List<TodoItem>>?,
    currentlyEditing: TodoItem?,
    onAddItem: (TodoItem) -> Unit,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
) {
    Column {
        val enableTopSection = currentlyEditing == null

        TodoItemInputBackground(elevate = enableTopSection, modifier = Modifier.fillMaxWidth()) {
            if (enableTopSection) {
                TodoItemEntryInput(onAddItem)
            } else {
                Text(
                    stringResource(R.string.editing_item),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }

        when (items) {
            is Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
            is Success -> TodoList(
                items = items.data,
                currentlyEditing = currentlyEditing,
                onRemoveItem = onRemoveItem,
                onStartEdit = onStartEdit,
                onEditItemChange = onEditItemChange,
                onEditDone = onEditDone,
                modifier = Modifier.weight(1f)
            )
            else -> Unit
        }
    }
}

@Composable
private fun TodoList(
    items: List<TodoItem>,
    currentlyEditing: TodoItem?,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.testTag("TodoList"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp + 16.dp)
    ) {
        items(key = { it.id }, items = items) { todo ->
            if (currentlyEditing?.id == todo.id) {
                TodoItemInlineEditor(
                    item = currentlyEditing,
                    onEditItemChange = onEditItemChange,
                    onEditDone = onEditDone,
                )
            } else {
                SwipeToDeleteListItem(
                    item = todo,
                    modifier = modifier,
                    onStartEdit = onStartEdit,
                    onDelete = onRemoveItem
                )
            }
            Divider()
        }
    }
}

@Composable
private fun TodoItemInputBackground(
    elevate: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val animatedElevation by animateDpAsState(if (elevate) 1.dp else 0.dp, TweenSpec(500))
    Surface(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        elevation = animatedElevation,
        shape = RectangleShape,
    ) {
        Row(
            modifier = modifier.animateContentSize(animationSpec = TweenSpec(300)),
            content = content
        )
    }
}

@Composable
fun SwipeToDeleteListItem(
    item: TodoItem,
    modifier: Modifier = Modifier,
    onStartEdit: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    val dismissState = rememberDismissState()

    if (dismissState.currentValue != Default) {
        onDelete(item)
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    Default -> Color.LightGray
                    else -> Color.Red
                }
            )

            val alignment = when (direction) {
                StartToEnd -> Alignment.CenterStart
                EndToStart -> Alignment.CenterEnd
            }

            val scale by animateFloatAsState(if (dismissState.targetValue == Default) 0.75f else 1f)
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        val isDismissing = dismissState.dismissDirection != null
        val cornerRadius by animateDpAsState(targetValue = if (isDismissing) 4.dp else 0.dp)
        val background = if (isDismissing) MaterialTheme.colors.surface else Color.Transparent
        ListItem(
            modifier = modifier
                .background(color = background, shape = RoundedCornerShape(cornerRadius))
                .clickable { onStartEdit(item) },
            text = { Text(item.task) },
            icon = {
                Icon(
                    imageVector = item.icon.imageVector,
                    contentDescription = stringResource(item.icon.contentDescription)
                )
            }
        )
    }
}

@Composable
private fun TodoItemEntryInput(onItemComplete: (TodoItem) -> Unit) {
    val (text, setText) = remember { mutableStateOf("") }
    val (icon, setIcon) = remember { mutableStateOf(TodoIcon.Default) }
    val iconsVisible = text.isNotBlank()

    val submit = {
        if (text.isNotBlank()) {
            onItemComplete(TodoItem(text, icon))
        }
        setIcon(TodoIcon.Default)
        setText("")
    }

    TodoItemInput(
        text = text,
        buttonText = stringResource(R.string.add),
        onTextChange = setText,
        icon = icon,
        onIconChange = setIcon,
        submit = submit,
        iconsVisible = iconsVisible
    )
}

@Composable
private fun TodoItemInput(
    text: String,
    buttonText: String,
    onTextChange: (String) -> Unit,
    icon: TodoIcon,
    onIconChange: (TodoIcon) -> Unit,
    submit: () -> Unit,
    iconsVisible: Boolean,
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            TodoInputText(
                text,
                onTextChange,
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .testTag("TodoItemInput"),
                onImeAction = submit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.align(Alignment.CenterVertically)) {
                TodoEditButton(
                    onClick = submit,
                    text = buttonText,
                    enabled = text.isNotBlank()
                )
            }
        }
        if (iconsVisible) {
            AnimatedIconRow(icon, onIconChange, Modifier.padding(top = 8.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TodoInputText(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
                keyboardController?.hideSoftwareKeyboard()
            }
        ),
        modifier = modifier
    )
}

@Composable
private fun TodoEditButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    TextButton(
        onClick = {
            onClick()
            focusManager.clearFocus()
        },
        shape = CircleShape,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedIconRow(
    icon: TodoIcon,
    onIconChange: (TodoIcon) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    initialVisibility: Boolean = false,
) {
    // remember these specs so they don't restart if recomposing during the animation
    // this is required since TweenSpec restarts on interruption
    val enter = remember { fadeIn(animationSpec = TweenSpec(300, easing = FastOutLinearInEasing)) }
    val exit = remember { fadeOut(animationSpec = TweenSpec(100, easing = FastOutSlowInEasing)) }
    Box(modifier.defaultMinSize(minHeight = 16.dp)) {
        AnimatedVisibility(
            visible = visible,
            initiallyVisible = initialVisibility,
            enter = enter,
            exit = exit,
        ) {
            IconRow(icon, onIconChange)
        }
    }
}

@Composable
private fun IconRow(
    icon: TodoIcon,
    onIconChange: (TodoIcon) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        for (todoIcon in TodoIcon.values()) {
            SelectableIconButton(
                icon = todoIcon.imageVector,
                iconContentDescription = todoIcon.contentDescription,
                onIconSelected = { onIconChange(todoIcon) },
                isSelected = todoIcon == icon
            )
        }
    }
}

@Composable
private fun SelectableIconButton(
    icon: ImageVector,
    @StringRes iconContentDescription: Int,
    onIconSelected: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    }
    TextButton(
        onClick = { onIconSelected() },
        shape = CircleShape,
        modifier = modifier
    ) {
        Column {
            Icon(
                imageVector = icon,
                tint = tint,
                contentDescription = stringResource(iconContentDescription)
            )
            if (isSelected) {
                Box(
                    Modifier
                        .padding(top = 3.dp)
                        .width(icon.defaultWidth)
                        .height(1.dp)
                        .background(tint)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun TodoItemInlineEditor(
    item: TodoItem,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
) = TodoItemInput(
    text = item.task,
    buttonText = stringResource(R.string.save),
    onTextChange = { onEditItemChange(item.copy(task = it)) },
    icon = item.icon,
    onIconChange = { onEditItemChange(item.copy(icon = it)) },
    submit = onEditDone,
    iconsVisible = true
)

// region Preview
@Preview
@Composable
fun PreviewTodoScreen() {
    val items = listOf(
        TodoItem("Line1", TodoIcon.Event),
        TodoItem("line2", TodoIcon.Trash)
    )

    TodoTheme {
        TodoScreen(Success(items), null, { }, { }, { }, { }, { })
    }
}

@Preview
@Composable
fun PreviewTodoScreenDark() {
    val items = listOf(
        TodoItem("Line1", TodoIcon.Event),
        TodoItem("line2", TodoIcon.Trash)
    )

    TodoTheme(darkTheme = true) {
        TodoScreen(Success(items), null, { }, { }, { }, { }, { })
    }
}

@Preview
@Composable
fun PreviewTodoItemInput() = TodoItemEntryInput { }
// endregion
