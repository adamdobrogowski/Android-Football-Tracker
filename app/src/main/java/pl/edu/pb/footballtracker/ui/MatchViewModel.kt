package pl.edu.pb.footballtracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.edu.pb.footballtracker.model.Match

class MatchViewModel : ViewModel() {
    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches

    init {
        loadTestMatches()
    }

    private fun loadTestMatches() {
        val testData = listOf(
            Match(1, "Real Madrid", "FC Barcelona", "2:1", "2025-10-26", "La Liga"),
            Match(2, "Liverpool", "Manchester City", "2:0", "2024-12-1", "Premier League"),
            Match(3, "PSG", "Bayern Munich", "0:1", "2025-08-23", "Champions League"),
            Match(4, "Jagiellonia Białystok", "Warta Poznań", "3:0", "2024-05-25", "Ekstraklasa")
        )
        _matches.value = testData
    }
}