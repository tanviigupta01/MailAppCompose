package com.example.assignment.models

import java.io.Serializable

data class CombinedItem(
    val post: PostDataClass,
    val meal: MealDataClass
) : Serializable
