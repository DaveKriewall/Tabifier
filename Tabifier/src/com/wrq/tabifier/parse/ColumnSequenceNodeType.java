/*
 * Copyright (c) 2003, 2010, Dave Kriewall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wrq.tabifier.parse;

import java.util.HashMap;
import java.util.Map;

/**
 * Type of ColumnSequence node in the column tree.  The types correspond to various strategic syntactic points
 * encountered as the PsiFile is parsed.
 */
public class ColumnSequenceNodeType
{
    public  static final ColumnSequenceNodeType ANNOTATION_GROUP              = new ColumnSequenceNodeType("ANNOTATION_GROUP"             );
    public  static final ColumnSequenceNodeType ANNOTATION                    = new ColumnSequenceNodeType("ANNOTATION"                   );
    public  static final ColumnSequenceNodeType ARITHMETIC_EXPRESSION         = new ColumnSequenceNodeType("ARITHMETIC_EXPRESSION"        );
    public  static final ColumnSequenceNodeType ASSIGNMENT_STATEMENTS         = new ColumnSequenceNodeType("ASSIGNMENT_STATEMENTS"        );
    public  static final ColumnSequenceNodeType BASE_SEQ                      = new ColumnSequenceNodeType("BASE_SEQ"                     );
    public  static final ColumnSequenceNodeType CLASS_SEQ                     = new ColumnSequenceNodeType("CLASS_SEQ"                    );
    public  static final ColumnSequenceNodeType CODE_BLOCK                    = new ColumnSequenceNodeType("CODE_BLOCK"                   );
    public  static final ColumnSequenceNodeType CONDITIONAL_EXPRESSION        = new ColumnSequenceNodeType("CONDITIONAL_EXPRESSION"       );
    public  static final ColumnSequenceNodeType DECLARATION                   = new ColumnSequenceNodeType("DECLARATION"                  );
    public  static final ColumnSequenceNodeType EXPRESSION_STATEMENTS         = new ColumnSequenceNodeType("EXPRESSION_STATEMENTS"        );
    public  static final ColumnSequenceNodeType FIELD                         = new ColumnSequenceNodeType("FIELD"                        );
    public  static final ColumnSequenceNodeType IF_STATEMENT                  = new ColumnSequenceNodeType("IF_STATEMENT"                 );
    public  static final ColumnSequenceNodeType IF_STATEMENT_ITSELF           = new ColumnSequenceNodeType("IF_STATEMENT_ITSELF"          );
    public  static final ColumnSequenceNodeType BRACES                        = new ColumnSequenceNodeType("BRACES"                       );
    public  static final ColumnSequenceNodeType LOGICAL_EXPRESSION            = new ColumnSequenceNodeType("LOGICAL_EXPRESSION"           );
    private static final ColumnSequenceNodeType METHOD_CALLS                  = new ColumnSequenceNodeType("METHOD_CALLS"                 );
    public  static final ColumnSequenceNodeType METHOD_DECLARATION_PARAMETERS = new ColumnSequenceNodeType("METHOD_DECLARATION_PARAMETERS");
    public  static final ColumnSequenceNodeType NEW_EXPRESSION                = new ColumnSequenceNodeType("NEW_EXPRESSION"               );
    public  static final ColumnSequenceNodeType PARAMLIST                     = new ColumnSequenceNodeType("PARAMLIST"                    );
    public  static final ColumnSequenceNodeType PARENTHESIZED_EXPRESSION      = new ColumnSequenceNodeType("PARENTHESIZED_EXPRESSION"     );
    public  static final ColumnSequenceNodeType RELATIONAL_EXPRESSION         = new ColumnSequenceNodeType("RELATIONAL_EXPRESSION"        );
    public  static final ColumnSequenceNodeType STATEMENT                     = new ColumnSequenceNodeType("STATEMENT"                    );
    public  static final ColumnSequenceNodeType TERM                          = new ColumnSequenceNodeType("TERM"                         );
    public  static final ColumnSequenceNodeType TYPE_CAST_EXPRESSION          = new ColumnSequenceNodeType("TYPE_CAST_EXPRESSION"         );
    public  static final ColumnSequenceNodeType UNKNOWN_TOKEN_SEQ             = new ColumnSequenceNodeType("UNKNOWN_TOKEN_SEQ"            );
    public  static final ColumnSequenceNodeType WHILE_STATEMENT               = new ColumnSequenceNodeType("WHILE_STATEMENT"              );
    public  static final ColumnSequenceNodeType WHILE_STATEMENT_ITSELF        = new ColumnSequenceNodeType("WHILE_STATEMENT_ITSELF"       );
    public  static final ColumnSequenceNodeType RETURN_STATEMENT              = new ColumnSequenceNodeType("RETURN_STATEMENT"             );
    public  static final ColumnSequenceNodeType ARRAY_INITIALIZER             = new ColumnSequenceNodeType("ARRAY_INITIALIZER"            );
    public  static final ColumnSequenceNodeType PREFIX_EXPRESSION             = new ColumnSequenceNodeType("PREFIX_EXPRESSION"            );
    public  static final ColumnSequenceNodeType HORIZONTAL_CODE_BLOCK         = new ColumnSequenceNodeType("HORIZONTAL_CODE_BLOCK"        );
    public  static final ColumnSequenceNodeType FIELD_COMMA_PAIR              = new ColumnSequenceNodeType("FIELD_COMMA_PAIR"             );

    private static final Map<String, ColumnSequenceNodeType> methodCallCSNTs =
            new HashMap<String, ColumnSequenceNodeType>();

    final String name;

    protected ColumnSequenceNodeType(String name)
    {
        this.name = name;
    }

    /**
     * Method calls are aligned in groups based on similarity of method call name, controlled by a threshold value
     * indicating the number of identical leading characters necessary to group methods together.  This method
     * returns a ColumnSequenceNodeType which is unique for the given method name and threshold.  If the threshold
     * is zero, it returns ColumnSequenceNodeType METHOD_CALLS (and all method calls will be aligned as a group.)
     *
     * If a method call involves nested method calls, e.g. this.getSomething().iterator(), then make the similarity
     * test apply to all leading characters following the dots.  In the example, if the threshold is four, then
     * methods matching "this.getS*.iter*" would be aligned together.
     */
    public static ColumnSequenceNodeType getMethodCallCSNT(String methodName, int threshold)
    {
        if (threshold == 0)
        {
            return METHOD_CALLS;
        }
        StringBuffer searchKey = new StringBuffer();
        while (methodName.length() > 0)
        {
            int    dot     = methodName.indexOf(".");
            String subname = (dot < 0 ? methodName : methodName.substring(0, dot));
            if (threshold < subname.length())
            {
                subname = subname.substring(0, threshold);
            }
            searchKey.append(subname);
            if (dot >= 0) {
                searchKey.append(".");
                methodName = methodName.substring(dot + 1, methodName.length());
            }
            else break;
        }

        ColumnSequenceNodeType result = methodCallCSNTs.get(searchKey.toString());
        if (result == null)
        {
            result = new ColumnSequenceNodeType(searchKey.toString());
            methodCallCSNTs.put(searchKey.toString(), result);
        }
        return result;
    }

    /**
     * Some CSNT's are shared at different points in the syntax tree but have the same sequences.  Therefore
     * the CSNT's list of choice and token columns should be initialized in a common place - here.  Over time,
     * every CSNT instance should be of a subclass of CSNT, and this class should be abstract.  For now, leave
     * initialization to the unique place the CSNT is used.
     */
    protected void initializeCSNT()
    {
        // initialize elsewhere.
    }
    public final String toString()
    {
        return name;
    }
}
