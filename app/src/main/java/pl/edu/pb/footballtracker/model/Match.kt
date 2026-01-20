    package pl.edu.pb.footballtracker.model

    data class Match(
        val id: Int,
        val homeTeam: String,
        val awayTeam: String,
        val score: String,
        val date: String,
        val leagueName: String
    )