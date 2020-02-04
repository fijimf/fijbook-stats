package com.fijimf.deepfij.stats.analysis

trait Metric[S] {
  val key: String

  val higherIsBetter: Boolean

  val zero: S

  def calculate(state: S, scoreboard: Scoreboard): (S, RawSnapshot)
}
