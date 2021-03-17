package ie.cm.citynavigation.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
  private val client = OkHttpClient.Builder().build()

  private val retrofit = Retrofit.Builder()
    .baseUrl("https://citynavigation.000webhostapp.com/citynavigation/index.php/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

  fun<T> buildService(service: Class<T>): T {
    return retrofit.create(service)
  }
}