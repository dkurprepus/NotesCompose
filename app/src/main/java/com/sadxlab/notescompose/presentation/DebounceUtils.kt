package com.sadxlab.notescompose.presentation

private var lastClickTime = 0L
fun debouncedClick(debouncedInterval: Long = 1000L, onClick: () -> Unit) {
    val now = System.currentTimeMillis()
    if (now - lastClickTime >= debouncedInterval) {
        lastClickTime = now
        onClick()
    }
}