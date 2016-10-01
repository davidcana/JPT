package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path.literals;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.path.FirstPathToken;

/**
 * <p>
 *   Represents a string literal (as <code>'string literal'</code>).
 * </p>
 * 
 * 
 *  Zenon Page Templates
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
public class StringLiteralExpression implements FirstPathToken {

	private static final String STRING_DELIMITER = "'";
	private static final long serialVersionUID = -8432766548104903105L;
	private static final String VOID_STRING = "";
	
	private String literal;
	
	
	public StringLiteralExpression(){}
	
	public StringLiteralExpression(String literal){
		this.literal = literal;
	}

	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.literal;
	}
	
	static public Object evaluate( String expression, EvaluationHelper evaluationHelper ) throws PageTemplateException {
		return expression.substring( 1, expression.length() - 1 );
	}
	
	static public StringLiteralExpression generate(String expression){
		
        if ( expression.startsWith( STRING_DELIMITER ) 
        		&& expression.endsWith( STRING_DELIMITER ) ) {
        	return new StringLiteralExpression(
        			expression.substring( 1, expression.length() - 1 ));
        }
        
        return null;
	}
	
	@Override
	public String getStringExpression() {
		return this.literal;
	}
	
	@Override
	public String toString(){
		
		if ( this.literal.isEmpty() ){
			return VOID_STRING;
		}
		
		return '\'' + this.literal + '\'' ;
	}
}
