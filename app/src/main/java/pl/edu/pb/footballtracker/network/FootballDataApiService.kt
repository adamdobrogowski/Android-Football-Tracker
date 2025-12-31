package pl.edu.pb.footballtracker.network

import pl.edu.pb.footballtracker.model.FootballDataMatchResponse
import pl.edu.pb.footballtracker.model.FootballDataTeamResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface FootballDataApiService {

    @GET("competitions/{leagueCode}/teams")
    suspend fun getTeams(
        @Path("leagueCode") leagueCode: String = "PL",
        @Header("X-Auth-Token") token: String
    ): FootballDataTeamResponse

    @GET("teams/{teamId}/matches?status=FINISHED&limit=5")
    suspend fun getTeamMatches(
        @Path("teamId") teamId: Int,
        @Header("X-Auth-Token") token: String
    ): FootballDataMatchResponse
}