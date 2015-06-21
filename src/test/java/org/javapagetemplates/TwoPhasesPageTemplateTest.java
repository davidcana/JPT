package org.javapagetemplates;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.javapagetemplates.common.AbstractJPTContext;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.JPTOutputFormat;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.junit.Test;
import org.xml.sax.SAXException;

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
 *  @author <a href="mailto:rossi+wzcommon@webslingerZ.com">Chris Rossi</a>
 *  @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 *  @version $Revision: 1.11 $
 */
public class TwoPhasesPageTemplateTest extends AbstractPageTemplateTest{
	/*
	@Test
    public void testPreExpressions() throws Exception {
		testPageTemplatePreprocessing( "expressions" );
    }

	@Test
    public void testPreStatements() throws Exception {
		testPageTemplatePreprocessing( "statements" );
    }
	
	
	private void testPageTemplatePreprocessing(String test)
			throws PageTemplateException, IOException, SAXException {
		
		String jpt = test + ".jpt";
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + jpt );
        
		JPTDocument jptDocument = JPTDocumentFactory.getInstance().getJPTDocument( resource ); 
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": preprocessed template in " + elapsed + " ms" );
        
        start = System.currentTimeMillis();
        String xml = jptDocument.toHtml();
        elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": show xml structure of template in " + elapsed + " ms" );

        System.out.println(xml);
	}*/
	
	
	@Test
    public void testXhtml() throws Exception {

		//JPTOutputFormat jptOutputFormat = new JPTOutputFormat();
		//jptOutputFormat.setDocType(DocType.xhtmlTransitional);
		//testPageTemplate( "validHtml", null, jptOutputFormat );
		
		testPageTemplate( "validXhtml", null, null );
    }
	
	@Test
    public void testHtml4() throws Exception {

		//JPTOutputFormat jptOutputFormat = new JPTOutputFormat();
		//jptOutputFormat.setDocType(DocType.xhtmlTransitional);
		//testPageTemplate( "validHtml", null, jptOutputFormat );
		
		testPageTemplate( "validHtml4", null, null );
    }
	
	@Test
    public void testHtml5() throws Exception {

		//JPTOutputFormat jptOutputFormat = new JPTOutputFormat();
		//jptOutputFormat.setDocType(DocType.xhtmlTransitional);
		//testPageTemplate( "validHtml", null, jptOutputFormat );
		
		testPageTemplate( "validHtml5", null, null );
    }
	
	@Override
    protected void testPageTemplate( String test, Map<String, Object> dictionary,
    		JPTOutputFormat jptOutputFormat ) throws Exception {
		
		// Get URL and PageTemplate
        String jpt = test + ".jpt";
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + jpt );
        TwoPhasesPageTemplate template = new TwoPhasesPageTemplateImpl( resource.toURI() );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": constructed template in " + elapsed + " ms" );

        // Test PageTemplate
        testPageTemplate(test, dictionary, jptOutputFormat, resource, template);
    }



	@Override
	protected void testStringTemplate(String test, String templateText,
			Map<String, Object> dictionary, JPTOutputFormat jptOutputFormat)
			throws Exception {
		
		// Get URL and PageTemplate
		String html = test + "-2P.html";
        long start = System.currentTimeMillis();
        URL resource = getClass().getResource( "/" + html );
        TwoPhasesPageTemplate template = new TwoPhasesPageTemplateImpl( templateText );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": constructed template in " + elapsed + " ms" );

        // Test PageTemplate
        testPageTemplate(test, dictionary, jptOutputFormat, resource, template);
	}
	
	
	private void testPageTemplate(String test, Map<String, Object> dictionary,
			JPTOutputFormat jptOutputFormat, URL resource,
			TwoPhasesPageTemplate template) throws SAXException,
			PageTemplateException, IOException, UnsupportedEncodingException,
			Exception, FileNotFoundException {

		TestObject testObject = new TestObject(true);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        long start = System.currentTimeMillis();
        template.process( buffer, testObject, dictionary, jptOutputFormat );
        long elapsed = System.currentTimeMillis() - start;
        System.err.println( test + ": processed template in " + elapsed + " ms" );

        checkText(
        		resource, 
        		buffer,
        		"/" + test + "-2P.html");
	}

	@Override
	protected boolean isTwoPhases() {
		return true;
	}

	@Override
	protected AbstractJPTContext getContext() {
		return JPTContext.getInstance();
	}
    
}

