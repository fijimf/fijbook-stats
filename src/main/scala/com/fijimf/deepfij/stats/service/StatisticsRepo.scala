package com.fijimf.deepfij.stats.service

import java.time.LocalDate

import cats.MonadError
import cats.effect.Sync
import cats.implicits._
import com.fijimf.deepfij.schedule.model.Team
import com.fijimf.deepfij.stats.analysis.RawSnapshot
import com.fijimf.deepfij.stats.model.{DailySnapshot, SeasonSnapshot, TeamStatistic}
import doobie.implicits._
import doobie.util.transactor.Transactor

case class StatisticsRepo[F[_] : Sync](xa: Transactor[F]) {
  val me: MonadError[F, Throwable] = implicitly[MonadError[F, Throwable]]

  def loadResults(seasonId:Long, model:String):ModelResults = ???

  def loadDigest(seasonId:Long, model:String):String = ???

  def saveResults(seasonId: Long, seasonDigest: String, model: String, teams:List[Team], results: ModelResults): F[List[TeamStatistic]] = {
    results.data.map(results=>{
      saveResults(seasonId, seasonDigest, model, teams, results)
    }).sequence.map(_.flatten)
  }

  def saveResults(seasonId: Long, seasonDigest: String, model: String, teams:List[Team],results: SeasonResults): F[List[TeamStatistic]] = {
    for {
      seasonSnap <- SeasonSnapshot.Dao
        .insert(SeasonSnapshot(0L, model, results.key, seasonId, seasonDigest))
        .withUniqueGeneratedKeys[SeasonSnapshot](SeasonSnapshot.Dao.cols: _*)
        .transact(xa).exceptSql(ex => me.raiseError[SeasonSnapshot](ex))
      ts <- saveSeasonResults(seasonSnap, teams, results.data)
    } yield {
      ts
    }
  }



  def saveSeasonResults(seasonSnapshot: SeasonSnapshot, teams:List[Team], data:List[RawSnapshot]): F[List[TeamStatistic]] = {
    data.map(raw=>{
      val key = Models.findKey(seasonSnapshot.model, seasonSnapshot.key)
      val (dailySnap, listStats)=raw(teams,seasonSnapshot.key, key.higherIsBetter, key.defaultValue)
      for {
        ds <- DailySnapshot.Dao
          .insert(dailySnap.copy(seasonSnapshotId = seasonSnapshot.id))
          .withUniqueGeneratedKeys[SeasonSnapshot](SeasonSnapshot.Dao.cols: _*)
          .transact(xa).exceptSql(ex => me.raiseError[SeasonSnapshot](ex))
        ts<-saveTeamResults(ds, listStats)
      } yield {
        ts
      }
    }).sequence.map(_.flatten)
  }
  def saveTeamResults(ds: SeasonSnapshot, listStats: List[TeamStatistic]): F[List[TeamStatistic]] = {
    listStats.map(s=> {
      TeamStatistic.Dao.insert(s.copy(dailySnapshotId = ds.id))
        .withUniqueGeneratedKeys[TeamStatistic](TeamStatistic.Dao.cols: _*)
    }).sequence.transact(xa).exceptSql(ex => me.raiseError[List[TeamStatistic]](ex))

  }
}

case class ModelResults(model: String, data: List[SeasonResults])

case class SeasonResults(key: String, data:List[RawSnapshot])

