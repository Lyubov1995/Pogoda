package com.example.pogoda.main

import androidx.compose.foundation.clickable
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pogoda.Data.weatherModel
import com.example.pogoda.ui.theme.BlueBlack
import com.example.pogoda.ui.theme.BlueLight

@Composable
fun MainList(list: List<weatherModel>, currentDay: MutableState<weatherModel>) {
    LazyColumn(modifier = Modifier.fillMaxSize())
    {
        itemsIndexed(
            list
        )
        { _, item ->
            UI(item, currentDay)
        }
    }
}


@Composable
fun UI(item: weatherModel, currentDay: MutableState<weatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clickable {
                if (item.hours.isEmpty())
                    return@clickable
                currentDay.value = item
            },

        backgroundColor = BlueLight,
        elevation = 0.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Text(text = item.time, color = BlueBlack)
                Text(text = item.condition, color = BlueBlack)
            }
            Text(
                text = item.currentTemp.ifEmpty {
                    "${
                        item.maxTemp.toFloat().toInt()
                    } °/${item.minTemp.toFloat().toInt()}°"
                },
                color = BlueBlack,
                style = TextStyle(fontSize = 25.sp)
            )
            AsyncImage(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(35.dp),
                model = "https:${item.icon}",
                contentDescription = "image"
            )
        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit:(String)->Unit) {
    val dialogText = remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
        dialogState.value = false
    },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text(text = "Ok")
            }
        },
        dismissButton =
        {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Column(modifier = Modifier.fillMaxWidth())
            {
                Text(text = "Введите название города: ")
                TextField(value =dialogText.value, onValueChange =
                {
                    dialogText.value = it
                })
            }
        }

    )
}
