package pl.edu.pb.footballtracker.data

import pl.edu.pb.footballtracker.model.FootballDataMatchResponse
import pl.edu.pb.footballtracker.model.NetworkMatchResponse
import pl.edu.pb.footballtracker.model.TeamResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FootballApiService {
    @GET("v4/matches")
    suspend fun getMatches(
        @Header("X-Auth-Token") apiKey: String
    ): FootballDataMatchResponse

    @GET("v4/competitions/{leagueCode}/teams")
    suspend fun getTeams(
        @Path("leagueCode") leagueCode: String,
        @Header("X-Auth-Token") apiKey: String
    ): TeamResponse

    @GET("v4/teams/{id}/matches")
    suspend fun getTeamMatches(
        @Path("id") teamId: Int,
        @Header("X-Auth-Token") token: String,
        @Query("status") status: String = "FINISHED",
        @Query("limit") limit: Int = 1
    ): NetworkMatchResponse

}