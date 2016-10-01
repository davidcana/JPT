package org.zenonpagetemplates;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.zenonpagetemplates.onePhaseImpl.OnePhasePageTemplate;
import org.zenonpagetemplates.onePhaseImpl.PageTemplateImpl;
import org.zenonpagetemplates.twoPhasesImpl.ZPTOutputFormat;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;

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
 *  @author <a href="mailto:rossi+wzcommon@webslingerZ.com">Chris Rossi</a>
 *  @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 *  @version $Revision: 1.0 $
 */
public class PerformanceTest {

	static int DEFAULT_NUMBER_OF_EXECUTIONS = 10;
	
	@Test
	public void testExpressionPerformance() throws Exception {
        Map<String, Object> dictionary1P = new HashMap<String, Object>();
        dictionary1P.put( "opinions", "everybodysgotone" );
        dictionary1P.put( "helper", new TestObject(false) );
        dictionary1P.put( "acquaintance", "friend" );

        Map<String, Object> dictionary2P = new HashMap<String, Object>();
        dictionary2P.put( "opinions", "everybodysgotone" );
        dictionary2P.put( "helper", new TestObject(true) );
        dictionary2P.put( "acquaintance", "friend" );
        
        testPerformance( "expressionsUsingBSH", dictionary1P, dictionary2P, null);
	}
	
	@Test
    public void testStatementsPerformance() throws Exception {
		testPerformance( "statements", null, null, null);
    }
	
	
	@Test
    public void testMacrosPerformance() throws Exception {
		testPerformance( "macros", null, null, null );
    }
	
	private void testPerformance(String test, 
			Map<String, Object> dictionary1P, Map<String, Object> dictionary2P, 
			ZPTOutputFormat zptOutputFormat) 
					throws Exception {
		
		testPerformance(test, dictionary1P, dictionary2P, zptOutputFormat, DEFAULT_NUMBER_OF_EXECUTIONS);
	}
	
	private void testPerformance(String test, 
			Map<String, Object> dictionary1P, Map<String, Object> dictionary2P, 
			ZPTOutputFormat zptOutputFormat, int numberOfExecutions) 
					throws Exception {
		
		System.err.println("-------- Begin test of template: " + test + " --------------");
		
		// Execute one phase
		long onePhaseStart = System.currentTimeMillis();
		
		execute1P(test, dictionary1P, zptOutputFormat, numberOfExecutions);
		
		long onePhaseElapsed = System.currentTimeMillis() - onePhaseStart;
		
		// Execute two phases
		long twoPhasesStart = System.currentTimeMillis();
		
		execute2P(test, dictionary2P, zptOutputFormat, numberOfExecutions);
		
		long twoPhasesElapsed = System.currentTimeMillis() - twoPhasesStart;
		
		// Show differences
		showResume(test, numberOfExecutions, onePhaseElapsed, twoPhasesElapsed);
		
		System.err.println("-------- End test of template: " + test + " --------------");
	}
	
	protected void showResume(String test, int numberOfExecutions,
			long onePhaseElapsed, long twoPhasesElapsed) {
		
		System.err.println("Template resume: " + test);
		System.err.println("Total time of " + numberOfExecutions + " executions at"
				+ " one phase: " + onePhaseElapsed + " ms");
		System.err.println("Average time of " + numberOfExecutions + " executions at"
				+ " one phase: " + (onePhaseElapsed / numberOfExecutions) + " ms");
		System.err.println("Total time of " + numberOfExecutions + " executions at"
				+ " two phases: " + twoPhasesElapsed + " ms");
		System.err.println("Average time of " + numberOfExecutions + " executions at"
				+ " two phases: " + (twoPhasesElapsed / numberOfExecutions) + " ms");
		if (onePhaseElapsed > twoPhasesElapsed){
			System.err.println("Performance of two phases is better in " + (onePhaseElapsed - twoPhasesElapsed) + " ms"
					+ ", one average execution in " + ((onePhaseElapsed - twoPhasesElapsed) / numberOfExecutions) + " ms"
					+ ", " + getPercent(onePhaseElapsed, twoPhasesElapsed) + "% better");
		} else if (onePhaseElapsed < twoPhasesElapsed){
			System.err.println("Performance of one phase is better in " + (twoPhasesElapsed - onePhaseElapsed) + " ms"
					+ ", one average execution in " + ((twoPhasesElapsed - onePhaseElapsed) / numberOfExecutions) + " ms"
					+ ", " + getPercent(twoPhasesElapsed, onePhaseElapsed) + "% better");
		} else {
			System.err.println("Times are equal!");
		}
	}

	protected void execute2P(String test, Map<String, Object> dictionary2P,
			ZPTOutputFormat zptOutputFormat, int numberOfExecutions)
			throws Exception {
		
		String zpt = null;
		URL resource = null;
		
		for (int c = 0; c < numberOfExecutions; ++c){
			
			long start = System.currentTimeMillis();
			
			if (zpt == null){
				zpt = test + AbstractPageTemplateTest.ZPT_FILE_EXTENSION;
			}
			if (resource == null){
				resource = getClass().getResource( "/" + zpt );
			}
			TwoPhasesPageTemplate template = new TwoPhasesPageTemplateImpl( resource.toURI() );
			
			TestObject testObject = new TestObject(true);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			template.process( buffer, testObject, dictionary2P, zptOutputFormat );
			
			System.err.println( test + ": processed two phases template in " 
					+ (System.currentTimeMillis() - start) 
					+ " ms" );
		}
	}
	
	protected void execute1P(String test, Map<String, Object> dictionary1P,
			ZPTOutputFormat zptOutputFormat, int numberOfExecutions)
			throws Exception {
		
		String zpt = null;
		URL resource = null;
		
		for (int c = 0; c < numberOfExecutions; ++c){
			long start = System.currentTimeMillis();
			
			if (zpt == null){
				zpt = test + AbstractPageTemplateTest.ZPT_FILE_EXTENSION;
			}
			if (resource == null){
				resource = getClass().getResource( "/" + zpt );
			}
			OnePhasePageTemplate template = new PageTemplateImpl( resource );
			TestObject testObject = new TestObject(false);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			
			start = System.currentTimeMillis();
			if ( dictionary1P == null ) {
			    template.process( buffer, testObject );
			} else {
			    template.process( buffer, testObject, dictionary1P );
			}
			
			System.err.println( test + ": processed one phase template in " 
					+ (System.currentTimeMillis() - start) 
					+ " ms" );
		}
	}
	
	private String getPercent(long number1, long number2) {
		return "" + (100 - number2 * 100 / number1);
	}

}
