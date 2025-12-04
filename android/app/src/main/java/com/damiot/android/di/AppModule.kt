package com.damiot.android.di

import com.damiot.android.data.api.DamiotApi
import com.damiot.android.data.repository.DeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para dependencias de la aplicación
 * 
 * Proporciona instancias singleton de las clases que necesitan
 * ser inyectadas en otros componentes de la aplicación.
 * 
 * @InstallIn(SingletonComponent::class) indica que estas dependencias
 * viven durante toda la vida de la aplicación.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Proporciona el repositorio de dispositivos
     * @param api Cliente de la API (proporcionado por NetworkModule)
     * @return Instancia singleton del repositorio
     */
    @Provides
    @Singleton
    fun provideDeviceRepository(api: DamiotApi): DeviceRepository {
        return DeviceRepository(api)
    }
}
