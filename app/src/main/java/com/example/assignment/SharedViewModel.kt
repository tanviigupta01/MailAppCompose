package com.example.assignment

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.assignment.models.CombinedItem

class SharedViewModel : ViewModel() {
    var selectedItem by mutableStateOf<CombinedItem?>(null)
}

