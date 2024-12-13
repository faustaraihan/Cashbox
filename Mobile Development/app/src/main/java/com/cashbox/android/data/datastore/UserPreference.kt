package com.cashbox.android.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference(private val dataStore: DataStore<Preferences>) {
    private val isUserLoginKey = booleanPreferencesKey("is_user_login")
    private val userTokenKey = stringPreferencesKey("user_token")
    private val userPhotoKey = stringPreferencesKey("user_photo")
    private val usernameKey = stringPreferencesKey("user_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userUidKey = stringPreferencesKey("user_uid")

    val isUserLogin: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[isUserLoginKey] ?: false
    }
    val userToken: Flow<String> = dataStore.data.map { preferences ->
        preferences[userTokenKey] ?: ""
    }
    val userPhoto: Flow<String> = dataStore.data.map { preferences ->
        preferences[userPhotoKey] ?: ""
    }
    val username: Flow<String> = dataStore.data.map { preferences ->
        preferences[usernameKey] ?: ""
    }
    val userEmail: Flow<String> = dataStore.data.map { preferences ->
        preferences[userEmailKey] ?: ""
    }
    val userUid: Flow<String> = dataStore.data.map { preferences ->
        preferences[userUidKey] ?: ""
    }

    suspend fun updateUserLoginStatusAndToken(status: Boolean, token: String) {
        dataStore.edit { preferences ->
            preferences[isUserLoginKey] = status
            preferences[userTokenKey] = token
        }
    }

    suspend fun updateUserData(
        userPhoto: String,
        username: String,
        userEmail: String,
        userUid: String
    ) {
        dataStore.edit { preferences ->
            preferences[userPhotoKey] = userPhoto
            preferences[usernameKey] = username
            preferences[userEmailKey] = userEmail
            preferences[userUidKey] = userUid
        }
    }
}