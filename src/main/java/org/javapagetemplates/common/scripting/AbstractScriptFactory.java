package org.javapagetemplates.common.scripting;

import java.io.IOException;
import java.net.URL;

import org.javapagetemplates.common.AbstractJPTContext;
import org.javapagetemplates.common.exceptions.EvaluationException;

/**
 * <p>
 *   Abstract class to make it easy creation of any kind of scripts depending
 *   on its extension.
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

abstract public class AbstractScriptFactory {
	
	/**
	 * Default constructor
	 * 
	 */
	protected AbstractScriptFactory(){}
	
	
	/**
	 * Create a Script instance. The specific class depends on the extension defined in the path.
	 * 
	 * @param path
	 * @param resource
	 * @return the new Script instance
	 * @throws IOException
	 * @throws EvaluationException
	 */
	public Script createScript( String path, URL resource ) throws IOException, EvaluationException {
		
		Evaluator evaluator = this.getContext().resolveScriptEvaluator( path );
		
		return evaluator.createScript( resource );
	}
	
	
	/**
	 * Create an EvaluationHelper instance. The specific class depends on the expression evaluator
	 * defined in JPT context.
	 * 
	 * @return the new EvaluationHelper instance
	 * @throws EvaluationException
	 */
	public EvaluationHelper createEvaluationHelper() throws EvaluationException {
		return this.getContext().getExpressionEvaluator().createEvaluationHelper();
	}
	
	
	/**
	 * Returns the JPTContext
	 * 
	 * @return the JPTContext
	 */
	abstract protected AbstractJPTContext getContext();
	
}
