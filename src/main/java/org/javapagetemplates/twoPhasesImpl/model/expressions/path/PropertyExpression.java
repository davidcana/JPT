package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;

import bsh.Interpreter;

/**
 * <p>
 *   Evaluates a property access from an expression.
 * </p>
 * 
 * 
 *  Java Page Templates
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class PropertyExpression implements NextPathToken {

	private static final long serialVersionUID = 7312431076703138158L;
	
	private String propertyName; 

	
	public PropertyExpression(){}

	public PropertyExpression(String propertyName){
		this.propertyName = propertyName;
	}


	public String getPropertyName() {
		return this.propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Object evaluate(Object parent, Interpreter beanShell) 
			throws ExpressionEvaluationException {
		return evaluate(parent, this.propertyName);
	}
	
    private static final Object[] emptyArray = new Object[0];
    @SuppressWarnings("rawtypes")
	public static final Object evaluate( Object object, String name ) 
        throws ExpressionEvaluationException {
    	
        try {
            // If object is a Map, use it like a dictionary and 
            // use property name as key
            if ( object instanceof Map ) {
                return ((Map)object).get( name );
            }

            // Use Bean introspection to get property of an object
            BeanInfo beanInfo = Introspector.getBeanInfo( object.getClass() );
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            for ( int i = 0; i < properties.length; i++ ) {
                if ( properties[i].getName().equals( name ) ) {
                    Method reader = properties[i].getReadMethod();
                    if ( reader == null ) {
                        throw new ExpressionEvaluationException
                            ( "property '" + name + "' of " + object.getClass().getName() + " can't be read" );
                    }
                    return reader.invoke( object, emptyArray );
                }
            }
        }
        catch( Exception e ) {
            throw new ExpressionEvaluationException(
            		e.getCause() != null? e.getCause(): e);
        }
        throw new ExpressionEvaluationException( "no such property '" + name + "' of " + object.getClass().getName() );
    }

    public static final PropertyExpression generate( String propertyName ) 
	        throws ExpressionSyntaxException {
		return new PropertyExpression(propertyName);
	}
	
	@Override
	public String getStringExpression() {
		return this.propertyName;
	}
    
}
