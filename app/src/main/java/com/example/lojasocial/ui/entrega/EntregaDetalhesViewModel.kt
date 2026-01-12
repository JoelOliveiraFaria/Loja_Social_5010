package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.*
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
    var nomeBeneficiario = mutableStateOf("")
    var textoPedido = mutableStateOf("")
    var erro = mutableStateOf<String?>(null)
    var avisoStock = mutableStateOf<String?>(null)

    private val _produtosDisponiveis = MutableStateFlow<List<Produto>>(emptyList())
    val produtosDisponiveis: StateFlow<List<Produto>> = _produtosDisponiveis

    val produtosComStockAtualizado: List<Produto>
        get() {
            val itensNaEntregaAtual = entrega.value?.itens ?: emptyList()
            val produtosNoBanco = produtosDisponiveis.value

            return produtosNoBanco.map { produto ->
                // Verificamos se este produto já está na lista que estamos a editar
                val itemEdicao = itensNaEntregaAtual.find { it.produtoId == produto.id }
                val qtdNaEdicao = itemEdicao?.lotesConsumidos?.sumOf { it.quantidade } ?: 0

                // IMPORTANTE: Como no Detalhes o stock do DB já está sem a quantidade original,
                // aqui só precisamos de subtrair o que o utilizador adicionou A MAIS agora.
                // Mas para facilitar a tua lógica, vamos focar no que resta no armazém:
                produto.copy(quantidadeTotal = produto.quantidadeTotal)
            }
        }

    fun carregarEntrega(entregaId: String) {
        viewModelScope.launch {
            try {
                val e = entregaRepository.getEntregaById(entregaId) ?: return@launch
                entrega.value = e

                e.beneficiarioId?.let {
                    val r = beneficiarioRepository.obterBeneficiario(it)
                    if (r is ResultWrapper.Success) nomeBeneficiario.value = r.value.nome ?: ""
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

    private fun carregarProdutosInventario() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collectLatest { result ->
                if (result is ResultWrapper.Success) {
                    _produtosDisponiveis.value = result.value
                }
            }
        }
    }

    fun atualizarData(novaData: Long?) {
        entrega.value = entrega.value?.copy(dataEntrega = novaData)
    }

    fun atualizarStatus(novoStatus: EntregaStatus) {
        entrega.value = entrega.value?.copy(status = novoStatus)
    }

    fun adicionarProduto(produto: Produto) {
        val atual = entrega.value ?: return
        val itensAtuais = atual.itens.toMutableList()

        if (produto.quantidadeTotal <= 0) {
            avisoStock.value = "Não há mais stock de ${produto.nome} no armazém."
            return
        }

        val itemExistente = itensAtuais.find { it.produtoId == produto.id }

        if (itemExistente != null) {
            aumentarQuantidade(produto.id ?: "")
        } else {
            val novoItem = ItemEntrega(
                produtoId = produto.id ?: "",
                produtoNome = produto.nome,
                lotesConsumidos = listOf(LoteConsumido(quantidade = 1))
            )
            itensAtuais.add(novoItem)
            entrega.value = atual.copy(itens = itensAtuais)

            atualizarStockLocal(produto.id ?: "", -1)
        }
    }

    fun aumentarQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val produtoNoBanco = produtosDisponiveis.value.find { it.id == produtoId }
        val stockDisponivelNoArmazem = produtoNoBanco?.quantidadeTotal ?: 0

        if (stockDisponivelNoArmazem > 0) {
            val novosItens = atual.itens.map { item ->
                if (item.produtoId == produtoId) {
                    val lote = item.lotesConsumidos.first()
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade + 1)))
                } else item
            }
            entrega.value = atual.copy(itens = novosItens)

            atualizarStockLocal(produtoId, -1)
        } else {
            avisoStock.value = "Não é possível adicionar mais. Stock esgotado no armazém."
        }
    }

    fun diminuirQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val novosItens = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val lote = item.lotesConsumidos.first()
                if (lote.quantidade > 1) {
                    atualizarStockLocal(produtoId, 1)
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade - 1)))
                } else item
            } else item
        }
        entrega.value = atual.copy(itens = novosItens)
    }

    private fun atualizarStockLocal(produtoId: String, delta: Int) {
        val listaAtual = _produtosDisponiveis.value.toMutableList()
        val index = listaAtual.indexOfFirst { it.id == produtoId }
        if (index != -1) {
            val p = listaAtual[index]
            listaAtual[index] = p.copy(quantidadeTotal = p.quantidadeTotal + delta)
            _produtosDisponiveis.value = listaAtual
        }
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return

        val itemParaRemover = atual.itens.find { it.produtoId == produtoId }
        val qtdParaDevolver = itemParaRemover?.lotesConsumidos?.sumOf { it.quantidade } ?: 0

        if (qtdParaDevolver > 0) {
            atualizarStockLocal(produtoId, qtdParaDevolver)
        }
        
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    fun guardar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entrega.value?.let { entregaRepository.atualizarEntrega(it) }
                onSuccess()
            } catch (e: Exception) { erro.value = e.message }
        }
    }

    fun limparAviso() { avisoStock.value = null }
}