package ie.cm.citynavigation.api

data class OutputLogin(
  val id: Int,
  val email: String,
  val password: String,
  val nome: String
)

data class OutputNewReport (
  val status: Boolean,
  val msg: String
)

data class OutputDeleteReport (
  val status: Boolean,
  val msg: String
)

data class OutputEditReport (
  val status: Boolean,
  val msg: String
)