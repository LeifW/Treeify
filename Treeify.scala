def localName(res:UriRef) = sp.split(res.uri)(1)

g.triplesLike(subject,UriRef,Node) collect {
  case RdfTriple(_, RDF.Type, typ:UriRef) => "type" -> localName(typ)
  case RdfTriple(_, pred, value:SubjectNode) if !visited.contains(value)=>localName(pred) -> value // recurse o'er that thar value
  case RdfTriple(_, pred, TypedLiteral(value, datatype)) => (localName(pred), datatype match {
    case XSD.int | XSD.integer => value.toInt
    case XSD.float | XSD.double => value.toFloat
    case XSD.boolean => value.toBoolean
    case other => value
  })
  case RdfTriple(_, pred, PlainLiteral(value, _)) => localName(pred) -> value
}