package controllers

import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{DiscardingCookie, Action}
import play.mvc.Controller
import play.api.mvc.Results._
import shared.{UltraRapidImage, Image}

import scala.collection.mutable.ArrayBuffer

case class UserReq(email: String, name: String, nationality: String, gender: String, age: Int, license: String,
                   time: Int, pet: String, testName: String)

object PsychoTest extends Controller {

  val userMapping =
    mapping(
      "email" -> email,
      "name" -> nonEmptyText(maxLength = 200),
      "nationality" -> nonEmptyText(maxLength = 200),
      "gender" -> nonEmptyText(maxLength = 200),
      "age" -> number(min = 0),
      "driving-license" -> nonEmptyText(maxLength = 10),
      "driving-time" -> number(min = 0),
      "pet" -> nonEmptyText(maxLength = 10),
      "test" -> nonEmptyText
    )(UserReq.apply)(UserReq.unapply)

  val testForm: Form[UserReq] = Form(userMapping)

  def readAllUltraRapidImages(): ArrayBuffer[UltraRapidImage] = {
    val res = new ArrayBuffer[UltraRapidImage]()
    for (i <- 1 to 7) {
      i match {
        case 1 => for (j <- 1 to 80) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 2 => for (j <- 1 to 122) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 3 => for (j <- 1 to 87) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 4 => for (j <- 1 to 119) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 5 => for (j <- 1 to 104) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 6 => for (j <- 1 to 103) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 7 => {
          for (j <- 1 to 35) {
            res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j) + "_1_1"))
          }
          for (j <- 36 to 54) {
            res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j) + "_1_2"))
          }
          for (j <- 55 to 93) {
            res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j) + "_2_1"))
          }
          for (j <- 94 to 107) {
            res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j) + "_2_2"))
          }
        }
      }
    }
    res
  }

  def readOpenExperimentImages(): ArrayBuffer[UltraRapidImage] = {
    val res = new ArrayBuffer[UltraRapidImage]()
    // adding open experiment to cache
    for (i <- 1 to 29) {
      res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i)))
    }
    res
  }

  /**
    * 1 - горный пейзаж
    * 2 - морской берег
    * 3 - поля
    *
    * 1 - городская сцена
    * 2 - интерьер дома
    * 3 - памятники культуры, древние постройки
    * @return
    */
  def readMultiChoiceImages(): ArrayBuffer[UltraRapidImage] = {
    val res = new ArrayBuffer[UltraRapidImage]()
    // adding multi choice images to cache
    for (i <- 1 to 6) {
      for (j <- 1 to 3) {
        for (k <- 1 to 10) {
          res.append(new UltraRapidImage(s"${i}_${j}", s"$k"))
        }
      }
    }
    res
  }

  def startTest() = Action { implicit req =>
    testForm.bindFromRequest.fold(
      errors =>
        BadRequest(views.html.tests(TestDAO.findAll, errors)),
      user => {
        val addedUser: User = new User(None, user.name, user.email, user.gender, user.nationality,
          user.age, user.license, user.time, user.pet, Integer.parseInt(user.testName.split("#")(0)))
        val id = UserDAO.addUser(addedUser)
        addedUser.id = Some(id)
        user.testName.split("#")(1) match {
          case "Kagan test" => {
            val kaganImages = new Array[Image](8)
            for (i <- 1 to 8) {
              kaganImages(i - 1) = new Image("kagan", 1, i)
            }
            val pattern = new Image("kagan", 1, 0)
            Ok(views.html.kaganTest(addedUser, pattern, kaganImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Ultra rapid categorization" => {
            Ok(views.html.ultraRapidTest(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Gothshild test" => {
            Ok(views.html.gothshildTest(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Stroop test" => {
            Ok(views.html.stroopTest(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Ultra rapid categorization ENG" => {
            Ok(views.html.ultraRapidTestEn(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Gothshild test ENG" => {
            Ok(views.html.gothshildTestEn(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Stroop test ENG" => {
            Ok(views.html.stroopTestEn(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Kagan test ENG" => {
            val kaganImages = new Array[Image](8)
            for (i <- 1 to 8) {
              kaganImages(i - 1) = new Image("kagan", 1, i)
            }
            val pattern = new Image("kagan", 1, 0)
            Ok(views.html.kaganTestEn(addedUser, pattern, kaganImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Тест Глобальное узнавание" => {
            Ok(views.html.globalRecognitionTest(addedUser, readOpenExperimentImages())).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Тест быстрой категоризации" => {
            Ok(views.html.multiChoiceTest(addedUser, readMultiChoiceImages())).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
        }
      }
    )
  }

  def extractUserName(s: String) = s.substring(0, s.indexOf("="))

  def finishTest = Action { implicit req =>
    val report = req.body.asText.get
    val userID = extractUserName(report)
    ReportDAO.addReport(new Report(None, Integer.parseInt(userID), report.substring(report.indexOf("=") + 1, report.length - 1)))
    Ok(views.html.index("You successfully finish testing! Go and check more new tests!"))
  }
}
