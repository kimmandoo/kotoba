package com.kotoba.di

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextProvider {
    private lateinit var _context: Context
    val context: Context
        get() = _context

    fun initialize(context: Context) {
        _context = context.applicationContext
    }
}
