package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.model.expressions.Nocallable;

/**
 * <p>
 *   Evaluates a var name.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class VarNameExpression implements FirstPathToken {

	private static final long serialVersionUID = -4903031873047204665L;
	
	private String varName;
	
	
	public VarNameExpression(){}

	public VarNameExpression( String varName ){
		this.varName = varName;
	}


	public String getVarName() {
		return this.varName;
	}

	public void setVarName( String varName ) {
		this.varName = varName;
	}

	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.varName, evaluationHelper );
	}
	
	static public Object evaluate( String varName, EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		Object result = evaluationHelper.get( varName );
		
		if ( result instanceof Nocallable ){
			Nocallable nocallable = ( Nocallable ) result;
			return nocallable.evaluateNocallable( evaluationHelper );
		}
		
		return result;
	}
	
	static public VarNameExpression generate( String varName ){
		return new VarNameExpression( varName );
	}
	
	@Override
	public String getStringExpression() {
		return this.varName;
	}
	
	@Override
    public String toString(){
    	return this.varName;
    }
}
