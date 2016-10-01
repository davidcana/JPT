package org.zenonpagetemplates.twoPhasesImpl;

import java.io.OutputStream;
import java.util.Map;

import org.zenonpagetemplates.common.PageTemplate;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Extends PageTemplate interface with related to two phase
 *   implementation methods.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public interface TwoPhasesPageTemplate extends PageTemplate {
	
	// Methods
	public void process( OutputStream output, Object context, Map<String, Object> dictionary,
			ZPTOutputFormat zptOutputFormat )
        throws PageTemplateException;
    
	Resolver getResolver();
    void setResolver( Resolver resolver );
    
	Map<String, Macro> getMacros() throws PageTemplateException;
}
