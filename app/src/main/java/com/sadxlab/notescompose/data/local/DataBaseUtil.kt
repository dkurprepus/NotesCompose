package com.sadxlab.notescompose.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DataBaseUtil {
    val MIGRATION_1_2= object : Migration(1,2){
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN timestamp INT NOT NULL DEFAULT 0")
        }

    }
}