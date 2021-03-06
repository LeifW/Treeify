package treeify

import org.scardf._

import org.scalatest.{Matchers, FlatSpec}
import org.json4s.native.{renderJValue, prettyJson}

class TreeifySpec extends FlatSpec with Matchers {
  "A graph" should "be represented as a tree" in {
    val List(leif, john, bill, name, knows) =
      List("http://leif.com", "http://john.com", "http://bill.com", "http://schema.com/name", "http://schema.com/knows").map( UriRef(_) )
    val graph = Graph(
      leif -(name->"Leif", knows-> ObjSet(bill, john)),
      john-name->"John",
      bill-name->"Bill"
    )

    println(prettyJson(renderJValue(Treeify(graph/leif))))
    prettyJson(renderJValue(Treeify(graph/leif))) should equal (
      """{
        |  "@context":{
        |    "name":"http://schema.com/name",
        |    "knows":"http://schema.com/knows"
        |  },
        |  "@id":"http://leif.com",
        |  "knows":{
        |    "@id":"http://bill.com",
        |    "name":"Bill"
        |  },
        |  "knows":{
        |    "@id":"http://john.com",
        |    "name":"John"
        |  },
        |  "name":"Leif"
        |}""".stripMargin
    )
  }

}
