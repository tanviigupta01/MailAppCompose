package com.example.assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment.models.MealDataClass
import com.example.assignment.models.MealResponseDataClass
import com.example.assignment.models.PostDataClass
import com.example.assignment.uiViews.CombinedList
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface JsonPlaceholderApi {
    @GET("posts")
    fun getPosts(): Single<List<PostDataClass>>
}

interface TheMealDbApi {
    @GET("search.php")
    fun getMeals(@Query("s") query: String): Single<MealResponseDataClass>
}


class MainActivity : ComponentActivity() {
    private val sharedViewModel by lazy { SharedViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonPlaceholderApi = createRetrofit("https://jsonplaceholder.typicode.com/")
            .create(JsonPlaceholderApi::class.java)

        val mealDbApi = createRetrofit("https://www.themealdb.com/api/json/v1/1/")
            .create(TheMealDbApi::class.java)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6C5CE7),
                    secondary = Color(0xFFA29BFE),
                    background = Color(0xFF1A1A1A),
                    surface = Color(0xFF2D2D2D),
                    onPrimary = Color.White,
                    onSecondary = Color.White,
                    onBackground = Color(0xFFE0E0E0),
                    onSurface = Color(0xFFE0E0E0)
                )
            ) {
                val navController = rememberNavController()
                var posts by remember { mutableStateOf<List<PostDataClass>>(emptyList()) }
                var meals by remember { mutableStateOf<List<MealDataClass>>(emptyList()) }
                var loading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                fun fetchData() {

                    loading = true
                    errorMessage = null

                    Single.zip(
                        jsonPlaceholderApi.getPosts(),
                        mealDbApi.getMeals("")
                    ) { postList, mealResp ->
                        val postSubset = postList.take(10)
                        val mealSubset = (mealResp.meals ?: emptyList()).take(10)
                        postSubset to mealSubset
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ (postData, mealData) ->
                            posts = postData
                            meals = mealData
                            loading = false
                            errorMessage = null
                        }, { error ->
                            error.printStackTrace()
                            loading = false
                            errorMessage = when (error) {
                                is java.net.UnknownHostException -> "No internet connection"
                                is retrofit2.HttpException -> "API error: ${error.code()}"
                                else -> "Something went wrong"
                            }
                        })
                }

                // Initial fetch
                LaunchedEffect(Unit) {
                    fetchData()
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1A1A1A)) // match theme
                        ) {
                            if (!loading && posts.isEmpty() && meals.isEmpty() && errorMessage != null) {
                                Card(
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .align(Alignment.Center),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .widthIn(min = 280.dp, max = 360.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Text(
                                            text = errorMessage ?: "An unexpected error occurred",
                                            color = Color(0xFFE0E0E0),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            onClick = { fetchData() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF6C5CE7),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            } else {
                                CombinedList(
                                    posts = posts,
                                    meals = meals,
                                    onItemClick = { combinedItem ->
                                        sharedViewModel.selectedItem = combinedItem
                                        navController.navigate("detail")
                                    },
                                    isLoading = loading
                                )

                                if (errorMessage != null) {
                                    Text(
                                        text = errorMessage ?: "",
                                        color = Color(0xFFE57373),
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 16.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(Color(0xFF2D2D2D))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { navController.navigate("viewFeedback") }) {
                                        Text(
                                            text = "View Feedback",
                                            color = Color(0xFF6C5CE7),
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }

                                    TextButton(onClick = { navController.navigate("feedback") }) {
                                        Text(
                                            text = "Give Feedback",
                                            color = Color(0xFF6C5CE7),
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                }

                            }
                        }
                    }

                    composable("feedback") {
                        FeedbackFormScreen(navController)
                    }

                    composable("viewFeedback") {
                        ViewFeedbackScreen(navController)
                    }

                    composable("detail") {
                        DetailScreen(
                            sharedViewModel = sharedViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory.create())
            .client(OkHttpClient())
            .build()
    }
}








