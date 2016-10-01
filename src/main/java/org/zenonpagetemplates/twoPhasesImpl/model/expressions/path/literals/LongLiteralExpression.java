package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path.literals;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.EvaluableToNumber;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.path.FirstPathToken;

/**
 * <p>
 *   Represents a long literal (as <code>5l</code>).
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
public class LongLiteralExpression implements EvaluableToNumber, FirstPathToken {

	private static final long serialVersionUID = -3138512000373794137L;
	
	private Long literal;
	
	
	public LongLiteralExpression(){}
	
	public LongLiteralExpression( Long literal ){
		this.literal = literal;
	}

	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.literal;
	}
	
	@Override
	public Number evaluateToNumber( EvaluationHelper evaluationHelper )
			throws EvaluationException {
		return this.literal;
	}
	
	static public Object evaluate( String expression, EvaluationHelper evaluationHelper ) throws PageTemplateException {
		return new Long( expression.substring( 0, expression.length() - 1 ) );
	}
	
	static public LongLiteralExpression generate(String expression){
		
		if ( expression.endsWith( TwoPhasesPageTemplate.LONG_LITERAL_SUFFIX ) ) {
            try {
                return new LongLiteralExpression(
                		new Long( expression.substring( 0, expression.length() - 1 ) ));
            } catch( NumberFormatException e ) {}
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
