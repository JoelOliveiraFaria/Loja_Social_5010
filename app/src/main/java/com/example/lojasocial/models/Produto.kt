package com.example.lojasocial.models

// O "Mestre" do Produto
data class Produto(
    val id: String? = null,
    val nome: String? = null,
    val descricao: String? = null,
    val quantidadeTotal: Int = 0 // Soma de todos os lotes
)

// O Lote específico (Cada entrada de stock)
data class LoteStock(
    val id: String? = null,
    val quantidade: Int = 0,
    val dataValidade: String? = null, // Pode ser null (ex: papel higiénico)
    val dataEntrada: String? = null
)