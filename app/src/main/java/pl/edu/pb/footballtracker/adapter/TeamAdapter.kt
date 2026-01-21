package pl.edu.pb.footballtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pb.footballtracker.R
import pl.edu.pb.footballtracker.databinding.ItemTeamBinding
import pl.edu.pb.footballtracker.loadSvg
import pl.edu.pb.footballtracker.model.NetworkTeam
import java.util.Locale

class TeamAdapter(
    private var teams: List<NetworkTeam>,
    private var favoriteIds: Set<Int> = emptySet(),
    private val onFavoriteClick: (NetworkTeam) -> Unit,
    private val onItemClick: (NetworkTeam) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private var allTeams: List<NetworkTeam> = teams

    class TeamViewHolder(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]

        holder.binding.apply {
            tvTeamName.text = team.name

            team.badgeUrl?.let { url ->
                ivBadge.loadSvg(url)
            }

            val isFavorite = favoriteIds.contains(team.id)

            ivFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            ivFavorite.setOnClickListener {
                onFavoriteClick(team)
            }

            root.setOnClickListener {
                onItemClick(team)
            }
        }
    }

    override fun getItemCount(): Int = teams.size

    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        teams = if (lowerCaseQuery.isEmpty()) {
            allTeams
        } else {
            allTeams.filter { team ->
                team.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
        notifyDataSetChanged()
    }

    fun updateFavorites(newFavoriteIds: Set<Int>) {
        this.favoriteIds = newFavoriteIds
        notifyDataSetChanged()
    }

    fun updateTeams(newTeams: List<NetworkTeam>) {
        this.allTeams = newTeams
        this.teams = newTeams
        notifyDataSetChanged()
    }
}