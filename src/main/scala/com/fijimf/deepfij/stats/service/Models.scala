package com.fijimf.deepfij.stats.service

import com.fijimf.deepfij.stats.analysis.Key

object Models {

  def findKey(model:String, key:String):Key={
    Key("",0.0, higherIsBetter = true)
  }

}
