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
import com.cmgapps.android.compomaeon.infra.noPosition

class TodoViewModel : ViewModel() {

    private var currentEditPosition by mutableStateOf(noPosition)
    var todoItems: List<TodoItem> by mutableStateOf(listOf())

    val currentEditItem: TodoItem?
        get() = todoItems.getOrNull(currentEditPosition)

    fun addItem(item: TodoItem) {
        todoItems = todoItems + item
    }

    fun removeItem(item: TodoItem) {
        todoItems = todoItems.toMutableList().also { it.remove(item) }
        onEditDone()
    }

    fun onEditItemSelected(item: TodoItem) {
        currentEditPosition = todoItems.indexOf(item)
    }

    fun onEditDone() {
        currentEditPosition = noPosition
    }

    fun onEditItemChange(item: TodoItem) {
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id) {
            "You can only change an item with the same id as currentEditItem"
        }

        todoItems = todoItems.toMutableList().also {
            it[currentEditPosition] = item
        }
    }
}
