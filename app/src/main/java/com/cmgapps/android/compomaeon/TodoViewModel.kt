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

package com.cmgapps.android.compomaeon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.android.compomaeon.data.TodoItem
import com.cmgapps.android.compomaeon.data.TodoItemRepository
import com.cmgapps.android.compomaeon.infra.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
) : ViewModel() {

    var todoItems: Flow<Resource<List<TodoItem>>> = todoItemRepository.getItems()

    var currentEditItem: TodoItem? by mutableStateOf(null)
        private set

    fun addItem(item: TodoItem) {
        viewModelScope.launch {
            todoItemRepository.addItem(item)
        }
    }

    fun removeItem(item: TodoItem) {
        viewModelScope.launch {
            todoItemRepository.removeItem(item)
        }
    }

    fun onEditItemSelected(item: TodoItem) {
        currentEditItem = item
    }

    fun onEditDone() {
        val currentItem = requireNotNull(currentEditItem)

        viewModelScope.launch {
            todoItemRepository.updateItem(currentItem)
            currentEditItem = null
        }
    }

    fun onEditItemChange(item: TodoItem) {
        currentEditItem = item
    }
}
