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

/**
 * Id$
 *
 * Tabifier (major release 2) plugin for IntelliJ IDEA.  Based on Jordan Zimmerman's work in release 1, but
 * completely rewritten to support more flexible alignment for any type of syntactic arrangement.
 *
 * Source code may be freely copied and reused.  Please copy credits, and send any bug fixes to the author.
 *
 * @author Dave Kriewall, WRQ, Inc.
 * September, 2003
 */
package org.intellij.psi.codeStyle;

import org.apache.log4j.Logger;

/**
 * Code to hook the reformat plugin so that subsequent tabification of the reformatted file occurs.
 * Placed here, in a separate package, to avoid class loader problems when tabifier plugin is
 * present but reformat plugin is not.
 */
public final class TabifierUtility
{
    private static final Logger  logger                     = Logger.getLogger("com.wrq.tabifier");
            static       boolean chain_from_reformat_plugin;

    public static void hookReformatPlugin()
    {
//        final ReformatManager rm = ReformatManager.getInstance();
//        rm.addReformatListener(new ReformatListener()
//        {
//            public void psiElementReformatted(final ReformatEvent event)
//            {
//                logger.debug("ReformatListener notification; chain=" + chain_from_reformat_plugin +
//                             ", isSelectionReformatted=" + event.isSelectionReformatted());
//                if (!chain_from_reformat_plugin)
//                    return;
//                if (event.isSelectionReformatted())
//                {
//                    /** tabify selection; identical to being called directly from IDEA. */
//                    new TabifierActionHandler().executeWriteAction(event.getEditor(), event.getDataContext());
//                }
//                else {
//                    /** tabify entire file. */
//                new TabifierActionHandler().tabify(event.getEditor(), event.getDataContext());
//                }
//            }
//        });
    }

    public static void setChainFromReformatPlugin(final boolean chain)
    {
        chain_from_reformat_plugin = chain;
    }
}
