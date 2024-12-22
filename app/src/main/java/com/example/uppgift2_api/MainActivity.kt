package com.example.uppgift2_api

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.uppgift2_api.ui.theme.Uppgift2_APITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class Joke(val id : String, val value : String)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Uppgift2_APITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun fetchJoke(onFactFetched: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.chucknorris.io/jokes/random")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        onFactFetched("Failed to fetch joke. Please try again.")
                    }
                    return@use
                }

                val jsonstring = response.body!!.string()
                val theJoke = Json { ignoreUnknownKeys = true }.decodeFromString<Joke>(jsonstring)

                withContext(Dispatchers.Main) {
                    onFactFetched(theJoke.value)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onFactFetched("Error: ${e.message}")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val jokeText = remember { mutableStateOf("") }


    Column {

        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Text(
            text = jokeText.value
        )

        Button(onClick = {
            fetchJoke { joke ->
                jokeText.value = joke
            }

        }) {
            Text(text = "Get a Chuck Norris joke")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Uppgift2_APITheme {
        Greeting("Android")
    }
}