package com.fishstory.oopsday.interfaces.tipping
import com.fishstory.oopsday.interfaces.shared.validation.Validations
import org.junit.Test
import com.fishstory.oopsday.interfaces.shared.validation.FAILURE
import com.fishstory.oopsday.interfaces.shared.validation.Validate
import com.fishstory.oopsday.interfaces.shared.validation.IsNotBlank
import com.fishstory.oopsday.interfaces.shared.validation.And
import com.fishstory.oopsday.interfaces.shared.validation.MaxLength

class UT_Validations {

  @Test
  def test_is_not_blank {

    var params: Map[String, Seq[String]] = Map.empty
    params += ("tip_title" -> List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

    Validations(params,
      ("tip_title", "title", IsNotBlank() :: And() :: MaxLength(20) :: Nil)).result match {
        case FAILURE(messages) => println(messages)
        case _                 =>
      }
  }

  @Test
  def test_another_style {
    var params: Map[String, Seq[String]] = Map.empty
    params += ("tip_title" -> List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

    params.foreach(
      _ match {
        case ("tip_title", value) => println(value.head)
      }
    )
  }

  //    validations(params) {
  //      validation("tip_title")(isNotBlank, maxLength(20))
  //    } match {
  //      case FAILUREResult(messages: Map[String, String]) => println(messages)
  //      case _ =>
  //    }
  //  }

  //  def isNotBlank(a: String, implicit b: Map[String, Seq[String]]): ValidationResult = {
  //    var result = SUCCESSResult()
  //    if (b.get(a).isEmpty || b(a).isEmpty || b(a).head.isNotBlank) {
  //      result = FAILUREResult(a, " is must")
  //    }
  //    result
  //  }
  // def maxLength[A](length: Int)(a: A): ValidationResult = true

  //  object validations {
  //    def apply[A, B](
  //      validatable: A)(
  //        a: => B): ValidationResult = {
  //      FAILUREResult(Map.empty)
  //    }
  //
  //  }
  //
  //  object validation {
  //    def apply[A, B](
  //      validatable: A,
  //      b: List[A => ValidationResult])(
  //        implicit c: B): Boolean = {
  //      var result: List[Boolean] = List.empty
  //      for (express <- b) {
  //        result = express(validatable) :: result
  //      }
  //      true
  //    }
  //  }
  //
  //  abstract class ValidationResult {}
  //
  //  case class SUCCESSResult extends ValidationResult {
  //    def get = true
  //  }
  //
  //  case class FAILUREResult(messages: Map[String, String]) extends ValidationResult {
  //    def get = false
  //  }

}