package treeify

import org.scardf.UriRef
import org.json4s.JsonAST.JValue

/*
// Like a Writer Monad, I guess.
case class Context[A](context: Map[String, UriRef], a: A) {

  def map[B](f: A => B): Context[B] = copy( a = f(a) )
  def leftMap(f: Map[String, UriRef] => Map[String, UriRef]): Context[A] =
    copy( context = f(context))

  def flatMap[B](f: A => Context[B]): Context[B] =
    f(a).leftMap(_ ++ context)

  //def map2[B, C, D](other)
  def |@|[B, C](other: Context[B])(f:(A, B) => C) = for {
    x <- this
    y <- other
  } yield f(x, y)
  /*
  {

    // Maybe just pull in lenses, do:
    // f(a).copy( context == context ++ _.context)
    // basically, f(a).leftMap(_ ++ context)
    val Context(newContext, newB) =
    Context(context ++ newContext, newB)
  }
  */


}

class ApplicativeBuilder
*/
