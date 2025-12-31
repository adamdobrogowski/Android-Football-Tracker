package pl.edu.pb.footballtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pb.footballtracker.loadSvg
import pl.edu.pb.footballtracker.databinding.ItemTeamBinding
import pl.edu.pb.footballtracker.model.TeamDto

class TeamAdapter(
    private var teams: List<TeamDto>,
    private val onItemClick: (TeamDto) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = teams[position]
        holder.binding.tvTeamName.text = team.name

        holder.binding.ivBadge.loadSvg(team.badgeUrl)

        holder.itemView.setOnClickListener {
            onItemClick(team)
        }
    }

    override fun getItemCount(): Int = teams.size

    fun updateTeams(newTeams: List<TeamDto>) {
        this.teams = newTeams
        notifyDataSetChanged()
    }
}