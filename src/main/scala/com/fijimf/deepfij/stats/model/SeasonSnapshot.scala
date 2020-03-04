package com.fijimf.deepfij.stats.model

import doobie.implicits._
import doobie.util.update.Update0

case class SeasonSnapshot(id: Long, model: String, key: String, seasonId: Long, seasonDigest: String)

object SeasonSnapshot {

  object Dao extends AbstractDao {

    override def cols: Array[String] = Array("id", "model", "key", "season_id", "season_digest")

    override def tableName: String = "season_snapshot"

    def insert(snap: SeasonSnapshot): Update0 =
      (fr"""INSERT INTO season_snapshot(model, key, season_id, season_digest)
            VALUES (${snap.model},${snap.key}, ${snap.seasonId},  ${snap.seasonDigest})
            ON CONFLICT (model, key, season_id)
               DO UPDATE SET season_digest = EXCLUDED.season_digest
                 WHERE season_snapshot.season_digest <> EXCLUDED.season_digest
            RETURNING """ ++ colFr).update

    def update(snap: SeasonSnapshot): Update0 =
      (fr"""UPDATE season_snapshot SET season_digest = ${snap.seasonDigest}
            WHERE model=${snap.model} AND key=${snap.key} AND season_id=${snap.seasonId}
            RETURNING """ ++ colFr).update

    def find(id: Long): doobie.Query0[SeasonSnapshot] = (baseQuery ++ fr" WHERE id = $id").query[SeasonSnapshot]
    def findAll(): doobie.Query0[SeasonSnapshot] = baseQuery.query[SeasonSnapshot]

    def findBySeasonId(seasonId: Long): doobie.Query0[SeasonSnapshot] = (baseQuery ++ fr" WHERE season_id = $seasonId").query[SeasonSnapshot]

    def findBySeasonIdModel(seasonId: Long, model: String): doobie.Query0[SeasonSnapshot] = (baseQuery ++ fr" WHERE season_id = $seasonId and model = $model").query[SeasonSnapshot]

    def findBySeasonIdModelKey(seasonId: Long, model: String, key: String): doobie.Query0[SeasonSnapshot] = (baseQuery ++ fr" WHERE season_id = $seasonId and model = $model and key = $key").query[SeasonSnapshot]

    def list(): doobie.Query0[SeasonSnapshot] = baseQuery.query[SeasonSnapshot]

    def delete(id: Long): doobie.Update0 = sql"DELETE FROM season_snapshot where id=$id".update

  }

}

