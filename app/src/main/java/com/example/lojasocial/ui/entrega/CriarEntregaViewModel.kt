package com.example.lojasocial.ui.entrega

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CriarEntregaViewModel @Inject constructor(
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

    var produtosDisponiveis = mutableStateOf<List<Produto>>(emptyList())
        private set

    var beneficiarios = mutableStateOf<List<Beneficiario>>(emptyList())
        private set

    var beneficiarioSelecionado = mutableStateOf<String?>(null)
        private set

    init {

        viewModelScope.launch {
            beneficiarioRepository.observeBeneficiarios().collect { res ->
                if (res is ResultWrapper.Success) {
                    beneficiarios.value = res.value
                }
            }
        }
        carregarProdutos()
    }

    fun iniciarEntrega(beneficiarioId: String?, pedidoId: String?) {
        entrega.value = Entrega(
            beneficiarioId = beneficiarioId,
            pedidoId = pedidoId,
            itens = emptyList()
        )

        if (beneficiarioId != null) carregarBeneficiario(beneficiarioId)
        if (pedidoId != null) carregarPedido(pedidoId)
    }

    private fun carregarBeneficiario(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            when (val res = beneficiarioRepository.obterBeneficiario(id)) {
                is ResultWrapper.Success -> {
                    nomeBeneficiario.value = res.value.nome ?: "—"
                    beneficiarioSelecionado.value = res.value.id
                }
                else -> nomeBeneficiario.value = "Beneficiário desconhecido"
            }
        }
    }

    private fun carregarPedido(id: String) {
        viewModelScope.launch {
            val pedido = pedidoRepository.getPedidoById(id)
            textoPedido.value = pedido?.textoPedido ?: ""
        }
    }

    private fun carregarProdutos() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collect { res ->
                when (res) {
                    is ResultWrapper.Success -> produtosDisponiveis.value = res.value
                    is ResultWrapper.Error -> erro.value = res.message
                    else -> Unit
                }
            }
        }
    }

    fun setBeneficiarioManual(id: String, nome: String) {
        beneficiarioSelecionado.value = id
        nomeBeneficiario.value = nome
    }

    // ---------- Produtos com lotes ----------
    @RequiresApi(Build.VERSION_CODES.O)
    fun adicionarProduto(produto: Produto, quantidade: Int) {
        val atual = entrega.value ?: return
        viewModelScope.launch {
            try {
                val lotes = produtoRepository.observeLotes(produto.id!!).filterIsInstance<ResultWrapper.Success<List<LoteStock>>>().first().value
                val lotesDisponiveis = lotes.filter { it.quantidade > 0 }.sortedBy { it.dataValidade }
                if (lotesDisponiveis.isEmpty()) throw Exception("Sem stock disponível")

                var qtdRestante = quantidade
                val lotesConsumidos = mutableListOf<LoteConsumido>()

                for (lote in lotesDisponiveis) {
                    if (qtdRestante <= 0) break
                    val usar = minOf(qtdRestante, lote.quantidade)
                    lotesConsumidos.add(LoteConsumido(lote.id, usar))
                    qtdRestante -= usar
                }
                if (qtdRestante > 0) throw Exception("Stock insuficiente")

                val itensAtualizados = atual.itens.toMutableList()
                val index = itensAtualizados.indexOfFirst { it.produtoId == produto.id }
                if (index >= 0) {
                    val itemAtual = itensAtualizados[index]
                    itensAtualizados[index] = itemAtual.copy(
                        lotesConsumidos = itemAtual.lotesConsumidos + lotesConsumidos
                    )
                } else {
                    itensAtualizados.add(
                        ItemEntrega(
                            produtoId = produto.id,
                            produtoNome = produto.nome,
                            lotesConsumidos = lotesConsumidos
                        )
                    )
                }
                entrega.value = atual.copy(itens = itensAtualizados)

            } catch (e: Exception) {
                erro.value = e.message
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun aumentarQuantidade(produtoId: String) {
        val produto = produtosDisponiveis.value.firstOrNull { it.id == produtoId } ?: return
        adicionarProduto(produto, 1)
    }

    fun diminuirQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val itensAtualizados = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val qtdAtual = item.lotesConsumidos.sumOf { it.quantidade }
                if (qtdAtual <= 1) return@map item
                item.copy(lotesConsumidos = listOf(LoteConsumido(item.lotesConsumidos.first().loteId, qtdAtual - 1)))
            } else item
        }
        entrega.value = atual.copy(itens = itensAtualizados)
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    // ---------- Salvar entrega ----------
    fun salvarEntrega(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val atual = entrega.value ?: return onError("Entrega inválida ou sem produtos")
        if (atual.itens.isEmpty()) return onError("Entrega sem produtos")
        if (beneficiarioSelecionado.value.isNullOrBlank()) return onError("Selecionar um beneficiário")

        viewModelScope.launch {
            try {
                val entregaParaSalvar = atual.copy(
                    beneficiarioId = beneficiarioSelecionado.value
                )
                val idEntrega = entregaRepository.criarEntrega(entregaParaSalvar)

                // Atualiza status do pedido se houver
                atual.pedidoId?.let {
                    pedidoRepository.atualizarStatus(it, PedidoStatus.EM_ANDAMENTO)
                }

                onSuccess(idEntrega)
            } catch (e: Exception) {
                onError(e.message ?: "Erro ao criar entrega")
            }
        }
    }
}
