package com.example.pogoda

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pogoda.Data.weatherModel
import com.example.pogoda.main.DialogSearch
import com.example.pogoda.main.MainScreen
import com.example.pogoda.main.TabLayout
import com.example.pogoda.ui.theme.PogodaTheme
import org.json.JSONObject

const val API_KEY ="1980ad230b80450db3e55544231110"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysList = remember { mutableStateOf(listOf<weatherModel>()) }
            val dialogState = remember { mutableStateOf(false) }
            val currentDay = remember { mutableStateOf(weatherModel(" ",
                "",
                "0.0",
                "",
                "",
                "0.0",
                "0.0",
                ""
            )) }
            if (dialogState.value)
            {
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, daysList, currentDay)
                })
            }

            getData("London", this, daysList, currentDay)
            Image(
                painter = painterResource(id = R.drawable.ssss),
                contentDescription = "im1",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column {
                MainScreen(currentDay, onClickSync ={
                    getData("London", this@MainActivity, daysList, currentDay)
                }, onClickSearch ={
                   dialogState.value = true
                }  )

                TabLayout(daysList, currentDay)
            }
//           Greeting("London", this)
        }
    }
}
fun getData(city:String, context: Context, daysList:MutableState<List<weatherModel>>,
            currentDay:MutableState<weatherModel>)
{
    val url = "https://api.weatherapi.com/v1/forecast.json?key=1980ad230b80450db3e55544231110&q=$city&days=3&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(Request.Method.GET,
        url,
        { response->
            val list = getWeatherByDays(response)
            currentDay.value=list[0]
            daysList.value = list
        },
        {
                error ->
        }
    )
    queue.add(stringRequest)
}

fun getWeatherByDays(response:String):List<weatherModel>
{
    if (response.isEmpty())
        return listOf()
    val list = ArrayList<weatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length())
    {
        val item = days[i] as JSONObject
        list.add(
            weatherModel(
                city, item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            ))
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}














@Composable
fun Greeting(name: String, context: Context) {
    val state = remember { mutableStateOf("Unknow") }
    Column(modifier =Modifier.fillMaxSize())
    {
        Box(modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center)
        {
            Text("Temp in $name= ${state.value} C")
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter)
        {
            Button(onClick = { getResalt(name, state, context)
            }, modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
            )
            {
                Text("Refresh")
            }
        }
    }
}

fun getResalt(city:String, state: MutableState<String>, context: Context)
{
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY&q=$city&aqi=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        { response->
           val obj = JSONObject(response)
           state.value = obj.getJSONObject("current").getString("temp_c")
//            Log.d("MyLog", "Responce: $response")
        },
        {
            error ->
        }
    )
    queue.add(stringRequest)
}