package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.CoffeeShopRepository
import com.example.ui.CoffeeShopScreen
import com.example.ui.CoffeeShopViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge system configurations
        enableEdgeToEdge()

        // Room DB & MVVM Repository instantiation
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CoffeeShopRepository(database.coffeeShopDao())
        
        // Viewmodel attachment via standard platform Provider delegate
        val viewModel: CoffeeShopViewModel by viewModels {
            CoffeeShopViewModel.Factory(repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CoffeeShopScreen(viewModel = viewModel)
                }
            }
        }
    }
}
