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

/**
 * An AlignableColumnNodeType identifies an AlignableColumn in the column tree.  A SequenceNode may be searched to
 * find an AlignableColumn of the given type.  Types correspond to certain Java syntactic elements.
 */
public class AlignableColumnNodeType
{
    public static final AlignableColumnNodeType ASSIGNMENT_EXPRESSIONS     = new AlignableColumnNodeType("ASSIGNMENT_EXPRESSIONS"    );
    public static final AlignableColumnNodeType ASSIGNMENT_MODIFIERS       = new AlignableColumnNodeType("ASSIGNMENT_MODIFIERS"      );
    public static final AlignableColumnNodeType ASSIGNMENT_OPERATORS       = new AlignableColumnNodeType("ASSIGNMENT_OPERATORS"      );
    public static final AlignableColumnNodeType ASSIGNMENT_VARNAMES        = new AlignableColumnNodeType("ASSIGNMENT_VARNAMES"       );
    public static final AlignableColumnNodeType CLASS                      = new AlignableColumnNodeType("CLASS_SEQ"                 );
    public static final AlignableColumnNodeType CLOSE_PAREND               = new AlignableColumnNodeType("CLOSE_PAREND"              );
    public static final AlignableColumnNodeType CODE_BLOCK                 = new AlignableColumnNodeType("CODE_BLOCK"                );
    public static final AlignableColumnNodeType COLON                      = new AlignableColumnNodeType("COLON"                     );
    public static final AlignableColumnNodeType COMMAS                     = new AlignableColumnNodeType("COMMAS"                    );
    public static final AlignableColumnNodeType CONDITION                  = new AlignableColumnNodeType("CONDITION"                 );
    public static final AlignableColumnNodeType DECLARATIONS               = new AlignableColumnNodeType("DECLARATIONS"              );
    public static final AlignableColumnNodeType ELSE_EXPRESSION            = new AlignableColumnNodeType("ELSE_EXPRESSION"           );
    public static final AlignableColumnNodeType EXPRESSION_STATEMENT       = new AlignableColumnNodeType("EXPRESSION_STATEMENT"      );
    public static final AlignableColumnNodeType EXPR_LOPERAND              = new AlignableColumnNodeType("EXPR_LOPERAND"             );
    public static final AlignableColumnNodeType EXPR_ROPERAND              = new AlignableColumnNodeType("EXPR_ROPERAND"             );
    public static final AlignableColumnNodeType FIELD                      = new AlignableColumnNodeType("FIELD"                     );
    public static final AlignableColumnNodeType IF_ELSE_KEYWORDS           = new AlignableColumnNodeType("IF_ELSE_KEYWORDS"          );
    public static final AlignableColumnNodeType IF_STMT_CLOSE_PAREND       = new AlignableColumnNodeType("IF_STMT_CLOSE_PAREND"      );
    public static final AlignableColumnNodeType IF_STMT_CONDITIONALS       = new AlignableColumnNodeType("IF_STMT_CONDITIONALS"      );
    public static final AlignableColumnNodeType IF_STMT_LINES              = new AlignableColumnNodeType("IF_STMT_LINES"             );
    public static final AlignableColumnNodeType IF_STMT_OPEN_PAREND        = new AlignableColumnNodeType("IF_STMT_OPEN_PAREND"       );
    public static final AlignableColumnNodeType BRACES                     = new AlignableColumnNodeType("BRACES"                    );
    public static final AlignableColumnNodeType METHOD_NAME                = new AlignableColumnNodeType("METHOD_NAME"               );
    public static final AlignableColumnNodeType NEW_TOKEN                  = new AlignableColumnNodeType("NEW_TOKEN"                 );
    public static final AlignableColumnNodeType OPEN_PAREND                = new AlignableColumnNodeType("OPEN_PAREND"               );
    public static final AlignableColumnNodeType OPERATOR                   = new AlignableColumnNodeType("OPERATOR"                  );
    public static final AlignableColumnNodeType PARAM                      = new AlignableColumnNodeType("PARAM"                     );
    public static final AlignableColumnNodeType PARAMS                     = new AlignableColumnNodeType("PARAMS"                    );
    public static final AlignableColumnNodeType PROGRAM                    = new AlignableColumnNodeType("PROGRAM"                   );
    public static final AlignableColumnNodeType QUESTION_MARK              = new AlignableColumnNodeType("QUESTION_MARK"             );
    public static final AlignableColumnNodeType REFERENCE_ELEMENT          = new AlignableColumnNodeType("REFERENCE_ELEMENT"         );
    public static final AlignableColumnNodeType SECONDARY_IF_KEYWORDS      = new AlignableColumnNodeType("SECONDARY_IF_KEYWORDS"     );
    public static final AlignableColumnNodeType START_OF_COLUMN            = new AlignableColumnNodeType("START_OF_COLUMN"           );
    public static final AlignableColumnNodeType STATEMENT                  = new AlignableColumnNodeType("STATEMENT"                 );
    public static final AlignableColumnNodeType STATEMENT_SEMICOLONS       = new AlignableColumnNodeType("STATEMENT_SEMICOLONS"      );
    public static final AlignableColumnNodeType TERM                       = new AlignableColumnNodeType("TERM"                      );
    public static final AlignableColumnNodeType TERMS                      = new AlignableColumnNodeType("TERMS"                     );
    public static final AlignableColumnNodeType THEN_EXPRESSION            = new AlignableColumnNodeType("THEN_EXPRESSION"           );
    public static final AlignableColumnNodeType TRAILING_COMMENTS          = new AlignableColumnNodeType("TRAILING_COMMENTS"         );
    public static final AlignableColumnNodeType VARNAMES                   = new AlignableColumnNodeType("VARNAMES"                  );
    public static final AlignableColumnNodeType VARTYPES                   = new AlignableColumnNodeType("VARTYPES"                  );
    public static final AlignableColumnNodeType WHILE_STMT_LINES           = new AlignableColumnNodeType("WHILE_STMT_LINES"          );
    public static final AlignableColumnNodeType WHILE_KEYWORDS             = new AlignableColumnNodeType("WHILE_KEYWORDS"            );
    public static final AlignableColumnNodeType WHILE_STMT_OPEN_PAREND     = new AlignableColumnNodeType("WHILE_STMT_OPEN_PAREND"    );
    public static final AlignableColumnNodeType WHILE_STMT_CLOSE_PAREND    = new AlignableColumnNodeType("WHILE_STMT_CLOSE_PAREND"   );
    public static final AlignableColumnNodeType RETURN_KEYWORD             = new AlignableColumnNodeType("RETURN_KEYWORD"            );
    public static final AlignableColumnNodeType ARRAY_INITIALIZER          = new AlignableColumnNodeType("ARRAY_INITIALIZER"         );
    public static final AlignableColumnNodeType PREFIX_EXPRESSION_OPERATOR = new AlignableColumnNodeType("PREFIX_EXPRESSION_OPERATOR");
    public static final AlignableColumnNodeType LEFT_BRACE                 = new AlignableColumnNodeType("LEFT_BRACE"                );
    public static final AlignableColumnNodeType RIGHT_BRACE                = new AlignableColumnNodeType("RIGHT_BRACE"               );
    public static final AlignableColumnNodeType ARRAY_TYPE                 = new AlignableColumnNodeType("ARRAY_TYPE"                );
    public static final AlignableColumnNodeType ANNOTATIONS                = new AlignableColumnNodeType("ANNOTATIONS"               );

    private final String name;

    protected AlignableColumnNodeType(String name)
    {
        this.name = name;
    }

    public final String getName()
    {
        return name;
    }

    public final String toString()
    {
        return name;
    }
}
