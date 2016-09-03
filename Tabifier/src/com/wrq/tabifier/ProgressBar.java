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
package com.wrq.tabifier;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Code to handle progress bar display.  All updating of documents must happen on the Swing dispatch
 * thread.  All inspection (and alignment calculations) can happen on another thread.
 */
public class ProgressBar
{

    private static final Logger       logger        = Logger.getLogger("com.wrq.rearranger.ProgressBar");

                         int          maxValue;
                         int          progressValue;

                         boolean      isCancelled;
                         JProgressBar progressBar;
                         JLabel       filename;
                   final Project      project;
                         JDialog      dialog;

    public ProgressBar(Project project)
    {
        progressBar  = new JProgressBar(JProgressBar.HORIZONTAL);
        filename     = new JLabel();
        this.project  =   project;
    }

    public boolean isCancelled()
    {
        return isCancelled;
    }

    public void setMaximum(int maximum)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new RuntimeException("Swing update function called on non-swing dispatch thread");
        }
        progressBar.setMaximum(maximum); // must be called by Swing dispatch thread
    }

    public void setCurrent(int progress)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new RuntimeException("Swing update function called on non-swing dispatch thread");
        }
        logger.debug("setCurrent:progress=" + progress + ", max=" + maxValue);
        progressBar.setValue(progress);                                        // must be called by Swing dispatch thread
        progressBar.repaint();
    }

    public void setFilename(String filename)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new RuntimeException("Swing update function called on non-swing dispatch thread");
        }
        logger.debug("setFilename=" + filename);
        this.filename.setText(filename);         // must be called by Swing dispatch thread
        this.filename.repaint();
    }

    /**
     * returns an unpacked JDialog containing the progress bar.
     * 
     * @return 
     */
    public JDialog getProgressBarDialog()
    {
        final Object parent = WindowManager.getInstance().suggestParentWindow(project);
        logger.debug("suggested parent window=" + parent);
        dialog = parent == null? new JDialog()
                                :   (parent instanceof JDialog ? new JDialog((JDialog) parent)
                                                               : new JDialog((JFrame) parent) );
        final Container          pane              =   dialog.getContentPane();
        final JLabel             rearrangingLabel = new JLabel("Rearranging files, please wait...");
        final JPanel             progressPanel    = new JPanel(new GridBagLayout());
        final GridBagConstraints constraints      = new GridBagConstraints();
        constraints.anchor      =   GridBagConstraints.NORTHWEST;
        constraints.gridx       =   0;
        constraints.gridy       =   0;
        constraints.fill        =   GridBagConstraints.NONE;
        constraints.gridwidth   =   GridBagConstraints.REMAINDER;
        constraints.gridheight  =   1;
        constraints.weightx     =   1;
        constraints.insets     = new Insets(15, 15, 0, 0);
        progressPanel.add(rearrangingLabel, constraints);
        constraints.gridwidth = 1;
        constraints.gridy     = 1;
        progressBar.setMinimum    (   0                  );
        progressBar.setPreferredSize(new Dimension(500, 15));
        progressBar.setMinimumSize(new Dimension(500, 15));
        progressBar.setStringPainted(true);
        constraints.insets = new Insets(15, 15, 5, 15);
        progressPanel.add(progressBar, constraints);
        constraints.gridwidth  =   GridBagConstraints.REMAINDER;
        constraints.gridx      =   1;
        constraints.insets    = new Insets(9, 0, 5, 15);
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(final ActionEvent e)
                    {
                        logger.debug("cancel button pressed");
                        filename.setText("cancelling...");
                        isCancelled = true;
                    }
                }
                );
        progressPanel.add(cancelButton, constraints);
        constraints.gridx       =   0;
        constraints.gridy       =   2;
        constraints.weighty     =   1;
        constraints.anchor      =   GridBagConstraints.NORTHWEST;
        constraints.gridheight  =   GridBagConstraints.REMAINDER;
        constraints.insets     = new Insets(0, 15, 15, 0);
        filename.setSize(500, 15);
        filename.setPreferredSize(new Dimension(500, 15));
//        filename.setFont(new Font("dialog", Font.PLAIN, 12));
        progressPanel.add(filename, constraints);
        pane.add(progressPanel, BorderLayout.CENTER);
        dialog.setTitle("Tabifier");
        dialog.addWindowListener(
                new WindowListener()
                {
                    public void windowClosed(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }

                    public void windowActivated(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }

                    public void windowClosing(final WindowEvent e)
                    {
                        logger.debug("dialog closed, cancel Tabifier");
                        filename.setText("cancelling...");
                        isCancelled = true;
                    }

                    public void windowDeactivated(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }

                    public void windowDeiconified(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }

                    public void windowIconified(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }

                    public void windowOpened(final WindowEvent e)
                    {
                        //To change body of implemented methods use Options | File Templates.
                    }
                }
                );
        return dialog;
    }

    public void showDialog()
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new RuntimeException("Swing update function called on non-swing dispatch thread");
        }
        dialog.pack();
        dialog.setVisible(true);
        dialog.repaint();
    }

    public void closeDialog()
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new RuntimeException("Swing update function called on non-swing dispatch thread");
        }
        dialog.setVisible(false);
    }
}

