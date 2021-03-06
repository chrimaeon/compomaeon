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

import com.cmgapps.android.compomaeon.infra.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@ViewModelScoped
class TodoItemRepository @Inject constructor(private val todoDao: TodoDao) {

    fun getItems(): Flow<Resource<List<TodoItem>>> = todoDao.getTodos().map {
        Resource.Success(it)
    }.catch {
        Resource.Error(it)
    }

    suspend fun addItem(item: TodoItem) {
        todoDao.insertItem(item)
    }

    suspend fun removeItem(item: TodoItem) {
        todoDao.deleteItem(item)
    }

    suspend fun updateItem(item: TodoItem) {
        todoDao.updateItem(item)
    }
}
