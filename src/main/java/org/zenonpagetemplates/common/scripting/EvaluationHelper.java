package org.zenonpagetemplates.common.scripting;

import java.util.Map;

import org.zenonpagetemplates.common.exceptions.EvaluationException;

/**
 * <p>
 *   Interface that defines a helper class to set, unset, get and check existence
 *   of any kind of variables. 
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

public interface EvaluationHelper {

	/**
	 * Set the value of a variable
	 * 
	 * @param name the name of the variable
	 * @param value the value of the variable
	 * @throws EvaluationException If an error occurs
	 */
	public void set( String name, Object value ) throws EvaluationException;
	
	
	/**
	 * Unset a variable
	 * 
	 * @param name the name of the variable
	 * @throws EvaluationException If an error occurs
	 */
	public void unset( String name ) throws EvaluationException;
	
	
	/**
	 * Get the value of a variable
	 * 
	 * @param name the name of the variable
	 * @return the value of a variable
	 * @throws EvaluationException If an error occurs
	 */
	public Object get( String name ) throws EvaluationException;
	
	
	/**
	 * Check if a variable exists 
	 * 
	 * @param name the name of the variable
	 * @return <code>true</code> if the variable exists, <code>false</code> otherwise
	 */
	public boolean has( String name );
	
	
	/**
	 * Return a map with all the variables
	 * 
	 * @return a map with all the variables
	 * @throws EvaluationException If an error occurs
	 */
	public Map<String, Object> getVariables() throws EvaluationException;
	
	
	/**
	 * Return the class that stores the variables
	 * 
	 * @return the class that stores the variables
	 */
	@SuppressWarnings("rawtypes")
	public Class getClassType();
	
}
