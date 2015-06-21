package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;

/**
 * <p>
 *   Evaluates a static call expression.
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
public class StaticCallExpression implements FirstPathToken {

	private static final long serialVersionUID = -8225990345354241953L;
	
	private String expression;
	
	
	public StaticCallExpression(){}

	public StaticCallExpression(String expression){
		this.expression = expression;
	}


	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.expression );
	}
	
	static public Object evaluate( String expression ) throws EvaluationException {
		
		try {
			return new StaticCall( Class.forName( expression ) );
			
		} catch ( ClassNotFoundException e ) {
			throw new EvaluationException( e );
		}
	}
	
	static public StaticCallExpression generate( String expression ){
		
		try {
			evaluate( expression );
			return new StaticCallExpression( expression );
			
		} catch ( PageTemplateException e ) {}
		
		return null;
	}

	@Override
	public String getStringExpression() {
		return this.expression;
	}
	
	@Override
    public String toString(){
    	return this.expression;
    }
}
