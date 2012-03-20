package com.fishstory.oopsday.interfaces.shared.validation


trait Validation {


  def messageTemplate(value: List[String])(_message: String): String = {
    var start: Int = 0
    var times = 0
    var message = _message
    start = message.indexOf("<>", start)
    while (start >= 0 && times < value.size) {
      message = message.replaceFirst("""<>""", value(times))
      start = message.indexOf("<>", start)
      times = times + 1
    }
    message
  }

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
          expression.messages.clearRound
        }
        expression = expression.next
      }
      result
    }

    def apply[A](_expression: Expression[A], values: A*): Boolean = {
      var isEverViolated = false
      for (value <- values) {
        if (evaluate(_expression, value)) isEverViolated = true
      }
      isEverViolated
    }
  }


  case class _IsEmpty[A]() extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a == null || a.isEmpty, a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isEmpty || a.get.isEmpty, a.toString) || evaluate(a.get.head)
  }

  case class _MaxLength[A](length: Int) extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a.length <= length, length.toString)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(!a.isEmpty && !a.get.isEmpty, a.toString) && evaluate(a.get.head)

  }

  case class _NotBlank[A]() extends Expression[A] {
    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a != null && !a.isEmpty, a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isDefined && !a.get.isEmpty, a.toString) && evaluate(a.get.head)
  }

  case class _IsNumeric[A]() extends Expression[A] {

    def evaluate(a: A): Boolean = {
      a match {
        case string: String => evaluate(string)
        case option: Option[Seq[String]] => evaluate(option)
        case _ => throw new IllegalArgumentException("unsupported")
      }
    }

    def evaluate(a: String): Boolean = evaluate1(a.forall(_.isDigit), a)

    def evaluate(a: Option[Seq[String]]): Boolean = evaluate2(a.isDefined && !a.get.isEmpty, a.toString) && evaluate(a.get.head)
  }


  abstract class Expression[A] {

    var and = true
    var next: Expression[A] = null
    var head: Expression[A] = this
    var _message: Message = null
    var result = false

    def messages: Message = {
      if (head == this && _message == null) {
        _message = new Message()
      }
      head._message
    }

    def ||(expression: Expression[A]): Expression[A] = {
      and = false
      next = expression
      expression.head = this.head
      expression
    }

    def &&(expression: Expression[A]): Expression[A] = {
      next = expression
      expression.head = this.head
      expression
    }

    def hasNext = head == this || next != null

    def evaluate(a: A): Boolean

    def retry(b: Expression[A]): Expression[A] = {
      b.head._message = head._message
      head = b.head
      b.head
    }

    def evaluate2(a: Boolean, values: String*): Boolean = {
      if (!a) messages + (Some(messageTemplate(values.toList)))
      a
    }

    def evaluate1(a: Boolean, values: String*): Boolean = {
      val result = a
      var message: Option[(String) => String] = None
      if (!result) message = Some(messageTemplate(values.toList))
      messages + (message)
      result
    }
  }

}