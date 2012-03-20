package com.fishstory.oopsday.interfaces.tipping

import org.junit.Test
import org.junit.Assert._
import com.fishstory.oopsday.interfaces.shared.validation._

class UT_Validations extends Validation {

  @Test
  def test_is_not_blank {

    var params: Map[String, Seq[String]] = Map.empty
    params += ("tip_title" -> List("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

    Validations(params,
      ("tip_title", "title", __IsNotBlank() ::
        And() :: __MaxLength(20) :: Nil)).result match {
      case FAILURE(messages) => println(messages)
      case _ =>
    }
  }


  @Test
  def test_style_3 {
    val expression = IsEmpty[String]() || IsNumeric()
    assertTrue(evaluating(expression, ""))
    assertTrue(evaluating(expression, "123"))
    assertFalse(evaluating(expression, "aaaa"))
    assertEquals(3, expression.results.messages.size)
    assertEquals(1, expression.results.size(0))
    assertEquals("aaaa is not numeric", expression.results.print(0, 0, "<> is not numeric"))
  }

  @Test
  def test_validate_2_params {
    var expression = NotBlank[String]() && MaxLength(10)

    assertTrue(evaluating(expression, "1234"))
    assertFalse(evaluating(expression, "12333333333333333333"))
    expression retry (NotBlank[String]() && MaxLength(12))
    assertFalse(evaluating(expression, ""))
    assertFalse(evaluating(expression, "1222222222222222222222222"))

    assertEquals(4, expression.results.messages.size)
    assertEquals("the title 1222222222222222222222222 is more than 12", expression.results.print(0, 1, "the title <> is more than <>"))
    assertEquals("the title  is must", expression.results.print(1, 0, "the title <> is must"))

    var expression1 = NotBlank[Option[Seq[String]]]() && MaxLength(12)
    var a: Option[Seq[String]] = Some(List[String]("").toSeq)
    assertFalse(evaluating[Option[Seq[String]]](expression1, a))
    assertEquals(1, expression1.results.messages.size)
    assertEquals("the title  is must", expression1.results.print(0, 0, "the title <> is must"))

  }

  @Test
  def test_message_template {
    val a = fillValuesOfMessage("a" :: "ba" :: Nil)(_)
    assertEquals("a is not ba", a("<> is not <>"))
    assertEquals("is not ba", a("is not <1>"))
  }

}