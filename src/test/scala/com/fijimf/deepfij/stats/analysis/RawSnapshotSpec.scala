package com.fijimf.deepfij.stats.analysis

import org.scalatest.FunSpec

class RawSnapshotSpec extends FunSpec {
  describe("RawSnapshot") {
    it("should build a rank map (unique values)") {
      val xs = List(
        1L -> 1.0, //<<== 1L is lowest
        2L -> 2.0,
        3L -> 3.0,
        4L -> 4.0,
        5L -> 5.0, //<<== 5L is highest
      )

      val rankMap1: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = true)
      assert(rankMap1.get(0L) === None)
      assert(rankMap1.get(1L) === Some(5))
      assert(rankMap1.get(2L) === Some(4))
      assert(rankMap1.get(3L) === Some(3))
      assert(rankMap1.get(4L) === Some(2))
      assert(rankMap1.get(5L) === Some(1))

      val rankMap2: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = false)
      assert(rankMap2.get(0L) === None)
      assert(rankMap2.get(1L) === Some(1))
      assert(rankMap2.get(2L) === Some(2))
      assert(rankMap2.get(3L) === Some(3))
      assert(rankMap2.get(4L) === Some(4))
      assert(rankMap2.get(5L) === Some(5))
    }

    it("should build a rank map (duplicated values 1)") {
      val xs = List(
        1L -> 1.0, //<<== 1L is lowest
        2L -> 1.0,
        3L -> 1.0,
        4L -> 4.0,
        5L -> 5.0, //<<== 5L is highest
      )

      val rankMap1: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = true)
      assert(rankMap1.get(0L) === None)
      assert(rankMap1.get(1L) === Some(3))
      assert(rankMap1.get(2L) === Some(3))
      assert(rankMap1.get(3L) === Some(3))
      assert(rankMap1.get(4L) === Some(2))
      assert(rankMap1.get(5L) === Some(1))

      val rankMap2: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = false)
      assert(rankMap2.get(0L) === None)
      assert(rankMap2.get(1L) === Some(1))
      assert(rankMap2.get(2L) === Some(1))
      assert(rankMap2.get(3L) === Some(1))
      assert(rankMap2.get(4L) === Some(4))
      assert(rankMap2.get(5L) === Some(5))
    }

    it("should build a rank map (duplicated values 2)") {
      val xs = List(
        1L -> 1.0, //<<== 1L is lowest
        2L -> 1.0,
        3L -> 3.0,
        4L -> 5.0,
        5L -> 5.0, //<<== 5L is highest
      )

      val rankMap1: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = true)
      assert(rankMap1.get(0L) === None)
      assert(rankMap1.get(1L) === Some(4))
      assert(rankMap1.get(2L) === Some(4))
      assert(rankMap1.get(3L) === Some(3))
      assert(rankMap1.get(4L) === Some(1))
      assert(rankMap1.get(5L) === Some(1))

      val rankMap2: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = false)
      assert(rankMap2.get(0L) === None)
      assert(rankMap2.get(1L) === Some(1))
      assert(rankMap2.get(2L) === Some(1))
      assert(rankMap2.get(3L) === Some(3))
      assert(rankMap2.get(4L) === Some(4))
      assert(rankMap2.get(5L) === Some(4))
    }

    it("should build a rank map (duplicated values 3)") {
      val xs = List(
        1L -> 0.0, //<<== 1L is lowest
        2L -> 0.0,
        3L -> 0.0,
        4L -> 0.0,
        5L -> 0.0, //<<== 5L is highest
      )

      val rankMap1: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = true)
      assert(rankMap1.get(0L) === None)
      assert(rankMap1.get(1L) === Some(1))
      assert(rankMap1.get(2L) === Some(1))
      assert(rankMap1.get(3L) === Some(1))
      assert(rankMap1.get(4L) === Some(1))
      assert(rankMap1.get(5L) === Some(1))

      val rankMap2: Map[Long, Int] = RawSnapshot.createRankMap(xs, higherIsBetter = false)
      assert(rankMap2.get(0L) === None)
      assert(rankMap2.get(1L) === Some(1))
      assert(rankMap2.get(2L) === Some(1))
      assert(rankMap2.get(3L) === Some(1))
      assert(rankMap2.get(4L) === Some(1))
      assert(rankMap2.get(5L) === Some(1))
    }
  }

}