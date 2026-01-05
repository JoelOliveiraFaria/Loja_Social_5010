package com.example.lojasocial.di

import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.BeneficiarioRepositoryFirestore
import com.example.lojasocial.repositories.CampanhasRepository
import com.example.lojasocial.repositories.CampanhasRepositoryFirestore
import com.example.lojasocial.repositories.ProdutoRepository
import com.example.lojasocial.repositories.ProdutoRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideCampanhasRepository(
        repo: CampanhasRepositoryFirestore
    ): CampanhasRepository = repo

    @Provides
    @Singleton
    fun provideBeneficiarioRepository(
        repo: BeneficiarioRepositoryFirestore
    ): BeneficiarioRepository = repo

    @Provides
    @Singleton
    fun provideProdutoRepository(
        repo: ProdutoRepositoryFirestore
    ): ProdutoRepository = repo
}
