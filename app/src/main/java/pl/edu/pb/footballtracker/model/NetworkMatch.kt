package pl.edu.pb.footballtracker.model

data class NetworkMatchResponse(
    val matches: List<NetworkMatchDetails>?
)

data class NetworkMatchDetails(
    val homeTeam: MatchTeamDetails,
    val awayTeam: MatchTeamDetails,
    val score: MatchScoreDetails,
    val utcDate: String,
    val status: String?
)

data class MatchTeamDetails(
    val name: String
)

data class MatchScoreDetails(
    val fullTime: ScoreValuesDetails
)

data class ScoreValuesDetails(
    val home: Int?,
    val away: Int?
)