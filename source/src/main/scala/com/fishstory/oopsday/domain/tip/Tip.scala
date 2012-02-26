package com.fishstory.oopsday.domain.tip

import org.joda.time.DateTime
import java.util.Date
import com.fishstory.oopsday.domain.tag.Tag
import scala.collection.JavaConverters._
import javax.persistence._

@Entity
class Tip() {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tip_id")
  var id: Long = 0
  @Column(name = "content", length = 3000)
  private var _content: String = null
  @Column(name = "author")
  private var _author: String = null
  @Column(name = "title", nullable = false)
  var title: String = null
  @Column(name = "modified_date")
  private var _modified_date: Date = null
  @Column(name = "created_date")
  private val _created_date: Date = DateTime.now().toDate()
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "TIP_MTM_TAG",
    joinColumns = Array(new JoinColumn(
      name = "TIP_ID", referencedColumnName = "tip_id", unique = false)),
    inverseJoinColumns = Array(new JoinColumn(name = "TAG_ID",
      referencedColumnName = "tag_id",
      unique = false)))
  @OrderColumn
  var tags: java.util.List[Tag] = List.empty.asJava

  def update_content(a_content: String) {
    if (!_content.equals(a_content)) {
      _content = a_content
      _modified_date = DateTime.now().toDate
    }
  }

  def created_date = _created_date

  def author = _author

  def modified_date = _modified_date

  def content: String = _content

}

object Tip {

  private var _maxNumberOfChar = 3000

  def set_maxNumberOfCharForContent(a_maxNumberOfChar: Int) = {
    _maxNumberOfChar = a_maxNumberOfChar
  }

  def maxNumberOfChar = _maxNumberOfChar

  def create(a_title: String, a_content: String, a_author: String): Tip = {

    if (a_content.length() > maxNumberOfChar) {
      throw new InvalidTipException
    }

    val tip = new Tip()

    tip._content = a_content
    tip._author = a_author
    tip.title = a_title
    tip
  }

  def createWithTag(a_title: String, a_content: String, a_author: String, a_tags: List[Tag]): Tip = {

    val tip = create(a_title, a_content, a_author)
    tip.tags = a_tags.asJava
    tip
  }

  def mock(a_title: String, a_content: String, a_author: String): Tip = {
    var tip = new Tip()

    tip._content = a_content
    tip._author = a_author
    tip.title = a_title
    tip
  }

  def NullObject: Tip = {
    new Tip
  }

  def isNullObject(tip: Tip): Boolean = {
    tip == null || tip.id <= 0
  }
}
