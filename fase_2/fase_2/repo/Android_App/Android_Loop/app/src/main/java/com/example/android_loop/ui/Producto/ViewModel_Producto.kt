package com.example.android_loop.ui.Producto

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.Producto.CreateEtiquetaRequest
import com.example.android_loop.data.Producto.CreateProductRequest
import com.example.android_loop.data.Producto.Etiqueta
import com.example.android_loop.data.Producto.EtiquetaData
import com.example.android_loop.data.Producto.ImageRequest
import com.example.android_loop.data.Producto.ImagenConDatos
import com.example.android_loop.data.accesoApi.ApiProductoLoop
import com.example.android_loop.data.model_dataClass.Producto
import kotlinx.coroutines.launch

class ViewModel_Producto : ViewModel() {

    private val api = ApiProductoLoop()

    var products by mutableStateOf<List<Producto>>(emptyList())
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

    fun loadProducts(token: String) {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            api.getProductos(token)
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
        token: String,
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

                api.createProduct(token = token, request = request)
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

    fun createEtiqueta(token: String, name: String) {

        viewModelScope.launch {

            val request = CreateEtiquetaRequest(
                data = EtiquetaData(name = name)
            )

            api.createEtiqueta(token = token, request = request)
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

    fun loadEtiquetas(token: String) {

        viewModelScope.launch {

            api.getEtiquetas(token)
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

    fun loadProductImages(token: String, productId: Int) {

        viewModelScope.launch {

            productImages = emptyList()

            api.getProductImages(token, productId)
                .onSuccess {
                    productImages = it
                }
                .onFailure {
                    // Las imágenes no se muestran pero no bloquean la pantalla
                }
        }
    }
}