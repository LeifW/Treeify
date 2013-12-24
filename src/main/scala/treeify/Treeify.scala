package treeify

import effectful._
import scalaz.{\&/, Writer, Semigroup, Equal}
import scalaz.syntax.writer._
import scalaz.syntax.applicative._
import scalaz.syntax.traverse._
import scalaz.std.list._
import scalaz.std.map._
import scalaz.syntax.equal._
import org.scardf._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import scalaz.\&/.{Both, That, This}

/*
object PropertyInfo extends Enumeration {
  val id = Value("@id")
  val typ = Value("@type")
  val context = Value("@context")
}
*/

sealed abstract class PropertyInfo(label: String)
case class Id(i: UriRef) extends PropertyInfo("@id")
case class Typ(t: UriRef) extends PropertyInfo("@type")

object Treeify {

  type Context[A] = Writer[Map[String, Id \&/ Typ], A]
  implicit val groupie = Semigroup.instance[UriRef]{ case (UriRef(u1), UriRef(u2)) =>
    if (u1 != u2) sys.error(u1 + " did not equal " + u2)
    UriRef(u1)
  }

  implicit val idEq = Equal.equalA[Id]
  implicit val typEq = Equal.equalA[Typ]

  implicit val theseAdd = Semigroup.instance[Id \&/ Typ] {
    case (This(t1), That(t2)) => Both(t1, t2)
    case (t1, t2) if t1 === t2 => t1
    case (Both(a, b), o) if o.bimap(_ === a, _ === b).foldRight(true)(_ && _) => Both(a, b)
    case (o, Both(a, b)) if o.bimap(_ === a, _ === b).foldRight(true)(_ && _) => Both(a, b)
    case (t1, t2) => sys.error("Conflicting information about document: " + t1 + " & " + t2)
  }

  val lastDelimiter = ".*[#/]".r
  def localName(res:UriRef): Context[String] = {
    val local = lastDelimiter.split(res.uri)(1)
    local set Map(local -> This(Id(res)))
  }

  def apply(start:GraphNode) = {
    val graph = start.graph

    def treeify(subject:SubjectNode, visited:Set[SubjectNode]): Context[JObject] =
      (graph.triplesLike(subject, UriRef, Node) collect {
        case RdfTriple(_, RDF.Type, typ: UriRef) =>
          effectfully( "@type" -> JString(localName(typ)!) )
        case RdfTriple(_, pred, value: SubjectNode) if !visited.contains(value) =>
          effectfully( localName(pred).! -> treeify(value, visited + subject).! )
        case RdfTriple(_, pred, TypedLiteral(value, datatype)) => for {
          l <- localName(pred)
          d <- datatype match {
            case XSD.int | XSD.integer => JInt(value.toInt).point[Context]
            case XSD.float | XSD.double => JDouble(value.toDouble).point[Context]
            case XSD.boolean => JBool(value.toBoolean).point[Context]
            case other => JString(value) set Map(l -> That[Id, Typ]( Typ(other) ) )
          }
        } yield l -> d
        case RdfTriple(_, pred, PlainLiteral(value, _)) =>
          effectfully( localName(pred).! -> JString(value) )
      }).toList.sequenceU map {data =>
        JObject(
          subject match {
            case Blank(_) => data
            case UriRef(uri) => ("@id" -> JString(uri)) :: data
          }
        )
      }

    val (context, JObject(values)) = treeify(start.node, Set(start.node)).run
    JObject(
      JField("@context", context mapValues {
        case This(Id(UriRef(id))) => JString(id)
        case Both(Id(UriRef(id)), Typ(UriRef(typ))) => JObject(
          "@id" -> id,
          "@type" -> typ
        )
      }) :: values
    )
  }

}
