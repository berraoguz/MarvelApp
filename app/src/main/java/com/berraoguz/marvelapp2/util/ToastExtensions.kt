// utils/ToastExtensions.kt
package com.berraoguz.marvelapp2.util

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
