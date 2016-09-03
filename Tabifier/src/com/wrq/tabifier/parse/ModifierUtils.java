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

import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

/**
 * Contains several utility functions for rearranging and aligning modifiers.
 */
public final class ModifierUtils
{
    /**
     * Calculate the longest possible modifier string given the complete mask of modifiers.
     * This would be straightforward, except that certain modifiers are incompatible and would
     * never occur together; therefore, the space allocated for these mutually exclusive modifiers
     * can be shared.  For example, if 'private' and 'public' are both in the completeMask, the
     * length would not be 7 + 1 + 6 = 14; it would be 7, because no Java object can be both
     * private and public at the same time.
     *
     * @param completeMask  bitmask representing the set of all modifiers in all lines of the group.
     * @return the length of the longest possible modifier string.
     */
    public static int calculateLongestModifierString(int completeMask)
    {
        int result = 0;
        /** first, handle public/private/protected; these are mutually exclusive and occupy the same column. */
        if ((completeMask & Modifier.PROTECTED) > 0)  // PROTECTED is handled first because it is the longest.
        {
            result = Modifier.toString(Modifier.PROTECTED).length();
        }
        else if ((completeMask & Modifier.PRIVATE) > 0)
        {
            result = Modifier.toString(Modifier.PRIVATE).length();
        }
        else if ((completeMask & Modifier.PUBLIC) > 0)
        {
            result = Modifier.toString(Modifier.PUBLIC).length();
        }
        completeMask &= ~(Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED);

        /** Handle synchronized (exclusive from transient or volatile) */
        if ((completeMask & Modifier.SYNCHRONIZED) > 0)
        {
            if (result > 0) result++;
            result       += Modifier.toString(Modifier.SYNCHRONIZED).length();
            completeMask &= ~(Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE);
        }

        /** Handle volatile (exclusive from final) */
        if ((completeMask & Modifier.VOLATILE) > 0)
        {
            if (result > 0) result++;
            result += Modifier.toString(Modifier.VOLATILE).length();
        }
        else if ((completeMask & Modifier.FINAL) > 0)
        {
            if (result > 0) result++;
            result += Modifier.toString(Modifier.FINAL).length();
        }
        completeMask &= ~(Modifier.FINAL | Modifier.VOLATILE);

        for (int i = 1; i != 0; i <<= 1)
        {
            if ((completeMask & i) > 0)
            {
                if (result > 0)
                {
                    result++; // allow for spaces between modifiers
                }
                result = result + Modifier.toString(i).length();
            }
        }
        return result;
    }

    /**
     * Converts string form of modifiers into a mask.
     *
     * @param  modifierString      modifiers to be parsed.
     * @return                     modifier mask corresponding to modifiers in the supplied string.
     */
    public static int getModifierMask(String modifierString)
    {
        /**
         * first, determine the modifier mask for the modifiers in the incoming modifier string.
         */
        final StringTokenizer st   = new StringTokenizer(modifierString);
              int             mask = 0;
        while (st.hasMoreTokens())
        {
            final String m = st.nextToken();
            for (int i = 1; i != 0; i <<= 1)
            {
                if (Modifier.toString(i).equals(m))
                {
                    mask = mask | i;
                    break;
                }
            }
        }
        return mask;
    }

    /**   string buffer to which results will be appended. */
    private StringBuffer buffer;
    /** Bit mask of all modifiers on all lines */
    private int completeMask;
    /** bit mask of all modifiers on this line */
    private int lineMask;

