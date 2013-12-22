package treeify

import org.scardf.{Turtle, NTriple, UriRef, Serializator}
//import org.scardf.jena.JenaSerializator
import java.io.{BufferedReader, InputStreamReader, FileReader}


object Main {
  def main( args:Array[String] ) {
    args match {
      case Array(filename, startNode) => 
        val file = new BufferedReader(new FileReader(filename))
        val graph = new Serializator(NTriple) readFrom file
        println( Treeify( graph/UriRef(startNode) ) )
      case Array(startNode) => 
        val stdin = new BufferedReader(new InputStreamReader(System.in))
        val graph = new Serializator(NTriple) readFrom stdin
        //println( Treeify( graph/UriRef(startNode) ) )
      case other => println("Usage: treeify <rdf_graph.nt> <start-node> ")
    }
  }
}
