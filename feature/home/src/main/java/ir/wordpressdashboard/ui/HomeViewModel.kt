package ir.wordpressdashboard.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.usecase.GetProductUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProducts: GetProductUseCase
) : ViewModel() {
    var products by mutableStateOf<List<Products>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            try{
                products =getProducts()
                Log.d("HomeViewModel", "loadProducts: $products")
            }catch (e:Exception){
                products = emptyList()
                Log.e("HomeViewModel","${e.message}")
            }finally {
                isLoading = false
            }
            products = getProducts()
            isLoading = false
        }
    }
}