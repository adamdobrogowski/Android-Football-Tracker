package pl.edu.pb.footballtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pb.footballtracker.databinding.ItemTeamBinding
import pl.edu.pb.footballtracker.loadSvg
import pl.edu.pb.footballtracker.model.NetworkTeam

class TeamAdapter(
    private var teams: List<NetworkTeam>,
    private val onItemClick: (NetworkTeam) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        holder.binding.tvTeamName.text = team.name

        team.badgeUrl?.let { url ->
            holder.binding.ivBadge.loadSvg(url)
        }

        holder.itemView.setOnClickListener {
            onItemClick(team)
        }
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<NetworkTeam>) {
        this.teams = newTeams
        notifyDataSetChanged()
    }
}