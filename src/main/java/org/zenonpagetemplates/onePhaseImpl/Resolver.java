package org.zenonpagetemplates.onePhaseImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.Script;

import java.io.IOException;

/**
 * <p>
 *   This class makes it easy to find resources such as templates
 *   and scripts.
 * </p>
 * 
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.5 $
 */
public abstract class Resolver {

	// Map of resources called by this template
    Map<String, OnePhasePageTemplate> templates = new HashMap<String, OnePhasePageTemplate>();
	Map<String, Script> scripts = new HashMap<String, Script>();
    
    public abstract URL getResource( String path ) throws MalformedURLException;
    
	public OnePhasePageTemplate getPageTemplate( String path ) 
        throws PageTemplateException, MalformedURLException {
		
		OnePhasePageTemplate template = this.templates.get( path );
        if ( template == null ) {
            URL resource = getResource( path );
            if ( resource != null ) {
                template = new PageTemplateImpl( resource );
                this.templates.put( path, template );
            }
        }
        
        return template;
    }
    
	public Script getScript( String path ) 
        throws MalformedURLException, IOException, EvaluationException {
		
        Script script = this.scripts.get( path );
        if ( script == null ) {
            URL resource = this.getResource( path );
            if ( resource != null ) {
            	script = ScriptFactory.getInstance().createScript( path, resource );
                this.scripts.put( path, script );
            }
        }
        
        return script;
    }
}

