package com.example.lojasocial.models

data class Beneficiario (
    val id : String? = null,
    val nome : String? = null,
    val nif : String? = null,
    val email : String? = null,
    val telefone : String? = null,
    val estado : Boolean? = true,
    val senhaTemporaria : String? = null
)

