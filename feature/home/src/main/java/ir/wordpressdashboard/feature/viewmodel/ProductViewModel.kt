package ir.wordpressdashboard.feature.viewmodel

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
//    private val productApiService: ProductApiService
) :ViewModel() {
    private val _product = MutableLiveData<Products>()
    val product : LiveData<Products> get() = _product


//    fun loadProduct() {
//        viewModelScope.launch {
//            _product.value = productApiService.getProduct()
//        }
//    }
}