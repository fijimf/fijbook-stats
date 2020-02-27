package com.fijimf.deepfij.stats.analysis

import com.fijimf.deepfij.schedule.model.Schedule

trait Model {
  val name:String
  val keys:List[Key]
  def process(s:Schedule): ModelResults = {
???
  }
}

case class Key(name:String, defaultValue:Double, higherIsBetter:Boolean)