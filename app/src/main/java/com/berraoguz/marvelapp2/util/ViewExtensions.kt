// utils/ViewExtensions.kt
package com.berraoguz.marvelapp2.util

import android.view.View

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}