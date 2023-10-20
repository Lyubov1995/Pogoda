package com.example.pogoda.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pogoda.Data.weatherModel
import com.example.pogoda.R
import com.example.pogoda.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainScreen(currentDay:MutableState<weatherModel>, onClickSync:()->Unit, onClickSearch:()->Unit) {
    Column(
        modifier = Modifier.padding(5.dp)
    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            elevation = 0.dp,
            backgroundColor = BlueLight
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        text = currentDay.value.time, fontSize = 15.sp,
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "im2",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(top = 3.dp, end = 8.dp)
                    )
                }
                Text(
                    text = currentDay.value.city, fontSize = 24.sp,
                    color = Color.White
                )
                Text(
                    text =if(currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() +"°C"
                    else "${currentDay.value.maxTemp.toFloat().toInt()} °C/${currentDay.value.minTemp.toFloat().toInt()}°C"
                    , fontSize = 65.sp,
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                     onClickSearch.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "icon",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "${currentDay.value.maxTemp.toFloat().toInt()} °/${currentDay.value.minTemp.toFloat().toInt()}°",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    IconButton(onClick = {
                        onClickSync.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "icon",
                            tint = Color.White
                        )
                    }
                }
            }
        }

    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<weatherModel>>, currentDay: MutableState<weatherModel>) {
    val tabList = listOf("Часы", "Дни")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex, indicator = {  position ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(position[tabIndex]))
            },
            containerColor = BlueLight
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(selected = false, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                    text = {
                        Text(text = text, color = Color.White, fontSize = 25.sp)
                    })
            }
        }
        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier =  Modifier.weight(1.0f)) {
                index ->
            val list = when(index)
            {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else ->daysList.value
            }
                MainList(list, currentDay)
        }
    }
}

fun getWeatherByHours(hours:String):List<weatherModel>
{
    if (hours.isEmpty())
        return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<weatherModel>()

    for (i in 0 until hoursArray.length())
    {
        val item = hoursArray[i] as JSONObject
        list.add(
            weatherModel(
                "", item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString()+"°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
        "", "", ""
            ))
    }
    return list
}