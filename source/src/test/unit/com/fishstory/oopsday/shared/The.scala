package com.fishstory.oopsday.shared

import java.util.Date
import org.joda.time.DateTime

class The {
  private var _date: Date = null
  private var _string: String = null
  private var _number:Option[Long] = None

  def should_be_now_approximately = {

    var date1 = new DateTime(The._the._date)

    if (!(date1.getMillis() > DateTime.now().getMillis() - 500)) {
      throw new AssertException("\n\n ===> {" + date1 + "} is not around the current time\n")
    }
  }

  def should_equal_to(a_date: Date) = {

    if (!The._the._date.equals(a_date)) {
      throw new AssertException("\n\n Actual      |  {" + _date + "} \n Expected    |  {" + a_date + "}\n")
    }

  }

  def should_equal_to(a_string: String) = {
    if (!The._the._string.equals(a_string)) {
      throw new AssertException("\n\n Actual      |  {" + _string + "} \n Expected    |  {" + a_string + "}\n")
    }
  }

  def should_be_null_date = {
    if (!(_date == null)) {
      throw new AssertException("\n\n ===> {" + _date + "} is not null\n")
    }
  }

  def should_contain(a_string: String) = {
    if (!_string.contains(a_string)) {
      throw new AssertException("\n\n Actual      |  {" + _string + "} \n Expected    |  {" + a_string + "}\n")
    }
  }
  
  def should_be_greater_than_number(a_num:Long) = {
    if (!(_number.isDefined && _number.get > a_num)) {
      throw new AssertException("\n\n Actual      |  {" + _number + "} \n Expected    |  {" + a_num + "}\n")
    }
  }
  
  def should_end_with(a_string:String) = {
    if (!_string.endsWith(a_string)) {
      throw new AssertException("\n\n Actual      |  {" + _string + "} \n Expected    |  {" + a_string + "}\n")
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
  
  def number(a_number:Long):The ={
    _the = new The()
    _the._number = Some(a_number)
    return _the
  }

  def string(a_string: String): The = {
    _the = new The()
    _the._string = a_string
    return _the
  }

}

