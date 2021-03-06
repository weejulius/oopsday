package com.fishstory.oopsday.interfaces.tipping

import unfiltered.request._
import unfiltered.request.GET
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import com.fishstory.oopsday.interfaces.shared._
import com.fishstory.oopsday.infrustructure.tag.TagRepositoryJPAImpl
import com.fishstory.oopsday.domain.tip.{emptyTip, InvalidTip, Tip, TipRepository}
import unfiltered.response.{Redirect, ResponseFunction, NotFound, Found}
import java.util.ArrayList
import com.fishstory.oopsday.domain.tag.{Tag, TagRepository}
import com.fishstory.oopsday.infrustructure.{tagRepository, tipRepository}

/**Used to handle the requests regarding tip
 */
class TipFace extends AbstractPlan {

  type ErrorMessage = com.fishstory.oopsday.interfaces.shared.validation.ValidationResult

  override def delegates = {

    case req@GET(Path("/tips")) & Params(params) => {

      val validation = validate(params.get("page")) using isEmpty or isNumeric
      if (!validation) Scalate(req, "bad_user_request.ssp")
      else {

        var pageNumber: Int = 1

        if (validation.result().isSatisfiedAt(0, 1)) {
          pageNumber = params("page").head.toInt
        }

        transaction {
          Scalate(req, "tip/tips.ssp",
            ("tips", tipRepository.page(pageNumber)),
            ("page_nav", PageNavigation(pageNumber, tipRepository.count())))
        }
      }
    }

    case req@Path("/tips/new") => req match {

      case GET(_) => editable_page(req, None, None)
      case POST(_) & Params(params) => create_or_update_tip(req, None, params)
    }

    case req@GET(Path(Seg("tips" :: id :: Nil))) => {

      if (!(validate(id) using isNumeric)) Scalate(req, "bad_user_request.ssp")
      else {

        val _tip: Option[Tip] = transaction(tipRepository.id(id.toLong))

        if (_tip.isDefined) {
          Found ~> index_page(req, _tip.get)
        } else {
          NotFound ~> not_found_page(req)
        }
      }
    }

    case req@Path(Seg("tips" :: id :: "edit" :: Nil)) => req match {

      case POST(_) & Params(params) => create_or_update_tip(req, Some(id), params)

      case GET(_) => {

        if (!(validate(id) using isNumeric)) Scalate(req, "bad_user_request.ssp")
        else {

          val _tip = transaction(tipRepository.id(id.toLong))

          if (_tip.isDefined) {
            editable_page(req, _tip, None)
          } else {
            not_found_page(req)
          }
        }
      }
    }
  }

  private def create_or_update_tip(
                                    req: HttpRequest[Any],
                                    tip_id: Option[String],
                                    params: Map[String, Seq[String]]): ResponseFunction[Any] = {

    var _tip_id: Long = 0

    val idValidation = validate(tip_id) using isEmpty or isNumeric

    if (!idValidation) return Scalate(req, "bad_user_request.ssp")

    if (idValidation.result().isSatisfiedAt(0, 1)) _tip_id = tip_id.get.toInt

    val validations = validate(params.get("tip_title")) using (notBlank) and MaxLength(120)
    validations andValidate (params.get("tip_content")) using notBlank and MaxLength(3500)

    if (!validations) return editable_page(
      req,
      Some(InvalidTip(params("tip_title").head, params("tip_content").head, "")),
      Some(validations.result))


    val _tip_title: String = params("tip_title").head
    val _tip_content: String = params("tip_content").head
    var _tip: Option[Tip] = None
    var _tags: List[Tag] = List.empty

    startTransaction

    if (!params("tip_tag").isEmpty) {
      for (a_tag: String <- params("tip_tag").head.trim.split(",")) {
        _tags = _tags::: List(tagRepository.save(Tag(a_tag.trim)) when  tagRepository.column("title",a_tag.trim).isEmpty)
      }
    }

    if (isToCreateTip(_tip_id)) {
      _tip = Some(new Tip(_tip_title, _tip_content, "", _tags))

    } else {

      _tip = tipRepository.id(_tip_id.toLong)

      if (_tip.isEmpty) return Scalate(req, "bad_user_request.ssp")

      _tip.get.update_content(_tip_content)
      val tags = new ArrayList[Tag]
      for (a_tag <- _tags) {
        tags.add(a_tag)
      }
      _tip.get.tags = tags

    }

    tipRepository.saveNewOrUpdate(_tip.get)
    commitAndCloseTransaction
    Redirect("/tips/" + _tip.get.id)
  }

  private def isToCreateTip(tip_id: Long) = tip_id.toLong <= 0

  private def not_found_page(req: HttpRequest[Any]) = {
    Scalate(req, "not_found.ssp")
  }

  private def index_page(req: HttpRequest[Any], _tip: Tip) = {
    Scalate(req, "tip/tip.ssp", ("_tip", _tip))
  }

  private def editable_page(req: HttpRequest[Any], _tip: Option[Tip], _validation_tip_message: Option[ErrorMessage]) = {
    var tip: Tip = emptyTip;
    if (_tip.isDefined) {
      tip = _tip.get
    }
    Scalate(req, "tip/edit_tip.ssp", ("tip", tip), ("validation_tip_message", _validation_tip_message))
  }
}
