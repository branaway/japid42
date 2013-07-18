/**
 * 
 */
package cn.bran.play.routing

import java.lang.reflect.Method

class JavaActionBridge (targetClass:Class[_], meth: Method, resultBuilder: ResultBuilder)  extends play.core.j.JavaAction with play.api.mvc.Handler {
    def invocation: play.mvc.Result = {
		resultBuilder.create()
	}
    def controller: Class[_] = targetClass
    override def method: java.lang.reflect.Method = meth
}

