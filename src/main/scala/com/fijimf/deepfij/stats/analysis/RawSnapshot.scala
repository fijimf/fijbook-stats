package com.fijimf.deepfij.stats.analysis

import java.time.LocalDate

import com.fijimf.deepfij.schedule.model.Team
import com.fijimf.deepfij.stats.model.{TeamSnapshot, TeamStatistic}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

case class RawSnapshot(date: LocalDate, data: Map[Long, Double], defaultValue: Double) {

  def set(id:Long, x:Double): RawSnapshot = copy(data=data+(id->x))
  def increment(id:Long, x:Double=1.0): RawSnapshot = set(id, value(id)+x)
  def decrement(id:Long, x:Double=1.0): RawSnapshot = set(id, value(id)-x)

  def value(id: Long): Double = data.withDefaultValue(defaultValue)(id)

  def apply(teams: List[Team], key: String, higherIsBetter: Boolean): (TeamSnapshot, List[TeamStatistic]) = {
    val valueMap: Map[Long, Double] = teams.map(t => t.id -> value(t.id)).toMap
    val rankMap: Map[Long, Int] = RawSnapshot.createRankMap(valueMap.toList, higherIsBetter)
    val s = new DescriptiveStatistics(valueMap.values.toArray)
    val snapshot: TeamSnapshot = TeamSnapshot(0L, key, 0L, date, teams.size, s.getMax, s.getPercentile(0.5), s.getMin, s.getMean, s.getStandardDeviation)

    val statistics: List[TeamStatistic] = for {
      t <- teams
      v <- valueMap.get(t.id).toList
      r <- rankMap.get(t.id).toList
    } yield {
      TeamStatistic(0L, 0L, t.id, v, r)
    }
    (snapshot, statistics)
  }
}

object RawSnapshot {
  def empty(defaultValue: Double): RawSnapshot = RawSnapshot(
    LocalDate.of(1900, 1, 1),
    Map.empty[Long, Double],
    defaultValue
  )

  def createRankMap(valueMap: List[(Long, Double)], higherIsBetter: Boolean): Map[Long, Int] = {
    val items: List[(Long, Double)] = if (higherIsBetter)
      valueMap.sortBy(-_._2) // This seems backward, but the fold construction below reverses the order of the list
    else
      valueMap.sortBy(_._2)
    items.foldLeft(List.empty[(Long, Double, Int, Int)]) { case (list, (id, value)) =>
      list.headOption match {
        case None => List((id, value, 1, 1))
        case Some((_, x, first, last)) =>
          if (value == x) {
            (id, value, first, last + 1) :: list
          } else {
            (id, value, last + 1, last + 1) :: list
          }

      }
    }.map(t => t._1 -> t._3).toMap
  }

}

