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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.cmgapps.android.compomaeon

import com.cmgapps.android.compomaeon.data.TodoDao
import com.cmgapps.android.compomaeon.data.TodoItem
import com.cmgapps.android.compomaeon.data.TodoItemRepository
import com.cmgapps.android.compomaeon.infra.Resource
import com.cmgapps.android.compomaeon.util.generateRandomTodoItem
import com.cmgapps.extension.CoroutinesTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class TodoViewModelShould {

    private lateinit var viewModel: TodoViewModel

    @BeforeEach
    fun beforeEach() {
        viewModel = TodoViewModel(TodoItemRepository(TestTodoDao()))
    }

    @Test
    fun `remove items`() = runBlockingTest {
        val item1 = generateRandomTodoItem()
        val item2 = generateRandomTodoItem()
        viewModel.addItem(item1)
        viewModel.addItem(item2)

        viewModel.removeItem(item1)
        assertThat(viewModel.todoItems.single(), `is`(Resource.Success(listOf(item2))))
    }

    @Test
    fun `set edit item`() {
        val item = generateRandomTodoItem()
        viewModel.addItem(item)

        viewModel.onEditItemSelected(item)

        assertThat(viewModel.currentEditItem, `is`(item))
    }

    @Test
    fun `update edit item`() = runBlockingTest {
        val item = generateRandomTodoItem()
        viewModel.addItem(item)

        viewModel.onEditItemSelected(item)
        val copy = item.copy(task = "ChangedTask")
        viewModel.onEditItemChange(copy)
        viewModel.onEditDone()

        assertThat(viewModel.todoItems.single(), `is`(Resource.Success(listOf(copy))))
    }
}

private class TestTodoDao : TodoDao {

    private val todos = mutableListOf<TodoItem>()

    override fun getTodos(): Flow<List<TodoItem>> = flowOf(todos)

    override suspend fun insertItem(item: TodoItem) {
        todos.add(item)
    }

    override suspend fun updateItem(item: TodoItem) {
        todos[todos.indexOfFirst { it.id == item.id }] = item
    }

    override suspend fun deleteItem(item: TodoItem) {
        todos.remove(item)
    }
}
