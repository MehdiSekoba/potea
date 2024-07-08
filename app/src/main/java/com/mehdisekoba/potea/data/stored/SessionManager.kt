package com.mehdisekoba.potea.data.stored

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mehdisekoba.potea.utils.IS_ENABLED
import com.mehdisekoba.potea.utils.ONBOARD
import com.mehdisekoba.potea.utils.SESSION_AUTH_DATA
import com.mehdisekoba.potea.utils.USER_TOKEN_DATA
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val appContext = context.applicationContext

    companion object {
        //save token
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            SESSION_AUTH_DATA
        )
        private val USER_TOKEN = stringPreferencesKey(USER_TOKEN_DATA)

        //save onboarding
        private val ONBOARDING = stringPreferencesKey(ONBOARD)
    }

    suspend fun saveToken(token: String) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    val getToken: Flow<String?> = appContext.dataStore.data.map {
        it[USER_TOKEN]
    }

    suspend fun saveOnboardingPreference(isEnabled: Boolean) {
        appContext.dataStore.edit {
            it[booleanPreferencesKey(IS_ENABLED)] = isEnabled
        }
    }

    val getOnboardingPreference: Flow<Boolean?> =
        appContext.dataStore.data.map {
            it[booleanPreferencesKey(IS_ENABLED)] ?: false
        }
    suspend fun clearToken(){
        appContext.dataStore.edit {
            it.clear()
        }
    }
}