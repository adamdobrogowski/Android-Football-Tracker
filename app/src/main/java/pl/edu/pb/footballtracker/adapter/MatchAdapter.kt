package pl.edu.pb.footballtracker.adapter

import pl.edu.pb.footballtracker.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pb.footballtracker.databinding.ItemMatchBinding
import pl.edu.pb.footballtracker.model.Match


class MatchAdapter(private var matches: List<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.binding.apply {
            textTeams.text = "${match.homeTeam} vs ${match.awayTeam}"

            val scoreText = holder.itemView.context.getString(R.string.score_label)
            textScore.text = "$scoreText ${match.score} (${match.date})"
        }
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<Match>) {
        this.matches = newMatches
        notifyDataSetChanged()
    }
}