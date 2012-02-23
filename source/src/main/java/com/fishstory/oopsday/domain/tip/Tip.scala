package com.fishstory.oopsday.domain.tip
import org.joda.time.DateTime
import java.util.Date
import scala.actors.threadpool.AtomicInteger
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.ManyToMany
import com.fishstory.oopsday.domain.tag.Tag
import javax.persistence.JoinTable
import javax.persistence.JoinColumn

@Entity
class Tip() {

  private def Tip() {}

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
  private var _created_date: Date = DateTime.now().toDate()
  @ManyToMany
  @JoinTable(
    name="TIP_MTM_TAG",
    joinColumns=Array(new JoinColumn(name="TIP_ID", referencedColumnName="id", unique=false)),
    inverseJoinColumns=Array(new JoinColumn(name="TAG_ID",
      referencedColumnName="id",
      unique=false)))
  var _tags:Set[Tag]=Set.empty
  

  def update_content(a_content: String) = {
    if (!_content.equals(a_content)) {
      _content = a_content
      _modified_date = DateTime.now().toDate()
    }
  }

  def created_date = _created_date

  def author = _author

  def modified_date = _modified_date

  def content: String = _content
}

object Tip {

  private var _maxNumberOfChar = 3000
  def set_maxNumberOfCharForContent(a_maxNumberOfChar: Int) = { _maxNumberOfChar = a_maxNumberOfChar }

  def maxNumberOfChar = _maxNumberOfChar

  def create(a_title: String, a_content: String, a_author: String): Tip = {

    if (a_content.length() > maxNumberOfChar) {
      throw new InvalidTipException
    }

    var tip = new Tip()

    tip._content = a_content
    tip._author = a_author
    tip.title = a_title

    return tip
  }
  
    def createWithTag(a_title: String, a_content: String, a_author: String, a_tags:Set[Tag]): Tip = {
      
     val tip=create(a_title,a_content,a_author)
     tip._tags=a_tags
     tip     
  }

  def mock(a_title: String, a_content: String, a_author: String): Tip = {
    var tip = new Tip()

    tip._content = a_content
    tip._author = a_author
    tip.title = a_title

    return tip
  }

  def NullObject: Tip = {
    new Tip
  }

  def isNullObject(tip: Tip): Boolean = {
    tip == null || tip.id <= 0
  }
}
