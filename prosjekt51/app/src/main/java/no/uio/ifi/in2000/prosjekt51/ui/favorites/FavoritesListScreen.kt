package no.uio.ifi.in2000.prosjekt51.ui.favorites

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.room.Room
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation

@Composable
fun FavoritesListScreen(
    navController: NavController) {
    val context = LocalContext.current
    val viewModel: FavoriteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = DatabaseManager.getFavoriteRepository(context)
                if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FavoriteViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    // Collect the list of favorites from the ViewModel.
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        bottomBar = {
            BottomAppBar {
                BottomNavigation(navController = navController)
            }
        }

    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Favorites", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn {
                        items(favorites) { favorite ->
                            FavoriteItem(favorite, viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(favorite: Favorite, viewModel: FavoriteViewModel, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = { navController.navigate("searchScreen/${favorite.lat}/${favorite.lon}")}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name: ${favorite.name}",
                modifier = Modifier.weight(1f) // This will make the Text take all available space pushing the Icon to the end
            )
            IconButton(
                onClick = { viewModel.deleteFavorite(favorite) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}


object DatabaseManager {
    private var appDatabase: AppDatabase? = null

    private fun getDatabase(context: Context): AppDatabase {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "favoritesDatabase.db"
            ).build()
        }
        return appDatabase!!
    }

    fun getFavoriteRepository(context: Context): FavoriteRepository {
        val dao = getDatabase(context).favoriteDao()
        return FavoriteRepository(dao)
    }
}
