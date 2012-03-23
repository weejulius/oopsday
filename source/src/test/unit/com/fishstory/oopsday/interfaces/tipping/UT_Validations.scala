package com.fishstory.oopsday.interfaces.tipping

import org.junit.Test
import org.junit.Assert._
import com.fishstory.oopsday.interfaces.shared.validation._
import collection.mutable.ListBuffer

class UT_Validations extends Validation {

  @Test
  def test_isEmpty {
    var a: List[ListBuffer[Seq[String]]] = List[ListBuffer[Seq[String]]](ListBuffer[Seq[String]]())
    assertTrue(a.forall(_.isEmpty))

    var b: Seq[String] = List.empty
    b = b :+ "hello"
    assertEquals(1, b.length)

    assertEquals(0, a.head.length)

    a(0) += b

    assertEquals(1, a.head.length)
  }

  @Test
  def testOptionString {
    assertTrue(validate(Some("")) using isEmpty)
  }

  @Test
  def testIsEmpty {
    assertTrue(validate(None) using isEmpty)
    assertTrue(validate(Some("")) using isEmpty)
    assertTrue(validate(Some("")) using isEmpty result() isSatisfied)
    assertTrue(validate(Some(List(""))) using isEmpty)
    assertTrue(validate("") using isEmpty)
    assertTrue(validate(null) using isEmpty)
    assertTrue(validate(Some(Nil)) using isEmpty)
    assertFalse(validate(Some("a")) using isEmpty)
    assertEquals("a is not empty", validate(Some("a")) using isEmpty result() print(0, 0, "&_ is not empty"))

    assertTrue(isEmpty.evaluate(Some("")))
  }


  @Test
  def test2 {
    val a: Option[Seq[String]] = Some(List[String]("aaaaaaaa").toSeq)
    val b: Option[Seq[String]] = Some(List[String]("abbbbbbbbbbbbbbbbbbbbbbbbb").toSeq)
    assertTrue(validate(a) using notBlank and MaxLength(10))
    assertFalse(validate(b) using notBlank and MaxLength(10) result() isSatisfied)
  }


  @Test
  def test_message_template {
    val a = new ValidationResult().fillValuesOfMessage("a" :: "ba" :: Nil)(_)
    assertEquals("a is not ba", a("&_ is not &_"))
    assertEquals("is not ba", a("is not &_1"))
  }


}