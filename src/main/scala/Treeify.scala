import org.json4s.JsonAST.{JObject, JString, JDouble, JInt, JBool}
import org.scardf._

object Treeify {

  val lastDelimiter = ".*[#/]".r
  def localName(res:UriRef) = lastDelimiter.split(res.uri)(1)

  def apply(start:GraphNode): JObject = {
    val graph = start.graph

    def treeify(subject:SubjectNode, visited:Set[SubjectNode]): JObject = {
      val data = graph.triplesLike(subject,UriRef,Node) collect {
        case RdfTriple(_, RDF.Type, typ:UriRef) => "@type" -> JString(localName(typ))
        case RdfTriple(_, pred, value:SubjectNode) if !visited.contains(value)=> localName(pred) -> treeify(value, visited + subject)
        case RdfTriple(_, pred, TypedLiteral(value, datatype)) => (localName(pred), datatype match {
          case XSD.int | XSD.integer => JInt(value.toInt)
          case XSD.float | XSD.double => JDouble(value.toDouble)
          case XSD.boolean => JBool(value.toBoolean)
          case other => JString(value)
        })
        case RdfTriple(_, pred, PlainLiteral(value, _)) => localName(pred) -> JString(value)
      } toList

      JObject( subject match {
        case Blank(_) => data
        case UriRef(uri) => ("@id" -> JString(uri)) :: data
      })
    }

    treeify(start.node, Set(start.node))
  }

}
