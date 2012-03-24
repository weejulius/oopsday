package com.fishstory.oopsday.domain.tip

import org.joda.time.DateTime
import java.util.Date
import com.fishstory.oopsday.domain.tag.Tag
import scala.collection.JavaConverters._
import javax.persistence._
import com.fishstory.oopsday.domain.entityValidationDef

/**A tip is a short words used to recall
 */
@javax.persistence.Entity
class Tip extends com.fishstory.oopsday.domain.DomainEntity {

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

  def this(a_title: String, a_content: String, a_author: String, a_tags: List[Tag]) = {
    this()

    if (isValid) {
      if (a_content.length() > entityValidationDef.tipMaxLengthOfContent) {
        throw InvalidTipException("the length of content is more than " + entityValidationDef.tipMaxLengthOfContent)
      }
    }
    content = a_content
    author = a_author
    title = a_title
    tags = a_tags.asJava
  }

  def this(a_title: String, a_content: String, a_author: String) = this(a_title, a_content, a_author, List.empty);

  def update_content(a_content: String) {
    if (!content.equals(a_content)) {
      content = a_content
      modified_date = DateTime.now().toDate
    }
  }
}

object emptyTip extends Tip {
}

case class InvalidTip(a_title: String, a_content: String, a_author: String) extends Tip(a_title, a_content, a_author) {
  override def isValid: Boolean = false
}

