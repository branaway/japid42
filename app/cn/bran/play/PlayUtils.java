/**
 * 
 */
package cn.bran.play;

import java.util.ArrayList;
import java.util.List;

import play.data.Form.Field;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.libs.F.Tuple;

/**
 * @author bran
 *
 */
public class PlayUtils {
    public static List<String> getErrorMessages(Field f){
    	List<String> ret = new ArrayList<String>();
    	List<ValidationError> errors = f.errors();
    	if (errors != null) {
    		for (ValidationError ve: errors) {
    			List<Object> args = ve.arguments();
				ret.add(Messages.get(ve.message(), args.toArray()));
    		}
    	}
    	return ret;
    }
    
    public static List<String> getConstraintMessages(Field f){
    	List<String> ret = new ArrayList<String>();
    	List<Tuple<String,List<Object>>> errors = f.constraints();
    	if (errors != null) {
    		for (Tuple<String, List<Object>> ve: errors) {
    			List<Object> args = ve._2;
    			ret.add(Messages.get(ve._1, args.toArray()));
    		}
    	}
    	return ret;
    }
}
