@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rental_danar_uas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List


/* =======================
   SCREEN STATE
   ======================= */
enum class Screen {
    RENTAL, FORM, BOOKING
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentalDanarApp()
        }
    }
}

/* =======================
   APP ROOT
   ======================= */
@Composable
fun RentalDanarApp() {
    var currentScreen by remember { mutableStateOf(Screen.RENTAL) }
    var mobilDipilih by remember { mutableStateOf<Mobil?>(null) }

    val daftarBooking = remember { mutableStateListOf<Booking>() }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onClick = { screen ->
                        currentScreen = screen
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (currentScreen) {
                    Screen.RENTAL -> RentalMobilApp(
                        onSewaClick = {
                            mobilDipilih = it
                            currentScreen = Screen.FORM
                        }
                    )

                    Screen.FORM -> BookingForm(
                        mobil = mobilDipilih!!,
                        onBooking = {
                            daftarBooking.add(it)
                            currentScreen = Screen.BOOKING
                        }
                    )

                    Screen.BOOKING -> BookingList(daftarBooking)
                }
            }
        }
    }
}

/* =======================
   BOTTOM NAVIGATION
   ======================= */
@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onClick: (Screen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screen.RENTAL,
            onClick = { onClick(Screen.RENTAL) },
            label = { Text("Rental") },
            icon = { Icon(Icons.Default.Home, null) }
        )

        NavigationBarItem(
            selected = currentScreen == Screen.BOOKING,
            onClick = { onClick(Screen.BOOKING) },
            label = { Text("Booking") },
            icon = { Icon(Icons.Default.List, null) }
        )
    }
}

/* =======================
   DATA MODEL
   ======================= */
data class Mobil(
    val nama: String,
    val hargaPerHari: Int,
    val tersedia: Boolean
)

data class Booking(
    val namaPenyewa: String,
    val lamaSewa: Int,
    val mobil: Mobil
)

/* =======================
   DATA DUMMY
   ======================= */
val daftarMobil = listOf(
    Mobil("Toyota Avanza", 300000, true),
    Mobil("Honda Brio", 250000, true),
    Mobil("Mitsubishi Xpander", 350000, false),
    Mobil("Toyota Innova", 450000, true)
)

/* =======================
   LIST MOBIL
   ======================= */
@Composable
fun RentalMobilApp(onSewaClick: (Mobil) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rental Mobil Danar") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(daftarMobil) { mobil ->
                MobilCard(mobil, onSewaClick)
            }
        }
    }
}

/* =======================
   CARD MOBIL
   ======================= */
@Composable
fun MobilCard(mobil: Mobil, onSewaClick: (Mobil) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(mobil.nama, style = MaterialTheme.typography.titleMedium)
            Text("Harga: Rp ${mobil.hargaPerHari} / hari")

            Spacer(Modifier.height(8.dp))

            if (mobil.tersedia) {
                Button(
                    onClick = { onSewaClick(mobil) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Sewa")
                }
            } else {
                Text("Tidak Tersedia", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/* =======================
   FORM BOOKING
   ======================= */
@Composable
fun BookingForm(mobil: Mobil, onBooking: (Booking) -> Unit) {
    var nama by remember { mutableStateOf("") }
    var lama by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Form Booking") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mobil: ${mobil.nama}")
            Text("Harga: Rp ${mobil.hargaPerHari} / hari")

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Penyewa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lama,
                onValueChange = { lama = it },
                label = { Text("Lama Sewa (hari)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onBooking(
                        Booking(
                            namaPenyewa = nama,
                            lamaSewa = lama.toInt(),
                            mobil = mobil
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nama.isNotEmpty() && lama.isNotEmpty()
            ) {
                Text("Simpan Booking")
            }
        }
    }
}

/* =======================
   LIST DATA BOOKING
   ======================= */
@Composable
fun BookingList(list: List<Booking>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Data Booking") })
        }
    ) { padding ->
        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada booking")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(list) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Nama: ${booking.namaPenyewa}")
                            Text("Mobil: ${booking.mobil.nama}")
                            Text("Lama: ${booking.lamaSewa} hari")

                            val total =
                                booking.lamaSewa * booking.mobil.hargaPerHari
                            Text(
                                "Total: Rp $total",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/* =======================
   PREVIEW
   ======================= */
@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    RentalDanarApp()
}
