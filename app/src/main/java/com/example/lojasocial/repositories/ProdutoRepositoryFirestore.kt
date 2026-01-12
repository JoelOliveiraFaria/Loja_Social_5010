package com.example.lojasocial.repositories

import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.models.Produto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
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
            val produtoRef = col.document(produtoId)
            val lotesCol = produtoRef.collection("lotes")
            val newDoc = lotesCol.document()

            // Usamos uma "WriteBatch" para garantir que se um falhar, o outro não acontece
            val batch = db.batch()

            // 1. Criar o lote na sub-coleção
            batch.set(newDoc, lote.copy(id = newDoc.id))

            // 2. Incrementar a quantidadeTotal no documento do Produto
            batch.update(produtoRef, "quantidadeTotal", FieldValue.increment(lote.quantidade.toLong()))

            batch.commit().await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao adicionar stock")
        }
    }

    override suspend fun atualizarStock(produtoId: String, quantidadeAlteracao: Int) {
        try {
            val docRef = db.collection("produtos").document(produtoId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val stockAtual = snapshot.getLong("quantidadeTotal") ?: 0L
                transaction.update(docRef, "quantidadeTotal", stockAtual + quantidadeAlteracao)
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun eliminarLote(produtoId: String, loteId: String): ResultWrapper<Unit> {
        return try {
            val produtoRef = col.document(produtoId)
            val loteRef = produtoRef.collection("lotes").document(loteId)
            val snapshot = loteRef.get().await()
            val qtdParaSubtrair = snapshot.getLong("quantidade") ?: 0L
            val batch = db.batch()

            batch.delete(loteRef)
            batch.update(produtoRef, "quantidadeTotal", FieldValue.increment(-qtdParaSubtrair))
            batch.commit().await()
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

    override suspend fun atualizarQuantidadeLote(
        produtoId: String,
        loteId: String,
        novaQuantidade: Int
    ) {
        col.document(produtoId)
            .collection("lotes")
            .document(loteId)
            .update("quantidade", novaQuantidade)
            .await()
    }

}


