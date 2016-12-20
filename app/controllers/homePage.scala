package controllers
//Imports
import play.api.mvc.Action
import play.api.mvc._
import views.html.InputPage
import views.html.helper
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import javax.inject.Inject
import views.html.helper.inputText
import models.User
import javax.inject.Inject
import reactivemongo.api.Cursor
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
import com.mongodb.casbah.Imports._

/**
  * Created by colin on 19/12/2016.
  */
//Message API required for Form's
class homePage @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport  {
 //Used to load the page with input boxes

  def load= Action {
    Ok(InputPage(inputform))
  }

  //form to create/handel inputs
  val inputform = Form(
    mapping(
      "Name" -> nonEmptyText,
    "Email" -> nonEmptyText
    )(User.apply)(User.unapply))
  //create user input
  def createUser = Action { implicit request =>
    inputform.bindFromRequest().fold(
      formWithErrors => BadRequest(InputPage(formWithErrors)),
      user => Ok(s"user ${user.name} created successfully"))}

}

class MongoDbHandler{
  //defines mongodb
    val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("userdb")
  db.collectionNames
  val coll = db("userdb")

}