package com.lucidsoftworksllc.spinthewheel.util


import com.lucidsoftworksllc.spinthewheel.BuildConfig
import com.lucidsoftworksllc.spinthewheel.util.Constants.ROOT_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    fun<Api> buildApi(
        api: Class<Api>
    ): Api{
        return Retrofit.Builder()
            .baseUrl(ROOT_URL)
                .client(OkHttpClient.Builder()
                    .addInterceptor{chain ->
                        chain.proceed(chain.request().newBuilder().build())
                    }.also { client ->
                    if(BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
                )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

}