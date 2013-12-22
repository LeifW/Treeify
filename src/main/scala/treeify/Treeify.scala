package treeify

import effectful._
import scalaz.{Writer, Semigroup}
import scalaz.syntax.writer._
import scalaz.syntax.applicative._
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.map._
import org.scardf._
import org.json4s.JsonAST._

object PropertyInfo extends Enumeration {
  val id = Value("@id")
  val typ = Value("@type")
}

object Treeify {

  type Context[A] = Writer[Map[String, UriRef], A]
  implicit val groupie = Semigroup.instance[UriRef]{ case (UriRef(u1), UriRef(u2)) =>
    if (u1 != u2) sys.error(u1 + " did not equal " + u2)
    UriRef(u1)
  }

  val lastDelimiter = ".*[#/]".r
  def localName(res:UriRef): Context[String] = {
    val local = lastDelimiter.split(res.uri)(1)
    local set Map(local -> res)
  }

  def apply(start:GraphNode) = {
    val graph = start.graph

    def treeify(subject:SubjectNode, visited:Set[SubjectNode]): Context[JObject] =
      (graph.triplesLike(subject, UriRef, Node) collect {
        case RdfTriple(_, RDF.Type, typ: UriRef) => effectfully { ("@type" -> JString(localName(typ)!)) }
        case RdfTriple(_, pred, value: SubjectNode) if !visited.contains(value) =>
           effectfully { localName(pred).! -> treeify(value, visited + subject).! }
        case RdfTriple(_, pred, TypedLiteral(value, datatype)) => for {
          l <- localName(pred)
          d <- datatype match {
            case XSD.int | XSD.integer => JInt(value.toInt).point[Context]
            case XSD.float | XSD.double => JDouble(value.toDouble).point[Context]
            case XSD.boolean => JBool(value.toBoolean).point[Context]
            case other => JString(value).point[Context]
          }
        } yield l -> d
        case RdfTriple(_, pred, PlainLiteral(value, _)) =>
          effectfully{ (localName(pred).! -> JString(value)) }
      }).toList.sequenceU map {data =>
        JObject(
          subject match {
            case Blank(_) => data
            case UriRef(uri) =>
              ("@id" -> JString(uri)) :: data
          }
        )
      }

    treeify(start.node, Set(start.node)).run
  }

}
