package org.zenonpagetemplates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.zenonpagetemplates.common.AbstractZPTContext;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.onePhaseImpl.ZPTContext;
import org.zenonpagetemplates.onePhaseImpl.OnePhasePageTemplate;
import org.zenonpagetemplates.onePhaseImpl.PageTemplateImpl;
import org.zenonpagetemplates.twoPhasesImpl.ZPTOutputFormat;

/**
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
 * @version $Revision: 1.11 $
 */
public class OnePhasePageTemplateTest extends AbstractPageTemplateTest{
	
	@Override
	protected void testPageTemplate( String test, Map<String, Object> dictionary, ZPTOutputFormat zptOutputFormat ) 
			throws Exception {
		
		// Get URL and PageTemplate
        String zpt = test + ZPT_FILE_EXTENSION;
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + zpt );
		OnePhasePageTemplate template = new PageTemplateImpl( resource );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": constructed template in " + elapsed + " ms" );

        // Test PageTemplate
        testPageTemplate(test, dictionary, resource, template);
    }
	
	@Test
    public void testListExpressions() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "number1", 1 );
        dictionary.put( "number2", 2 );
        dictionary.put( "number3", 3 );
        dictionary.put( "number5", 5 );
        dictionary.put( "number-2", -2 );
        
		testPageTemplate( "listExpressions", dictionary, null );
    }
	
	@Override
	protected void testStringTemplate( String test, String templateText, Map<String, Object> dictionary, ZPTOutputFormat zptOutputFormat ) 
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
	protected AbstractZPTContext getContext() {
		return ZPTContext.getInstance();
	}
}

