package com.fijimf.deepfij.stats.analysis

import java.time.LocalDate

import com.fijimf.deepfij.schedule.model.{Game, Result}

case class

object Counters {
  def winner(g:Game, r:Result) = if (r.homeScore>r.awayScore) g.homeTeamId else g.awayTeamId
  def loser(g:Game, r:Result) = if (r.homeScore<r.awayScore) g.homeTeamId else g.awayTeamId


  object wins extends Metric[RawSnapshot] {
    override val key: String = "Wins"
    override val higherIsBetter: Boolean = true
    override val zero: RawSnapshot = RawSnapshot.empty(0.0)

    override def calculate(state: RawSnapshot, scoreboard: Scoreboard): (RawSnapshot, RawSnapshot) = {
      val snap= scoreboard.games.foldLeft(state) { case (acc, (game, result)) =>
        val id: Long = winner(game, result)
        acc.set(id, acc.value(id) + 1)
      }
      (snap,snap)
    }

    object losses extends Metric[RawSnapshot] {
    override val key: String = "Losses"
    override val higherIsBetter: Boolean = false
    override val zero: RawSnapshot = RawSnapshot.empty(0.0)

    override def calculate(state: RawSnapshot, scoreboard: Scoreboard): (RawSnapshot, RawSnapshot) = {
      val snap= scoreboard.games.foldLeft(state) { case (acc, (game, result)) =>
        val id: Long = loser(game, result)
        acc.set(id, acc.value(id) + 1)
      }
      (snap,snap)
    }
    
  }

}
