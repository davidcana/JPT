package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class GeneralPathExpressionItem implements JPTExpression {

	private static final long serialVersionUID = -1840080964134073908L;
	
	private JPTExpression jptExpression;
	
	public GeneralPathExpressionItem(){}
	public GeneralPathExpressionItem(String stringExpression) throws ExpressionSyntaxException {
		this.jptExpression = ExpressionUtils.generate(stringExpression);
	}
	public GeneralPathExpressionItem(JPTExpression jptExpression){
		this.jptExpression = jptExpression;
	}

	
	public JPTExpression getJptExpression() {
		return jptExpression;
	}

	public void setJptExpression(JPTExpression jptExpression) {
		this.jptExpression = jptExpression;
	}
	
	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return this.jptExpression.evaluate(beanShell);
	}

	@Override
	public String getStringExpression() {
		return this.jptExpression.getStringExpression();
	}
}
