package pl.edu.pb.footballtracker.data

import pl.edu.pb.footballtracker.BuildConfig
import pl.edu.pb.footballtracker.di.RetrofitInstance
import pl.edu.pb.footballtracker.model.Match
import pl.edu.pb.footballtracker.model.toMatch

class FootballRepository {

    private val api = RetrofitInstance.api

    suspend fun getMatches(): List<Match> {
        return try {
            val response = api.getMatches("CL", BuildConfig.FOOTBALL_API_TOKEN)

            response.matches?.let { allMatches ->
                allMatches
                    .filter { it.status == "FINISHED" }
                    .takeLast(5)
                    .reversed()
                    .map { it.toMatch() }
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}