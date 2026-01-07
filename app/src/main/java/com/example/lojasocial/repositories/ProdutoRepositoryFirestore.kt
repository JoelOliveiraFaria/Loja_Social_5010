package com.example.lojasocial.repositories

import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.models.Produto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProdutoRepositoryFirestore @Inject constructor(
    private val db: FirebaseFirestore
) : ProdutoRepository {

    private val col = db.collection("produtos")

    override fun getProdutos(): Flow<ResultWrapper<List<Produto>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            val snapshot = col.get().await()
            val list = snapshot.toObjects(Produto::class.java)
            emit(ResultWrapper.Success(list))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: "Erro ao carregar inventário"))
        }
    }

   /* override fun observeLotes(produtoId: String): Flow<ResultWrapper<List<LoteStock>>> = flow {
        emit(ResultWrapper.Loading)
        try {
            // Acede à sub-coleção 'lotes' dentro do documento do produto
            val snapshot = col.document(produtoId).collection("lotes").get().await()
            val list = snapshot.toObjects(LoteStock::class.java).filterNotNull()
            emit(ResultWrapper.Success(list))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(e.message ?: "Erro ao carregar lotes"))
        }
    }
    */

    override fun observeLotes(produtoId: String): Flow<ResultWrapper<List<LoteStock>>> = flow {
        emit(ResultWrapper.Loading)
        val snapshot = col.document(produtoId).collection("lotes").get().await()
        emit(ResultWrapper.Success(snapshot.toObjects(LoteStock::class.java).filterNotNull()))
    }.catch { e ->
        emit(ResultWrapper.Error(e.message ?: "Erro ao carregar lotes"))
    }


    override suspend fun adicionarProduto(produto: Produto): ResultWrapper<Unit> {
        return try {
            val docRef = col.document()
            docRef.set(produto.copy(id = docRef.id)).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao criar produto")
        }
    }

    override suspend fun adicionarLote(produtoId: String, lote: LoteStock): ResultWrapper<Unit> {
        return try {
            val lotesCol = col.document(produtoId).collection("lotes")
            val newDoc = lotesCol.document()
            lotesCol.document(newDoc.id).set(lote.copy(id = newDoc.id)).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao adicionar stock")
        }
    }

    override suspend fun eliminarLote(produtoId: String, loteId: String): ResultWrapper<Unit> {
        return try {
            col.document(produtoId).collection("lotes").document(loteId).delete().await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error("Erro ao apagar lote")
        }
    }

    override suspend fun eliminarProduto(produto: Produto): ResultWrapper<Unit> {
        return try {
            // Regra BPMN pág. 14: Só elimina se o stock válido for 0 [cite: 617]
            if (produto.quantidadeTotal > 0) return ResultWrapper.Error("Ainda existe stock disponível")
            produto.id?.let { col.document(it).delete().await() }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error("Erro ao eliminar")
        }
    }

    override fun getProdutosExpirados(): Flow<ResultWrapper<List<Produto>>> = flow { }
}