package com.example.lojasocial.ui.entrega

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.*
import com.example.lojasocial.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class CriarEntregaViewModel @Inject constructor(
    private val entregaRepository: EntregaRepository,
    private val beneficiarioRepository: BeneficiarioRepository,
    private val pedidoRepository: PedidoRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    /* ---------- STATE ---------- */

    private val _entrega = mutableStateOf<Entrega?>(null)
    val entrega: State<Entrega?> = _entrega

    private val _nomeBeneficiario = mutableStateOf("")
    val nomeBeneficiario: State<String> = _nomeBeneficiario

    private val _textoPedido = mutableStateOf("")
    val textoPedido: State<String> = _textoPedido

    private val _erro = mutableStateOf<String?>(null)
    val erro: State<String?> = _erro

    private val _produtosDisponiveis = mutableStateOf<List<Produto>>(emptyList())
    val produtosDisponiveis: State<List<Produto>> = _produtosDisponiveis

    /* ---------- INIT ---------- */

    fun iniciarEntrega(beneficiarioId: String?, pedidoId: String?) {
        _entrega.value = Entrega(
            beneficiarioId = beneficiarioId,
            pedidoId = pedidoId,
            itens = emptyList()
        )

        carregarBeneficiario(beneficiarioId)
        carregarPedido(pedidoId)
    }

    fun carregarProdutos() {
        viewModelScope.launch {
            produtoRepository.getProdutos().collect { res ->
                when (res) {
                    is ResultWrapper.Success -> _produtosDisponiveis.value = res.value
                    is ResultWrapper.Error -> _erro.value = res.message
                    else -> Unit
                }
            }
        }
    }

    private fun carregarBeneficiario(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            when (val res = beneficiarioRepository.obterBeneficiario(id)) {
                is ResultWrapper.Success ->
                    _nomeBeneficiario.value = res.value.nome ?: "—"
                else ->
                    _nomeBeneficiario.value = "Beneficiário desconhecido"
            }
        }
    }

    private fun carregarPedido(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            _textoPedido.value =
                pedidoRepository.getPedidoById(id)?.textoPedido ?: ""
        }
    }

    /* ---------- PRODUTOS (SIMPLIFICADO – SEM DATAS) ---------- */

    @RequiresApi(Build.VERSION_CODES.O)
    fun adicionarProduto(
        produto: Produto,
        quantidadePedida: Int
    ) {
        val entregaAtual = _entrega.value ?: return

        viewModelScope.launch {
            try {
                // 🔹 Pega apenas o Success do Flow
                val res = produtoRepository.observeLotes(produto.id!!)
                    .filterIsInstance<ResultWrapper.Success<List<LoteStock>>>()
                    .first()

                val hoje = LocalDate.now()

                // 🔹 Filtra lotes com stock e dataValidade não nula
                val lotesDisponiveis = res.value
                    .filter { it.quantidade > 0 && it.dataValidade != null }

                if (lotesDisponiveis.isEmpty()) {
                    throw Exception("Sem stock disponível")
                }

                // 🔹 Ordena pelo lote com validade mais próxima do dia atual
                val loteSelecionado = lotesDisponiveis.minByOrNull {
                    val dataValidade = LocalDate.parse(it.dataValidade)
                    ChronoUnit.DAYS.between(hoje, dataValidade).absoluteValue
                }!!

                // 🔹 Verifica stock suficiente
                if (loteSelecionado.quantidade < quantidadePedida) {
                    throw Exception("Stock insuficiente neste lote")
                }

                // 🔹 Cria item da entrega
                val item = ItemEntrega(
                    produtoId = produto.id,
                    produtoNome = produto.nome,
                    lotesConsumidos = listOf(
                        LoteConsumido(
                            loteId = loteSelecionado.id!!,
                            quantidade = quantidadePedida
                        )
                    )
                )

                // 🔹 Atualiza estado da entrega
                _entrega.value = entregaAtual.copy(
                    itens = entregaAtual.itens + item
                )

            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }


    /* ---------- GUARDAR ---------- */

    fun salvarEntrega(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val entregaAtual = _entrega.value
        if (entregaAtual == null || entregaAtual.itens.isEmpty()) {
            onError("Entrega inválida ou sem produtos")
            return
        }

        viewModelScope.launch {
            try {
                val id = entregaRepository.criarEntrega(entregaAtual)
                onSuccess(id)
            } catch (e: Exception) {
                onError(e.message ?: "Erro ao guardar entrega")
            }
        }
    }
}
