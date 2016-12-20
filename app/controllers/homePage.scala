package controllers

import play.api.mvc.Action
import play.api.mvc._
import views.html.InputPage
/**
  * Created by colin on 19/12/2016.
  */
class homePage extends Controller {
  def load= Action {
    Ok(InputPage.render())
  }

}

