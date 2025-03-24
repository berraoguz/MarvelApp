// extensions.kt
package com.berraoguz.marvelapp2.util

// String için limitText uzantısı
fun String.limitText(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength) + "..."
    } else {
        this
    }
}
