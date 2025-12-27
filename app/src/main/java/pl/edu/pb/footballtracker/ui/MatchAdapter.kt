package pl.edu.pb.footballtracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pb.footballtracker.databinding.ItemMatchBinding
import pl.edu.pb.footballtracker.model.Match

class MatchAdapter(private val matches: List<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.binding.textTeams.text = "${match.homeTeam} vs ${match.awayTeam}"
        holder.binding.textScore.text = "Wynik: ${match.score} | Data: ${match.date}"
    }

    override fun getItemCount() = matches.size
}