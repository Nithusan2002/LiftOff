package no.uio.ifi.in2000.prosjekt51.ui.favorites

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.room.Room

@ExperimentalMaterial3Api
@Composable
fun FavoritesListScreen(navController: NavController) {

    // Get Favorites-database
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

    val favorites by viewModel.favorites.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingFavorite by remember { mutableStateOf<Favorite?>(null) }


    // Dialogs for adding or editing favorites-elements
    if (showDialog) {
        AddFavoriteDialog(viewModel = viewModel) {
            showDialog = false
        }
    }

    if (editingFavorite != null) {
        EditFavoriteDialog(editingFavorite!!, viewModel = viewModel) {
            editingFavorite = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
            title = { Text(text = "Favorites") },
            actions = {
                IconButton(onClick = { showDialog = true }, Modifier.testTag("addFavoriteButton")) {
                    Icon(Icons.Default.Add, contentDescription = "Add favorite")
                }
            }
        )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(favorites) { favorite ->
                FavoriteItem(favorite, viewModel, navController, onEdit = { selectedFavorite ->
                    editingFavorite = selectedFavorite
                })
            }
        }
    }
}

@Composable
fun FavoriteItem(favorite: Favorite, viewModel: FavoriteViewModel, navController: NavController, onEdit: (Favorite) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = { navController.navigate("searchScreen/${favorite.lat}/${favorite.lon}") }
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
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onEdit(favorite) }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
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


@Composable
fun AddFavoriteDialog(viewModel: FavoriteViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }
    var inputValid by remember(name, lat, lon) {
        mutableStateOf(false)
    }

    LaunchedEffect(name, lat, lon) {
        inputValid = name.isNotBlank() &&
                isValidLatitude(lat) &&
                isValidLongitude(lon)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add New Favorite") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                TextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") })
                TextField(value = lon, onValueChange = { lon = it }, label = { Text("Longitude") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addFavorite(Favorite(name = name, lat = lat.toDouble(), lon = lon.toDouble()))
                    onDismiss()
                },
                enabled = inputValid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

fun isValidLatitude(s: String): Boolean {
    return s.toDoubleOrNull()?.let {
        it in -90.0..90.0
    } ?: false
}

fun isValidLongitude(s: String): Boolean {
    return s.toDoubleOrNull()?.let {
        it in -180.0..180.0
    } ?: false
}


@Composable
fun EditFavoriteDialog(favorite: Favorite, viewModel: FavoriteViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(favorite.name) }
    var lat by remember { mutableStateOf(favorite.lat.toString()) }
    var lon by remember { mutableStateOf(favorite.lon.toString()) }
    var inputValid by remember(name, lat, lon) {
        mutableStateOf(true)
    }

    LaunchedEffect(name, lat, lon) {
        inputValid = name.isNotBlank() &&
                isValidLatitude(lat) &&
                isValidLongitude(lon)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Edit Favorite") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                TextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") })
                TextField(value = lon, onValueChange = { lon = it }, label = { Text("Longitude") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateFavorite(favorite.copy(name = name, lat = lat.toDouble(), lon = lon.toDouble()))
                    onDismiss()
                },
                enabled = inputValid
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
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


