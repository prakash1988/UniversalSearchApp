package com.example.universalsearchapp.data

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {
    private const val AUTHORITY = "com.example.searchable"
    private const val SCHEME = "content"

    class UserColumns : BaseColumns {
        companion object {
            private const val TABLE_NAME = "user_table"
            const val ID = "id"
            const val NAME = "name"
            const val IMAGE = "image"
            const val DESC = "desc"

            val MY_CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(TABLE_NAME)
                    .build()
        }
    }
}