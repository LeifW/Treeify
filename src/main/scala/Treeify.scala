import org.scardf._

object Treeify {

def localName(res:UriRef) = ".*[#/]".r.split(res.uri)(1)

def apply(start:GraphNode) = {
  val graph = start.graph

  def treeify(subject:SubjectNode, visited:Set[SubjectNode]):Any = {
    val data = graph.triplesLike(subject,UriRef,Node) collect {
      case RdfTriple(_, RDF.Type, typ:UriRef) => "type" -> localName(typ)
      case RdfTriple(_, pred, value:SubjectNode) if !visited.contains(value)=>localName(pred) -> treeify(value, visited + subject) 
      case RdfTriple(_, pred, TypedLiteral(value, datatype)) => (localName(pred), datatype match {
        case XSD.int | XSD.integer => value.toInt
        case XSD.float | XSD.double => value.toFloat
        case XSD.boolean => value.toBoolean
        case other => value
      })
      case RdfTriple(_, pred, PlainLiteral(value, _)) => localName(pred) -> value
    } groupBy (_._1) mapValues (set=>
        if (set.size == 1)
          set.head._2
        else
          set.map(_._2)
      )

    subject match {
      case UriRef(uri) if data.isEmpty => uri
      case UriRef(uri) => data + ("uri" -> uri)
      case Blank(_) => data
    }
  }

  treeify(start.node, Set(start.node))
}

}
