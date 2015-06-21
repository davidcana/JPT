package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.scripting.EvaluationHelper;

/**
 * <p>
 *   Evaluates a class expression.
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
 * @version $Revision: 1.0 $
 */
public class ClassExpression implements FirstPathToken {

	private static final long serialVersionUID = -4903031873047204665L;
    private static final String CLASS_EXTENSION = ".class";
    
	private String className;
	
	
	public ClassExpression(){}

	public ClassExpression( String className ){
		this.className = className;
	}


	public String getClassName() {
		return this.className;
	}

	public void setClassName( String className ) {
		this.className = className;
	}

	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.className );
	}
	
	static public Object evaluate( String className ) throws EvaluationException {
		
		try {
			return Class.forName( className );
			
		} catch ( ClassNotFoundException e ) {
			throw new EvaluationException( e );
		}
	}
	
	static public ClassExpression generate( String token ){
		
		try {
            if ( token.endsWith( CLASS_EXTENSION ) ) {
                String className = token.substring( 0, token.length() - CLASS_EXTENSION.length() );
                Class.forName( className );
    			return new ClassExpression( className );
            }
			
		} catch ( Exception e ) {}
		
		return null;
	}

	@Override
	public String getStringExpression() {
		return this.className;
	}
	
	@Override
    public String toString(){
    	return this.className;
    }
	
}
