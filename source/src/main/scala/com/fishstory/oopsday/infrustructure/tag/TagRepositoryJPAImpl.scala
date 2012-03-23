package com.fishstory.oopsday.infrustructure.tag

import com.fishstory.oopsday.domain.tag.TagRepository
import com.fishstory.oopsday.domain.tag.Tag
import com.fishstory.oopsday.infrustructure.Transactions

class TagRepositoryJPAImpl extends TagRepository with Transactions {

  def find_by_name_or_save_new(name: String): Tag = {
    var tags = get().createQuery(
      """
         select tag
           from com.fishstory.oopsday.domain.tag.Tag tag
          where tag.name=?1
      """, classOf[Tag]).setParameter(1, name).getResultList()

    if (tags.isEmpty()) {
      var tag = Tag(name)
      get().persist(tag)
      tag

    } else {
      tags.get(0)
    }
  }

}