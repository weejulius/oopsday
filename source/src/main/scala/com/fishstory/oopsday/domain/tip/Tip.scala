package com.fishstory.oopsday.domain.tip

import org.joda.time.DateTime
import java.util.Date
import com.fishstory.oopsday.domain.tag.Tag
import scala.collection.JavaConverters._
import javax.persistence._
import scala.reflect.BeanProperty
import com.fishstory.oopsday.domain.entityValidationDef

/** A tip is a short words used to recall
 */
@javax.persistence.Entity
class Tip extends com.fishstory.oopsday.domain.Entity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tip_id")
  var id: Long = 0

  @Column(name = "content", length = 3000)
  var content: String = null

  @Column(name = "author")
  var author: String = null

  @Column(name = "title", nullable = false)
  var title: String = null

  @Column(name = "modified_date")
  var modified_date: Date = null

  @Column(name = "created_date")
  val created_date: Date = DateTime.now().toDate()

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
    if (!content.equals(a_content)) {
      content = a_content
      modified_date = DateTime.now().toDate
    }
  }

}
object Tip {
  def create(a_title: String, a_content: String, a_author: String): Tip = {

    if (a_content.length() > entityValidationDef.tipMaxLengthOfContent) {
      throw InvalidTipException("the length of content is more than " + entityValidationDef.tipMaxLengthOfContent)
    }

    val tip = new Tip()

    tip.content = a_content
    tip.author = a_author
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

    tip.content = a_content
    tip.author = a_author
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

