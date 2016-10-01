package org.zenonpagetemplates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;

import org.xnap.commons.i18n.I18n;
import org.zenonpagetemplates.common.TemplateError;

import bsh.EvalError;

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
 *  @version $Revision: 1.0 $
 */
public class TestObject extends TestObjectSuperClass {
	
	private boolean twoPhases = true;
    List<String> people;
    String[] animals = {
        "horse", 
        "dog",
        "cat",
        "pig",
        "crocodile"
    };
    
    int[][] table = {
        { 1, 2, 3, 4 },
        { 5, 6, 7, 8 },
        { 9, 10 }
    };
    
    TestObject(boolean twoPhases) {
    	
    	this.twoPhases = twoPhases;
        people = new ArrayList<String>( 5 );
        people.add( "Chris" );
        people.add( "Karen" );
        people.add( "Mike" );
        people.add( "Marsha" );
        people.add( "Christiane" );
    }
    
    public I18n getI18n1(String language) throws FileNotFoundException, IOException {
    	return getI18n( "labels1_", language );
    }
    public I18n getI18n2(String language) throws FileNotFoundException, IOException {
    	return getI18n( "labels2_", language );
    }
	private I18n getI18n(String filePrefix, String language) throws IOException, FileNotFoundException {
		
		String i18nFileName = filePrefix + language + ".properties";
    	URL resource = getClass().getResource( "/" + i18nFileName );
    	File file = new File ( resource.getFile() );
    	I18n i18n = new I18n(
    			new PropertyResourceBundle(
    					new InputStreamReader(
    							new FileInputStream( file ), 
    							"UTF-8" ) ) );
    	
    	return i18n;
	}
    
    
    public int getError(){
    	return 1 / 0;
    }
    
    public String treatError1(){
    	return "You are in treatError1!";
    }
    
    public String treatError2(){
    	return "You are in treatError2!";
    }
    /*
    private TemplateError getTemplateError(){
    	
    	if (this.twoPhases){
    		return org.zenonpagetemplates.twoPhasesImpl.JPTContext.getInstance().getError();
    	}

    	return org.zenonpagetemplates.onePhaseImpl.JPTContext.getInstance().getError();
    }*/

    
    public String treatError3(TemplateError error) throws EvalError {

    	if ( error == null ){
    		return "Error trying to get error!";
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append( "<pre>" + '\n' );
    	sb.append( "Error" + '\n' );
    	sb.append( "Type: "+ error.getType() + '\n' );
    	sb.append( "Value: " + error.getValue() + '\n' );
    	
    	/*
    	sb.append("traceback: " + '\n');
    	
    	for (StackTraceElement element: error.getTraceback()){
    		sb.append("traceback element: " 
    				+ element.getClassName() + " / "
    				+ element.getMethodName() + " / "
    				+ element.getFileName() + " / "
    				+ element.getLineNumber() + '\n');
    	}*/
    	sb.append( "</pre>" + '\n' );
    	
    	return sb.toString();
    }
    
    public List<String> getPeople() {
        return people;
    }

    public String[] getAnimals() {
        return animals;
    }

    public int[][] getTable() {
        return table;
    }
    
    public String getFavoriteColor() {
        return "red";
    }

    public boolean isGoodLooking() {
        return true;
    }

    public boolean isDumb() {
        return false;
    }

    public int add( int x, int y ) {
        return x + y;
    }

    public int multiply( int x, int y ) {
        return x * y;
    }

    private final TestObject2 friend = new TestObject2();
    public TestObject2 getFriend() {
        return friend;
    }

    public TestObject2 getEnemy() {
        return null;
    }

    public String getDiatribe() {
        return "<b>The cabinet</b> has <i>usurped</i> the authority of the <h3>president</h3>";
    }

    public String getDiatribe2() {
        return "<b>The cabinet</b> has <i>usurped</i> the authority of the <h3>president</h3>";
    }
    
    public Object getDiatribe3() throws Exception {
    	
    	String string = "<b>The cabinet</b> has <i>usurped</i> the authority of the <h3>president</h3>";
    	
    	if ( this.twoPhases ){
    		return new org.zenonpagetemplates.twoPhasesImpl.HTMLFragment( string );
    	}
        
		return new org.zenonpagetemplates.onePhaseImpl.HTMLFragment( string );
    }
    
    public String getDiatribe4() {
        return "<b>The cabinet</b> has <i>usurped</i><br />: <span id=\"test\" />the authority of the <h3>president</h3>";
    }
    
    public int getNumber() {
        return 9;
    }
    
    public List<Integer> getNumbers() {
        List<Integer> numbers = new ArrayList<Integer>( 100 );
        for ( int i = 100; i > 0; i-- ) {
            numbers.add( new Integer( i ) );
        }
        return numbers;
    }
    
    public Date getBirthday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set( 1975, 6, 7, 7, 57, 23 );
        return calendar.getTime();
    }
}