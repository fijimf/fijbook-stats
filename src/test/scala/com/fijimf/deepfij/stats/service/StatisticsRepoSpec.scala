package com.fijimf.deepfij.stats.service

import java.time.LocalDate

import com.fijimf.deepfij.schedule.model.Team
import com.fijimf.deepfij.stats.DbIntegrationSpec
import com.fijimf.deepfij.stats.analysis.{Key, RawSnapshot}
import com.fijimf.deepfij.stats.model.{DailySnapshot, SeasonSnapshot, TeamStatistic}
import doobie.implicits._
import org.apache.commons.codec.digest.DigestUtils

class StatisticsRepoSpec extends DbIntegrationSpec {
  val containerName = "stats-repo-spec"
  val port = "17374"
  val teams = List(
    Team(1L, "gtown", "Georgetown", "", "", "", ""),
    Team(2L, "nova", "Villanova", "", "", "", ""),
    Team(3L, "uconn", "Connecticut", "", "", "", ""),
    Team(4L, "duke", "Duke", "", "", "", "")
  )
  describe("A StatisticsRepo ") {
    val repo = StatisticsRepo(transactor)

    it("should insert new TeamResults") {
      val (ds, stats) = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      ).toSnapshotAndStats(teams, Key("max-margin", 0.0, true))
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        a <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
        y <- repo.saveTeamResults(ds.copy(id = 99L), stats).compile.toList
        z1 <- TeamStatistic.Dao.findByDailySnapshotId(99L).to[List].transact(transactor)
        z2 <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
        zx <- TeamStatistic.Dao.findByDailySnapshotId(123L).to[List].transact(transactor)
      } yield {
        assert(a.isEmpty)

        assert(y.size === 4)
        assert(z1.size === 4)
        assert(z2.size === 4)
        assert(zx.size === 0)
        z1.foreach(t => {
          assert(y.contains(t))
          assert(t.id > 0L)
        })
      }).unsafeRunSync()
    }
    it("insert of Team results should be idempotent") {
      val (ds, stats) = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      ).toSnapshotAndStats(teams, Key("max-margin", 0.0, true))
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        y1 <- repo.saveTeamResults(ds.copy(id = 99L), stats).compile.to[List]
        y2 <- repo.saveTeamResults(ds.copy(id = 99L), stats).compile.to[List]
        z <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
      } yield {
        assert(y1.size === 4)
        assert(y2.size === 0)
        assert(z.size === 4)
        z.foreach(t => {
          assert(y1.contains(t))
          assert(t.id > 0L)
        })
      }).unsafeRunSync()
    }
    it("insert of Team change results should retain the same ids and only update changed values") {
      val (ds, stats1) = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      ).toSnapshotAndStats(teams, Key("max-margin", 0.0, true))
      val (_, stats2) = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 3.0, 2L -> 2.0, 3L -> 2.0, 4L -> -3.0)
      ).toSnapshotAndStats(teams, Key("max-margin", 0.0, true))
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        y1 <- repo.saveTeamResults(ds.copy(id = 99L), stats1).compile.to[List]
        y2 <- repo.saveTeamResults(ds.copy(id = 99L), stats2).compile.to[List]
        z <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
      } yield {
        assert(y1.size === 4)
        assert(y2.size === 2)
        assert(z.size === 4)
        y2.foreach(t => {
          assert(z.contains(t))
          assert(t.id > 0L)
        })
      }).unsafeRunSync()
    }
    it("insert new DateResults") {
      val seasonSnapshot = SeasonSnapshot(1L, "scoring", "max-margin", 4L, DigestUtils.md5Hex("Jim ROolz"))
      val rs1 = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      )
      val rs2 = RawSnapshot(
        LocalDate.of(2020, 2, 19),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      )
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        _ <- DailySnapshot.Dao.truncate().run.transact(transactor)
        y1 <- repo.saveDateResults(seasonSnapshot, teams, List(rs1,rs2)).compile.to[List]
        z <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
        w <- DailySnapshot.Dao.findAll().to[List].transact(transactor)
      } yield {
        assert(y1.size === 8)
        assert(w.size === 2)
        assert(z.size === 8)
      }).unsafeRunSync()
    }
    it("insert of DateResults is idempotent") {
      val seasonSnapshot = SeasonSnapshot(1L, "scoring", "max-margin", 4L, DigestUtils.md5Hex("Jim ROolz"))
      val rs1 = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      )
      val rs2 = RawSnapshot(
        LocalDate.of(2020, 2, 19),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      )
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        _ <- DailySnapshot.Dao.truncate().run.transact(transactor)
        y1 <- repo.saveDateResults(seasonSnapshot, teams, List(rs1,rs2)).compile.to[List]
        y2 <- repo.saveDateResults(seasonSnapshot, teams, List(rs1,rs2)).compile.to[List]
        z <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
        w <- DailySnapshot.Dao.findAll().to[List].transact(transactor)
      } yield {
        assert(y1.size === 8)
        assert(y2.size === 0)
        assert(w.size === 2)
        assert(z.size === 8)
      }).unsafeRunSync()
    }
    it("insert of changed DateResults leave id of DailySnap the same") {
      val seasonSnapshot = SeasonSnapshot(1L, "scoring", "max-margin", 4L, DigestUtils.md5Hex("Jim ROolz"))
      val rs1 = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 2.0, 4L -> -3.0)
      )
      val rs1a = RawSnapshot(
        LocalDate.of(2020, 2, 20),
        Map(1L -> 2.0, 2L -> 3.1, 3L -> 2.0, 4L -> -3.1)
      )
      val rs2 = RawSnapshot(
        LocalDate.of(2020, 2, 19),
        Map(1L -> 2.0, 2L -> 3.0, 3L -> 5.0, 4L -> -3.0)
      )
      (for {
        _ <- TeamStatistic.Dao.truncate().run.transact(transactor)
        _ <- DailySnapshot.Dao.truncate().run.transact(transactor)
        y1 <- repo.saveDateResults(seasonSnapshot, teams, List(rs1,rs2)).compile.to[List]
        w1 <- DailySnapshot.Dao.findAll().to[List].transact(transactor)
        y2 <- repo.saveDateResults(seasonSnapshot, teams, List(rs1a, rs2)).compile.to[List]
        w2 <- DailySnapshot.Dao.findAll().to[List].transact(transactor)
        z <- TeamStatistic.Dao.findAll().to[List].transact(transactor)
      } yield {
        assert(y1.size === 8)
        assert(y2.size === 2)
        assert(w1.size === 2)
        assert(w2.size === 2)
        assert(w1.map(_.id).sorted === w2.map(_.id).sorted)
        assert(z.size === 8)
      }).unsafeRunSync()
    }
  }
}