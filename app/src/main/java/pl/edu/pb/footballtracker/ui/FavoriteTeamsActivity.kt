package pl.edu.pb.footballtracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.adapter.TeamAdapter
import pl.edu.pb.footballtracker.data.local.AppDatabase
import pl.edu.pb.footballtracker.databinding.ActivityFavoriteTeamsBinding
import pl.edu.pb.footballtracker.model.FavoriteTeam
import pl.edu.pb.footballtracker.model.NetworkTeam

class FavoriteTeamsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteTeamsBinding
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteTeamsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ulubione"

        setupRecyclerView()
        loadFavoritesFromDb()
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(
            teams = emptyList(),
            favoriteIds = emptySet(),
            onFavoriteClick = { team ->
            },
            onItemClick = { team ->
                val intent = Intent(this, TeamDetailsActivity::class.java).apply {
                    putExtra("TEAM_ID", team.id)
                    putExtra("TEAM_NAME", team.name)
                    putExtra("TEAM_BADGE", team.badgeUrl)
                }
                startActivity(intent)
            }
        )

        binding.rvFavorites.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(this@FavoriteTeamsActivity)
        }
    }

    private fun loadFavoritesFromDb() {
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.favoriteTeamDao().getAllFavorites().collectLatest { favorites ->
                val teamList = favorites.map { fav ->
                    NetworkTeam(
                        id = fav.id,
                        name = fav.name,
                        badgeUrl = fav.badgeUrl,
                        shortName = fav.name
                    )
                }

                teamAdapter.updateTeams(teamList)

                val ids = favorites.map { it.id }.toSet()
                teamAdapter.updateFavorites(ids)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}