package pl.edu.pb.footballtracker.data

import pl.edu.pb.footballtracker.model.FootballDataMatchResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface FootballApiService {
    @GET("v4/matches")
    suspend fun getMatches(
        @Header("X-Auth-Token") apiKey: String
    ): FootballDataMatchResponse
}