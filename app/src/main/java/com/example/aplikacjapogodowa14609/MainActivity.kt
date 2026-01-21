package com.example.aplikacjapogodowa14609

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikacjapogodowa14609.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class ClothingInfo(
    val summary: String,
    val detailedAdvice: String,
    val icon: ImageVector,
    val cardColor: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var cityInput by remember { mutableStateOf("") }
            var cityName by remember { mutableStateOf("Wpisz miasto") }
            var temp by remember { mutableStateOf("--") }
            var tempApparent by remember { mutableStateOf("--") }
            var humidity by remember { mutableStateOf("--") }
            var windSpeed by remember { mutableStateOf("--") }
            var windDirection by remember { mutableStateOf("--") }
            var time by remember { mutableStateOf("--") }
            var pressureSeaLevel by remember { mutableStateOf("--") }
            var weatherDesc by remember { mutableStateOf("Nieznana") }
            var rainInfo by remember { mutableStateOf("Brak opadów") }
            var uvIndex by remember { mutableStateOf("--") }
            var cloudCover by remember { mutableStateOf("--") }
            var visibility by remember { mutableStateOf("--") }
            var isRainingNow by remember { mutableStateOf(false) }
            var isWindyNow by remember { mutableStateOf(false) }
            var clothingInfo by remember { mutableStateOf<ClothingInfo?>(null) }
            var showDetails by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()


            fun getWeatherDescription(code: Int): String {
                return when (code) {
                    1000 -> "Czyste niebo"
                    1100 -> "Przeważnie słonecznie"
                    1101 -> "Częściowe zachmurzenie"
                    1102 -> "Pochmurno"
                    1001 -> "Zachmurzenie całkowite"
                    2000, 2100 -> "Mgiełka / Mgła"
                    4000, 4200 -> "Lekki deszcz"
                    4001 -> "Deszcz"
                    5000, 5100 -> "Śnieg"
                    6000, 6001 -> "Marznący deszcz"
                    7000, 7101 -> "Grad / Deszcz ze śniegiem"
                    8000 -> "Burza"
                    else -> "Inna ($code)"
                }
            }

            fun updateClothingAdvice(temperature: Double, wind: Double, rain: Int, snow: Int) {
                isRainingNow = rain > 0 || snow > 0
                isWindyNow = wind > 6.0

                clothingInfo = when {
                    temperature < -5 -> ClothingInfo(
                        "Bardzo zimno!",
                        "Załóż najcieplejszą kurtkę zimową, czapkę, szalik i rękawiczki. Pamiętaj o warstwach (ubiór na cebulkę).",
                        Icons.Default.AcUnit,
                        Color(0xFFD1E9FF)
                    )
                    temperature in -5.0..6.0 -> {
                        val rainExtra = if (isRainingNow) " Koniecznie kurtka nieprzemakalna!" else ""
                        ClothingInfo(
                            "Zimno",
                            "Zalecana kurtka zimowa i czapka.$rainExtra Chroń organizm przed wychłodzeniem.",
                            Icons.Default.Checkroom,
                            Color(0xFFE3F2FD)
                        )
                    }
                    temperature in 6.1..15.0 -> {
                        val windExtra = if (isWindyNow) " Mocno wieje, załóż wiatrówkę." else " Wystarczy kurtka przejściowa."
                        ClothingInfo(
                            "Chłodno",
                            "Zalecana lekka kurtka lub grubszy sweter.$windExtra",
                            Icons.Default.DryCleaning,
                            Color(0xFFF5F5F5)
                        )
                    }
                    temperature in 15.1..22.0 -> {
                        val rainExtra = if (isRainingNow) " Weź parasol!" else " Ciesz się słońcem."
                        ClothingInfo(
                            "Umiarkowanie",
                            "Bluza lub koszula z długim rękawem.$rainExtra Pogoda idealna na spacer.",
                            Icons.Default.Checkroom,
                            Color(0xFFFFF9C4)
                        )
                    }
                    else -> ClothingInfo(
                        "Ciepło / Gorąco",
                        "T-shirt i krótkie spodenki. Pamiętaj o okularach przeciwsłonecznych i piciu dużej ilości wody!",
                        Icons.Default.WbSunny,
                        Color(0xFFFFECB3)
                    )
                }
            }

            val fetchWeather = {
                if (cityInput.isNotBlank()) {
                    scope.launch {
                        try {
                            val encodedLocation = URLEncoder.encode(cityInput.trim(), "UTF-8")

                            val response = RetrofitInstance.api.getRealtimeWeather(
                                location = encodedLocation,
                                apiKey = "MlCggxYrDOHLF0dLgJq9gDxdw3osDjWG"
                            )
                            val data = response.data.values

                            cityName = response.location.name
                            temp = "${data.temperature.toInt()}°C"
                            tempApparent = "${data.temperatureApparent.toInt()}°C"
                            weatherDesc = getWeatherDescription(data.weatherCode.toInt())

                            rainInfo = when {
                                data.snowIntensity > 0 -> "Pada śnieg"
                                data.rainIntensity > 0 -> "Pada deszcz"
                                data.sleetIntensity > 0 -> "Deszcz ze śniegiem"
                                data.precipitationProbability > 10 -> "Prawdopodobieństwo opadów: ${data.precipitationProbability.toInt()}%"
                                else -> "Brak opadów"
                            }

                            humidity = "${data.humidity.toInt()}%"
                            windSpeed = "${data.windSpeed} m/s"
                            windDirection = "${data.windDirection.toInt()}°"
                            pressureSeaLevel = "${data.pressureSeaLevel.toInt()} hPa"
                            uvIndex = data.uvIndex.toString()
                            cloudCover = "${data.cloudCover.toInt()}%"
                            visibility = "${data.visibility} km"
                            time = response.data.time.replace("T", " ").substring(0, 16)

                            updateClothingAdvice(data.temperature, data.windSpeed, data.rainIntensity, data.snowIntensity)
                        } catch (e: retrofit2.HttpException) {
                            cityName = if (e.code() == 429) {
                                "Przekroczono limit zapytań. Spróbuj za godzinę."
                            } else {
                                "Błąd serwera: ${e.code()}"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            cityName = "Błąd połączenia / Nie znaleziono"
                        }
                    }
                }
            }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { fetchWeather() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Odśwież")
                    }
                },
                bottomBar = {
                    BottomAppBar {
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            placeholder = { Text("Wpisz miasto, np. Warszawa, PL", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).padding(8.dp),
                            singleLine = true
                        )
                        IconButton(onClick = { fetchWeather() }) {
                            Icon(Icons.Default.Search, contentDescription = "Szukaj")
                        }
                        IconButton(
                            onClick = { if (temp != "--") showDetails = true },
                            enabled = temp != "--"
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Szczegóły")
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = cityName, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(text = temp, fontSize = 90.sp, fontWeight = FontWeight.Thin)
                    Text(text = "Odczuwalna: $tempApparent", color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = weatherDesc, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(text = rainInfo, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    clothingInfo?.let { info ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = info.cardColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(info.icon, contentDescription = null, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = info.summary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.1f))
                                Text(text = info.detailedAdvice, fontSize = 16.sp, lineHeight = 22.sp)

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (isRainingNow) {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text("Weź parasol") },
                                            leadingIcon = { Icon(Icons.Default.Umbrella, null, modifier = Modifier.size(18.dp)) }
                                        )
                                    }
                                    if (isWindyNow) {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text("Wiatrówka") },
                                            leadingIcon = { Icon(Icons.Default.Air, null, modifier = Modifier.size(18.dp)) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDetails) {
                AlertDialog(
                    onDismissRequest = { showDetails = false },
                    confirmButton = { Button(onClick = { showDetails = false }) { Text("Zamknij") } },
                    title = { Text("Szczegóły pogody") },
                    text = {
                        Column {
                            DetailRow("Wiatr", windSpeed)
                            DetailRow("Wilgotność", humidity)
                            DetailRow("Ciśnienie", pressureSeaLevel)
                            DetailRow("Zachmurzenie", cloudCover)
                            DetailRow("Indeks UV", uvIndex)
                            DetailRow("Widzialność", visibility)
                            DetailRow("Kierunek wiatru", windDirection)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(text = "Aktualizacja: $time", fontSize = 10.sp)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}