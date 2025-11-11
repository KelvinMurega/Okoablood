package com.example.okoablood.data.util

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.suspendAwait(): T = suspendCoroutine { continuation ->
    this.addOnSuccessListener { result ->
        continuation.resume(result)
    }.addOnFailureListener { exception ->
        continuation.resumeWithException(exception)
    }
}
