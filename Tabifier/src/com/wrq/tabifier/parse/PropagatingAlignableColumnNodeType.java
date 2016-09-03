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
 * Marker class indicating that the alignment of this column is to propagate to the first column below with tokens.
 * For example, a column of parameters ("PARAM") may be aligned.  If so, then the contents of the parameters (method
 * calls, expressions) must be aligned regardless of their settings.  So the parameter column is marked with an object
 * of this class. The TokenColumn.align() method detects the presence of a marker and overrides the alignment of the
 * subordinate column node.
 */
public class PropagatingAlignableColumnNodeType
        extends AlignableColumnNodeType
{
    public static final PropagatingAlignableColumnNodeType PROPAGATING_PARAM =
            new PropagatingAlignableColumnNodeType("PROPAGATING_PARAM");
    public static final PropagatingAlignableColumnNodeType PROPAGATING_PARAM_DECL =
            new PropagatingAlignableColumnNodeType("PROPAGATING_PARAM_DECL");

    public PropagatingAlignableColumnNodeType(String name)
    {
        super(name);
    }
}
