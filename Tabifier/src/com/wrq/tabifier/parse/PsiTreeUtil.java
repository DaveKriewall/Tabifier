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

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;

/**
 * method and support classes to walk the PsiTree within the selection start and end, breaking
 * statements into groups based on their nesting level. Only statements at the same nesting level
 * and with no intervening changes in nesting level can affect each others' formatting. Each group
 * of "adjacent" PsiStatements (at the same nesting level) is passed in turn to the user-supplied
 * processRange() method for analysis or reformatting.
 * <p>
 * Caller creates a PsiTreeUtil.State object with initial PsiElement and text offset of end of
 * selection. Caller can subclass this class to add any additional data it desires to pass to the
 * processRange() method.
 * <p>
 * Caller then constructs an EndAwarePsiRecursiveElementVisitor with that State object and with any
 * desired hooks (by overriding methods of PsiRecursiveElementVisitor).
 * <p>
 * Finally, caller invokes visitSelectionElements with the visitor.
 */
public final class PsiTreeUtil
{
    public static String elname(final PsiElement e)
    {
        if (e == null) return "null";
        String s = e.toString();
        if (s.length() > 40)
        {
            s = s.substring(0, 40);
        }
        s = s.replaceAll("\n", "<newline>");
        s = s.replaceAll(" ", "'");
        if (e instanceof PsiDirectory)
        {
            return s; // directory has no text range
        }
        return s + " [" +
                e.getTextRange().getStartOffset() + "," +
                e.getTextRange().getEndOffset() + "]";
    }

}
