package ir.wordpressdashboard.feature.home

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Products
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _product = MutableLiveData<Products>()
    val product : LiveData<Products> get() = _product


//    var products by mutableStateOf<List<Products>>(emptyList())
//        private set
//    var isLoading by mutableStateOf(true)
//        private set

}