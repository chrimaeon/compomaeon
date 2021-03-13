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

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.cmgapps.android.compomaeon.R
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity(tableName = "todos")
data class TodoItem(
    val task: String,
    val icon: TodoIcon = TodoIcon.Default,
    @PrimaryKey val id: UUID = UUID.randomUUID(),
)

enum class TodoIcon(val imageVector: ImageVector, @StringRes val contentDescription: Int) {
    Square(Icons.Default.CropSquare, R.string.cd_expand),
    Done(Icons.Default.Done, R.string.cd_done),
    Event(Icons.Default.Event, R.string.cd_event),
    Privacy(Icons.Default.PrivacyTip, R.string.cd_privacy),
    Trash(Icons.Default.RestoreFromTrash, R.string.cd_restore);

    companion object {
        val Default = Square
    }
}

class TodoConverter {

    @TypeConverter
    fun toIcon(name: String): TodoIcon = when (name) {
        Icons.Default.CropSquare.name -> TodoIcon.Square
        Icons.Default.Done.name -> TodoIcon.Done
        Icons.Default.Event.name -> TodoIcon.Event
        Icons.Default.PrivacyTip.name -> TodoIcon.Privacy
        Icons.Default.RestoreFromTrash.name -> TodoIcon.Trash
        else -> error("$name cannot be mapped")
    }

    @TypeConverter
    fun fromIcon(icon: TodoIcon): String = icon.imageVector.name

    @TypeConverter
    fun toUuid(uuid: String): UUID = UUID.fromString(uuid)

    @TypeConverter
    fun fromUuid(uuid: UUID): String = uuid.toString()
}

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getTodos(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TodoItem)

    @Update
    suspend fun updateItem(item: TodoItem)

    @Delete
    suspend fun deleteItem(item: TodoItem)

    @Query("DELETE FROM todos")
    suspend fun deleteItems()
}

@Database(entities = [TodoItem::class], version = 1)
@TypeConverters(TodoConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
