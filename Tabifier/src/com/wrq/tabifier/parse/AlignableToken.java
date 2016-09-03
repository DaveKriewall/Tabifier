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

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

import java.util.List;
import java.util.ListIterator;

/**
 * Contains common attributes for Psi-based and indentation tokens.
 */
public final class AlignableToken
{
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.parse.AlignableToken");
    private final String  value;
    private Line    line;
    /**
     * column to which this token will be aligned, or -1 if appended to previous token.
     */
    private int linePosition;
    /**
     * some tokens, such as modifiers, can be rearranged or extended with spaces.  Once lines have been grouped
     * together, but before tabstops are determined, alternate representations of the tokens can be determined.
     * These will then be used instead of the original value.
     */
    private String alternateRepresentation;
    /**
     * the column node to which this token belongs.  May be null if token was unrecognized (and hence will be
     * unaligned.)
     */
    private TokenColumn column;
    /** true if the element is white space (a PsiWhiteSpace element, or a substring of another element. */
    private boolean whiteSpace;
    /** element whose value token represents. */
    private final PsiElement element;
    /**
     * For PsiWhiteSpace elements, the token may represent only a substring of the element -- in particular,
     * newlines and space are treated separately.
     */
    private int     elementOffset;
    private int     elementLength;
    private boolean isSubElement;  // true for PsiWhiteSpace elements -- see above
    private boolean rightJustified;
    private boolean appendSpace; // true if the token had a space appended because of code style settings

    public AlignableToken(PsiElement element)
    {
        this.element   = element;
        column         = null;
        line           = null;
        rightJustified = false;
        elementOffset  = 0;
        isSubElement   = false;
        value          = element.getText         ();
        elementLength  = element.getText().length();
        whiteSpace     = element instanceof PsiWhiteSpace;
        appendSpace    = false;
    }

    public AlignableToken(PsiElement element, int offset, int length)
    {
        this(element);
        elementOffset = offset;
        elementLength = length;
        isSubElement  = true;
        whiteSpace    = (element.getText().length() > 0);
        for (int i = offset; (i < offset+length) && whiteSpace; i++) {
            final char c = element.getText().charAt(i);
            whiteSpace &= (c == ' ' || c == '\n' || c == '\t');
        }
    }

    /**
     * Certain types of IDEA tokens have special formatting rules, where a space is prefixed or appended to the
     * Java token (operators, parentheses, commas, semicolons, etc.)
     *
     * @param element      element whose text content comprises the token
     * @param prefixSpace  true if code style settings require a space to be emitted before the element
     * @param appendSpace  true if code style settings require a space to be emitted after the element
     */
    public AlignableToken(PsiElement element, boolean prefixSpace, boolean appendSpace)
    {
        this(element);
        String s = element.getText();
        if (prefixSpace)
        {
            s = " " + s;
        }
        if (appendSpace)
        {
            s = s + " ";
        }
        alternateRepresentation = s;
        this.appendSpace = appendSpace;
    }

    public final String getValue()
    {
        return alternateRepresentation == null ?
                (isSubElement ? value.substring(elementOffset, elementOffset + elementLength) : value)
                : alternateRepresentation;
    }

    public final String getOriginalValue()
    {
        return value;
    }

    public final Line getLine()
    {
        return line;
    }

    public final void setLine(Line line)
    {
        this.line = line;
    }

    public final int getLinePosition()
    {
        return linePosition;
    }

    public final void setLinePosition(int linePosition)
    {
        this.linePosition = linePosition;
        logger.debug("setLinePosition: " + linePosition + " for token '" + getValue() + "'");
    }

    public final void setAlternateRepresentation(String alternateRepresentation)
    {
        this.alternateRepresentation = alternateRepresentation;
        if (whiteSpace && alternateRepresentation.replaceAll("[ \n\t]", "").length() > 0) {
            whiteSpace = false;
        }
    }

    public final TokenColumn getColumn()
    {
        return column;
    }

    public final void setTokenColumn(TokenColumn tbc)
    {
        this.column = tbc;
    }

    public final int getWidthIgnoringTrailingPadding()
    {
        return getWidth() - (appendSpace ? 1 : 0);
    }

    public final int getWidth()
    {
        return getValue().length();
    }

    public final boolean isWhiteSpace()
    {
        return whiteSpace;
    }

    public final boolean isRightJustified()
    {
        return rightJustified;
    }

    public final PsiElement getElement()
    {
        return element;
    }

