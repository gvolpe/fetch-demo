package com.gvolpe.fetch

import cats.data.NonEmptyList
import fetch._

object DataSources {

  implicit object ToStringSource extends DataSource[Int, String]{
    override def name = "ToString"

    override def fetchOne(id: Int): Query[Option[String]] = {
      Query.sync({
        println(s"[${Thread.currentThread.getId}] One ToString $id")
        Option(id.toString)
      })
    }
    override def fetchMany(ids: NonEmptyList[Int]): Query[Map[Int, String]] = {
      Query.sync({
        println(s"[${Thread.currentThread.getId}] Many ToString $ids")
        ids.toList.map(i => (i, i.toString)).toMap
      })
    }
  }

  def fetchString(n: Int): Fetch[String] = Fetch(n) // or, more explicitly: Fetch(n)(ToStringSource)

  implicit object LengthSource extends DataSource[String, Int]{
    override def name = "Length"

    override def fetchOne(id: String): Query[Option[Int]] = {
      Query.async((ok, _) => {
        println(s"[${Thread.currentThread.getId}] One Length $id")
        ok(Option(id.length))
      })
    }
    override def fetchMany(ids: NonEmptyList[String]): Query[Map[String, Int]] = {
      Query.async((ok, _) => {
        println(s"[${Thread.currentThread.getId}] Many Length $ids")
        ok(ids.toList.map(i => (i, i.length)).toMap)
      })
    }
  }

  def fetchLength(s: String): Fetch[Int] = Fetch(s)

}
