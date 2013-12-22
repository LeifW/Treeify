package treeify

import org.json4s.JsonAST._
import org.scardf._
//import scalaz.Writer
//import scalaz.syntax.writer._
import scalaz._
import Scalaz._
import effectful._
//import scalaz.syntax.applicative._
//import scalaz.syntax.monad._
//import scalaz.std.map._
//import scalaz.std.list._
import org.scardf.RdfTriple
import org.json4s.JsonAST.JString
import org.scardf.GraphNode

//import Scalaz._

object PropertyInfo extends Enumeration {
  val id = Value("@id")
  val typ = Value("@type")
}

object Treeify {

  type Context[A] = Writer[Map[String, UriRef], A]
  implicit val groupie = Semigroup.instance[UriRef]((u1, u2) => UriRef(u1.uri ++ u2.uri))

  val lastDelimiter = ".*[#/]".r
  def localName(res:UriRef): Context[String] = {
    val local = lastDelimiter.split(res.uri)(1)
    local set Map(local -> res)
  }
  //def apply(start:GraphNode): Context[JObject] = null

  def apply(start:GraphNode): Context[JObject] = {
    val graph = start.graph

    def treeify(subject:SubjectNode, visited:Set[SubjectNode]): Context[JObject] = {
      //val data: List[Context[(String, _ <: JValue)]] = graph.triplesLike(subject,UriRef,Node) collect {
      val data: Context[List[(String, JValue)]] = (graph.triplesLike(subject, UriRef, Node) collect {
        case RdfTriple(_, RDF.Type, typ: UriRef) => effectfully { ("@type" -> JString(localName(typ)!)) }
        //case RdfTriple(_, pred, value:SubjectNode) if !visited.contains(value)=> localName(pred) flatMap (_ -> treeify(value, visited + subject))
        case RdfTriple(_, pred, value: SubjectNode) if !visited.contains(value) =>
          (localName(pred) |@| treeify(value, visited + subject)).tupled
        /*
          for {
            localPred <- localName(pred)
            subtree <- treeify(value, visited + subject)
          } yield localPred -> subtree
          */
        case RdfTriple(_, pred, TypedLiteral(value, datatype)) => for {
          l <- localName(pred)
          d <- datatype match {
            case XSD.int | XSD.integer => JInt(value.toInt).point[Context]
            case XSD.float | XSD.double => JDouble(value.toDouble).point[Context]
            case XSD.boolean => JBool(value.toBoolean).point[Context]
            case other => JString(value).point[Context]
          }
        } yield l -> d
        /*
        case RdfTriple(_, pred, TypedLiteral(value, datatype)) => localName(pred) flatMap (_ ->
          datatype match {
            case XSD.int | XSD.integer => JInt(value.toInt).point[Context]
            case XSD.float | XSD.double => JDouble(value.toDouble).point[Context]
            case XSD.boolean => JBool(value.toBoolean).point[Context]
            case other => JString(value).point[Context]
        })
        */
        case RdfTriple(_, pred, PlainLiteral(value, _)) =>
          effectfully{ (localName(pred).! -> JString(value)) }
      }).toList.sequenceU

      /*
      JObject( subject match {
        //case Blank(_) => data
        //case UriRef(uri) => ("@id" -> JString(uri)) :: data
        //case UriRef(uri) => ("@id" -> JString(uri))
        case UriRef(uri) =>
          data.sequenceU //map (v => List("@id" -> JString(uri)) :: v)
      }).point[Context]
      */
      effectfully{ JObject(
        subject match {
          case Blank(_) => data!
          case UriRef(uri) =>
            ("@id" -> JString(uri)) :: data.!
        }
      )}
    }

    treeify(start.node, Set(start.node))
  }

}
