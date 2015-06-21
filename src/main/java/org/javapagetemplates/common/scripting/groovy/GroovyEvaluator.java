package org.javapagetemplates.common.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.common.scripting.Script;
import org.javapagetemplates.common.scripting.Evaluator;

/**
 * <p>
 *   Simple class that implements Evaluator for Groovy.
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
public class GroovyEvaluator implements Evaluator {
	
	static private GroovyEvaluator instance;
	
	private GroovyEvaluator(){}
	
	
	@Override
	public Object evaluate( Script script, EvaluationHelper helper ) throws EvaluationException {
		
		return this.evaluate( script.getScript(), helper );	
	}
	
	
	@Override
	public Object evaluate( String expression, EvaluationHelper helper ) throws EvaluationException {
    	
    	try {
    		GroovyShell shell = new GroovyShell( 
    				this.getBinding( helper ) ); 
    		return shell.evaluate( expression );
    		
		} catch ( Exception e ) {
			throw new EvaluationException( e, expression );
		}
	}
	
	
	private Binding getBinding( EvaluationHelper helper ) throws EvaluationException {
		
		if ( helper instanceof GroovyEvaluationHelper ){
			GroovyEvaluationHelper gHelper = ( GroovyEvaluationHelper ) helper;
			return gHelper.getBinding();
		}

		return new Binding( helper.getVariables() );
	}
	
	
	@Override
	public Script createScript( URL resource ) throws IOException {
		
		return new GroovyScript( 
        		new InputStreamReader( resource.openStream() ) );
	}
	

	@Override
	public EvaluationHelper createEvaluationHelper() throws EvaluationException {
		return new GroovyEvaluationHelper();
	}
	
	
    public static GroovyEvaluator getInstance(){

        if ( instance == null ){
            instance = new GroovyEvaluator();
        }

        return instance;
    }

}
