package org.javapagetemplates.twoPhasesImpl;

import java.net.URI;
import java.net.URL;

import org.javapagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Simple class that extends Resolver class using a URIResolver.
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
public class DefaultResolver extends Resolver {
	
    private URIResolver uriResolver;
    

    DefaultResolver(URI uri) {
        if ( uri != null ) {
            this.uriResolver = new URIResolver( uri );
        }
    }
    
    @Override
    public URL getResource( String path ) 
        throws java.net.MalformedURLException {
    	
        URL resource = null;

        if ( this.uriResolver != null ) {
            resource = this.uriResolver.getResource( path );
        }

        return resource;
    }
    
    @Override
    public TwoPhasesPageTemplate getPageTemplate( String path )
        throws PageTemplateException, java.net.MalformedURLException {
    	
    	TwoPhasesPageTemplate template = null;

        if ( this.uriResolver != null ) {
            template = this.uriResolver.getPageTemplate( path );
        }

        return template;
    }
}