    public final String toString()
    {
        return getValue();
    }

    public final int getElementOffset()
    {
        return elementOffset;
    }

    public final int getElementLength()
    {
        return elementLength;
    }

    public final void setRightJustified()
    {
        this.rightJustified = true;
    }

    public boolean isAppendSpace()
    {
        return appendSpace;
    }

    public void setAppendSpace(boolean appendSpace)
    {
        this.appendSpace = appendSpace;
    }

    /**
     * Returns true if the previous token ends with a Java identifier character and this token begins with a valid start of Java identifier
     * (including "@", which is a valid start of an annotation identifier.)
     * @param previousToken previous non-whitespace token on the line; null if none.
     * @return
     */
    public final boolean needSpaceAfterPreviousToken(AlignableToken previousToken)
    {
        return previousToken != null                                                                                &&
               getValue().length() > 0                                                                              &&
               previousToken.getValue().length() > 0                                                                &&
               (Character.isJavaIdentifierStart(getValue().charAt(0)) || getValue().charAt(0) == '@')               &&
               Character.isJavaIdentifierPart(previousToken.getValue().charAt(previousToken.getValue().length() - 1));
    }

    /**
     * Find the rightmost non-blank (non-whitespace) token to the left of this token on this token's line.
     * @return previous token, or null if none (or if the line is immutable)
     */
    public final AlignableToken findPreviousNonBlankToken()
    {
        final Line line = getLine();
        if (line.isImmutable()) return null;
        AlignableToken previousToken = null;
        List/*<AlignableToken>*/ tokens = line.getTokens();
        ListIterator/*<AlignableToken>*/ lit = tokens.listIterator(tokens.indexOf(this));
        while (lit.hasPrevious())
        {
            AlignableToken temp = (AlignableToken) lit.previous();
            if (!(temp.isWhiteSpace()) && temp.getValue().length() > 0)
            {
                previousToken = temp;
                break;
            }
        }
        return previousToken;
    }

    public static AlignableToken createToken(
            PsiJavaToken child,
            final CodeStyleSettings codeStyleSettings,
            final TabifierSettings settings
    )
    {
        AlignableToken result;
        if (child.getTokenType() == JavaTokenType.EQ ||
                child.getTokenType() == JavaTokenType.PLUSEQ ||
                child.getTokenType() == JavaTokenType.MINUSEQ ||
                child.getTokenType() == JavaTokenType.ASTERISKEQ ||
                child.getTokenType() == JavaTokenType.DIVEQ ||
                child.getTokenType() == JavaTokenType.PERCEQ ||
                child.getTokenType() == JavaTokenType.ANDEQ ||
                child.getTokenType() == JavaTokenType.OREQ ||
                child.getTokenType() == JavaTokenType.GTGTEQ ||
                child.getTokenType() == JavaTokenType.LTLTEQ ||
                child.getTokenType() == JavaTokenType.GTGTGTEQ)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS &&
                            (!settings.no_space_before_assignment_operators.get()),
                    codeStyleSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.EQEQ ||
                child.getTokenType() == JavaTokenType.NE)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_EQUALITY_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_EQUALITY_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.LE ||
                child.getTokenType() == JavaTokenType.GE ||
                child.getTokenType() == JavaTokenType.LT ||
                child.getTokenType() == JavaTokenType.GT)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_RELATIONAL_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_RELATIONAL_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.GTGT ||
                child.getTokenType() == JavaTokenType.LTLT ||
                child.getTokenType() == JavaTokenType.GTGTGT)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_SHIFT_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_RELATIONAL_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.PLUS ||
                child.getTokenType() == JavaTokenType.MINUS)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_ADDITIVE_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_ADDITIVE_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.ASTERISK ||
                child.getTokenType() == JavaTokenType.DIV ||
                child.getTokenType() == JavaTokenType.PERC)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.ANDAND ||
                child.getTokenType() == JavaTokenType.OROR)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_LOGICAL_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_LOGICAL_OPERATORS);
        } else if (child.getTokenType() == JavaTokenType.AND ||
                child.getTokenType() == JavaTokenType.OR ||
                child.getTokenType() == JavaTokenType.TILDE)
        {
            result = new AlignableToken(child,
                    codeStyleSettings.SPACE_AROUND_BITWISE_OPERATORS,
                    codeStyleSettings.SPACE_AROUND_BITWISE_OPERATORS);
        } else
        {
            result = new AlignableToken(child);
        }
        return result;
    }
}
