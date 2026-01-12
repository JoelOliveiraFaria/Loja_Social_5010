package com.example.lojasocial.repositories

import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntregaRepositoryFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) : EntregaRepository {

    private val entregasCollection = firestore.collection("entregas")
    private val produtosCollection = firestore.collection("produtos")

    override suspend fun criarEntrega(entrega: Entrega): String {
        val newDocRef = entregasCollection.document()
        val entregaId = newDocRef.id
        val entregaComId = entrega.copy(id = entregaId)

        firestore.runTransaction { transaction ->
            transaction.set(newDocRef, entregaComId)
            entrega.itens.forEach { item ->
                val produtoRef = produtosCollection.document(item.produtoId)
                val totalParaRetirar = item.lotesConsumidos.sumOf { it.quantidade }
                transaction.update(produtoRef, "quantidadeTotal", FieldValue.increment(-totalParaRetirar.toLong()))
            }
        }.await()

        return entregaId
    }

    override suspend fun atualizarEntrega(entrega: Entrega) {
        val id = entrega.id ?: return
        val entregaRef = entregasCollection.document(id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(entregaRef)
            val entregaAntiga = snapshot.toObject(Entrega::class.java) ?: return@runTransaction

            val qtdsAntigas = entregaAntiga.itens.associate { it.produtoId to it.lotesConsumidos.sumOf { l -> l.quantidade } }
            val qtdsNovas = entrega.itens.associate { it.produtoId to it.lotesConsumidos.sumOf { l -> l.quantidade } }

            val todosProdutos = (qtdsAntigas.keys + qtdsNovas.keys).distinct()

            todosProdutos.forEach { pId ->
                val antiga = qtdsAntigas[pId] ?: 0
                val nova = qtdsNovas[pId] ?: 0
                val diferenca = antiga - nova

                if (diferenca != 0) {
                    val produtoRef = produtosCollection.document(pId)
                    transaction.update(produtoRef, "quantidadeTotal", FieldValue.increment(diferenca.toLong()))
                }
            }

            transaction.set(entregaRef, entrega)
        }.await()
    }

    override suspend fun getEntregaById(entregaId: String): Entrega? {
        val doc = entregasCollection.document(entregaId).get().await()
        return doc.toObject(Entrega::class.java)?.copy(id = doc.id)
    }

    override suspend fun temEntregaAtiva(beneficiarioId: String): Boolean {
        val snapshot = entregasCollection
            .whereEqualTo("beneficiarioId", beneficiarioId)
            .whereIn("status", listOf("EM_ANDAMENTO", "PRONTO"))
            .get()
            .await()
        return !snapshot.isEmpty
    }

    override fun getEntregasPorStatus(status: EntregaStatus): Flow<List<Entrega>> = callbackFlow {
        val listener = entregasCollection
            .whereEqualTo("status", status.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val entregas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Entrega::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(entregas)
            }
        awaitClose { listener.remove() }
    }
}