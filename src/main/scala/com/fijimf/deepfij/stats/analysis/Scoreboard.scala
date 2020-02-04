package com.fijimf.deepfij.stats.analysis

import java.time.LocalDate

import com.fijimf.deepfij.schedule.model.{Game, Result}

case class Scoreboard(date: LocalDate, games: List[(Game, Result)])
