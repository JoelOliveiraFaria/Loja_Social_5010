package com.example.lojasocial.ui.entrega

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregaDetalhesViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    // Estado da entrega
    var entrega = mutableStateOf<Entrega?>(null)
        private set

    // Nome do beneficiário
    var nomeBeneficiario = mutableStateOf("")
        private set

    // Texto do pedido
    var textoPedido = mutableStateOf("")
        private set

    // Mensagem de erro
    var erro = mutableStateOf<String?>(null)
        private set

    // Lista de produtos disponíveis (para o Dialog)
    private val _produtosDisponiveis = MutableStateFlow<List<Produto>>(emptyList())
    val produtosDisponiveis: StateFlow<List<Produto>> get() = _produtosDisponiveis

    // ---------------- Funções ----------------

    fun carregarEntrega(entregaId: String) {
        viewModelScope.launch {
            try {
                val e = entregaRepository.getEntregaById(entregaId)
                if (e == null) {
                    erro.value = "Entrega não encontrada"
                    return@launch
                }
                entrega.value = e

                // Carregar nome do beneficiário
                e.beneficiarioId?.let { id ->
                    when (val r = beneficiarioRepository.obterBeneficiario(id)) {
                        is ResultWrapper.Success -> nomeBeneficiario.value = r.value.nome.toString()
                        else -> nomeBeneficiario.value = "Beneficiário"
                    }
                }

                // Carregar texto do pedido
                e.pedidoId?.let { pid ->
                    pedidoRepository.getPedidoById(pid)?.let {
                        textoPedido.value = it.textoPedido
                    }
                }

                // Carregar produtos disponíveis (exemplo, substitui pelo teu repo de produtos)
                carregarProdutosDisponiveis()

            } catch (ex: Exception) {
                erro.value = ex.message
            }
        }
    }

    // ---------------- Produtos Disponíveis ----------------
    private fun carregarProdutosDisponiveis() {
        viewModelScope.launch {
            // Aqui deves substituir pelo teu repository de produtos
            // Por exemplo: produtoRepository.getProdutos()
            // Exemplo hardcoded (substituir):
            val listaExemplo = listOf(
                Produto(id = "1", nome = "Produto A"),
                Produto(id = "2", nome = "Produto B"),
                Produto(id = "3", nome = "Produto C")
            )
            _produtosDisponiveis.value = listaExemplo
        }
    }

    // ---------------- Adicionar Produto ----------------
    fun adicionarProduto(produto: Produto, quantidade: Int) {
        val atual = entrega.value ?: return
        val novoItem = criarItemEntrega(produto, quantidade)
        entrega.value = atual.copy(itens = atual.itens + novoItem)
    }

    private fun criarItemEntrega(produto: Produto, quantidade: Int): ItemEntrega {
        return ItemEntrega(
            produtoId = produto.id ?: "",
            produtoNome = produto.nome,
            lotesConsumidos = listOf(
                LoteConsumido(
                    loteId = "manual", // ou outro id
                    quantidade = quantidade
                )
            )
        )
    }

    // ---------------- Salvar alterações ----------------
    fun salvarAlteracoes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entrega.value?.let {
                    entregaRepository.atualizarEntrega(it)
                    onSuccess()
                }
            } catch (ex: Exception) {
                erro.value = ex.message
            }
        }
    }

    // ---------------- Marcar como terminada ----------------
    fun marcarComoTerminada(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entrega.value?.let {
                    entregaRepository.atualizarEntrega(
                        it.copy(status = EntregaStatus.TERMINADO)
                    )
                    onSuccess()
                }
            } catch (ex: Exception) {
                erro.value = ex.message
            }
        }
    }
}
