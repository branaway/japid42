/**
 *
 */
package cn.bran.play.routing

import java.lang.reflect.Method
import play.core.j.{ JavaAction, JavaActionAnnotations}
import play.mvc.{ Action => JAction, Result => JResult, SimpleResult => JSimpleResult }
import play.libs.F.{ Promise => JPromise }

class JavaActionBridge(targetClass: Class[_], meth: Method, resultBuilder: ResultBuilder) extends play.core.j.JavaAction with play.api.mvc.Handler {
  val annotations = new JavaActionAnnotations(targetClass, meth)
  val parser = annotations.parser
  def invocation: JPromise[JResult] = {
    JPromise.pure(resultBuilder.create())
  }
}

