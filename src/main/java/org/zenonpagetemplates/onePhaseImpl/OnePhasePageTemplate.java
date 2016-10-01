package org.zenonpagetemplates.onePhaseImpl;

import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.zenonpagetemplates.common.PageTemplate;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Extends PageTemplate interface with related to one phase
 *   implementation methods.
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
 * @version $Revision: 1.6 $
 */
public interface OnePhasePageTemplate extends PageTemplate {

	// Methods
	void process( ContentHandler contentHandler, LexicalHandler lexicalHandler, Object context, Map<String, Object> dictionary )
        throws PageTemplateException;
    
	Resolver getResolver();
    void setResolver( Resolver resolver );
    
	Map<String, Macro> getMacros();
}
