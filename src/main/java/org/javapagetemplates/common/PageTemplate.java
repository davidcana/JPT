package org.javapagetemplates.common;

import java.io.OutputStream;
import java.util.Map;

import org.javapagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Base interface with constants and methods to implement for 
 *   JPT implementation classes.
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
 * @version $Revision: 1.5 $
 */
public interface PageTemplate {
	
	// Namespaces
    static final String TAL_NAMESPACE_URI = "http://xml.zope.org/namespaces/tal";
    static final String METAL_NAMESPACE_URI = "http://xml.zope.org/namespaces/metal";
    static final String I18N_NAMESPACE_URI = "http://xml.javapagetemplates.org/namespaces/i18n";
    
    // Tal namespace
    static final String TAL_DEFINE = "define";
    static final String TAL_CONDITION = "condition";
    static final String TAL_REPEAT = "repeat";
    static final String TAL_CONTENT = "content";
    static final String TAL_REPLACE = "replace";
    static final String TAL_ATTRIBUTES = "attributes";
    static final String TAL_OMIT_TAG = "omit-tag";
    static final String TAL_ON_ERROR = "on-error";
    static final String TAL_TAG = "tag";
    
    // Metal namespace
    static final String METAL_USE_MACRO = "use-macro";
    static final String METAL_DEFINE_SLOT = "define-slot";
    static final String METAL_DEFINE_MACRO = "define-macro";
    static final String METAL_FILL_SLOT = "fill-slot";
    
    // I18n namespace
    static final String I18N_DOMAIN = "domain";
    static final String I18N_DEFINE = TAL_DEFINE;
    static final String I18N_CONTENT = TAL_CONTENT;
    static final String I18N_REPLACE = TAL_REPLACE;
    static final String I18N_ATTRIBUTES = TAL_ATTRIBUTES;
    static final String I18N_PARAMS = "params";
    static final String I18N_ON_ERROR = TAL_ON_ERROR;
    
    // Expressions
    static final String EXPRESSION_SUFFIX = ":";
    static final String EXPR_STRING = "string" + EXPRESSION_SUFFIX;
    static final String EXPR_EXISTS = "exists" + EXPRESSION_SUFFIX;
    static final String EXPR_NOCALL = "nocall" + EXPRESSION_SUFFIX;
    static final String EXPR_NOT = "not" + EXPRESSION_SUFFIX;
    static final String EXPR_JAVA = "java" + EXPRESSION_SUFFIX;
    static final String EXPR_BSH = "bsh" + EXPRESSION_SUFFIX;
    static final String EXPR_GROOVY = "groovy" + EXPRESSION_SUFFIX;
    static final String EXPR_EQUALS = "equals" + EXPRESSION_SUFFIX;
    static final String EXPR_GREATER = "greater" + EXPRESSION_SUFFIX;
    static final String EXPR_LOWER = "lower" + EXPRESSION_SUFFIX;
    static final String EXPR_ADD = "+" + EXPRESSION_SUFFIX;
    static final String EXPR_SUB = "-" + EXPRESSION_SUFFIX;
    static final String EXPR_MUL = "*" + EXPRESSION_SUFFIX;
    static final String EXPR_DIV = ":" + EXPRESSION_SUFFIX;
    static final String EXPR_MOD = "%" + EXPRESSION_SUFFIX;
    static final String EXPR_OR = "or" + EXPRESSION_SUFFIX;
    static final String EXPR_AND = "and" + EXPRESSION_SUFFIX;
    static final String EXPR_COND = "cond" + EXPRESSION_SUFFIX;
    
    // String expressions
    static final String STRING_EXPRESSION_SUFIX = " ";
	static final String STRING_EXPR_TEXT = "text" + STRING_EXPRESSION_SUFIX;
	static final String STRING_EXPR_STRUCTURE = "structure" + STRING_EXPRESSION_SUFIX;
	
	// Delimiters
	static final char DEFINE_DELIMITER = ';';
	static final char IN_DEFINE_DELIMITER = ' ';
	static final char ATTRIBUTE_DELIMITER = ';';
	static final char IN_ATTRIBUTE_DELIMITER = ' ';
	static final char PATH_DELIMITER = '|';
	static final char EXPRESSION_DELIMITER = ' ';
	
	// Boolean strings
	static final String FALSE_STRING = "false";
	static final String TRUE_STRING = "true";
	
	// Literal suffixes
	static final String FLOAT_LITERAL_SUFFIX = "f";
	static final String DOUBLE_LITERAL_SUFFIX = "d";
	static final String LONG_LITERAL_SUFFIX = "l";
	
	// Preset vars
	static final String HERE_VAR_NAME = "here";
	static final String TEMPLATE_VAR_NAME = "template";
	static final String RESOLVER_VAR_NAME = "resolver";
	static final String BOOL_HELPER_VAR_NAME = "bool";
	static final String DATE_HELPER_VAR_NAME = "date";
	static final String REPEAT_VAR_NAME = "repeat";               // Only defined inside a tal:repeat
	public static final String TEMPLATE_ERROR_VAR_NAME = "error"; // Only defined inside a tal:on-error
	static final String SHELL_VAR_NAME = "shell";
	 
	// Methods
    void process( OutputStream output, Object context )
        throws PageTemplateException;
	void process( OutputStream output, Object context, Map<String, Object> dictionary )
        throws PageTemplateException;
	
    String toLetter( int n );
    String toCapitalLetter( int n );
    String toRoman( int n );
    String toCapitalRoman( int n );
}
