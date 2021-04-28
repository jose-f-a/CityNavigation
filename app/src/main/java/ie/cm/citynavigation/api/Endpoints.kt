package ie.cm.citynavigation.api

import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface Endpoints {
  @GET("users/")
  fun getUsers(): Call<List<User>>

  @GET("users/{id}")
  fun getUserById(@Path("id") id: Int): Call<List<User>>

  @FormUrlEncoded
  @POST("user/login")
  fun login(@Field("email") email: String,
            @Field("password") password: String): Call<OutputLogin>

  @GET("reports")
  fun getReports(): Call<List<Report>>

  @GET("report/{id}")
  fun getReport(@Path("id") id: Int): Call<List<Report>>

  @FormUrlEncoded
  @POST("report/new")
  fun newReport(@Field("titulo") titulo: String,
                @Field("descricao") descricao: String,
                @Field("data") data: String,
                @Field("imagem") imagem: String,
                @Field("latitude") latitude: Double,
                @Field("longitude") longitude: Double,
                @Field("user_id") user_id: Int,
                @Field("categoria_id") categoria_id: Int): Call<OutputNewReport>

  @POST("report/delete/{id}")
  fun deleteReport(@Path("id") id: Int): Call<OutputDeleteReport>
}