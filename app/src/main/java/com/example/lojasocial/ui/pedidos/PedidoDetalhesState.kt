package com.example.lojasocial.ui.pedidos

import com.example.lojasocial.models.Pedido

data class PedidoDetalhesState(
    val isLoading: Boolean = true,
    val pedido: Pedido? = null,
    val nomeBeneficiario: String? = "",
    val error: String? = null
)
