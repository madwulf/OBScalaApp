package controllers

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
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages.Implicits._
/**
  * Created by colin on 19/12/2016.
  */

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

  def createUser = Action { implicit request =>
    inputform.bindFromRequest().fold(
      formWithErrors => BadRequest(InputPage(formWithErrors)),
      user => Ok(s"Customer ${user.name} created successfully"))
  }
}



