/**
 *
 */
package cn.bran.play.routing

import java.lang.reflect.Method
import play.core.j.{ JavaAction, JavaActionAnnotations}

class JavaActionBridge(targetClass: Class[_], meth: Method, resultBuilder: ResultBuilder) extends play.core.j.JavaAction with play.api.mvc.Handler {
  val annotations = new JavaActionAnnotations(targetClass, meth)
  val parser = annotations.parser
  def invocation: play.mvc.Result = {
    resultBuilder.create()
  }
}

