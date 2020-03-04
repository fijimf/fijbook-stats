package com.fijimf.deepfij.stats.service

import cats.MonadError
import cats.effect.Sync
import com.fijimf.deepfij.schedule.model.Team
import com.fijimf.deepfij.stats.analysis.{Key, ModelResults, RawSnapshot, SeasonResults}
import com.fijimf.deepfij.stats.model.{DailySnapshot, SeasonSnapshot, TeamStatistic}
import doobie.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream

case class StatisticsRepo[F[_] : Sync](xa: Transactor[F]) {
  val me: MonadError[F, Throwable] = implicitly[MonadError[F, Throwable]]

  def loadResults(seasonId: Long, model: String): ModelResults = ???

  def loadDigest(seasonId: Long, model: String): String = ???

  def saveModelResults(seasonId: Long, seasonDigest: String, model: String, teams: List[Team], results: ModelResults): Stream[F, TeamStatistic] = {
    Stream.emits(results.data).flatMap(results => {
      saveSeasonResults(seasonId, seasonDigest, model, teams, results)
    })
  }

  def saveSeasonResults(seasonId: Long, seasonDigest: String, model: String, teams: List[Team], results: SeasonResults): Stream[F, TeamStatistic] = {
    for {
      seasonSnap <- SeasonSnapshot.Dao
        .insert(SeasonSnapshot(0L, model, results.key, seasonId, seasonDigest))
        .withGeneratedKeys[SeasonSnapshot](SeasonSnapshot.Dao.cols: _*)
        .transact(xa)
      ts <- saveDateResults(seasonSnap, teams, results.data)
    } yield {
      ts
    }
  }

  def saveDateResults(seasonSnapshot: SeasonSnapshot, teams: List[Team], data: List[RawSnapshot]): Stream[F, TeamStatistic] = {
    val key: Key = Models.findKey(seasonSnapshot.model, seasonSnapshot.key)

    for {
      raw <- Stream.emits(data)
      (dailySnap, listStats) = raw.toSnapshotAndStats(teams, key)
      ds <- DailySnapshot.Dao
        .insert(dailySnap.copy(seasonSnapshotId = seasonSnapshot.id))
        .withGeneratedKeys[DailySnapshot](DailySnapshot.Dao.cols: _*)
        .transact(xa)
      ts <- saveTeamResults(ds, listStats)
    } yield {
      ts
    }
  }

  def saveTeamResults(ds: DailySnapshot, listStats: List[TeamStatistic]): Stream[F, TeamStatistic] = {
   Stream
     .emits(listStats)
     .flatMap(teamStatistic=>{
     for {
       teamStream <- TeamStatistic.Dao
         .insert(teamStatistic.copy(dailySnapshotId = ds.id))
         .withGeneratedKeys[TeamStatistic](TeamStatistic.Dao.cols: _*)
         .transact(xa)
     } yield{
       teamStream
     }
   })
  }
}





