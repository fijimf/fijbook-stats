package com.fijimf.deepfij.stats.model

import java.time.LocalDate

import doobie.util.update.Update0
import doobie.implicits._

case class DailySnapshot(id:Long, seasonSnapshotId: Long,  date:LocalDate, count:Int, max:Double, median:Double, min:Double, mean:Double, stdDev:Double){

}

object DailySnapshot {
  object Dao extends AbstractDao {

    override def cols: Array[String] = Array("id", "season_snapshot_id", "date", "count", "max", "median","min", "mean", "std_dev")

    override def tableName: String = "daily_snapshot"

    def insert(snap: DailySnapshot): Update0 =
      (fr"""INSERT INTO daily_snapshot(season_snapshot_id, date, count, max, median, min, mean, std_dev)
            VALUES (${snap.seasonSnapshotId},${snap.date}, ${snap.count},  ${snap.max},${snap.median},${snap.min},${snap.mean},${snap.stdDev} )
            RETURNING """ ++ colFr).update

    def update(snap: DailySnapshot): Update0 =
      (fr"""UPDATE daily_snapshot SET count = ${snap.count}, max = ${snap.max}, median= ${snap.median}, min = ${snap.min}, mean = ${snap.mean}, std_dev = ${snap.stdDev}
            WHERE season_snapshot_id=${snap.seasonSnapshotId} AND date=${snap.date}
            RETURNING """ ++ colFr).update

    def find(id: Long): doobie.Query0[DailySnapshot] = (baseQuery ++ fr" WHERE id = $id").query[DailySnapshot]

    def findBySeasonSnap(seasonSnapshotId: Long): doobie.Query0[DailySnapshot] = (baseQuery ++ fr" WHERE season_snapshot_id = $seasonSnapshotId").query[DailySnapshot]

    def list(): doobie.Query0[DailySnapshot] = baseQuery.query[DailySnapshot]

    def delete(id: Long): doobie.Update0 = sql"DELETE FROM daily_snapshot where id=$id".update

  }
}


