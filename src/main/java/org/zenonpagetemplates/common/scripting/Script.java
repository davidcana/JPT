package org.zenonpagetemplates.common.scripting;

import org.zenonpagetemplates.common.exceptions.EvaluationException;

/**
 * <p>
 *   Interface that defines methods needed by any Script class, such as
 *   get the script String, evaluate it and get the evaluator instance.
 * </p>
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public interface Script {
	
	/**
	 * Return the script string
	 * 
	 * @return the script string
	 */
	public String getScript();
	
	
	/**
	 * Evaluate the script using the given evaluation helper
	 * 
	 * @param helper
	 * @return the evaluation
	 * @throws EvaluationException
	 */
	public Object evaluate( EvaluationHelper helper ) throws EvaluationException;
	
	
	/**
	 * Return the evaluator instance to use for evaluating
	 * 
	 * @return the evaluator instance
	 */
	public Evaluator getEvaluator();
}
