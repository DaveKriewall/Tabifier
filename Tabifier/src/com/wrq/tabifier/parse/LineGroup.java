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

import com.wrq.tabifier.columnizer.DocumentParser;
import com.wrq.tabifier.settings.TabifierSettings;
import com.wrq.tabifier.tabifier;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Contains the current group of lines which are being parsed into the column tree.  These lines will be aligned as a
 * group.
 */
public final class LineGroup
{
    private static final Logger logger = Logger.getLogger("com.wrq.tabifier.parse.LineGroup");

    public static final class LineType
    {
        final String name;

        private LineType(String name)
        {
            this.name = name;
        }

        public final String toString()
        {
            return name;
        }
    }

    public static final LineType VAR_OR_FIELD_DECLARATION = new LineType("VAR_OR_FIELD_DECLARATION");
    public static final LineType ASSIGNMENT = new LineType("ASSIGNMENT");
    public static final LineType MULTILINE_METHOD_DECLARATION = new LineType("MULTILINE_METHOD_DECLARATION");
    public static final LineType SINGLELINE_METHOD_DECLARATION = new LineType("SINGLELINE_METHOD_DECLARATION");
    public static final LineType METHOD_CALL = new LineType("METHOD_CALL");
    public static final LineType OTHER_EXPRESSION_STATEMENT = new LineType("OTHER_EXPRESSION_STATEMENT");
    public static final LineType IF_STATEMENT = new LineType("IF_STATEMENT");
    public static final LineType NONE = new LineType("NONE");
    private static final LineType UNKNOWN = new LineType("UNKNOWN_TOKEN_SEQ");

    private final DocumentParser parser;
    private final TabifierSettings settings;
    private final ArrayList<Line> groupedLines;
    public static final ArrayList<Line> reformatableLines = new ArrayList<>();
    private LineType lastStatement;
    private boolean includeCurrentLineInAlignmentGroup;

    public LineGroup(DocumentParser parser, TabifierSettings settings)
    {
        groupedLines = new ArrayList<>();
        this.parser = parser;
        this.settings = settings;
        lastStatement = NONE;
        includeCurrentLineInAlignmentGroup = true;
    }

    public static void reset()
    {
        reformatableLines.clear();
    }

    public void setStatementType(LineType type)
    {
        boolean debug = tabifier.seeingTokensInRange;
        if (debug) logger.debug("setStatementType:" + type);
        if (lastStatement == IF_STATEMENT && type != NONE)
        {
            if (debug) logger.debug("ignoring new type, within if-statement");
            return;
        }
        if (lastStatement == IF_STATEMENT && type == NONE)
        {
            if (debug) logger.debug("resetting type after if-statement");
            lastStatement = type;
            return;
        }
//        if (lastStatement == MULTILINE_METHOD_DECLARATION && type != NONE)
//        {
//            if (debug) logger.debug("ignoring new type, within method declaration");
//            return;
//        }
//        if (lastStatement == MULTILINE_METHOD_DECLARATION && type == NONE)
//        {
//            if (debug) logger.debug("resetting type after method declaration");
//            lastStatement = type;
//            return;
//        }

        LineType oldStatement = lastStatement;
        lastStatement = type;
        if (lastStatement == UNKNOWN && settings.delimit_by_non_blank_lines.get())
        {
            parser.scheduleAlignment("saw unknown line type");
        }
        /**
         * setting the type to NONE after any other type of statement, or having the type be NONE when encountering
         * any type of statement, will not by itself trigger an alignment.
         */
        if (oldStatement == NONE || type == NONE) return;

        if (lastStatement != oldStatement && settings.delimit_by_statement_type.get())
        {
            if (((lastStatement == ASSIGNMENT && oldStatement == VAR_OR_FIELD_DECLARATION) ||
                    (lastStatement == VAR_OR_FIELD_DECLARATION && oldStatement == ASSIGNMENT)) &&
                    settings.group_assignments_with_declarations.get())
            {
                return; // do nothing to separate assignments and field declarations.
            }
            includeCurrentLineInAlignmentGroup = false;
            parser.scheduleAlignment("previous type type=" + oldStatement + ", new=" + lastStatement);
        }
    }

    public boolean immediateAlignmentIndicated()
    {
        return !includeCurrentLineInAlignmentGroup;
    }

    public void addLine(Line line)
    {
        groupedLines.add(line);
    }

    public Line getLastLine()
    {
        return (groupedLines.size() > 1 ? groupedLines.get(groupedLines.size() - 2) : null);
    }
    public ArrayList<Line> getLinesToAlign()
    {
        if (tabifier.seeingTokensInRange)
        {
            logger.debug("getLinesToAlign: include current line in alignment group=" + includeCurrentLineInAlignmentGroup);
        }
        if (includeCurrentLineInAlignmentGroup) return groupedLines;
        ArrayList<Line> result = new ArrayList<>(groupedLines);
        if (result.size() > 0)
        {
            result.remove(result.size() - 1);
        }
        return result;
    }

    public void resetLines()
    {
        if (includeCurrentLineInAlignmentGroup)
        {
            groupedLines.clear();
        }
        else
        {
            if (groupedLines.size() > 0)
            {
                Line currentLine = groupedLines.get(groupedLines.size() - 1);
                groupedLines.clear();
                groupedLines.add(currentLine);
            }
        }
        includeCurrentLineInAlignmentGroup = true;
//        if (lastStatement != IF_STATEMENT) lastStatement = NONE;
    }

    public Line getUngroupedCurrentLine()
    {
        if (includeCurrentLineInAlignmentGroup || groupedLines.size() == 0)
            return null;
        else
        {
            return groupedLines.get(groupedLines.size() - 1);
        }
    }
}
