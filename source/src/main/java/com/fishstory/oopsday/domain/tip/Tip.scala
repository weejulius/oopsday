package com.fishstory.oopsday.domain.tip
import org.joda.time.DateTime
import java.util.Date

class Tip(private var _content: String, private var _author: String) {

  private var _created_date:Date = DateTime.now().toDate()

  if (_content.length() > Tip._maxNumberOfChar)
    throw new InvalidTipException()

  def content: String = _content

  def set_content(a_content: String) = { _content = a_content }

  def created_date = _created_date
  
  def author = _author
}

object Tip {

  private var _maxNumberOfChar = 20;

  def set_maxNumberOfChar(a_maxNumberOfChar: Int) = { _maxNumberOfChar = a_maxNumberOfChar }
}