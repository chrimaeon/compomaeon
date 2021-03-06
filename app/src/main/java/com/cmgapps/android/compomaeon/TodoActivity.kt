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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.cmgapps.android.compomaeon.ui.TodoTheme

class TodoActivity : AppCompatActivity() {

    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                Surface {
                    TodoActivityScreen(viewModel)
                }
            }
        }
    }
}

@Composable
private fun TodoActivityScreen(viewModel: TodoViewModel) {
    TodoScreen(
        items = viewModel.todoItems,
        currentlyEditing = viewModel.currentEditItem,
        onAddItem = viewModel::addItem,
        onRemoveItem = viewModel::removeItem,
        onStartEdit = viewModel::onEditItemSelected,
        onEditItemChange = viewModel::onEditItemChange,
        onEditDone = viewModel::onEditDone
    )
}