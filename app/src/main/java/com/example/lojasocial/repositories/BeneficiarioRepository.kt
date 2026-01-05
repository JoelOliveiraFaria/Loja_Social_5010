package com.example.lojasocial.repositories

import com.example.lojasocial.models.Beneficiario
import kotlinx.coroutines.flow.Flow

interface BeneficiarioRepository {

    fun observeBeneficiarios(): Flow<ResultWrapper<List<Beneficiario>>>

    suspend fun criarBeneficiario(b: Beneficiario): ResultWrapper<Unit>

    suspend fun atualizarBeneficiario(b: Beneficiario): ResultWrapper<Unit>

    suspend fun eliminarBeneficiario(id: String): ResultWrapper<Unit>

    suspend fun obterBeneficiario(id: String): ResultWrapper<Beneficiario>

}