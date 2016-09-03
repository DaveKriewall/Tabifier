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
 * Tabifier (major release 2) plugin for IntelliJ IDEA.  Based on Jordan Zimmerman's work in release 1, but
 * completely rewritten to support more flexible alignment for any type of syntactic arrangement.
 *
 * Source code may be freely copied and reused.  Please copy credits, and send any bug fixes to the author.
 *
 * @author Dave Kriewall, WRQ, Inc.
 * September, 2003
 */
package com.wrq.tabifier;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.JDOMExternalizable;
import com.wrq.tabifier.settings.TabifierSettings;
import com.wrq.tabifier.ui.TabifierSettingsPanel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

/**
 * The tabifier is an IntelliJ IDEA plugin that aligns various syntactic elements of field and
 * variable declarations and assignment statements. The user selects a region of text (if none is
 * selected, the current line is used) and invokes the tabifier with Ctrl-Shift-Alt-T or
 * Edit...Tabifier. The selection is expanded to include full lines. Text in the selection is broken
 * into groups according to various rules. Indentation level changes (denoted with braces in the
 * text) always separates groups. Additional rules may break groups into smaller "subgroups."
 * Finally, the text in each group is aligned by parsing each statement into a series of tokens of
 * the various alignable syntactic elements.  The plugin calculates the column offsets (tabstops) of
 * each syntactic element to be aligned (such as type, identifier, or trailing comment), and
 * adjusts white space to cause vertical alignment of these syntactic elements at the desired
 * location.
 */
public final class tabifier
        implements ApplicationComponent, Configurable, JDOMExternalizable
{
// ------------------------------ FIELDS ------------------------------

    public  static final String VERSION        = "5.9.1";
    /** boolean to help control excess debugging output.  Is true when processing tokens in range; otherwise false. */
    public static boolean seeingTokensInRange;
    private static final String COMPONENT_NAME = "Tabifier";
    private static final Logger logger         = Logger.getLogger("com.wrq.tabifier");

    private transient TabifierSettingsPanel preferences_panel_mbr;
    private           TabifierSettings      settings              = new TabifierSettings();

    /**
     * if the Reformat plugin is present, obtain a reference to our TabifierUtility class, which will register
     * for callbacks from the Reformat plugin.
     */
    private Class tabifierUtilityClass = null;

// --------------------------- CONSTRUCTORS ---------------------------

    public tabifier()
    {
        setApplicationFlags();                         // done here to potentially enable debug logging.
        logger.debug("tabifier() constructor called");
        preferences_panel_mbr = null;
        /**
         * If the Reformat plugin is present, register for a callback after code reformatting
         * takes place.  This allows user to optimize imports, reformat code, and tabify all
         * with one keystroke.
         */
        try
        {
            if (Class.forName("org.intellij.psi.codeStyle.ReformatManager") != null)
            {
                /**
                 * Reformat plugin is available.  Hook it by calling code in TabifierUtility.
                 * Do this dynamically using reflection so we don't get class loading problems.
                 */
                logger.debug("found ReformatManager class, attempting to hook.");
                tabifierUtilityClass = Class.forName("org.intellij.psi.codeStyle.TabifierUtility");
                final Method hookit = tabifierUtilityClass.getMethod("hookReformatPlugin", new Class[]{});
                hookit.invoke(null, (Object[])null);
            }
        }
        catch (ClassNotFoundException e)
        {
            /**
             * no reformat plugin available. Just continue with normal tabifier behavior.
             */
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            logger.info(e.toString(), e);
        }
        setApplicationFlags();
    }

    /**
     * set any application-wide values after a potential settings change.
     */
    private void setApplicationFlags()
    {
        logger.setLevel(settings.debug.get() ? Level.DEBUG : Level.INFO);
        if (tabifierUtilityClass != null)
        {
            try
            {
                final Method enable = tabifierUtilityClass.getMethod("setChainFromReformatPlugin",
                                                                     new Class[]{boolean.class}   );
                enable.invoke(null, new Object[]{Boolean.valueOf(settings.chain_from_reformat_plugin.get())});
            }
            catch (NoSuchMethodException e)
            {
                logger.info("could not find setChainFromReformatPlugin method", e);
            }
            catch (SecurityException | IllegalAccessException | InvocationTargetException e)
            {
                logger.info("problem with setChainFromReformatPlugin method", e);
            }
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public final TabifierSettings getSettings()
    {
        return settings;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface BaseComponent ---------------------


    public final void initComponent()
    {
        logger.debug("tabifier.initComponent()");
    }

    public final void disposeComponent()
    {
        logger.debug("tabifier.disposeComponent()");
    }

// --------------------- Interface Configurable ---------------------


    public final String getDisplayName()
    {
        return COMPONENT_NAME;
    }

    public final String getHelpTopic()
    {
        return null;
    }

// --------------------- Interface JDOMExternalizable ---------------------


    public final void readExternal(final Element element) //throws InvalidDataException
    {
        logger.debug("tabifier.readExternal()");
        final List entries = element.getChildren("tabifier");
        if (entries.size() > 0)
        {
            final Element entry = (Element) entries.get(0);
            settings.readExternal(entry);
        }
        setApplicationFlags();
    }

    public final void writeExternal(final Element element) //throws WriteExternalException
    {
        logger.debug("tabifier.writeExternal()");
        final Element our_element = new Element("tabifier");
        settings.writeExternal(our_element);
        element.getChildren().clear();
        element.addContent(our_element);
        setApplicationFlags();
    }

// --------------------- Interface NamedComponent ---------------------


    @NotNull
    public final String getComponentName()
    {
        return COMPONENT_NAME;
    }

// --------------------- Interface UnnamedConfigurable ---------------------

    public final JComponent createComponent()
    {
        logger.debug("tabifier.createComponent(), thread=" + Thread.currentThread().toString());
        preferences_panel_mbr = new TabifierSettingsPanel(settings, ProjectManager.getInstance().getDefaultProject());

        return preferences_panel_mbr;
    }

    public final boolean isModified()
    {
        final boolean result = preferences_panel_mbr.getSettings().equals(settings);
        logger.debug("tabifier.isModified(): returning " + (!result));
        return (!result);
    }

    public final void apply() //throws ConfigurationException
    {
        logger.debug("tabifier.apply()");
        settings = (TabifierSettings) preferences_panel_mbr.getSettings().deepCopy();
        setApplicationFlags();
    }

    public final void reset()
    {
        logger.debug("tabifier.reset()");
    }

    public final void disposeUIResources()
    {
        if (preferences_panel_mbr != null)
        {
            preferences_panel_mbr.dispose();
            preferences_panel_mbr = null;
        }
    }

// -------------------------- OTHER METHODS --------------------------

    public final Icon getIcon()
    {
        final URL iconURL = this.getClass().getClassLoader().getResource("com/wrq/tabifier/TabifierIcon.png");
        if (iconURL != null)
        {
            return new ImageIcon(iconURL, "Tabifier");
        }
        return null;
    }
}

