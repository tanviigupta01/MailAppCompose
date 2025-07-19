package com.example.assignment.models

import java.io.Serializable

data class MealDataClass(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String?
) : Serializable

