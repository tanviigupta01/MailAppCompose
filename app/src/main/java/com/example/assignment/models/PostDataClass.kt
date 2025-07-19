package com.example.assignment.models

import java.io.Serializable

data class PostDataClass(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
) : Serializable


