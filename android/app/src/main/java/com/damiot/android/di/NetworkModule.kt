package com.damiot.android.di

import com.damiot.android.data.api.DamiotApi
import com.damiot.android.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo de Hilt para configuración de red
 * 
 * Proporciona las instancias necesarias para la comunicación HTTP
 * con el backend: OkHttpClient, Retrofit y la interfaz de la API.
 * 
 * @author Emilio José Salmerón Arjona
 * @since 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Proporciona el cliente HTTP OkHttp configurado
     * 
     * Configuración:
     * - Logging: Registra peticiones y respuestas (útil para depuración)
     * - Timeouts: 30 segundos para conexión, lectura y escritura
     * 
     * @return Cliente OkHttp configurado
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Interceptor para logging de peticiones HTTP
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Proporciona la instancia de Retrofit configurada
     * 
     * Retrofit convierte las llamadas HTTP en llamadas a métodos Kotlin.
     * Usa Gson para serializar/deserializar JSON automáticamente.
     * 
     * @param okHttpClient Cliente HTTP a usar
     * @return Instancia de Retrofit configurada
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.Network.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Proporciona la implementación de la interfaz DamiotApi
     * 
     * Retrofit genera automáticamente la implementación de la interfaz
     * basándose en las anotaciones de los métodos.
     * 
     * @param retrofit Instancia de Retrofit
     * @return Implementación de DamiotApi
     */
    @Provides
    @Singleton
    fun provideDamiotApi(retrofit: Retrofit): DamiotApi {
        return retrofit.create(DamiotApi::class.java)
    }
}
