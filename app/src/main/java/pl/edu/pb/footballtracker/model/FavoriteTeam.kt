package pl.edu.pb.footballtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_teams")
data class FavoriteTeam(
    @PrimaryKey val id: Int,
    val name: String,
    val badgeUrl: String?
)