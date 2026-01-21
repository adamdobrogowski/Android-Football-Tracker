package pl.edu.pb.footballtracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "favorite_teams")
data class FavoriteTeam(
    @PrimaryKey val id: Int,
    val name: String,
    val badgeUrl: String
)

@Entity(
    tableName = "team_notes",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteTeam::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeamNote(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val teamId: Int,
    val content: String,
    val timestamp: Long
)

data class TeamWithNotes(
    @Embedded val team: FavoriteTeam,
    @Relation(
        parentColumn = "id",
        entityColumn = "teamId"
    )
    val notes: List<TeamNote>
)