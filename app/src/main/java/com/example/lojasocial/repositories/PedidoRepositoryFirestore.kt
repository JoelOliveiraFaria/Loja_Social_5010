package com.example.lojasocial.repositories

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

        val listener = pedidosCollection
            .whereEqualTo("status", status.name)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Pedido::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getPedidoById(pedidoId: String): Pedido? {
        val doc = pedidosCollection.document(pedidoId).get().await()
        return doc.toObject(Pedido::class.java)?.copy(id = doc.id)
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
}