    /**
     * Generate a string containing the same modifiers as the lineMask parameter indicates, but
     * rearranged and spaced for alignment with modifiers indicated by completeMask. Return in
     * canonical order specified by Modifier.toString().
     * <p/>
     * In order to make alignment of modifiers the most attractive, identical modifiers should be
     * aligned. For example, in aligning "private final" and "final", the two "final" keywords
     * should be aligned and the latter should contain spaces corresponding to the position of
     * "private".
     * <p/>
     * To do this, a completely new string is formed which contains the modifiers for the current
     * line separated by blanks where a modifier exists for any other line. This reorders the
     * modifiers according to their bitmask value order specified in the Modifier class.
     * <p/>
     * However, certain syntactically mutually exclusive modifiers can share the same space; for
     * example, no variable may legally be both private and public. Therefore, keywords like
     * private, public and protected should be aligned vertically.
     * Likewise, final and volatile should be aligned vertically.
     *
     * @return string containing reordered, aligned modifiers
     */
    public String generateModifierString(int completeBitmask, int lineBitmask)
    {
        completeMask = completeBitmask;
        lineMask = lineBitmask;
        buffer = new StringBuffer();
        /**
         * special case handling for mutually exclusive modifiers. Allow public/private/protected to
         * occupy the same column; allow synchronized and transient/volatile to occupy the same
         * column.
         */
        generateSelectedModifiers(new int[]{Modifier.PUBLIC, Modifier.PRIVATE, Modifier.PROTECTED});
        generateSelectedModifiers(new int[]{Modifier.ABSTRACT});
        generateSelectedModifiers(new int[]{Modifier.STATIC});
        // in order to ensure that 'volatile' follows 'transient' to comply with JLS sections 8.1.1/8.3.1/8.4.3,
        // and yet to allow volatile and final to occupy the same column (only when there is no 'transient' specified in the complete mask),
        // it's necessary to special case the emitting of 'final' or 'volatile'.
        int[] finalVolatileValues;
        if ((completeMask & Modifier.TRANSIENT) != 0)
        {
            finalVolatileValues = new int[]{Modifier.FINAL};
        } else
        {
            finalVolatileValues = new int[]{Modifier.FINAL, Modifier.VOLATILE};
        }
        generateSelectedModifiers(finalVolatileValues);
        // if 'transient' is not specified, then remove 'volatile' from the lineMask as it would have just been generated by the previous call
        // and we don't want it to be emitted a second time.
        if ((completeMask & Modifier.TRANSIENT) == 0)
        {
            lineMask = lineMask & (~Modifier.VOLATILE);
            completeMask = completeMask & (~Modifier.VOLATILE);
        }
        if ((completeMask & Modifier.TRANSIENT) != 0 &&
                (completeMask & Modifier.VOLATILE) != 0 &&
                (completeMask & Modifier.SYNCHRONIZED) == 0)
        {
            // we have both transient and volatile specified; in this case, they must occupy separate columns since either or both keywords could appear
            // on the line.
            generateSelectedModifiers(new int[]{Modifier.TRANSIENT});
            generateSelectedModifiers(new int[]{Modifier.VOLATILE});
        } else
        {
            generateSelectedModifiers(new int[]{Modifier.TRANSIENT | Modifier.VOLATILE, Modifier.SYNCHRONIZED});
        }
        generateSelectedModifiers(new int[]{Modifier.NATIVE});
        generateSelectedModifiers(new int[]{Modifier.STRICT});
        generateSelectedModifiers(new int[]{Modifier.INTERFACE});
        return buffer.toString();
    }

    /**
     * Modifiers in overlapMask may be aligned in the same column. If completeMask contains any of
     * the overlappable modifiers, figure out the maximum length of the modifiers in (completeMask &
     * overlapMask). Then generate the modifiers in (lineMask & overlapMask) and pad this buffer to
     * the maximum length.
     *
     * @param  overlapMasks        array of modifier masks which may occupy same column.
     */
    private  void generateSelectedModifiers(int[] overlapMasks)
    {
        int completeOverlapMasks = 0;
        for (int overlapMask : overlapMasks)
        {
            completeOverlapMasks |= overlapMask;
        }
        if ((completeMask & completeOverlapMasks) > 0)
        {
            int maxlen = 0;

            for (int overlapMask : overlapMasks)
            {
                if ((completeMask & overlapMask) > 0)
                {
                    int thisLen = Modifier.toString(completeMask & overlapMask).length();
                    if (maxlen < thisLen)
                    {
                        maxlen = thisLen;
                    }
                }
            }
            String s       = Modifier.toString(lineMask & completeOverlapMasks);
            int    spacing = 0;
            if (buffer.length() > 0)
            {
                if (s.length() > 0)
                {
                    /**
                     * we're appending a non-empty string, so we have to pad one space here.
                     * If we were not appending anything, hold off on padding one space -- it
                     * may be possible to combine that pad space with subsequent space and
                     * render it as a tab character.
                     */
                    pad(1);
                }
                else
                    spacing = 1;
            }
            buffer.append(s);
            pad(maxlen + spacing - s.length());
        }
    }

    /**
     * Pad the StringBuffer with the indicated number of spaces.
     *
     * @param  nSpaces             number of columns to pad the buffer.
     */
    private void pad(int nSpaces)
    {
        while (nSpaces-- > 0)
        {
            buffer.append(' ');
        }
    }
}
