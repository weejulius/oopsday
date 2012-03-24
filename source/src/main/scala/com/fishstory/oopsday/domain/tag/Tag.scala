package com.fishstory.oopsday.domain.tag

import reflect.BeanProperty
import com.fishstory.oopsday.domain.DomainEntity
import javax.persistence._

@Entity
case class Tag(
                @BeanProperty
                @Column
                val name: String) extends DomainEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  val id: Long = 0

}