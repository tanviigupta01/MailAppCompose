package com.example.assignment

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assignment.models.FeedbackDataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FeedbackDataStore {
    private val Context.dataStore by preferencesDataStore("feedback_store")
    private val FEEDBACK_KEY = stringPreferencesKey("feedback_list")

    fun getFeedback(context: Context): Flow<List<FeedbackDataClass>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[FEEDBACK_KEY] ?: return@map emptyList()
            val type = object : TypeToken<List<FeedbackDataClass>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    suspend fun saveFeedback(context: Context, feedback: FeedbackDataClass) {
        context.dataStore.edit { preferences ->
            val existingJson = preferences[FEEDBACK_KEY]
            val type = object : TypeToken<List<FeedbackDataClass>>() {}.type
            val currentList = if (existingJson != null) {
                Gson().fromJson<List<FeedbackDataClass>>(existingJson, type)
            } else emptyList()

            val updatedList = currentList + feedback
            preferences[FEEDBACK_KEY] = Gson().toJson(updatedList)
        }
    }
}




