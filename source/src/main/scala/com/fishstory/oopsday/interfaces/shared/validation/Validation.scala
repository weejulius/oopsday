package com.fishstory.oopsday.interfaces.shared.validation

import collection.mutable.ListBuffer

trait Validation {

  //  def isNumeric(message: String)(a: String): Option[String] = {
  //    if (a != null && a.forall(_.isDigit)) {
  //      return None
  //    }
  //    Some(message.replaceAll("\\{\\}", a))
  //  }
  //
  //  def isNumeric1(message: String)(a: Option[Seq[String]]): Option[String] = {
  //    if (!a.isEmpty && !a.get.isEmpty) {
  //      return isNumeric(message)(a.get.head)
  //    }
  //    Some(message.replaceAll("\\{\\}", a.toString))
  //  }
  //
  //  def isNotBlank(message: String)(a: Option[Seq[String]]): Option[String] = {
  //    if (!a.isEmpty && !a.get.isEmpty && !a.get.head.isEmpty) {
  //      return None
  //    }
  //    Some(message)
  //  }
  //
  //  def isEmpty(message: String)(a: Option[Seq[String]]): Option[String] = {
  //    if (a.isEmpty || a.get.isEmpty || a.get.head.isEmpty) {
  //      return None
  //    }
  //    Some(message)
  //  }
  //
  //  def maxLength(length: Int)(message: String)(a: Option[Seq[String]]): Option[String] = {
  //    if (a.get.head.length() <= length) {
  //      return None
  //    }
  //    Some(message)
  //  }
  //
  //  case class validation[A, B, C](validatable: B, key: A, result: Result[A, C]) {
  //    def by(expressions: B => Option[C] *): Result[A, C] = {
  //
  //      var isFirstExpression: Boolean = true
  //      val iterator = expressions.iterator
  //      while (iterator.hasNext) {
  //
  //        iterator.next()(validatable) match {
  //          case Some("OR") => {
  //
  //            if (isFirstExpression) {
  //              throw new IllegalArgumentException("or expression should not be the first")
  //            }
  //          }
  //          case message => {
  //            println(message)
  //            if (iterator.hasNext) {
  //
  //              iterator.next() match {
  //                case a: (AnyRef => Option[C]) => {
  //                  if (message.isEmpty) {
  //                    println("go out")
  //                    println(result)
  //                    return result;
  //                  }
  //                }
  //                case next => {
  //                  if (message.isDefined) {
  //                    result.add(key, message.get)
  //                    return result
  //                  } else {
  //                    val nextMessage = next(validatable)
  //                    if (nextMessage.isDefined) {
  //                      result.add(key, nextMessage.get)
  //                      return result
  //                    }
  //                  }
  //                }
  //              }
  //            } else {
  //              if (message.isDefined) {
  //                result.add(key, message.get)
  //                return result
  //              }
  //            }
  //            isFirstExpression = false
  //          }
  //        }
  //      }
  //      result
  //    }
  //  }

  object evaluation {

    private def evaluate[A](_expression: Expression[A], value: A): Boolean = {
      var expression: Expression[A] = _expression.head
      expression.messages.registerNewRound()
      var result = false
      while (expression != null) {
        result = expression.evaluate(value)
        expression.result = result
        if ((result && !expression.and) || (!result && expression.and)) {
          return result
        }
        if (!result && !expression.and) {
          expression.messages.clear
        }
        expression = expression.next
      }
      result
    }

    def apply[A](_expression: Expression[A], values: A*): Boolean = {
      _expression.messages.clear
      var isEverVoilated = false
      for (value <- values) {
        if (evaluate(_expression, value)) isEverVoilated = true
      }
      isEverVoilated
    }
  }

  case class _IsEmpty[A](message: String = "") extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = {
      val result = a == null || a.isEmpty
      if (!result) messages + (message.replaceAll("""\{\}""", a))
      result
    }

    def evaluate(a: Option[Seq[String]]): Boolean = {
      val result = a.isEmpty || a.get.isEmpty
      if (!result) messages + (message.replaceAll("\\{\\}", a.toString))
      result || evaluate(a.get.head)
    }
  }

  case class _MaxLength[A](length: Int, message: String = "the {} is less than {}") extends Expression[A] {
    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = a.length <= length

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate(a.get.head)
  }

  case class _NotBlank[A](message: String = "the {}:{} is must") extends Expression[A] {
    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = a != null && !a.isEmpty

    def evaluate(a: Option[Seq[String]]): Boolean = {
      val result = a.isDefined && !a.get.isEmpty
      if (!result) messages + (message.replaceAll("\\{\\}", a.toString))
      result || evaluate(a.get.head)
    }
  }

  case class _IsNumeric[A](message: String = "") extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = {
      val result = a.forall(_.isDigit)
      if (!result) messages + (message.replaceAll("\\{\\}", a))
      result
    }

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate(a.get.head)
  }

  class Message {
    var messages: List[ListBuffer[String]] = List.empty
    var index = 0

    def +(a: String) {
      messages(index) += a
    }

    def clear {
      messages = List.empty
    }

    def size(index: Int): Int = messages(index).size

    def registerNewRound() {
      index = index + 1
      var newRound: ListBuffer[String] = ListBuffer.empty
      messages = messages ::: List(newRound)
    }
  }

  abstract class Expression[A] {

    var and = true
    var next: Expression[A] = null
    var head: Expression[A] = this
    var messages: Message = new Message()
    var result: Boolean = false

    def ||(expression: Expression[A]): Expression[A] = {
      and = false
      next = expression
      expression.head = this.head
      expression.messages = this.messages
      expression
    }

    def &&(expression: Expression[A]): Expression[A] = {
      next = expression
      expression.head = this.head
      expression.messages = this.messages
      expression
    }

    def hasNext = head == this || next != null

    def evaluate(a: A): Boolean
  }

  //  case class Result[A, B] {
  //    var messages: Map[A, B] = Map.empty
  //
  //    def add(a: A, b: B) = {
  //      messages += (a -> b)
  //    }
  //
  //    def check[C](f: A => C, a: A) = validation(f(a), a, this)
  //
  //    def check[C](a: A) = validation(a, a, this)
  //  }
  //
  //  def validations[A](a: => A): A = {
  //    (a _)()
  //  }
}