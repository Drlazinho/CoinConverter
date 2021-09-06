package br.com.dio.coinconverter.data.di

import android.util.Log
import br.com.dio.coinconverter.data.database.AppDatabase
import br.com.dio.coinconverter.data.repository.CoinRepository
import br.com.dio.coinconverter.data.repository.CoinResposityoryImpl
import br.com.dio.coinconverter.data.services.AwesomeService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object DataModules {

    private const val HTTP_TAG = "OkHttp"

    fun load() {
        loadKoinModules(networkModule() + repositoryModules() + databaseModule())
    }

    private fun networkModule(): Module {
        return module {
            single {
                val interceptor = HttpLoggingInterceptor{
                    Log.e(HTTP_TAG, ": $it")
                }
                interceptor.level = HttpLoggingInterceptor.Level.BODY

               val build = OkHttpClient.Builder().addInterceptor(interceptor).build()
                build
            }

            single {
                GsonConverterFactory.create(GsonBuilder().create())
            }

            single {
                createService<AwesomeService>(get(), get())
            }
        }
    }

    private fun repositoryModules(): Module {
        return module {
            single<CoinRepository> { CoinResposityoryImpl(get(), get())  }
        }
    }

    private fun databaseModule(): Module {
        return module {
            single {
                AppDatabase.getInstance(androidApplication())
            }
        }
    }

    private inline fun <reified T> createService(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory) : T {
        return Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br")
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(T::class.java)
    }
}