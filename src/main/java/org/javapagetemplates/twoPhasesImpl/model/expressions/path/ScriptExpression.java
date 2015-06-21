package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.common.scripting.Script;

/**
 * <p>
 *   Evaluates a script.
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
 * @version $Revision: 1.0 $
 */
public class ScriptExpression implements FirstPathToken, NextPathToken {

	private static final long serialVersionUID = -4903031873047204665L;
	
	private Script script;
	
	
	public ScriptExpression(){}

	public ScriptExpression( Script script ){
		this.script = script;
	}


	public Script getScript() {
		return this.script;
	}

	public void setScript( Script script ) {
		this.script = script;
	}

	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.script, evaluationHelper );
	}

	static public ScriptExpression generate( Object object ){
		
        if ( object instanceof Script ) {
        	Script script = ( Script ) object;
            return new ScriptExpression( script );
        }
		
        return null;
	}
	
	@Override
	public String getStringExpression() {
		return this.script.toString();
	}

	@Override
	public Object evaluate( Object parent, EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.evaluate( evaluationHelper );
	}
	
	@Override
    public String toString(){
    	return this.script.toString();
    }
}
