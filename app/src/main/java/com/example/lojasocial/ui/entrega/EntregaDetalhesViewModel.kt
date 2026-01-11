package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.ProdutoRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregaDetalhesViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val pedidoRepository: PedidoRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    var entrega = mutableStateOf<Entrega?>(null)
        private set

    var nomeBeneficiario = mutableStateOf("")
        private set

    var textoPedido = mutableStateOf("")
        private set

    var erro = mutableStateOf<String?>(null)
        private set

    // -------- NOVOS CAMPOS --------
    var estadoSelecionado = mutableStateOf(EntregaStatus.EM_ANDAMENTO)
        private set

    var dataEntrega = mutableStateOf<Long?>(null)
        private set

    private val _produtosDisponiveis = MutableStateFlow<List<Produto>>(emptyList())
    val produtosDisponiveis: StateFlow<List<Produto>> = _produtosDisponiveis

    // -------- CARREGAR ENTREGA --------
    fun carregarEntrega(entregaId: String) {
        viewModelScope.launch {
            try {
                val e = entregaRepository.getEntregaById(entregaId) ?: return@launch
                entrega.value = e

                estadoSelecionado.value = e.status
                dataEntrega.value = e.dataEntrega

                e.beneficiarioId?.let {
                    when (val r = beneficiarioRepository.obterBeneficiario(it)) {
                        is ResultWrapper.Success -> nomeBeneficiario.value = r.value.nome ?: ""
                        else -> {}
                    }
                }

                e.pedidoId?.let {
                    pedidoRepository.getPedidoById(it)?.let { pedido ->
                        textoPedido.value = pedido.textoPedido
                    }
                }

                carregarProdutosInventario()

            } catch (e: Exception) {
                erro.value = e.message
            }
        }
    }

    // -------- INVENTÁRIO --------
    private fun carregarProdutosInventario() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collectLatest { result ->
                if (result is ResultWrapper.Success) {
                    val lista = mutableListOf<Produto>()

                    result.value.forEach { produto ->
                        val id = produto.id ?: return@forEach
                        produtoRepository.observeLotes(id).collectLatest { lotes ->
                            if (lotes is ResultWrapper.Success) {
                                val stock = lotes.value.sumOf { it.quantidade }
                                if (stock > 0) {
                                    lista.add(produto.copy(quantidadeTotal = stock))
                                }
                            }
                        }
                    }
                    _produtosDisponiveis.value = lista
                }
            }
        }
    }

    // -------- ITENS DA ENTREGA --------
    fun adicionarProduto(produto: Produto, quantidade: Int) {
        val atual = entrega.value ?: return
        val itens = atual.itens.toMutableList()

        val index = itens.indexOfFirst { it.produtoId == produto.id }
        if (index >= 0) {
            val atualQtd = itens[index].lotesConsumidos.sumOf { it.quantidade }
            itens[index] = itens[index].copy(
                lotesConsumidos = listOf(LoteConsumido("manual", atualQtd + quantidade))
            )
        } else {
            itens.add(
                ItemEntrega(
                    produtoId = produto.id!!,
                    produtoNome = produto.nome,
                    lotesConsumidos = listOf(LoteConsumido("manual", quantidade))
                )
            )
        }

        entrega.value = atual.copy(itens = itens)
    }

    fun aumentarQuantidade(produtoId: String) = alterarQuantidade(produtoId, +1)
    fun diminuirQuantidade(produtoId: String) = alterarQuantidade(produtoId, -1)

    private fun alterarQuantidade(produtoId: String, delta: Int) {
        val atual = entrega.value ?: return

        val novosItens = atual.itens.mapNotNull {
            if (it.produtoId == produtoId) {
                val novaQtd = it.lotesConsumidos.sumOf { l -> l.quantidade } + delta
                if (novaQtd <= 0) null
                else it.copy(lotesConsumidos = listOf(LoteConsumido("manual", novaQtd)))
            } else it
        }

        entrega.value = atual.copy(itens = novosItens)
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    // -------- ALTERAR ESTADO / DATA --------
    fun alterarEstado(novo: EntregaStatus) {
        estadoSelecionado.value = novo
    }

    fun alterarData(novaData: Long) {
        dataEntrega.value = novaData
    }

    // -------- GUARDAR --------
    fun guardar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val e = entrega.value ?: return@launch

                // Consome stock apenas se mudou para ENTREGUE
                if (
                    estadoSelecionado.value == EntregaStatus.ENTREGUE &&
                    e.status != EntregaStatus.ENTREGUE
                ) {
                    e.itens.forEach { item ->
                        consumirLotesProduto(
                            item.produtoId,
                            item.lotesConsumidos.sumOf { it.quantidade }
                        )
                    }
                }

                // Atualiza pedido
                e.pedidoId?.let { pedidoId ->
                    val statusPedido = when (estadoSelecionado.value) {
                        EntregaStatus.PRONTO -> PedidoStatus.PRONTO
                        EntregaStatus.ENTREGUE -> PedidoStatus.ENTREGUE
                        else -> PedidoStatus.EM_ANDAMENTO
                    }
                    pedidoRepository.atualizarStatus(pedidoId, statusPedido)
                }

                val atualizada = e.copy(
                    status = estadoSelecionado.value,
                    dataEntrega = dataEntrega.value
                )

                entregaRepository.atualizarEntrega(atualizada)
                entrega.value = atualizada

                onSuccess()

            } catch (e: Exception) {
                erro.value = e.message
            }
        }
    }

    // -------- CONSUMO DE LOTES --------
    private suspend fun consumirLotesProduto(produtoId: String, qtd: Int) {
        var restante = qtd

        val resultado = produtoRepository.observeLotes(produtoId)
            .first { it !is ResultWrapper.Loading }

        if (resultado !is ResultWrapper.Success) {
            throw Exception("Erro ao carregar stock")
        }

        val lotes = resultado.value.sortedWith(
            compareBy<LoteStock> { it.dataValidade == null }.thenBy { it.dataValidade }
        )

        if (lotes.sumOf { it.quantidade } < qtd) {
            throw Exception("Stock insuficiente")
        }

        for (lote in lotes) {
            if (restante <= 0) break

            val consumir = minOf(lote.quantidade, restante)
            val novaQtd = lote.quantidade - consumir

            if (novaQtd == 0)
                produtoRepository.eliminarLote(produtoId, lote.id!!)
            else
                produtoRepository.atualizarQuantidadeLote(produtoId, lote.id!!, novaQtd)

            restante -= consumir
        }
    }
}
