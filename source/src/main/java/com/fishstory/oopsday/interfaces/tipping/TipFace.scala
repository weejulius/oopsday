package com.fishstory.oopsday.interfaces.tipping
import unfiltered.filter.Planify
import unfiltered.request.GET
import unfiltered.request.Path
import unfiltered.filter.Plan
import unfiltered.request.Seg
import unfiltered.response.ResponseString

class TipFace extends Plan {
  def intent = {
    case GET(Path(Seg("tip" :: id :: Nil))) => ResponseString("<h1 id = \"tip_content\">This is a tip</h1>")
      
  }
}

object TipFace {
  def create: TipFace = new TipFace()
}