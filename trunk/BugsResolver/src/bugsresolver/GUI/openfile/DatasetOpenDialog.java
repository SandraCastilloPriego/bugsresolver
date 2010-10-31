/*
Copyright 2007-2010 VTT Biotechnology

This file is part of GUINEU.

 */
package bugsresolver.GUI.openfile;

import bugsresolver.GUI.utils.ExitCode;
import com.sun.java.ExampleFileFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * File open dialog
 */
public class DatasetOpenDialog extends JDialog implements ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private JFileChooser fileChooser;
    private String datasetFile;
    private ExitCode exit = ExitCode.UNKNOWN;

    public DatasetOpenDialog(File lastpath, JFrame mainFrame) {

        super(mainFrame, "Please select a dataset file to open...", true);

        logger.finest("Displaying dataset open dialog");

        fileChooser = new JFileChooser();
        if (lastpath != null) {
            fileChooser.setCurrentDirectory(lastpath);
        }
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addActionListener(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        ExampleFileFilter csv = new ExampleFileFilter();
        csv.addExtension("csv");
        csv.addExtension("xls");
        csv.setDescription("Excel and Comma Separated Files");
        fileChooser.addChoosableFileFilter(csv);
        fileChooser.setFileFilter(csv);

        add(fileChooser, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(mainFrame);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        // check if user clicked "Open"

        if (command.equals("ApproveSelection")) {
            try {
                datasetFile = fileChooser.getSelectedFile().getAbsolutePath();
                exit = ExitCode.OK;
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(this,
                        "Could not open dataset file", "Dataset opening error",
                        JOptionPane.ERROR_MESSAGE);
                logger.fine("Could not open dataset file." + e.getMessage());
                exit = ExitCode.CANCEL;
            }
        } else {
            exit = ExitCode.CANCEL;
        }
        // discard this dialog
        dispose();
    }

    public String getCurrentDirectory() {
        return this.datasetFile;
    }

    public ExitCode getExitCode() {
        return exit;
    }
}
