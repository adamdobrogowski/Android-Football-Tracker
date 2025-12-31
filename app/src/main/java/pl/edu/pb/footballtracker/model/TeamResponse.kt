package pl.edu.pb.footballtracker.model

import com.google.gson.annotations.SerializedName

data class FootballDataTeamResponse(
    @SerializedName("teams") val teams: List<TeamDto>?
)

data class TeamDto(
    @SerializedName("id") val id: Int,
    @SerializedName("shortName") val name: String,
    @SerializedName("crest") val badgeUrl: String
)