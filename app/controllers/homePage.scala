package controllers
//Imports
import play.api.mvc.Action
import play.api.mvc._
import views.html.InputPage
import views.html.helper
import play.api.data.Form
import play.api.data.Forms._
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
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.{Cursor, DB, MongoConnection, MongoDriver}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
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
      user => Ok(s"Customer ${user.name} created successfully"))

  }
  //database def's
  trait ReactiveMongoApi {
    def driver: MongoDriver
    def connection: MongoConnection
    def db: DB
  }
}
//Database handeler
class MongoDbHandler@Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("users")

  def index = Action {
    Ok("works")
  }

  def create(name: String, email: String) = Action.async {
    val json = Json.obj(
      "name" -> name,
      "email" -> email,
      "created" -> new java.util.Date().getTime())

    collection.insert(json).map(lastError =>
      Ok("Mongo LastError: %s".format(lastError)))
  }

  def createFromJson = Action.async(parse.json) { request =>
    import play.api.libs.json.Reads._
    /*
     * request.body is a JsValue.
     * There is an implicit Writes that turns this JsValue as a JsObject,
     * so you can call insert() with this JsValue.
     * (insert() takes a JsObject as parameter, or anything that can be
     * turned into a JsObject using a Writes.)
     */
    val transformer: Reads[JsObject] =
      Reads.jsPickBranch[JsString](__ \ "firstName") and
        Reads.jsPickBranch[JsString](__ \ "lastName") and
        Reads.jsPickBranch[JsNumber](__ \ "age") reduce

    request.body.transform(transformer).map { result =>
      collection.insert(result).map { lastError =>

        Created
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }
  def findByName(lastName: String) = Action.async {
    // let's do our query
    val cursor: Cursor[User] = collection.
      // find all people with name `name`
      find(Json.obj("lastName" -> lastName)).
      // sort them by creation date
      sort(Json.obj("created" -> -1)).
      // perform the query and get a cursor of JsObject
      cursor[User]

    // gather all the JsObjects in a list
    val futureUsersList: Future[List[User]] = cursor.collect[List]()

    // everything's ok! Let's reply with the array
    futureUsersList.map { persons =>
      Ok(persons.toString)
    }
  }
}

