package pl.edu.pb.footballtracker.model

import com.google.gson.annotations.SerializedName

data class FootballDataMatchResponse(
    @SerializedName("matches") val matches: List<MatchDto>?
)

data class MatchDto(
    @SerializedName("homeTeam") val homeTeam: TeamInfo,
    @SerializedName("awayTeam") val awayTeam: TeamInfo,
    @SerializedName("score") val score: ScoreInfo,
    @SerializedName("utcDate") val date: String
)

data class TeamInfo(@SerializedName("shortName") val name: String)
data class ScoreInfo(@SerializedName("fullTime") val fullTime: FullTimeScore)
data class FullTimeScore(
    @SerializedName("home") val home: Int?,
    @SerializedName("away") val away: Int?
)

fun MatchDto.toMatch(): Match {
    val hScore = score.fullTime.home ?: 0
    val aScore = score.fullTime.away ?: 0

    return Match(
        id = 0,
        homeTeam = homeTeam.name,
        awayTeam = awayTeam.name,
        score = "$hScore : $aScore",
        date = date.take(10),
        leagueName = "Premier League"
    )
}