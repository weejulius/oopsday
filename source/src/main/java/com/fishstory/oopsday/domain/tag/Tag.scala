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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long=0

}