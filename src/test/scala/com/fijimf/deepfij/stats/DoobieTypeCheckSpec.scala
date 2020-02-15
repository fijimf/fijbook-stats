package com.fijimf.deepfij.stats


import java.time.LocalDate

import com.fijimf.deepfij.stats.model.{DailySnapshot, SeasonSnapshot}

class DoobieTypecheckSpec extends DbIntegrationSpec {
  val containerName = "doobie-typecheck-spec"
  val port = "17374"

  describe("Doobie typechecking Dao's") {
    describe("DailySnapshot.Dao") {
      it("insert should typecheck") {
        check(DailySnapshot.Dao.insert(DailySnapshot(0L, 1L, LocalDate.of(2019, 12, 31), 343, 1.2, 1.0, 0.8, 1.0, .001)))
      }
      it("update should typecheck") {
        check(DailySnapshot.Dao.update(DailySnapshot(0L, 1L, LocalDate.of(2019, 12, 31), 343, 1.2, 1.0, 0.8, 1.0, .001)))
      }
      it("find should typecheck") {
        check(DailySnapshot.Dao.find(1L))
      }
      it("findBySeasonSnap should typecheck") {
        check(DailySnapshot.Dao.findBySeasonSnap(1L))
      }
      it("delete should typecheck") {
        check(DailySnapshot.Dao.delete(1L))
      }
      it("truncate should typecheck") {
        check(DailySnapshot.Dao.truncate())
      }
    }
    describe("SeasonSnapshot.Dao") {
      it("insert should typecheck") {
        check(SeasonSnapshot.Dao.insert(SeasonSnapshot(0L, "won-lost", "wins", 1L, "1fgade23cnb99")))
      }
      it("update should typecheck") {
        check(SeasonSnapshot.Dao.update(SeasonSnapshot(9L, "won-lost", "wins", 1L, "1fgade23cnb99")))
      }
      it("find should typecheck") {
        check(SeasonSnapshot.Dao.find(1L))
      }
      it("findBySeasonId should typecheck") {
        check(SeasonSnapshot.Dao.findBySeasonId(1L))
      }
      it("findBySeasonIdModel should typecheck") {
        check(SeasonSnapshot.Dao.findBySeasonIdModel(1L,"won-lost"))
      }
      it("findBySeasonIdModelKey should typecheck") {
        check(SeasonSnapshot.Dao.findBySeasonIdModelKey(1L, "won-lost", "wins"))
      }
      it("delete should typecheck") {
        check(SeasonSnapshot.Dao.delete(1L))
      }
      it("truncate should typecheck") {
        check(SeasonSnapshot.Dao.truncate())
      }
    }
  }
}
