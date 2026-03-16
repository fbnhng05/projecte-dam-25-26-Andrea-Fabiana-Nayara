package com.example.android_loop.data.Producto

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.Producto.accesoApi.ApiProductLoop
import kotlinx.coroutines.launch

class _02_ProductViewModel : ViewModel() {

    private val api = ApiProductLoop()

    var products by mutableStateOf<List<Product>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
    var productCreated by mutableStateOf(false)
        private set
    var etiquetas by mutableStateOf<List<Etiqueta>>(emptyList())
        private set
    var productImages by mutableStateOf<List<ImagenConDatos>>(emptyList())
        private set

    fun resetProductCreated() {
        productCreated = false
    }

    // ===============================
    // 🔹 CARGAR PRODUCTOS
    // ===============================

    fun loadProducts() {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            api.getProducts()
                .onSuccess {
                    products = it.products
                }
                .onFailure {
                    errorMessage = it.message
                }

            isLoading = false
        }
    }

    // ===============================
    // 🔹 CREAR PRODUCTO
    // ===============================

    private fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun createProduct(
        context: Context,
        nombre: String,
        descripcion: String,
        precio: Double,
        estado: String,
        ubicacion: String,
        antiguedad: String,
        categoriaId: Int,
        imageUris: List<Uri>
    ) {

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            try {

                if (imageUris.isEmpty()) {
                    errorMessage = "Debes seleccionar al menos una imagen"
                    isLoading = false
                    return@launch
                }

                // 🔥 Convertir imágenes a Base64
                val imagenesList = imageUris.mapIndexed { index, uri ->

                    val base64Image = uriToBase64(context, uri)

                    ImageRequest(
                        imagen = base64Image,
                        is_principal = index == 0,
                        sequence = index + 1
                    )
                }

                val request = CreateProductRequest(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    estado = estado,
                    ubicacion = ubicacion,
                    antiguedad = antiguedad,
                    categoria_id = categoriaId,
                    imagenes = imagenesList
                )

                api.createProduct(request)
                    .onSuccess {
                        if (it.ok == true) {
                            productCreated = true
                        } else {
                            errorMessage = "Error al crear el producto (respuesta inesperada del servidor)"
                        }
                    }
                    .onFailure {
                        errorMessage = it.message
                    }

            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }

            isLoading = false
        }
    }

    // ===============================
    // 🔹 CREAR ETIQUETA
    // ===============================

    fun createEtiqueta(name: String) {

        viewModelScope.launch {

            val request = CreateEtiquetaRequest(
                data = EtiquetaData(name = name)
            )

            api.createEtiqueta(request)
                .onSuccess {
                    if (it.success != true) {
                        errorMessage = it.error ?: "Error desconocido"
                    }
                }
                .onFailure {
                    errorMessage = it.message
                }
        }
    }

    // ===============================
    // 🔹 CARGAR ETIQUETAS
    // ===============================

    fun loadEtiquetas() {

        viewModelScope.launch {

            api.getEtiquetas()
                .onSuccess {
                    etiquetas = it
                }
                .onFailure {
                    errorMessage = it.message
                }
        }
    }

    // ===============================
    // 🔹 CARGAR IMÁGENES DE PRODUCTO
    // ===============================

    fun loadProductImages(productId: Int) {

        viewModelScope.launch {

            productImages = emptyList()

            api.getProductImages(productId)
                .onSuccess {
                    productImages = it
                }
                .onFailure {
                    // Las imágenes no se muestran pero no bloquean la pantalla
                }
        }
    }
}