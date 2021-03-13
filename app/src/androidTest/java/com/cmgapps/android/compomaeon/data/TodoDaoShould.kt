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

package com.cmgapps.android.compomaeon.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class TodoDaoShould {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        todoDao = db.todoDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertTodo() = runBlockingTest {
        val todo = TodoItem("Test Item #1")
        todoDao.insertItem(todo)

        val dbTodo = todoDao.getTodos().take(1).toList()[0][0]
        assertThat(dbTodo, `is`(todo))
    }

    @Test
    fun updateTodo() = runBlockingTest {
        val todo = TodoItem("Test Item #1")
        todoDao.insertItem(todo)

        val updateTodo = todo.copy(task = "Todo Task Updated")
        todoDao.updateItem(updateTodo)

        val dbTodo = todoDao.getTodos().take(1).toList()[0][0]
        assertThat(dbTodo, `is`(updateTodo))
    }

    @Test
    fun deleteTodo() = runBlockingTest {
        val item1 = TodoItem("Test Item #1")
        todoDao.insertItem(item1)
        val item2 = TodoItem("Test Item #2")
        todoDao.insertItem(item2)

        todoDao.deleteItem(item1)

        val dbTodos = todoDao.getTodos().take(1).toList()[0]

        assertThat(dbTodos.size, `is`(1))
        assertThat(dbTodos[0], `is`(item2))
    }

    @Test
    fun deleteAllTodos() = runBlockingTest {
        for (i in 1..10) {
            todoDao.insertItem(TodoItem("item $i"))
        }

        var dbTodos = todoDao.getTodos().take(1).toList()[0]
        assertThat(dbTodos.size, `is`(10))

        todoDao.deleteItems()

        dbTodos = todoDao.getTodos().take(1).toList()[0]

        assertThat(dbTodos.size, `is`(0))
    }
}
