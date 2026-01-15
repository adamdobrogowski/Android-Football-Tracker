package pl.edu.pb.footballtracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.edu.pb.footballtracker.data.FootballRepository
import pl.edu.pb.footballtracker.model.Match

class MatchViewModel : ViewModel() {

    private val repository = FootballRepository()

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadLiveMatches()
    }

    private fun loadLiveMatches() {
        viewModelScope.launch {
            try {
                val result = repository.getMatches()

                if (result.isNotEmpty()) {
                    _matches.postValue(result)
                } else {
                    _error.postValue("Brak meczów na dziś lub błąd klucza API.")
                }
            } catch (e: Exception) {
                _error.postValue("Błąd sieci: ${e.message}")
            }
        }
    }
}