package org.javapagetemplates.twoPhasesImpl.model.expressions.path.literals;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.FirstPathToken;

/**
 * <p>
 *   Represents a boolean literal (<code>true</code> or <code>false</code>).
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
public class BooleanLiteralExpression implements FirstPathToken {

	private static final long serialVersionUID = -4262423649337574985L;
	
	private Boolean literal;
	
	
	public BooleanLiteralExpression(){}
	
	public BooleanLiteralExpression( Boolean literal ){
		this.literal = literal;
	}

	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.literal;
	}
	
	static public Object evaluate( String expression, EvaluationHelper evaluationHelper ) throws PageTemplateException {
		return expression.substring( 1, expression.length() - 1 );
	}
	
	static public BooleanLiteralExpression generate(String expression){
		
        if ( TwoPhasesPageTemplate.TRUE_STRING.equals( expression ) ) {
            return new BooleanLiteralExpression( Boolean.TRUE );
        }
        else if ( TwoPhasesPageTemplate.FALSE_STRING.equals( expression ) ) {
            return new BooleanLiteralExpression( Boolean.FALSE );
        }
        
        return null;
	}

	@Override
	public String getStringExpression() {
		return this.literal.toString();
	}
	
	@Override
	public String toString(){
		return '\'' + this.literal.toString() + '\'' ;
	}
}
