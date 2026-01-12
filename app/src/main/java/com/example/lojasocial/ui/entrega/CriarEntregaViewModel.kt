package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var nomeBeneficiario = mutableStateOf("")
    var beneficiarioSelecionadoId = mutableStateOf<String?>(null)
    var beneficiariosDisponiveis = mutableStateOf<List<Beneficiario>>(emptyList())
    var produtosDisponiveis = mutableStateOf<List<Produto>>(emptyList())
    var erro = mutableStateOf<String?>(null)

    // Estado para o Pop-up de aviso de stock
    var avisoStock = mutableStateOf<String?>(null)

    val produtosComStockAtualizado: List<Produto>
        get() {
            val itensNaEntrega = entrega.value?.itens ?: emptyList()
            return produtosDisponiveis.value.map { produto ->
                val selecionado = itensNaEntrega.find { it.produtoId == produto.id }
                val qtdSelecionada = selecionado?.lotesConsumidos?.sumOf { it.quantidade } ?: 0
                // Subtrai o que já está na lista do total para mostrar no Dialog
                produto.copy(quantidadeTotal = produto.quantidadeTotal - qtdSelecionada)
            }
        }


    fun inicializar(pedidoId: String?) {
        // 1. Carregar Beneficiários
        viewModelScope.launch {
            beneficiarioRepository.observeBeneficiarios().collect { result ->
                if (result is ResultWrapper.Success) {
                    beneficiariosDisponiveis.value = result.value
                }
            }
        }

        // 2. Carregar Produtos do Inventário
        viewModelScope.launch {
            produtoRepository.getProdutos().collect { result ->
                when (result) {
                    is ResultWrapper.Success -> produtosDisponiveis.value = result.value
                    is ResultWrapper.Error -> erro.value = result.message
                    else -> {}
                }
            }
        }

        // 3. Lógica de inicialização de Pedido ou Manual
        viewModelScope.launch {
            if (!pedidoId.isNullOrBlank()) {
                val pedido = pedidoRepository.getPedidoById(pedidoId)
                pedido?.let { p ->
                    beneficiarioSelecionadoId.value = p.beneficiarioId
                    val bResult = beneficiarioRepository.obterBeneficiario(p.beneficiarioId)
                    if (bResult is ResultWrapper.Success) {
                        nomeBeneficiario.value = bResult.value.nome ?: ""
                    }
                    entrega.value = Entrega(
                        pedidoId = pedidoId,
                        beneficiarioId = p.beneficiarioId,
                        status = EntregaStatus.EM_ANDAMENTO,
                        itens = emptyList()
                    )
                }
            } else {
                entrega.value = Entrega(status = EntregaStatus.EM_ANDAMENTO)
            }
        }
    }

    fun limparAviso() {
        avisoStock.value = null
    }

    fun selecionarBeneficiarioManual(b: Beneficiario) {
        beneficiarioSelecionadoId.value = b.id
        nomeBeneficiario.value = b.nome ?: ""
        entrega.value = entrega.value?.copy(beneficiarioId = b.id)
    }

    fun adicionarProduto(produto: Produto) {
        val atual = entrega.value ?: return
        val itensAtuais = atual.itens.toMutableList()

        // Validar se ainda há stock disponível desse produto
        if (produto.quantidadeTotal <= 0) {
            avisoStock.value = "Não há mais stock disponível de ${produto.nome}"
            return
        }

        val itemExistente = itensAtuais.find { it.produtoId == produto.id }

        if (itemExistente != null) {
            // Se já existe, aumentamos a quantidade se houver stock
            val qtdAtual = itemExistente.lotesConsumidos.sumOf { it.quantidade }
            if (qtdAtual < (produtosDisponiveis.value.find { it.id == produto.id }?.quantidadeTotal ?: 0)) {
                val novosItens = itensAtuais.map { item ->
                    if (item.produtoId == produto.id) {
                        val lote = item.lotesConsumidos.first()
                        item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade + 1)))
                    } else item
                }
                entrega.value = atual.copy(itens = novosItens)
            } else {
                avisoStock.value = "Limite de stock atingido para este produto."
            }
        } else {
            // Se é novo na lista, adicionamos com quantidade 1
            val novoItem = ItemEntrega(
                produtoId = produto.id ?: "",
                produtoNome = produto.nome,
                lotesConsumidos = listOf(LoteConsumido(quantidade = 1))
            )
            itensAtuais.add(novoItem)
            entrega.value = atual.copy(itens = itensAtuais)
        }
    }

    fun aumentarQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val stockMax = produtosDisponiveis.value.find { it.id == produtoId }?.quantidadeTotal ?: 0

        val novosItens = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val qtdAtual = item.lotesConsumidos.sumOf { it.quantidade }
                if (qtdAtual < stockMax) {
                    val lote = item.lotesConsumidos.first()
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade + 1)))
                } else {
                    avisoStock.value = "Não existe mais stock disponível."
                    item
                }
            } else item
        }
        entrega.value = atual.copy(itens = novosItens)
    }

    fun diminuirQuantidade(produtoId: String) {
        val atual = entrega.value ?: return
        val novosItens = atual.itens.map { item ->
            if (item.produtoId == produtoId) {
                val lote = item.lotesConsumidos.first()
                if (lote.quantidade > 1) {
                    item.copy(lotesConsumidos = listOf(lote.copy(quantidade = lote.quantidade - 1)))
                } else item
            } else item
        }
        entrega.value = atual.copy(itens = novosItens)
    }

    fun removerProduto(produtoId: String) {
        val atual = entrega.value ?: return
        entrega.value = atual.copy(itens = atual.itens.filterNot { it.produtoId == produtoId })
    }

    fun salvarEntrega(onSuccess: () -> Unit) {
        val atual = entrega.value ?: return
        if (beneficiarioSelecionadoId.value == null) {
            erro.value = "Selecione um beneficiário"
            return
        }
        if (atual.itens.isEmpty()) {
            erro.value = "Adicione pelo menos um produto"
            return
        }

        viewModelScope.launch {
            try {
                entregaRepository.criarEntrega(atual.copy(beneficiarioId = beneficiarioSelecionadoId.value))
                onSuccess()
            } catch (e: Exception) {
                erro.value = e.message
            }
        }
    }
}