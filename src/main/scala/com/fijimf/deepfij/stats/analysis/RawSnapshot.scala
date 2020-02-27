package com.fijimf.deepfij.stats.analysis

import java.time.LocalDate

import com.fijimf.deepfij.schedule.model.Team
import com.fijimf.deepfij.stats.model.{DailySnapshot, TeamStatistic}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

case class RawSnapshot(date: LocalDate, data: Map[Long, Double]) {

  def toSnapshotAndStats(teams: List[Team], key:Key): (DailySnapshot, List[TeamStatistic]) = {
    val valueMap: Map[Long, Double] = teams.map(t => t.id -> data.getOrElse(t.id,key.defaultValue)).toMap
    val rankMap: Map[Long, Int] = RawSnapshot.createRankMap(valueMap.toList, key.higherIsBetter)
    val s = new DescriptiveStatistics(valueMap.values.toArray)
    val snapshot: DailySnapshot = DailySnapshot(0L, 0L, date, teams.size, s.getMax, s.getPercentile(0.5), s.getMin, s.getMean, s.getStandardDeviation)

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
    Map.empty[Long, Double]
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

