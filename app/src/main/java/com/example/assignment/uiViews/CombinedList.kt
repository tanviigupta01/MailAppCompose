package com.example.assignment.uiViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment.models.CombinedItem
import com.example.assignment.models.MealDataClass
import com.example.assignment.models.PostDataClass


@Composable
fun CombinedList(
    posts: List<PostDataClass>,
    meals: List<MealDataClass>,
    onItemClick: (CombinedItem) -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF2D2D2D)
                    )
                )
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Meals",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFA29BFE)
                    ),
                    modifier = Modifier.padding(top = 36.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isLoading) {
                    items(5) {
                        ShimmerCard()
                    }
                } else {
                    items(posts.zip(meals)) { (post, meal) ->
                        val combinedItem = CombinedItem(post, meal)
                        MealPostCard(post, meal, onClick = {
                            if (!isLoading) {
                                onItemClick(combinedItem)
                            }
                        })
                    }
                }
            }
        }
    }
}