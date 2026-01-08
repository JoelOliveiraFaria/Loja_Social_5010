package com.example.lojasocial.models

enum class EntregaStatus {
    EM_ANDAMENTO,
    TERMINADO
}

data class Entrega(
    val id: String? = null,
    val beneficiarioId: String? = null,
    val pedidoId: String? = null,
    val itens: List<ItemEntrega> = emptyList(),
    val status: EntregaStatus = EntregaStatus.EM_ANDAMENTO,
    val dataCriacao: Long = System.currentTimeMillis()
)

data class ItemEntrega(
    val produtoId: String = "",
    val produtoNome: String? = null,
    val lotesConsumidos: List<LoteConsumido> = emptyList()
)

data class LoteConsumido(
    val loteId: String? = null,
    val quantidade: Int = 0
)
