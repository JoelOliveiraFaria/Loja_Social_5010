package com.example.lojasocial.models

data class Entrega(
    val id: String? = null,
    val pedidoId: String? = null,   // opcional
    val beneficiarioId: String,
    val produtos: List<ProdutoEntrega> = emptyList(),
    val dataCriacao: Long = System.currentTimeMillis()
)

data class ProdutoEntrega(
    val produtoId: String,
    val quantidade: Int
)