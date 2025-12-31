package pl.edu.pb.footballtracker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientSingleton {
    private const val BASE_URL = "https://api.football-data.org/v4/"

    val instance: FootballDataApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FootballDataApiService::class.java)
    }
}