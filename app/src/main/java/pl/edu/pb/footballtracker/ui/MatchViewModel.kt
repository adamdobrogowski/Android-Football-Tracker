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

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadLiveMatches()
    }

    fun refreshMatches() {
        loadLiveMatches()
    }

    private fun loadLiveMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = repository.getMatches()

                if (result.isNotEmpty()) {
                    _matches.value = result
                } else {
                    _error.value = "Nie znaleziono rozegranych meczów w tej lidze."
                }
            } catch (e: Exception) {
                _error.value = "Błąd połączenia: Sprawdź Internet."
            } finally {
                _isLoading.value = false
            }
        }
    }
}