package com.fishstory.oopsday.interfaces.shared.validation

trait Validation {

  def isNumeric(message: String)(a: String): Option[String] = {
    if (a != null && a.forall(_.isDigit)) {
      return None
    }
    Some(message.replaceAll("\\{\\}", a))
  }

  def isNumeric1(message: String)(a: Option[Seq[String]]): Option[String] = {
    if (!a.isEmpty && !a.get.isEmpty) {
      return isNumeric(message)(a.get.head)
    }
    Some(message.replaceAll("\\{\\}", a.toString))
  }

  def isNotBlank(message: String)(a: Option[Seq[String]]): Option[String] = {
    if (!a.isEmpty && !a.get.isEmpty && !a.get.head.isEmpty) {
      return None
    }
    Some(message)
  }

  def isEmpty(message: String)(a: Option[Seq[String]]): Option[String] = {
    if (a.isEmpty || a.get.isEmpty || a.get.head.isEmpty) {
      return None
    }
    Some(message)
  }

  def maxLength(length: Int)(message: String)(a: Option[Seq[String]]): Option[String] = {
    if (a.get.head.length() <= length) {
      return None
    }
    Some(message)
  }

  case class validation[A, B, C](validatable: B, key: A, result: Result[A, C]) {
    def by(expressions: B => Option[C]*): Result[A, C] = {

      var isFirstExpression: Boolean = true
      val iterator = expressions.iterator
      while (iterator.hasNext) {

        iterator.next()(validatable) match {
          case Some("OR") => {
            if (isFirstExpression) {
              throw new IllegalArgumentException("or expression should not be the first")
            }
          }
          case message => {
            println(message)
            if (iterator.hasNext) {
              val nextIterator=iterator.next
              
              if(nextIterator.toString().cont)
              iterator.next(). match {
                case a:(AnyRef => Option[C]) => {
                  if (message.isEmpty) {
                    println("go out")
                    println(result)
                    return result;
                  }
                }
                case next => {
                  if (message.isDefined) {
                    result.add(key, message.get)
                    return result
                  } else {
                    val nextMessage = next(validatable)
                    if (nextMessage.isDefined) {
                      result.add(key, nextMessage.get)
                      return result
                    }
                  }
                }
              }
            } else {
              if (message.isDefined) {
                result.add(key, message.get)
                return result
              }
            }
            isFirstExpression = false
          }
        }
      }
      result
    }
  }

  case class Result[A, B] {
    var messages: Map[A, B] = Map.empty

    def add(a: A, b: B) = {
      messages += (a -> b)
    }

    def check[C](f: A => C, a: A) = validation(f(a), a, this)

    def check[C](a: A) = validation(a, a, this)
  }

  def validations[A](a: => A): A = {
    (a _)()
  }
}