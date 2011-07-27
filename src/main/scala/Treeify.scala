import org.scardf._

object Treeify {

def localName(res:UriRef) = ".*[#/]".r.split(res.uri)(1)

def str(s:String) = "\"" + s + "\""
def obj(m:Map[String,String]) = m.map(Function.tupled(str(_) +":"+ _)).mkString("{",",","}")

def apply(start:GraphNode) = {
  val graph = start.graph

  def treeify(subject:SubjectNode, visited:Set[SubjectNode]):String = {
    val data = graph.triplesLike(subject,UriRef,Node) collect {
      case RdfTriple(_, RDF.Type, typ:UriRef) => ("type", str(localName(typ)))
      case RdfTriple(_, pred, value:SubjectNode) if !visited.contains(value) => (localName(pred), treeify(value, visited + subject))
      case RdfTriple(_, pred, TypedLiteral(value, datatype)) => (localName(pred), datatype match {
        case XSD.int | XSD.integer | XSD.float | XSD.double | XSD.decimal | XSD.boolean => value
        case other => str(value) 
      })
      case RdfTriple(_, pred, PlainLiteral(value, _)) => (localName(pred), str(value))
    } groupBy (_._1) mapValues (_.map(_._2).mkString("[",",","]"))

    obj(subject match {
      case UriRef(uri) => data + ("uri" -> str(uri))
      case Blank(_) => data
    })
  }

  treeify(start.node, Set(start.node))
}

}
