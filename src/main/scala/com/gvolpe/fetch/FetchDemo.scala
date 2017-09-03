package com.gvolpe.fetch

import DataSources._
import fetch._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FetchDemo extends App {

  // Fetch a single value
  val fetchValue: Fetch[String] = fetchString(1)

  import cats.Id
  import fetch.unsafe.implicits._
  import fetch.syntax._

  println(fetchValue.runA[Id])

  // Fetch values in batches
  import cats.syntax.cartesian._

  val fetchThree: Fetch[(String, String, String)] = (fetchString(1) |@| fetchString(2) |@| fetchString(3)).tupled

  println(fetchThree.runA[Id])

  // Fetch values from different data sources in parallel
  val fetchMulti: Fetch[(String, Int)] = (fetchString(1) |@| fetchLength("one")).tupled

  println(fetchMulti.runA[Id])

  // Caching values previously obtained for the same Id
  val fetchTwice: Fetch[(String, String)] = for {
    one <- fetchString(1)
    two <- fetchString(1)
  } yield (one, two)

  println(fetchTwice.runA[Id])

  // Concurrency using Future
  import fetch.implicits._
  import scala.concurrent.ExecutionContext.Implicits.global

  val fetch2 = fetchLength("Gabi")

  Fetch.runFetch[Future](fetch2)

  // Concurrency using Monix Task
  import monix.eval.Task
  import monix.execution.Scheduler

  import fetch.monixTask.implicits._

  implicit val scheduler = Scheduler.Implicits.global

  val task: Task[Int] = Fetch.run[Task](fetch2)
  Await.result(task.runAsync, Duration.Inf)

}
