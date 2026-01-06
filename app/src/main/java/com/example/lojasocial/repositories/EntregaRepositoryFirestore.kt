package com.example.lojasocial.repositories

import com.example.lojasocial.models.Entrega
import com.google.firebase.firestore.FirebaseFirestore
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
        if (entrega.id != null) {
            entregasCollection.document(entrega.id).set(entrega).await()
        }
    }

    override suspend fun getEntregaById(entregaId: String): Entrega? {
        val doc = entregasCollection.document(entregaId).get().await()
        return doc.toObject(Entrega::class.java)?.copy(id = doc.id)
    }
}


