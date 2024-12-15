package com.yuva.notetakingapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, ToDoItem::class], version = 9)
abstract class NoteRoomDataBase : RoomDatabase() {
    abstract fun noteDao(): NoteTakingDao
        companion object {
        const val DATABASE_NAME = "note_database"
    }

}
