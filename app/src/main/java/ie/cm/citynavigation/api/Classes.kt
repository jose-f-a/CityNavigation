package ie.cm.citynavigation.api

import java.sql.Blob
import java.util.*

data class Report(
  val id: Int,
  val titulo: String,
  val descricao: String,
  val data: String,
  val imagem: Blob,
  val geo_id: Int,
  val user_id: Int,
  val categoria_id: Int
)

data class ReportGet(
  val report: Report,
  val geo: Geo
)

data class Geo(
  val id: Int,
  val lat: String,
  val lng: String
)

data class User(
  val id: Int,
  val nome: String,
  val email: String,
  val password: String
)

data class Categoria(
  val id: Int,
  val categoria: String
)