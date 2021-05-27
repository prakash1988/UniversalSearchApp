package com.example.universalsearchapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.universalsearchapp.data.User
import com.example.universalsearchapp.data.UserDataSource
import com.example.universalsearchapp.data.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepo

    init {
        val source = UserDataSource(application.contentResolver)
        repository = UserRepo(source)
    }

    val allFavoriteUser: LiveData<List<User>> = repository.allFavoriteUser
}