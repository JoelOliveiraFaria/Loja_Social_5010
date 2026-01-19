package com.example.lojasocial.di

import com.example.lojasocial.repositories.BeneficiarioRepository
import com.example.lojasocial.repositories.BeneficiarioRepositoryFirestore
import com.example.lojasocial.repositories.CampanhasRepository
import com.example.lojasocial.repositories.CampanhasRepositoryFirestore
import com.example.lojasocial.repositories.ProdutoRepository
import com.example.lojasocial.repositories.ProdutoRepositoryFirestore
import com.example.lojasocial.repositories.PedidoRepository
import com.example.lojasocial.repositories.PedidoRepositoryFirestore
import com.example.lojasocial.repositories.EntregaRepository
import com.example.lojasocial.repositories.EntregaRepositoryFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import androidx.room.Room
import com.example.lojasocial.local.AppDatabase
import com.example.lojasocial.local.TrackedPedidoDao
import com.example.lojasocial.repositories.TrackedPedidoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun providePedidoRepository(
        repo: PedidoRepositoryFirestore
    ): PedidoRepository = repo

    @Provides
    @Singleton
    fun provideEntregaRepository(
        repo: EntregaRepositoryFirestore
    ): EntregaRepository = repo

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lojasocial.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTrackedPedidoDao(db: AppDatabase): TrackedPedidoDao =
        db.trackedPedidoDao()

    @Provides
    @Singleton
    fun provideTrackedPedidoRepository(dao: TrackedPedidoDao): TrackedPedidoRepository =
        TrackedPedidoRepository(dao)

}
