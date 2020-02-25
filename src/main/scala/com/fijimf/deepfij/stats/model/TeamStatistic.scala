package com.fijimf.deepfij.stats.model

import doobie.implicits._
import doobie.util.update.{Update, Update0}

case class TeamStatistic(id: Long, dailySnapshotId: Long, teamId: Long, value: Double, rank: Int) {

}

object TeamStatistic {

  object Dao extends AbstractDao {

    override def cols: Array[String] = Array("id", "daily_snapshot_id", "team_id", "value", "rank")

    override def tableName: String = "team_statistic"

    def insert(snap: TeamStatistic): Update0 =
      (fr"""INSERT INTO team_statistic(daily_snapshot_id, team_id, value, rank)
            VALUES (${snap.dailySnapshotId},${snap.teamId}, ${snap.value},  ${snap.rank})
            RETURNING """ ++ colFr).update

    def update(snap: TeamStatistic): Update0 =
      (fr"""UPDATE team_statistic SET value = ${snap.value}, rank = ${snap.rank}
            WHERE daily_snapshot_id=${snap.dailySnapshotId} AND team_id=${snap.teamId}
            RETURNING """ ++ colFr).update

    def find(id: Long): doobie.Query0[TeamStatistic] = (baseQuery ++ fr" WHERE id = $id").query[TeamStatistic]

    def findByDailySnapshotId(dailySnapshotId: Long): doobie.Query0[TeamStatistic] = (baseQuery ++ fr" WHERE daily_snapshot_id = $dailySnapshotId").query[TeamStatistic]

    def findByDailySnapshotIdTeam(dailySnapshotId: Long, teamId: Long): doobie.Query0[TeamStatistic] = (baseQuery ++ fr" WHERE daily_snapshot_id = $dailySnapshotId and team_id=$teamId").query[TeamStatistic]

    def list(): doobie.Query0[TeamStatistic] = baseQuery.query[TeamStatistic]

    def delete(id: Long): doobie.Update0 = sql"DELETE FROM team_statistic where id=$id".update

  }

}






