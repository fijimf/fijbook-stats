package com.fijimf.deepfij.stats.analysis

import com.fijimf.deepfij.schedule.model.{Game, Result, Schedule, ScheduleRoot, Team}
import com.fijimf.deepfij.stats.model.{TeamSnapshot, TeamStatistic}



object MetricAccumulator {
  /**
   *
   * @param root   The schedule root which holds all seasons schedules
   * @param metric The metric to be calculated
   * @return A list of Team statistics suitable for storing in the database
   */
  def processRoot[S](root: ScheduleRoot, metric: Metric[S]): List[TeamStatistic] = {
    root.schedules.flatMap(processSeason(_, metric))
  }

  /**
   * A season is processed by iterating over a series of Scoreboards, that is daily collections of results along
   * with the date.  It is guaranteed that there are not gaps in the dates and they come in order
   *
   * @param schedule A years schedule of results
   * @param metric   The metric to be calculated
   * @return A list of Team statistics suitable for storing in the database
   */
  def processSeason[S](schedule: Schedule, metric: Metric[S]): List[TeamStatistic] = {
    val scoreboards: List[Scoreboard] = schedule.seasonDates.map(d => Scoreboard(d, schedule.scoreboard(d)))
    val statistics: List[RawSnapshot] = scoreboards.foldLeft((metric.zero, List.empty[RawSnapshot])) { case ((s, r), scoreboard) =>
      val (state, snapshot) = metric.calculate(s, scoreboard)
      (state, snapshot :: r)
    }._2
    statistics.flatMap(s => s(schedule.teams, metric.key, metric.higherIsBetter)._2) //<<== FIXME
  }
}