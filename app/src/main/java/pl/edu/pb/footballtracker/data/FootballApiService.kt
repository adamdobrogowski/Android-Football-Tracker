package pl.edu.pb.footballtracker.data

import pl.edu.pb.footballtracker.model.FootballDataMatchResponse
import pl.edu.pb.footballtracker.model.TeamResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

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

}