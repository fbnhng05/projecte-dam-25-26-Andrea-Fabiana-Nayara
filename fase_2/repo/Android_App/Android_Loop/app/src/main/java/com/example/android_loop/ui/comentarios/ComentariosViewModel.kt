package com.example.android_loop.ui.comentarios

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.Producto.accesoApi.ApiProductLoop
import com.example.android_loop.data.Producto.accesoApi.TokenManager
import com.example.android_loop.data.repository.UserRepository
import com.example.android_loop.ui.comentarios.UpdateComentarioData
import com.example.android_loop.ui.comentarios.UpdateComentarioRequest
import kotlinx.coroutines.launch

class ComentariosViewModel : ViewModel() {

    private val api = ApiProductLoop()
    private val userRepository = UserRepository()

    var comentarios by mutableStateOf<List<Comentario>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var comentarioEnviado by mutableStateOf(false)
        private set
    var currentUserName by mutableStateOf("")
        private set
    var currentUserId by mutableStateOf(0)
        private set

    fun cargarUsuarioActual(token: String) {
        TokenManager.saveToken(token)
        viewModelScope.launch {
            userRepository.getUserData(token)
                .onSuccess {
                    currentUserName = it.name
                    currentUserId = it.id
                }
        }
    }

    fun cargarComentarios(productId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            api.getComentarios(productId)
                .onSuccess { comentarios = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun enviarComentario(usuarioId: Int, contenido: String, valoracion: Float? = null) {
        if (contenido.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            api.crearComentario(CreateComentarioRequest(data = CreateComentarioData(usuarioId, contenido, "published", valoracion)))
                .onSuccess { response ->
                    if (response.success == true) {
                        comentarioEnviado = true
                        cargarComentarios(usuarioId)
                    } else {
                        errorMessage = response.error ?: "Error al enviar el comentario"
                    }
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun resetComentarioEnviado() {
        comentarioEnviado = false
    }

    fun editarComentario(comentarioId: Int, contenido: String, valoracion: Float?, usuarioId: Int) {
        if (contenido.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            api.editarComentario(
                comentarioId,
                UpdateComentarioRequest(UpdateComentarioData(contenido, "published", valoracion))
            )
                .onSuccess { response ->
                    if (response.success == true) {
                        cargarComentarios(usuarioId)
                    } else {
                        errorMessage = response.error ?: "Error al editar el comentario"
                    }
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun eliminarComentario(comentarioId: Int, usuarioId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            api.eliminarComentario(comentarioId)
                .onSuccess { response ->
                    if (response.success == true) {
                        cargarComentarios(usuarioId)
                    } else {
                        errorMessage = response.error ?: "Error al eliminar el comentario"
                    }
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }
}
