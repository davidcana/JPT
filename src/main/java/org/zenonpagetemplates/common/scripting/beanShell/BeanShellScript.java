package org.zenonpagetemplates.common.scripting.beanShell;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

import org.zenonpagetemplates.common.scripting.Evaluator;
import org.zenonpagetemplates.common.scripting.Script;
import org.zenonpagetemplates.common.scripting.ScriptImpl;

/**
 * <p>
 *   Simple class that implements Script for Bean Shell.
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
public class BeanShellScript extends ScriptImpl implements Serializable, Script {

	private static final long serialVersionUID = -4711764237340508120L;
    
    public BeanShellScript( String script ) {
    	super( script );
    }
    
    public BeanShellScript( Reader reader ) throws IOException {
    	super( reader );
    }

	@Override
	public Evaluator getEvaluator() {
		return BeanShellEvaluator.getInstance();
	}
    
}