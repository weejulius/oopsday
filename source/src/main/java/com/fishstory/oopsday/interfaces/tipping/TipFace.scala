package com.fishstory.oopsday.interfaces.tipping

import unfiltered.request._
import com.fishstory.oopsday.domain.tip.Tip
import unfiltered.response.{ NotFound, Found }
import unfiltered.response.Redirect
import unfiltered.request.GET
import unfiltered.request.Seg
import com.fishstory.oopsday.infrustructure.tip.TipRepositoryJPAImpl
import com.fishstory.oopsday.domain.tip.TipRepository
import scala.collection.JavaConverters._
import unfiltered.response.ResponseFunction
import com.fishstory.oopsday.interfaces.shared.template.Strings
import com.fishstory.oopsday.interfaces.shared.validation.Validations
import com.fishstory.oopsday.interfaces.shared.validation.IsNotBlank
import com.fishstory.oopsday.interfaces.shared.validation.MaxLength
import com.fishstory.oopsday.interfaces.shared.validation.FAILURE
import com.fishstory.oopsday.interfaces.shared.validation.And
import com.fishstory.oopsday.interfaces.shared.validation.StringValidations
import com.fishstory.oopsday.interfaces.shared.validation.IsNumeric
import com.fishstory.oopsday.interfaces.shared.validation.SUCCESS
import com.fishstory.oopsday.interfaces.shared.validation.IsEmpty
import com.fishstory.oopsday.interfaces.shared.validation.Or
import com.fishstory.oopsday.interfaces.shared.validation.ParamIsNumeric
import com.fishstory.oopsday.interfaces.shared._

class TipFace extends AbstractPlan {

  private val _tipRepository: TipRepository = new TipRepositoryJPAImpl();

  override def delegates = {

    case req @ GET(Path("/tips")) & Params(params) => {

      Validations(params,
        ("page", "page size", IsEmpty() :: Or() :: ParamIsNumeric() :: Nil)).result match {
          case FAILURE(message) => Scalate(req, "bad_user_request.ssp")
          case SUCCESS(messages) =>
            var page: Int = 1
            val pageSize=TipFace.pageSize

            if (!params("page").isEmpty && Strings.is_numeric(params("page").head)) {
              page = params("page").head.toInt
            }

            start_transaction
            val tips = _tipRepository.find_all((page - 1) * pageSize, pageSize)
            val count_of_tips = _tipRepository.count
            commit_and_close_transaction

            Scalate(req, "tip/tips.ssp",
              ("tips", tips.asScala.toList), ("page_nav",PageNavigation(page,count_of_tips,pageSize)))
        }
    }

    case req @ Path("/tips/new") => req match {

      case GET(_) => editable_page(req, None, Map.empty)
      case POST(_) & Params(params) => create_or_update_tip(req, None, params)
    }

    case req @ GET(Path(Seg("tips" :: id :: Nil))) => {

      start_transaction
      var _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
      commit_and_close_transaction

      if (_tip.isDefined) {
        Found ~> index_page(req, _tip.get)
      } else {
        NotFound ~> not_found_page(req)
      }
    }

    case req @ Path(Seg("tips" :: id :: "edit" :: Nil)) => req match {

      case POST(_) & Params(params) => create_or_update_tip(req, Some(id), params)

      case GET(_) => {

        StringValidations((id, "tip id", IsNumeric() :: Nil)).result match {

          case FAILURE(messages) => Scalate(req, "bad_user_request.ssp")
          case _ =>
            start_transaction
            val _tip: Option[Tip] = _tipRepository.find_by_id_is(id.toLong)
            commit_and_close_transaction

            if (_tip.isDefined) {
              editable_page(req, _tip, Map.empty)
            } else {
              not_found_page(req)
            }
        }
      }
    }
  }

  private def create_or_update_tip(req: HttpRequest[Any], tip_id: Option[String], params: Map[String, Seq[String]]): ResponseFunction[Any] = {

    var _tip_id: Long = 0

    if (tip_id.isDefined && !tip_id.get.isEmpty()) {
      if (!Strings.is_numeric(tip_id.get)) {
        return Scalate(req, "bad_user_request.ssp")
      }
      _tip_id = tip_id.get.toLong
    }

    Validations(params,
      ("tip_title", "title", IsNotBlank() :: And() :: MaxLength(120) :: Nil),
      ("tip_content", "content", IsNotBlank() :: And() :: MaxLength(3000) :: Nil)).result match {
        case FAILURE(messages) => return editable_page(req, Some(Tip.mock(params("tip_title").head, params("tip_content").head, "")), messages)
        case _ =>
      }

    val _tip_title: String = params("tip_title").head
    val _tip_content: String = params("tip_content").head

    var _tip: Option[Tip] = None

    start_transaction
    if (is_to_create_tip(_tip_id)) {
      _tip = Some(Tip.create(_tip_title, _tip_content, ""))

    } else {
      _tip = _tipRepository.find_by_id_is(_tip_id.toLong);
      if (_tip.isEmpty) {
        return Scalate(req, "bad_user_request.ssp")
      } else {
        _tip.get.update_content(_tip_content)
      }
    }

    try {
      _tipRepository.save_new_or_update(_tip.get)
      Redirect("/tips/" + _tip.get.id)
    } finally {
      commit_and_close_transaction
    }
  }

  private def is_to_create_tip(tip_id: Long) = tip_id.toLong <= 0

  private def not_found_page(req: HttpRequest[Any]) = {
    Scalate(req, "not_found.ssp")
  }

  private def index_page(req: HttpRequest[Any], _tip: Tip) = {
    Scalate(req, "tip/tip.ssp", ("_tip", _tip))
  }

  private def editable_page(req: HttpRequest[Any], _tip: Option[Tip], _validation_tip_message: Map[String, String]) = {
    var tip: Tip = Tip.NullObject
    if (_tip.isDefined) {
      tip = _tip.get
    }
    Scalate(req, "tip/edit_tip.ssp", ("tip", tip), ("validation_tip_message", _validation_tip_message))
  }
}

object TipFace {
  var pageSize: Int = 2

  def create: TipFace = new TipFace()
  def set_page_size(page_size: Int) = pageSize = page_size
}