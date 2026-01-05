package com.example.lojasocial.repositories

import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.models.Produto
import kotlinx.coroutines.flow.Flow

interface ProdutoRepository {
    // RF2: Obter lista de produtos "mestre" para o inventário
    fun getProdutos(): Flow<ResultWrapper<List<Produto>>>

    // RF7/RF8: Observar os diferentes lotes/pacotes de um produto específico
    fun observeLotes(produtoId: String): Flow<ResultWrapper<List<LoteStock>>>

    // BPMN pág. 17: Criar a definição de um novo produto (apenas nome e descrição)
    suspend fun adicionarProduto(produto: Produto): ResultWrapper<Unit>

    // RF6: Adicionar um novo lote de stock com uma validade específica
    suspend fun adicionarLote(produtoId: String, lote: LoteStock): ResultWrapper<Unit>

    // Novo: Permite apagar um lote específico (usado na função de limpar expirados)
    suspend fun eliminarLote(produtoId: String, loteId: String): ResultWrapper<Unit>

    // BPMN pág. 14: Eliminar o produto mestre (apenas se não houver stock total)
    suspend fun eliminarProduto(produto: Produto): ResultWrapper<Unit>

    // RF8: Obter relatório de produtos que vão expirar brevemente
    fun getProdutosExpirados(): Flow<ResultWrapper<List<Produto>>>
}