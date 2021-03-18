package ie.cm.citynavigation.api

import retrofit2.Call
import retrofit2.http.*

interface Endpoints {
  @GET("users/")
  fun getUsers(): Call<List<User>>

  @GET("users/{id}")
  fun getUserById(@Path("id") id: Int): Call<List<User>>

  @GET("report")
  fun getReports(): Call<List<ReportGet>>

  @FormUrlEncoded
  @POST("user/login")
  fun login(@Field("email") email: String,
            @Field("password") password: String): Call<OutputLogin>
}