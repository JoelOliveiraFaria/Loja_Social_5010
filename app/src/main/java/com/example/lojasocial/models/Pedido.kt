package com.example.lojasocial.models
import com.google.firebase.Timestamp

data class Pedido(
    val id: String? = null,
    val beneficiarioId: String = "",
    val textoPedido: String = "",
    val status: PedidoStatus = PedidoStatus.NOVO,
    val respostaRecusa: String? = null
)

enum class PedidoStatus {
    NOVO,
    EM_ANDAMENTO,
    TERMINADO,
    RECUSADO
}
