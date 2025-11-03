package com.ax.library.ax_permission.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal fun LifecycleOwner.repeatOnCreated(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED, block)
    }
}

internal fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

internal fun LifecycleOwner.repeatOnResumed(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED, block)
    }
}

internal fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(block = block)
}

internal fun ViewModel.launched(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(block = block) // No Return
}

internal fun ViewModel.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(context = Dispatchers.Main, block = block)
}

internal fun ViewModel.launchedMain(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(context = Dispatchers.Main, block = block) // No Return
}

internal fun ViewModel.launchDefault(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(context = Dispatchers.Default, block = block)
}

internal fun ViewModel.launchedDefault(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(context = Dispatchers.Default, block = block) // No Return
}

internal fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(context = Dispatchers.IO, block = block)
}

internal fun ViewModel.launchedIO(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(context = Dispatchers.IO, block = block) // No Return
}