package org.javapagetemplates.common.exceptions;

import java.lang.reflect.InvocationTargetException;

/**
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
public class ExpressionEvaluationException extends PageTemplateException {

    private String expression;
    private String source;
    
    private static final long serialVersionUID = 2059091942240330015L;

    public ExpressionEvaluationException() {
        super();
    }

    public ExpressionEvaluationException( String message ) {
        super( message );
    }

    public ExpressionEvaluationException( String message, Throwable cause ) {
        super( message, cause );
    }
    
    public ExpressionEvaluationException( InvocationTargetException exception ) {
        super( exception.getTargetException() );
    }
    
    public ExpressionEvaluationException( Throwable cause ) {
        super( cause );
    }
    
    public ExpressionEvaluationException( Throwable cause, String expression ) {
        super( cause );
        this.expression = expression;
    }

    public void setExpression( String expression ) {
    	
        // Do not clobber first expression
        if ( this.expression == null ) {
            this.expression = expression;
        }
    }
    
    public String getExpression() {
        return this.expression;
    }

	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		
        // Do not clobber first expression
        if ( this.source == null ) {
        	this.source = source;
        }
	}
	
	public void setInfo(String expression, String source){
		this.setExpression(expression);
		this.setSource(source);
	}
	
	@Override
    public String getMessage() {
    	
    	StringBuilder sb = new StringBuilder();
        
        if ( this.source != null ) {
        	sb.append("[ source = " + this.source + " ] ");
        }
        
        if ( this.expression != null ) {
        	sb.append("[ expression = " + this.expression + " ] ");
        }
        
        sb.append( "[ description = " + super.getMessage() + " ]" );
        
        return sb.toString();
    }
    
}
