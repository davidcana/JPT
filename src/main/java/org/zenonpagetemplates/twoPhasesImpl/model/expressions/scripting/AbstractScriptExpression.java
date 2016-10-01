package org.zenonpagetemplates.twoPhasesImpl.model.expressions.scripting;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.common.scripting.Evaluator;
import org.zenonpagetemplates.twoPhasesImpl.ZPTContext;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpressionImpl;

/**
 * <p>
 *   Evaluates a script expression using any EvaluationHelper.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
abstract public class AbstractScriptExpression extends ZPTExpressionImpl {
	
	private static final long serialVersionUID = -6694380470011919648L;
	
	protected String scriptExpression;
	
	
	public AbstractScriptExpression(){}
	public AbstractScriptExpression( String stringExpression, String scriptExpression ){
		super( stringExpression );
		
		this.scriptExpression = scriptExpression;
	}

	
	public String getScriptExpression() {
		return this.scriptExpression;
	}

	
	public void setScriptExpression( String scriptExpression ) {
		this.scriptExpression = scriptExpression;
	}
	

	static public Object evaluate( String scriptExpression, EvaluationHelper evaluationHelper, Evaluator evaluator ) 
			throws EvaluationException {
    	
		if ( ! ZPTContext.getInstance().isScriptExpressionsOn() ){
    		throw new EvaluationException( "Script expressions not allowed." );
    	}
    	
		return evaluator.evaluate( 
        		ZPTContext.getInstance().restoreAmpersandsToScriptExpression( scriptExpression ),
				evaluationHelper );	
	}
}
