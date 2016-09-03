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

import com.wrq.tabifier.settings.RearrangeableColumnSetting;
import com.wrq.tabifier.settings.TabifierSettings;
import org.apache.log4j.Logger;

import java.util.ListIterator;

/**
 * A ModifierTokenColumn is a token column which contains Java modifiers, which may be rearranged within the token
 * before being rendered.
 */
public final class ModifierTokenColumn extends TokenColumn
{
    private              int    combinedModifierMask = 0;
    private static final Logger logger               = Logger.getLogger("com.wrq.tabifier.parse.ModifierTokenColumn");

    public ModifierTokenColumn(RearrangeableColumnSetting modifierColumnSetting,
                       AlignableColumnNodeType nodeType,
                       int tab_spacing,
                       ColumnSequence sequenceHead,
                       TabifierSettings settings)
    {
        super(modifierColumnSetting, nodeType, tab_spacing, sequenceHead, settings);
    }
    
    public final void addToken(AlignableToken token)
    {
        super.addToken(token);
        combinedModifierMask |= ModifierUtils.getModifierMask(token.getOriginalValue());
    }

    public final void calculateAlternateRepresentations(int indentBias)
    {
        RearrangeableColumnSetting cs = (RearrangeableColumnSetting) getColumnSetting();
        logger.debug("calc alternate representations for modifiers; subaligned=" + cs.isRearrange() +
                     ", column="                                                 + getColumnSetting().toString());
        if (cs.isAligned() && cs.isRearrange())
        {
            ListIterator/*<AlignableToken>*/ iterator = getTokenList(indentBias).listIterator();
            while (iterator.hasNext())
            {
                      AlignableToken token    = (AlignableToken) iterator.next();
                      int            lineMask = ModifierUtils.getModifierMask(token.getValue());
                final String         mods     = new ModifierUtils().generateModifierString(combinedModifierMask, lineMask);
                token.setAlternateRepresentation(mods);
            }
        }
        super.calculateAlternateRepresentations(indentBias);
    }

    public void clearTokens(Line except, int indentBias)
    {
        super.clearTokens(except, indentBias);
        combinedModifierMask = 0;
    }
}
