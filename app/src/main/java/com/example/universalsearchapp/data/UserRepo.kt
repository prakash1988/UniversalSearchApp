package com.example.universalsearchapp.data

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo  constructor(private val data: UserDataSource) {

    val allFavoriteUser = liveData {
        withContext(Dispatchers.IO) {
            emit(data.getUsers())
        }

    }
}