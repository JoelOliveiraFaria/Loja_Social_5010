package com.example.lojasocial.ui.pedidos

import com.example.lojasocial.models.Pedido

data class NovosPedidosState(
    val isLoading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val error: String? = null
)
