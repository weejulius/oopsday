package com.fishstory.oopsday.domain.tip
import org.joda.time.DateTime
import java.util.Date
import scala.actors.threadpool.AtomicInteger
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column

@Entity
class Tip() {

  private def Tip() {}

  @Id
  @Column(name = "tip_id")
  private var _id: Long = 0;
  @Column(name = "content")
  private var _content: String = null
  @Column(name = "author")
  private var _author: String = null
  @Column(name = "title")
  var _title: String = null
  @Column(name = "modified_date")
  private var _modified_date: Date = null
  @Column(name = "created_date")
  private var _created_date: Date = DateTime.now().toDate()

  def update_content(a_content: String) = {
    _content = a_content
    _created_date = DateTime.now().toDate()
  }

  def created_date = _created_date

  def author = _author

  def modified_date = _modified_date

  def title = _title

  def content: String = _content

  def id = _id
}

object Tip {

  private var _maxNumberOfChar = 88;
  private var _tip_generator: AtomicInteger = new AtomicInteger(0)

  def set_maxNumberOfCharForContent(a_maxNumberOfChar: Int) = { _maxNumberOfChar = a_maxNumberOfChar }

  def maxNumberOfChar = _maxNumberOfChar

  def create(a_title: String, a_content: String, a_author: String): Tip = {

    if (a_content.length() > maxNumberOfChar) {
      throw new InvalidTipException
    }

    var tip = new Tip()

    tip._id = _tip_generator.incrementAndGet()
    tip._content = a_content
    tip._author = a_author
    tip._title = a_title

    return tip;

  }

  def reset_tip_generator = {
    _tip_generator = new AtomicInteger(0)
  }
}
