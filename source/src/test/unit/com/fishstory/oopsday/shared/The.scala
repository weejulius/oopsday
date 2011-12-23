package com.fishstory.oopsday.shared
import java.util.Date
import org.joda.time.DateTime

class The {
  private var _date: Date = null
  private var _string: String = null

  def should_be_now_approximately = {

    var date1 = new DateTime(The._the._date)

    if (!(date1.getMillis() > DateTime.now().getMillis() - 500)) {
      throw new AssertException("the date " + date1 + " is not around the current time")
    }
  }

  def should_equal_to(a_date: Date) = {

    if (!The._the._date.equals(a_date)) {
      throw new AssertException("the date " + _date + " does not equal to the expected date " + a_date)
    }

  }

  def should_equal_to(a_string: String) = {
    if (!The._the._string.equals(a_string)) {
      throw new AssertException("the string " + _string + " does not equal to the expected string " + a_string)
    }
  }

  def should_be_null_date = {
     if(!(_date == null)){
       throw new AssertException("the date " + _date + " is not null ")
     }
  }
}

object The {

  private var _the: The = null

  def date(a_date: Date): The = {
    _the = new The()
    _the._date = a_date
    return _the
  }

  def string(a_string: String): The = {
    _the = new The()
    _the._string = a_string
    return _the
  }

}

