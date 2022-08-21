package utils

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val defaultState = Lifecycle.State.STARTED

fun <T> Fragment.collectLifecycleFlow(
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    collectFlow(viewLifecycleOwner, flow, map, collect)
}


fun <T> Fragment.collectLatestLifecycleFlow(
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    collectLatestFlow(viewLifecycleOwner, flow, map, collect)
}


fun <T> ComponentActivity.collectLifecycleFlow(
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    collectFlow(this, flow, map, collect)
}


fun <T> ComponentActivity.collectLatestLifecycleFlow(
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    collectLatestFlow(this, flow, map, collect)
}


private fun <T> collectFlow(
    lifecycleOwner: LifecycleOwner,
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(defaultState) {
            flow.map {
                if (map != null)
                    map(it)
                else
                    it
            }.collect(collect)
        }
    }
}


private fun <T> collectLatestFlow(
    lifecycleOwner: LifecycleOwner,
    flow: Flow<T>,
    map: (suspend (T) -> T)? = null,
    collect: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(defaultState) {
            flow.map {
                if (map != null)
                    map(it)
                else
                    it
            }.collectLatest(collect)
        }
    }
}