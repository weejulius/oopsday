package com.fishstory.oopsday.interfaces.tipping

import unfiltered.request._
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.request.GET
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import com.fishstory.oopsday.domain.tip.TipRepository
import com.fishstory.oopsday.interfaces.shared.template.Strings
import com.fishstory.oopsday.interfaces.shared._
import com.fishstory.oopsday.domain.tag.Tag
import com.fishstory.oopsday.domain.tag.TagRepository
import com.fishstory.oopsday.infrustructure.tag.TagRepositoryJPAImpl
import java.util.ArrayList
import com.fishstory.oopsday.domain.tip.emptyTip
import com.fishstory.oopsday.domain.tip.InvalidTip
import unfiltered.response.{NotFound, Found, Redirect, ResponseFunction}
import scala.collection.JavaConverters._

/**Used to handle the requests regarding tip
 */
class TipFace extends AbstractPlan {

  //Todo use DI to inject dependencies
  private val _tipRepository: TipRepository = new TipRepositoryJPAImpl
  private val _tagRepository: TagRepository = new TagRepositoryJPAImpl
  type ErrorMessage = com.fishstory.oopsday.interfaces.shared.validation.ValidationResult

  override def delegates = {

    case req@GET(Path("/tips")) & Params(params) => {

      if (Evaluates(params.get("page")) using IsEmpty() or IsNumeric() isPassed) {

        var page: Int = 1
        //TODO move to configuration area
        val pageSize = TipFace.pageSize

        if (!params("page").isEmpty && Strings.is_numeric(params("page").head)) {
          page = params("page").head.toInt
        }
        startTransaction
        val tips = _tipRepository.find_all((page - 1) * pageSize, pageSize)
        val countOfTips = _tipRepository.count()
        commitAndCloseTransaction
        Scalate(req, "tip/tips.ssp",
          ("tips", tips.asScala.toList),
          ("page_nav", PageNavigation(page, countOfTips, pageSize)))

      } else {
        Scalate(req, "bad_user_request.ssp")
      }
    }
    case req@Path("/tips/new") => req match {

      case GET(_) => editable_page(req, None, None)
      case POST(_) & Params(params) => create_or_update_tip(req, None, params)
    }

    case req@GET(Path(Seg("tips" :: id :: Nil))) => {

      if (Evaluates(id) using IsNumeric() isPassed) {

        startTransaction
        val _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
        commitAndCloseTransaction
        if (_tip.isDefined) {
          Found ~> index_page(req, _tip.get)
        } else {
          NotFound ~> not_found_page(req)
        }
      } else {
        Scalate(req, "bad_user_request.ssp")
      }
    }

    case req@Path(Seg("tips" :: id :: "edit" :: Nil)) => req match {

      case POST(_) & Params(params) => create_or_update_tip(req, Some(id), params)

      case GET(_) => {

        if (Evaluates(id) using IsNumeric() isPassed) {
          startTransaction
          val _tip = _tipRepository.find_by_id_is(id.toLong)
          commitAndCloseTransaction

          if (_tip.isDefined) {
            editable_page(req, _tip, None)
          } else {
            not_found_page(req)
          }
        } else {
          Scalate(req, "bad_user_request.ssp")
        }
      }
    }
  }

  private def create_or_update_tip(
                                    req: HttpRequest[Any],
                                    tip_id: Option[String],
                                    params: Map[String, Seq[String]]): ResponseFunction[Any] = {

    var _tip_id: Long = 0

    if (tip_id.isDefined && !tip_id.get.isEmpty()) {
      if (!Strings.is_numeric(tip_id.get)) {
        return Scalate(req, "bad_user_request.ssp")
      }
      _tip_id = tip_id.get.toLong
    }
    var isViolated = true
    val expression = NotBlank[Option[Seq[String]]]() && MaxLength(120)
    isViolated = evaluating(expression, params.get("tip_title"))
    expression.retry(NotBlank[Option[Seq[String]]]() && MaxLength(3500))
    isViolated = evaluating(expression, params.get("tip_content")) && isViolated
    if (!isViolated) {
      return editable_page(
        req,
        Some(InvalidTip(params("tip_title").head, params("tip_content").head, "")),
        Some(expression.results))
    }

    val _tip_title: String = params("tip_title").head
    val _tip_content: String = params("tip_content").head

    var _tip: Option[Tip] = None

    var _tags: List[Tag] = List.empty
    startTransaction
    if (!params("tip_tag").isEmpty) {
      for (a_tag: String <- params("tip_tag").head.trim.split(",")) {
        _tags = _tags ::: List(_tagRepository.find_by_name_or_save_new(a_tag.trim))
      }
    }

    if (is_to_create_tip(_tip_id)) {
      _tip = Some(new Tip(_tip_title, _tip_content, "", _tags))

    } else {
      _tip = _tipRepository.find_by_id_is(_tip_id.toLong);
      if (_tip.isEmpty) {
        return Scalate(req, "bad_user_request.ssp")
      } else {
        _tip.get.update_content(_tip_content)
        val tags = new ArrayList[Tag]
        for (a_tag <- _tags) {
          tags.add(a_tag)
        }
        _tip.get.tags = tags
      }
    }

    _tipRepository.save_new_or_update(_tip.get)
    commitAndCloseTransaction
    Redirect("/tips/" + _tip.get.id)
  }

  private def is_to_create_tip(tip_id: Long) = tip_id.toLong <= 0

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

object TipFace {
  var pageSize: Int = 10

  def create: TipFace = new TipFace()

  def set_page_size(page_size: Int) = pageSize = page_size
}
