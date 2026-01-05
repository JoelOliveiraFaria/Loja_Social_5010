package com.example.lojasocial.repositories

import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.models.Campanha
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BeneficiarioRepositoryFirestore @Inject constructor(
    private val db: FirebaseFirestore
) : BeneficiarioRepository {

    private val col = db.collection("beneficiarios")

    override fun observeBeneficiarios(): Flow<ResultWrapper<List<Beneficiario>>> = callbackFlow {
        trySend(ResultWrapper.Loading)

        val listener = col.addSnapshotListener { snap, err ->
            if (err != null) {
                trySend(ResultWrapper.Error(err.message ?: "Erro ao carregar beneficiario"))
                return@addSnapshotListener
            }

            val list = snap?.documents?.map { d ->
                Beneficiario(
                    id = d.id,
                    nome = d.getString("nome") ?: "",
                    nif = d.getString("nif") ?: "",
                    email = d.getString("email") ?: "",
                    telefone = d.getString("telefone") ?: "",
                    estado = d.getBoolean("estado") ?: true
                )
            } ?: emptyList()
            trySend(ResultWrapper.Success(list))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun criarBeneficiario(b: Beneficiario): ResultWrapper<Unit> {
        return try {
            val data = hashMapOf(
                "nome" to b.nome,
                "nif" to b.nif,
                "email" to b.email,
                "telefone" to b.telefone,
                "estado" to b.estado
            )
            col.add(data).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao criar campanha")
        }
    }

    override suspend fun atualizarBeneficiario(b: Beneficiario): ResultWrapper<Unit> {
        if (b.id.isNullOrEmpty()) {
            return ResultWrapper.Error("Erro: Não é possível atualizar um beneficiário sem ID.")
        }

        return try {
            val data = hashMapOf(
                "nome" to b.nome,
                "nif" to b.nif,
                "email" to b.email,
                "telefone" to b.telefone,
                "estado" to b.estado
            )
            col.document(b.id).set(data).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao atualizar campanha")
        }
    }

    override suspend fun eliminarBeneficiario(id: String): ResultWrapper<Unit> {
        return try {
            col.document(id).delete().await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao eliminar campanha")
        }
    }

    override suspend fun obterBeneficiario(id: String): ResultWrapper<Beneficiario> {
        return try {
            val d = col.document(id).get().await()

            if (!d.exists()) {
                ResultWrapper.Error("Beneficiario não encontrado")
            } else {
                ResultWrapper.Success(
                    Beneficiario(
                        id = d.id,
                        nome = d.getString("nome") ?: "",
                        nif = d.getString("nif") ?: "",
                        email = d.getString("email") ?: "",
                        telefone = d.getString("telefone") ?: "",
                        estado = d.getBoolean("estado") ?: true
                    )
                )
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Erro ao obter beneficiario")
        }
    }

}