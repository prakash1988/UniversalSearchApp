package com.example.universalsearchapp.data

import android.content.ContentResolver
import com.example.universalsearchapp.data.DatabaseContract.UserColumns.Companion.MY_CONTENT_URI

class UserDataSource(private val contentResolver: ContentResolver) {
    fun getUsers(): List<User> {
        val result: MutableList<User> = mutableListOf()

        val cursor = contentResolver.query(
             MY_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.apply {
            while (moveToNext()) {
                result.add(
                        User(
                                getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns.ID)),
                                getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.NAME)),
                                getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.IMAGE)),
                                getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.DESC))
                        )
                )
            }
            close()
        }
        return result.toList()
    }
}