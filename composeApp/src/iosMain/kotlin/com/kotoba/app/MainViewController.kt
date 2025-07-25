package com.kotoba.app

import androidx.compose.ui.window.ComposeUIViewController

@Suppress("ktlint:standard:function-naming")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
        },
    ) {
        App()
    }
