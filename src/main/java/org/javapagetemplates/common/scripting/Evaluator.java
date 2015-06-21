package org.javapagetemplates.common.scripting;

import java.io.IOException;
import java.net.URL;

import org.javapagetemplates.common.exceptions.EvaluationException;

/**
 * <p>
 *   Interface that defines methods to evaluate expressions and scripts. It also 
 *   defines methods to create scripts and evaluation helpers.
 * </p>
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

public interface Evaluator {
	
	/**
	 * Evaluate an expression using a given evaluation helper
	 * 
	 * @param expression
	 * @param helper
	 * @return the evaluation
	 * @throws EvaluationException
	 */
	public Object evaluate( String expression, EvaluationHelper helper ) throws EvaluationException;
	
	
	/**
	 * Evaluate an script using a given evaluation helper
	 * 
	 * @param script
	 * @param helper
	 * @return the evaluation
	 * @throws EvaluationException
	 */
	public Object evaluate( Script script, EvaluationHelper helper ) throws EvaluationException;

	
	/**
	 * Create a script
	 * 
	 * @param resource
	 * @return a new Script
	 * @throws IOException
	 */
	public Script createScript( URL resource ) throws IOException;
	
	
	/**
	 * Create an evaluation helper
	 * 
	 * @return a new EvaluationHelper
	 * @throws EvaluationException
	 */
	public EvaluationHelper createEvaluationHelper() throws EvaluationException;
}
