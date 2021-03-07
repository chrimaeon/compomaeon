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

package com.cmgapps.android.compomaeon.data

import com.cmgapps.android.compomaeon.util.generateRandomTodoItem
import com.cmgapps.extension.CoroutinesTestExtension
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(value = [CoroutinesTestExtension::class, MockitoExtension::class])
class TodoItemRepositoryShould {

    @Mock
    private lateinit var dao: TodoDao

    private lateinit var repository: TodoItemRepository

    @BeforeEach
    fun beforeEach() {
        repository = TodoItemRepository(dao)
    }

    @Test
    fun `get items from dao`() = runBlockingTest {
        val list = listOf(generateRandomTodoItem())
        val dao = mock<TodoDao> {
            on { getTodos() } doReturn flowOf(list)
        }

        val repo = TodoItemRepository(dao)
        assertThat(repo.getItems().single(), `is`(list))
    }

    @Test
    fun addItem() = runBlockingTest {
        val item = generateRandomTodoItem()
        repository.addItem(item)

        verify(dao).insertItem(item)
    }

    @Test
    fun removeItem() = runBlockingTest {
        val item = generateRandomTodoItem()
        repository.removeItem(item)

        verify(dao).deleteItem(item)
    }

    @Test
    fun updateItem() = runBlockingTest {
        val item = generateRandomTodoItem()
        repository.updateItem(item)

        verify(dao).updateItem(item)
    }
}
