package com.example.okoablood.common//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.okoablood.data.model.BloodRequest
//import kotlinx.coroutines.launch
//
//// Place in: app/src/main/java/com/example/okoablood/common/UiState.kt
//sealed interface UiState<out T> {
//    data object Loading : UiState<Nothing>
//    data class Success<T>(val data: T) : UiState<T>
//    data class Error(val message: String? = null) : UiState<Nothing>
//}
//
//// Example usage in ViewModel:
//class BloodRequestViewModel : ViewModel() {
//    private val _state = mutableStateOf<UiState<List<BloodRequest>>(UiState.Loading)
//    val state: State<UiState<List<BloodRequest>>> = _state
//
//    fun loadRequests() {
//        viewModelScope.launch {
//            _state.value = UiState.Loading
//            try {
//                val requests = repository.getRequests()
//                _state.value = UiState.Success(requests)
//            } catch (e: Exception) {
//                _state.value = UiState.Error("Failed to load requests")
//            }
//        }
//    }
//}