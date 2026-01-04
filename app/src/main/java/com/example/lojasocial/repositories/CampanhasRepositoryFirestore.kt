package com.example.lojasocial.repositories

import com.example.lojasocial.models.Campanha
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CampanhasRepositoryFirestore @Inject constructor(
    private val db: FirebaseFirestore
) : CampanhasRepository {

    private val col = db.collection("campanhas")

    override fun observeCampanhas(): Flow<ResultWrapper<List<Campanha>>> = callbackFlow {
        trySend(ResultWrapper.Loading)

        val listener = col.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(ResultWrapper.Error(err.message ?: "Erro ao carregar campanhas"))
                return@addSnapshotListener
            }

            val list = snap?.documents?.map { d ->
                Campanha(
                    id = d.id,
                    nome = d.getString("nome") ?: "",
                    descricao = d.getString("descricao") ?: "",
                    dataInicio = d.getString("dataInicio") ?: "",
                    dataFim = d.getString("dataFim") ?: ""
                )
            } ?: emptyList()

            trySend(ResultWrapper.Success(list))
        }

        awaitClose { listener.remove() }
    }

    override suspend fun criarCampanha(c: Campanha): ResultWrapper<Unit> {
        return try {
            val data = hashMapOf(
                "nome" to c.nome,
                "descricao" to c.descricao,
                "dataInicio" to c.dataInicio,
                "dataFim" to c.dataFim
            )
            col.add(data).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao criar campanha")
        }
    }

    override suspend fun atualizarCampanha(c: Campanha): ResultWrapper<Unit> {
        return try {
            val data = hashMapOf(
                "nome" to c.nome,
                "descricao" to c.descricao,
                "dataInicio" to c.dataInicio,
                "dataFim" to c.dataFim
            )
            col.document(c.id).set(data).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao atualizar campanha")
        }
    }

    override suspend fun eliminarCampanha(id: String): ResultWrapper<Unit> {
        return try {
            col.document(id).delete().await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao eliminar campanha")
        }
    }

    override suspend fun obterCampanha(id: String): ResultWrapper<Campanha> {
        return try {
            val d = col.document(id).get().await()

            if (!d.exists()) {
                ResultWrapper.Error("Campanha não encontrada")
            } else {
                ResultWrapper.Success(
                    Campanha(
                        id = d.id,
                        nome = d.getString("nome") ?: "",
                        descricao = d.getString("descricao") ?: "",
                        dataInicio = d.getString("dataInicio") ?: "",
                        dataFim = d.getString("dataFim") ?: ""
                    )
                )
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao obter campanha")
        }
    }
}
