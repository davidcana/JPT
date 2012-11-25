package org.javapagetemplates.onePhaseImpl;

import java.net.URI;
import java.net.URL;

/**
 * <p>
 *   Extends Resolver class to find resources using an URI
 *   as base.
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class URIResolver extends Resolver {
    protected URI uri;

    public URIResolver( URI uri ) {
        this.uri = uri;
    }

    @Override
    public URL getResource( String path ) 
        throws java.net.MalformedURLException
    {
        URI resource = this.uri.resolve( path );
        if ( resource != null ) {
            return resource.toURL();
        }
        return null;
    }
}

