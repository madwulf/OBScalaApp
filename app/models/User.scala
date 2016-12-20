package models


/**
  * Created by colin on 20/12/2016.
  */
case class User(name:String, email:String)

case class Feed(
                 name: String,
                 url: String)

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val feedFormat = Json.format[Feed]
  implicit val userFormat = Json.format[User]
}