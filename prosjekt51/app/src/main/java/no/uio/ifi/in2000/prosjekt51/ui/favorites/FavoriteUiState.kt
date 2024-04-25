package no.uio.ifi.in2000.prosjekt51.ui.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Database
import androidx.room.RoomDatabase

@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // auto-generated ID
    val lat: Double,
    val lon: Double,
    val name: String
)

@Dao
interface FavoriteDao {
    @Insert
    suspend fun addFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    suspend fun getFavorites(): List<Favorite>

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}

@Database(entities = [Favorite::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}

class FavoriteRepository(private val dao: FavoriteDao) {
    suspend fun addFavorite(favorite: Favorite) {
        dao.addFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: Favorite) {
        dao.deleteFavorite(favorite)
    }

    suspend fun getFavorites(): List<Favorite> = dao.getFavorites()
}