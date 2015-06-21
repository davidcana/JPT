package org.javapagetemplates.common.scripting.groovy;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

import org.javapagetemplates.common.scripting.Evaluator;
import org.javapagetemplates.common.scripting.Script;
import org.javapagetemplates.common.scripting.ScriptImpl;

/**
 * <p>
 *   Simple class that implements Script for Groovy.
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
public class GroovyScript extends ScriptImpl implements Serializable, Script {

	private static final long serialVersionUID = 5304431099127600167L;

	public GroovyScript( String script ) {
    	super( script );
    }
    
    public GroovyScript( Reader reader ) throws IOException {
    	super( reader );
    }

	@Override
	public Evaluator getEvaluator() {
		return GroovyEvaluator.getInstance();
	}
    
}