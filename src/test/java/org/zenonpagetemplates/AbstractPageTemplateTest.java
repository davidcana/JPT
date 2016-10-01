package org.zenonpagetemplates;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.junit.Test;
import org.zenonpagetemplates.common.AbstractJPTContext;
import org.zenonpagetemplates.common.scripting.beanShell.BeanShellEvaluator;
import org.zenonpagetemplates.common.scripting.groovy.GroovyEvaluator;
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
 *  @author <a href="mailto:rossi+wzcommon@webslingerZ.com">Chris Rossi</a>
 *  @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 *  @version $Revision: 1.11 $
 */

abstract public class AbstractPageTemplateTest {

	static final String ZPT_FILE_EXTENSION = ".zpt";
	
	protected abstract void testPageTemplate( String test, Map<String, Object> dictionary, 
			JPTOutputFormat jptOutputFormat ) throws Exception;
	protected abstract void testStringTemplate( String test, String templateText, Map<String, Object> dictionary, 
			JPTOutputFormat jptOutputFormat ) throws Exception;
	protected abstract boolean isTwoPhases();
	protected abstract AbstractJPTContext getContext();
	
	
	@Test
    public void testStatements() throws Exception {
		testPageTemplate( "statements", null, null );
    }
	
	@Test
    public void testExpressionsUsingBSH() throws Exception {
		testExpressions( "expressionsUsingBSH" );
    }
    
	@Test
    public void testExpressionsUsingGroovy() throws Exception {

		try {
			this.getContext().setExpressionEvaluator( 
					GroovyEvaluator.getInstance() );
			testExpressions( "expressionsUsingGroovy" );
			
		} catch ( Exception e ) {
			throw ( e );
			
		} finally {
			this.getContext().setExpressionEvaluator( 
					BeanShellEvaluator.getInstance() );
		}
    }
	
	@Test
    public void testAmpersandsInExpressions() throws Exception {
		testPageTemplate( "ampersandsInExpressions", null, null );
    }
	
