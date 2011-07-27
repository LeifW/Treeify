import org.scardf._


import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class TreeifySpec extends FlatSpec with ShouldMatchers {
  "A graph" should "be represented as regular JSON" in {
    val List(leif, john, bill, name, knows, homepage) = 
      List("http://leif.com", "http://john.com", "http://bill.com", "http://schema.com/name", "http://schema.com/knows", "http://schema.com/homepage").map( UriRef(_) )
    val graph = Graph(
      leif -(
        name ->"Leif", 
        homepage -> UriRef("http://leif.com/home"),
        knows -> ObjSet(bill, john)
      ),
      john -name-> "John",
      bill -name-> "Bill"
    )

    Treeify(graph/leif) should equal (
      """{"name":["Leif"],"knows":[{"name":["Bill"],"uri":"http://bill.com"},{"name":["John"],"uri":"http://john.com"}],"homepage":[{"uri":"http://leif.com/home"}],"uri":"http://leif.com"}"""
    )
  }

}
