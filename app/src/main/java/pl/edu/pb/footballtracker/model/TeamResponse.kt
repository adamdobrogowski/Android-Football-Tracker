package pl.edu.pb.footballtracker.model

import com.google.gson.annotations.SerializedName

data class TeamResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("teams") val teams: List<NetworkTeam>?
)

data class NetworkTeam(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("shortName") val shortName: String?,
    @SerializedName("crest") val badgeUrl: String?
)