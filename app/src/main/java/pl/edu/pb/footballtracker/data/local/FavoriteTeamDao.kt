package pl.edu.pb.footballtracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.edu.pb.footballtracker.model.FavoriteTeam
import pl.edu.pb.footballtracker.model.TeamNote
import pl.edu.pb.footballtracker.model.TeamWithNotes

@Dao
interface FavoriteTeamDao {

    @Query("SELECT * FROM favorite_teams")
    fun getAllFavorites(): Flow<List<FavoriteTeam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: FavoriteTeam)

    @Delete
    suspend fun delete(team: FavoriteTeam)

    @Query("SELECT EXISTS(SELECT * FROM favorite_teams WHERE id = :teamId)")
    suspend fun isFavorite(teamId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TeamNote)

    @Delete
    suspend fun deleteNote(note: TeamNote)

    @Transaction
    @Query("SELECT * FROM favorite_teams WHERE id = :teamId")
    fun getTeamWithNotes(teamId: Int): Flow<TeamWithNotes>
}