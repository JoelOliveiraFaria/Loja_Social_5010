package com.example.lojasocial.repositories

import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun criarEntrega(entrega: Entrega): String {
        val docRef = entregasCollection.add(entrega).await()
        return docRef.id
    }

    override suspend fun atualizarEntrega(entrega: Entrega) {
        entrega.id?.let {
            entregasCollection.document(it).set(entrega).await()
        }
    }

    override suspend fun getEntregaById(entregaId: String): Entrega? {
        val doc = entregasCollection.document(entregaId).get().await()
        return doc.toObject(Entrega::class.java)?.copy(id = doc.id)
    }

    override fun getEntregasPorStatus(
        status: EntregaStatus
    ): Flow<List<Entrega>> = callbackFlow {

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


