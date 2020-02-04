package com.fijimf.deepfij.stats.model

import java.time.LocalDate

case class TeamSnapshot(id:Long, key:String, seasonId:Long,  date:LocalDate, count:Int, max:Double, median:Double, min:Double, mean:Double, stdDev:Double){

}
