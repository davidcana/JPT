package org.zenonpagetemplates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.xml.sax.SAXException;
import org.zenonpagetemplates.common.AbstractJPTContext;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.onePhaseImpl.JPTContext;
import org.zenonpagetemplates.onePhaseImpl.OnePhasePageTemplate;
import org.zenonpagetemplates.onePhaseImpl.PageTemplateImpl;
import org.zenonpagetemplates.twoPhasesImpl.JPTOutputFormat;

/**
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
 * @version $Revision: 1.11 $
 */
public class OnePhasePageTemplateTest extends AbstractPageTemplateTest{
	
	@Override
	protected void testPageTemplate( String test, Map<String, Object> dictionary, JPTOutputFormat jptOutputFormat ) 
			throws Exception {
		
		// Get URL and PageTemplate
        String jpt = test + ZPT_FILE_EXTENSION;
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + jpt );
		OnePhasePageTemplate template = new PageTemplateImpl( resource );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": constructed template in " + elapsed + " ms" );

        // Test PageTemplate
        testPageTemplate(test, dictionary, resource, template);
    }
	
	
	@Override
	protected void testStringTemplate( String test, String templateText, Map<String, Object> dictionary, JPTOutputFormat jptOutputFormat ) 
			throws Exception {
		
		// Get URL and PageTemplate
        String html = test + "-1P.html";
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + html );
        
        byte[] bytesArray = templateText.getBytes("UTF-8");
        ByteArrayInputStream input = new ByteArrayInputStream(bytesArray);
        
		OnePhasePageTemplate template = new PageTemplateImpl( input );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": constructed template in " + elapsed + " ms" );

        // Test PageTemplate
        testPageTemplate(test, dictionary, resource, template);
    }
	
	
	private void testPageTemplate(String test, Map<String, Object> dictionary,
			URL resource, OnePhasePageTemplate template) throws SAXException,
			PageTemplateException, IOException, UnsupportedEncodingException,
			Exception, FileNotFoundException {
		
		TestObject testObject = new TestObject(false);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        long start = System.currentTimeMillis();
        if ( dictionary == null ) {
            template.process( buffer, testObject );
        } else {
            template.process( buffer, testObject, dictionary );
        }
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": processed template in " + elapsed + " ms" );

        checkText(
        		resource, 
        		buffer, 
        		"/" + test + "-1P.html");
	}
	
	
	@Override
	protected boolean isTwoPhases() {
		return false;
	}
	
	
	@Override
	protected AbstractJPTContext getContext() {
		return JPTContext.getInstance();
	}
}

