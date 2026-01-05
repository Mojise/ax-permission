package com.ax.library.ax_permission.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionChecker
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal object AxPermissionPrefs {

    object Key {

    }

//    @JvmSynthetic
//    internal fun showRational(permission: Permission.Runtime, result: PermissionChecker.Result.Runtime) {
//        permission.constant
//
//        permission.constant
//    }
//
//    private fun getPermissionPreferencesKey(permission: Permission.Runtime): String {
//
//
//        TODO()
//    }

    private lateinit var context: Context

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("com.ax.library.ax_permission.prefs")

    private fun <T> runBlockingPrefsGet(key: Preferences. Key<T>): T? =
        runBlocking { context.dataStore.data.firstOrNull()?.get(key) }

    private fun <T> runBlockingPrefsSet(key: Preferences. Key<T>, value: T): Unit =
        runBlocking { context.dataStore.edit { preferences -> preferences[key] = value } }

    @JvmSynthetic
    internal fun setContext(application: Application) {
        context = application.applicationContext
    }
}