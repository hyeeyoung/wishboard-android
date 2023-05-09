package com.hyeeyoung.wishboard.util.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.toStateFlow(coroutineScope: CoroutineScope, initialValue: T) =
    stateIn(coroutineScope, SharingStarted.Eagerly, initialValue)

inline fun <T, R: LifecycleOwner> R.collectFlow(
    flow: Flow<T>, crossinline block: suspend (T) -> Unit
) {
    when (this) {
        is AppCompatActivity -> flow.flowWithLifecycle(lifecycle).onEach { block(it) }
            .launchIn(lifecycleScope)

        is Fragment -> flow.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { block(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        else -> {}
    }
}