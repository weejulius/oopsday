package com.fishstory.oopsday.domain.tag
import javax.persistence.Entity
import scala.reflect.BeanProperty
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
case class Tag(
  @BeanProperty
  @Column
  val name: String) {

  def this()=this(null)

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="tag_id")
  val id: Long=0

}