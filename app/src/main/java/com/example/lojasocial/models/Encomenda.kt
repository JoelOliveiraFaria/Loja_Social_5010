package com.example.lojasocial.models

data class Encomenda(
    val id: String? = null,
    val pedidoId: String? = null,   // opcional
    val beneficiarioId: String,
    val produtos: List<ProdutoEncomenda> = emptyList(),
    val dataCriacao: Long = System.currentTimeMillis()
)

data class ProdutoEncomenda(
    val produtoId: String,
    val quantidade: Int
)