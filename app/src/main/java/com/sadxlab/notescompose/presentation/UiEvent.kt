package com.sadxlab.notescompose.presentation

sealed class UiEvent {
    object SaveSuccess : UiEvent()
    data class ShowToast(val message: String) : UiEvent()
}