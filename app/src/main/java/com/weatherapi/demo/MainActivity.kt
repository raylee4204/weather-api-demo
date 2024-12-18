package com.weatherapi.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.weatherapi.demo.api.WeatherLocation
import com.weatherapi.demo.api.response.WeatherResponse
import com.weatherapi.demo.ui.WeatherViewModel
import com.weatherapi.demo.ui.theme.WeatherAPIDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherApp()
        }
    }
}

@Composable
fun WeatherApp(viewModel: WeatherViewModel = viewModel()) {
    WeatherAPIDemoTheme {
        WeatherScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val textFieldState = rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(query = textFieldState.value,
                    onSearch = { expanded = false },
                    onQueryChange = {
                        textFieldState.value = it
                        viewModel.searchCity(it)
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search, contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (textFieldState.value.isNotEmpty() && expanded) {
                            IconButton(onClick = {
                                expanded = false
                                textFieldState.value = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close, contentDescription = "Close"
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text("Search Location")
                    })
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            if (uiState.isLoadingSearch) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(count = uiState.searchResult.size) { index ->
                        val location = uiState.searchResult[index].location
                        SearchResultItem(
                            onClick = {
                                viewModel.saveCity(
                                    location.id!!, location.name
                                )
                                expanded = false
                                textFieldState.value = ""
                            }, searchResult = uiState.searchResult[index]
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            if (uiState.selectedCity.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No City Selected", style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Please Search For A City",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                CurrentWeatherCard(uiState.selectedCity, uiState.currentWeather)
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(selectedCity: String, currentWeather: WeatherResponse.CurrentWeather?) {
    if (currentWeather == null) return

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            modifier = Modifier.requiredSize(200.dp),
            model = currentWeather.condition.iconUrl,
            contentDescription = "Weather Condition",
            contentScale = ContentScale.Fit
        )
        CityNameText(selectedCity)
        TemperatureText(
            tempCelsius = currentWeather.tempCelsius, tempSize = 50.sp, unitSize = 24.sp
        )
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CurrentWeatherInfo("Humidity", "${currentWeather.humidity}%")
                CurrentWeatherInfo("UV", "${currentWeather.uv}")
                CurrentWeatherInfo("Feels like", "${currentWeather.feelsLikeC}°")
            }
        }
    }
}

@Composable
fun CityNameText(selectedCity: String) {
    val cityString = buildAnnotatedString {
        append(selectedCity)
        append(" ")
        appendInlineContent("1", "[icon]")
    }
    val inlineContent = mapOf(Pair("1", InlineTextContent(
        Placeholder(
            width = 20.sp,
            height = 20.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location),
            "",
            modifier = Modifier.fillMaxSize()
        )
    }))
    Text(
        text = cityString,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun CurrentWeatherInfo(title: String, body: String) {
    Column(
        modifier = Modifier.wrapContentWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title, style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        )
        Text(
            text = body, style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(name = "Current Weather Card", showBackground = true)
@Composable
fun CurrentWeatherCardPreview() {
    CurrentWeatherCard(
        selectedCity = "London", currentWeather = WeatherResponse.CurrentWeather(
            lastUpdatedEpoch = 10000,
            tempCelsius = 10.0,
            tempFahrenheit = 10.0,
            condition = WeatherResponse.Condition(
                text = "Sunny", icon = "//cdn.weatherapi.com/weather/64x64/day/143.png", code = 1000
            ),
            humidity = 10,
            feelsLikeC = 15.0,
            feelsLikeF = 15.0,
            uv = 5.0
        )
    )
}

@Composable
fun SearchResultItem(onClick: () -> Unit, searchResult: WeatherResponse) {
    val location = searchResult.location
    Log.d("SEARCH", "${location.name} + ${location.region}, ${location.country}")
    Card(
        shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
        ), onClick = onClick, modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = location.name, style = MaterialTheme.typography.titleMedium)
                TemperatureText(searchResult.current.tempCelsius)
            }
            AsyncImage(
                modifier = Modifier.requiredWidth(100.dp),
                model = searchResult.current.condition.iconUrl,
                contentDescription = "Weather Condition",
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun TemperatureText(tempCelsius: Double, tempSize: TextUnit = 30.sp, unitSize: TextUnit = 16.sp) {
    val annotatedTemp = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(tempCelsius.toString())
        }
        withStyle(
            style = SpanStyle(
                fontSize = unitSize, baselineShift = BaselineShift.Superscript
            )
        ) {
            append("°")
        }
    }
    Text(text = annotatedTemp, style = MaterialTheme.typography.bodyLarge.copy(fontSize = tempSize))
}

@Preview(name = "Search Result Item", showBackground = true)
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(
        onClick = {}, searchResult = WeatherResponse(
            location = WeatherLocation(
                id = null,
                name = "Van Ferit Melen Airport",
                region = "England",
                country = "United Kingdom",
                lat = 10.0,
                lon = 10.0
            ),
            current = WeatherResponse.CurrentWeather(
                lastUpdatedEpoch = 10000,
                tempCelsius = 10.0,
                tempFahrenheit = 10.0,
                condition = WeatherResponse.Condition(
                    text = "Sunny",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/143.png",
                    code = 1000
                ),
                humidity = 10,
                feelsLikeC = 10.0,
                feelsLikeF = 10.0,
                uv = 10.0
            )
        )
    )
}