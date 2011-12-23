package com.fishstory.oopsday.domain.tip
import org.joda.time.DateTime
import java.util.Date

class Tip() {

  private var _content: String = null;
  private var _author: String = null;
  private var _modified_date: Date = null;
  private var _created_date: Date = DateTime.now().toDate()

  def content: String = _content

  def update_content(a_content: String) = {
    _content = a_content
    _created_date = DateTime.now().toDate()
  }

  def created_date = _created_date

  def author = _author
  
  def modified_date = _modified_date
}

object Tip {

  private var _maxNumberOfChar = 20;

  def set_maxNumberOfChar(a_maxNumberOfChar: Int) = { _maxNumberOfChar = a_maxNumberOfChar }

  def maxNumberOfChar = _maxNumberOfChar

  def create(a_content: String, a_author: String): Tip = {

    if (a_content.length() > maxNumberOfChar) {
      throw new InvalidTipException()
    }

    var tip = new Tip()

    tip._content = a_content
    tip._author = a_author

    return tip;

  }
}