    private void testExpressions( String template ) throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "opinions", "everybodysgotone" );
        dictionary.put( "helper", new TestObject( this.isTwoPhases() ) );
        dictionary.put( "acquaintance", "friend" );

        testPageTemplate( template, dictionary, null );
    }
	
	@Test
    public void testBSHScriptsAndExpressions() throws Exception {
		testPageTemplate( "bshScriptsAndExpressions", null, null );
    }
	
	@Test
    public void testGroovyScriptsAndExpressions() throws Exception {
		testPageTemplate( "groovyScriptsAndExpressions", null, null );
    }
	
	@Test
    public void testForcedExpressionsBSHDefault() throws Exception {
		testPageTemplate( "forcedExpressionsBshDefault", null, null );
    }
	
	@Test
    public void testForcedExpressionsGroovyDefault() throws Exception {

		try {
			this.getContext().setExpressionEvaluator( 
					GroovyEvaluator.getInstance() );
			testPageTemplate( "forcedExpressionsGroovyDefault", null, null );
			
		} catch ( Exception e ) {
			throw ( e );
			
		} finally {
			this.getContext().setExpressionEvaluator( 
					BeanShellEvaluator.getInstance() );
		}
    }
	@Test
    public void testNamespace() throws Exception {
        testPageTemplate( "namespace", null, null );
    }
	
	@Test
    public void testNamespace2() throws Exception {
        testPageTemplate( "namespace2", null, null );
    }
	
	@Test
    public void testStyle() throws Exception {
        testPageTemplate( "style", null, null );
    }
    
	@Test
    public void testOnError() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "helper", new TestObject( this.isTwoPhases() ) );
        
        testPageTemplate( "on-error", dictionary, null );
    }
	
	@Test
    public void testOnError2() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "helper", new TestObject( this.isTwoPhases() ) );
        
        testPageTemplate( "on-error2", dictionary, null );
    }
	
	@Test
    public void testI18n() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "helper", new TestObject( this.isTwoPhases() ) );
        
        testPageTemplate( "i18n", dictionary, null );
    }
	
	@Test
    public void testJavaOff() throws Exception {
		
		try {
			this.getContext().setScriptExpressionsOn( false );
			testPageTemplate( "javaOff", null, null );
			
		} catch ( Exception e ) {
			throw ( e );
			
		} finally {
			this.getContext().setScriptExpressionsOn( true );
		}
    }
	
	
	@Test
    public void testMacros() throws Exception {
        testPageTemplate( "macros", null, null );
    }

	@Test
    public void testMacros2() throws Exception {
        testPageTemplate( "macros2", null, null );
    }
	
	@Test
    public void testMacroExpressions() throws Exception {
        testPageTemplate( "macroExpressions", null, null );
    }
	
	@Test
    public void testMacroOmitTag() throws Exception {
        testPageTemplate( "macroOmitTag", null, null );
    }
	
	@Test
    public void testScopeWithBSH() throws Exception {
        testPageTemplate( "scopeWithBSH", null, null );
    }
	
	@Test
    public void testScopeWithGroovy() throws Exception {

		try {
			this.getContext().setExpressionEvaluator( 
					GroovyEvaluator.getInstance() );
			testPageTemplate( "scopeWithGroovy", null, null );
			
		} catch ( Exception e ) {
			throw ( e );
			
		} finally {
			this.getContext().setExpressionEvaluator( 
					BeanShellEvaluator.getInstance() );
		}
    }
	
	@Test
    public void testLazyEvaluation() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "helper", new TestObject( this.isTwoPhases() ) );
        
        testPageTemplate( "lazyEvaluation", dictionary, null );
    }
	
	@Test
    public void testConfigurableTags() throws Exception {
        testPageTemplate( "configurableTags", null, null );
    }
    
	public void testNocall() throws Exception {
        Map<String, Object> dictionary = new HashMap<String, Object>();
        dictionary.put( "testCounter1", new Counter( ) );
        dictionary.put( "testCounter2", new Counter( ) );
        //dictionary.put( "testCounter3", new Counter( ) );
        
        testPageTemplate( "nocall", dictionary, null );
    }
    
	@Test
    public void testString() throws Exception {
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("<html xmlns:tal=\"http://xml.zope.org/namespaces/tal\">" + '\n');
    	sb.append("    <head>" + '\n');
    	sb.append("        <title>This is a test</title>" + '\n');
    	sb.append("    </head>" + '\n');
    	sb.append("    <body>" + '\n');
    	sb.append("    <!-- This is a test -->" + '\n');
    	sb.append("    <p class=\"paragraph\">" + '\n');
    	sb.append("        I hope it works." + '\n');
    	sb.append("    </p>" + '\n');
    	sb.append("    <div>" + '\n');
    	sb.append("        Let's test some expressions:" + '\n');
    	sb.append("            <ul>" + '\n');
    	sb.append("                <li>A blank expression should be <i tal:content=\"\">blank</i></li>" + '\n');
    	sb.append("                <li>here/favoriteColor should be <b tal:content=\"here/favoriteColor\">a color</b></li>" + '\n');
    	sb.append("                <li>here/friend/number should be <span tal:content=\"here/friend/number\">a number</span></li>" + '\n');
    	sb.append("            </ul>" + '\n');
    	sb.append("        </div>" + '\n');
    	sb.append("    </body>" + '\n');
    	sb.append("</html>" + '\n');
    	
    	testStringTemplate( "string", sb.toString(), null, null );
    }
	
    /*
    static final void saveStringToFile(String string, File file) throws IOException{

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream, "UTF-8");
        BufferedWriter fileWriter = new BufferedWriter(outputStreamWriter);        
        PrintWriter out = new PrintWriter(fileWriter);

        // Print the string
        out.print(string);

        // Close all the open resources
        out.close();
        fileWriter.close();
        outputStreamWriter.close();
        fileOutputStream.close();
    }*/

    protected void checkText( URL resource, ByteArrayOutputStream buffer, String html )
			throws UnsupportedEncodingException, Exception,
			FileNotFoundException, IOException {
				
		String newHtml = html + ".new";
	    byte[] resultBinary = buffer.toByteArray();
	    String result = fixCRLF( new String( resultBinary, "UTF-8" ) );
	    String expected = fixCRLF( loadTextFile( getClass().getResourceAsStream( html ) ) );
	    
	    if ( ! filterText( result ).equals( 
	    		filterText( expected ) )){
	    	File parent = ( new File( resource.getFile() ) ).getParentFile();
	    	File file = new File ( parent.getPath() + newHtml );
	        FileOutputStream out = new FileOutputStream( file );
	        out.write( resultBinary );
	        out.close();
	
	        fail( "unexpected results: see " + newHtml );
	    }
	}
    
	static final String loadTextFile( InputStream input ) throws Exception {
        InputStreamReader reader = new InputStreamReader( input );
        StringWriter buffer = new StringWriter();
        char cbuf[] = new char[ 1024 ];
        int len = reader.read( cbuf );
        while ( len != -1 ) {
            buffer.write( cbuf, 0, len );
            len = reader.read( cbuf );
        }
        buffer.close();
        reader.close();
        return buffer.toString();
    }
    
    static final String fixCRLF( String source ) {
        StringBuffer buf = new StringBuffer( source.length() );
        StringTokenizer chunks = new StringTokenizer( source, "\r\n" );
        while ( chunks.hasMoreTokens() ) {
            buf.append( chunks.nextToken() );
            if ( buf.charAt( buf.length() - 1 ) != '\n' ) {
                buf.append( '\n' );
            }
        }
        return buf.toString();
    }
    
    static final String filterText( String source ) {
    	
    	String text = source.replaceAll( "\\r|\\n|\\t", "" );
    	
    	StringBuilder sb = new StringBuilder();

    	Scanner scanner = new Scanner( text );
    	while ( scanner.hasNextLine() ) {
    	  String line = scanner.nextLine();
    	  sb.append( 
    			  line.trim().replaceAll( 
    					  "\\s+", " " ) );
    	}
    	scanner.close();
    	
    	return sb.toString();
    }

    public static int subtract( int x, int y ) {
        return x - y;
    }
    
}

