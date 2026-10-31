package com.switsh.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LastConnectionDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $PROFILE_TABLE (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                display_name TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE $LAST_CONNECTION_TABLE (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                display_name TEXT NOT NULL,
                connected_at INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun saveProfileName(displayName: String) {
        val normalizedName = displayName.trim()
        if (normalizedName.isEmpty()) return

        val values = ContentValues().apply {
            put(ID_COLUMN, PROFILE_ROW_ID)
            put(DISPLAY_NAME_COLUMN, normalizedName)
        }
        writableDatabase.insertWithOnConflict(
            PROFILE_TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    fun getLastConnectedName(): String? =
        readableDatabase.query(
            LAST_CONNECTION_TABLE,
            arrayOf(DISPLAY_NAME_COLUMN),
            "$ID_COLUMN = ?",
            arrayOf(LAST_CONNECTION_ROW_ID.toString()),
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0).takeIf(String::isNotBlank) else null
        }

    fun recordSuccessfulConnection() {
        val profileName = readableDatabase.query(
            PROFILE_TABLE,
            arrayOf(DISPLAY_NAME_COLUMN),
            "$ID_COLUMN = ?",
            arrayOf(PROFILE_ROW_ID.toString()),
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0).takeIf(String::isNotBlank) else null
        } ?: return

        val values = ContentValues().apply {
            put(ID_COLUMN, LAST_CONNECTION_ROW_ID)
            put(DISPLAY_NAME_COLUMN, profileName)
            put(CONNECTED_AT_COLUMN, System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict(
            LAST_CONNECTION_TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    private companion object {
        const val DATABASE_NAME = "switsh.db"
        const val DATABASE_VERSION = 1
        const val PROFILE_TABLE = "account_profile"
        const val LAST_CONNECTION_TABLE = "last_connection"
        const val ID_COLUMN = "id"
        const val DISPLAY_NAME_COLUMN = "display_name"
        const val CONNECTED_AT_COLUMN = "connected_at"
        const val PROFILE_ROW_ID = 1
        const val LAST_CONNECTION_ROW_ID = 1
    }
}
