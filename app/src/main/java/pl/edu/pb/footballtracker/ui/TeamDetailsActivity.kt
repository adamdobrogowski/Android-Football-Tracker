package pl.edu.pb.footballtracker.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.adapter.MatchAdapter
import pl.edu.pb.footballtracker.databinding.ActivityTeamDetailsBinding
import pl.edu.pb.footballtracker.model.MatchDto
import pl.edu.pb.footballtracker.model.toMatch
import pl.edu.pb.footballtracker.network.RetrofitClientSingleton
import pl.edu.pb.footballtracker.BuildConfig

class TeamDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailsBinding
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val teamId = intent.getIntExtra("TEAM_ID", -1)
        val teamName = intent.getStringExtra("TEAM_NAME") ?: "Szczegóły"
        val teamBadge = intent.getStringExtra("TEAM_BADGE") ?: ""

        binding.tvDetailName.text = teamName
        binding.ivDetailBadge.load(teamBadge)

        setupRecyclerView()

        if (teamId != -1) {
            loadTeamMatches(teamId)
        }
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter(emptyList())
        binding.rvTeamMatches.apply {
            adapter = matchAdapter
            layoutManager = LinearLayoutManager(this@TeamDetailsActivity)
        }
    }

    private fun loadTeamMatches(teamId: Int) {
        Log.d("API_DETAILS", "Wysyłam zapytanie o mecze dla ID: $teamId")
        lifecycleScope.launch {
            try {
                val token = BuildConfig.FOOTBALL_API_TOKEN

                val response = RetrofitClientSingleton.instance.getTeamMatches(teamId, token)

                val matchesDto = response.matches ?: emptyList<MatchDto>()

                val domainMatches = matchesDto.map { it.toMatch() }
                matchAdapter.updateMatches(domainMatches)

            } catch (e: Exception) {
                Log.e("API_DETAILS", "Błąd pobierania meczów: ${e.message}")
            }
        }
    }
}