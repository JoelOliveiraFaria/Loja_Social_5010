package com.example.lojasocial.repositories

import android.util.Log // Importante para ver o erro do índice
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.models.PedidoStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PedidoRepositoryFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) : PedidoRepository {

    private val pedidosCollection = firestore.collection("pedidos")

    override fun getPedidosPorStatus(
        status: PedidoStatus
    ): Flow<List<Pedido>> = callbackFlow {

        // Query: Filtra pelo Status E Ordena pela Data (Mais recente primeiro)
        val listener = pedidosCollection
            .whereEqualTo("status", status.name)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    // --- PONTO CRÍTICO ---
                    // Se a lista estiver vazia, verifique o Logcat por "FIRESTORE_ERRO".
                    // Vai aparecer um link para criar o índice no Firebase Console.
                    Log.e("FIRESTORE_ERRO", "Erro ao carregar pedidos (${status.name}): ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    // Converte o documento para o objeto Pedido e anexa o ID do documento
                    doc.toObject(Pedido::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getPedidoById(pedidoId: String): Pedido? {
        return try {
            val doc = pedidosCollection.document(pedidoId).get().await()
            doc.toObject(Pedido::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            Log.e("FIRESTORE_ERRO", "Erro ao buscar pedido $pedidoId: ${e.message}")
            null
        }
    }

    override suspend fun aceitarPedido(pedidoId: String) {
        pedidosCollection.document(pedidoId).update(
            "status", PedidoStatus.EM_ANDAMENTO.name
        ).await()
    }

    override suspend fun recusarPedido(
        pedidoId: String,
        motivo: String
    ) {
        pedidosCollection.document(pedidoId).update(
            mapOf(
                "status" to PedidoStatus.RECUSADO.name,
                "respostaRecusa" to motivo
            )
        ).await()
    }

    override suspend fun atualizarStatus(
        pedidoId: String,
        status: PedidoStatus
    ) {
        pedidosCollection.document(pedidoId)
            .update("status", status.name)
            .await()
    }
}