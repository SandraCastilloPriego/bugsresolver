/*
 * Copyright 2010
 * This file is part of XXXXXX.
 * XXXXXX is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * XXXXXX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * XXXXXX; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * MainWindows.java
 *
 * Created on 30-oct-2010, 11:06:50
 */
package bugsresolver.GUI;

import bugsresolver.GUI.openfile.DatasetOpenDialog;
import bugsresolver.GUI.utils.BasicFilesParserCSV;
import bugsresolver.data.BugDataset;
import bugsresolver.data.PeakListRow;
import bugsresolver.world.Bug;
import bugsresolver.world.World;
import java.util.List;
import bugsresolver.GUI.utils.BasicFilesParserCSV;
import bugsresolver.data.BugDataset;
import bugsresolver.data.PeakListRow;
import bugsresolver.world.Bug;
import java.util.ArrayList;
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.ComplementNaiveBayes;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiScheme;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.meta.Stacking;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 *
 * @author bicha
 */
public class MainWindows extends javax.swing.JFrame {

    BugDataset dataset;
    boolean start = false;
    private sinkThread thread;
    CanvasWorld canvas;
    World world;
    BugDataset validate;
    private Classifier classifier;
    double spec = 0, sen = 0, totalspec = 0, totalsen = 0;
    List<Integer> ids;

    /** Creates new form MainWindows */
    public MainWindows() {
        initComponents();
       // BasicFilesParserCSV parser = new BasicFilesParserCSV("/home/scsandra/Desktop/test2.csv");
       // parser.fillData();
     //   validate = parser.getDataset();
        ids = new ArrayList<Integer>();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        canvasPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        startMenuItem = new javax.swing.JMenuItem();
        stopMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        purgeMenuItem = new javax.swing.JMenuItem();
        addBugsMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout canvasPanelLayout = new javax.swing.GroupLayout(canvasPanel);
        canvasPanel.setLayout(canvasPanelLayout);
        canvasPanelLayout.setHorizontalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 683, Short.MAX_VALUE)
        );
        canvasPanelLayout.setVerticalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 677, Short.MAX_VALUE)
        );

        getContentPane().add(canvasPanel);

        jMenu1.setText("File");

        startMenuItem.setText("Start");
        startMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(startMenuItem);

        stopMenuItem.setText("Stop");
        stopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(stopMenuItem);
        jMenu1.add(jSeparator1);

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        openMenuItem.setText("Open File");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(openMenuItem);

        saveMenuItem.setText("Save Result");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(saveMenuItem);

        purgeMenuItem.setText("Purge Bugs");
        purgeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purgeMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(purgeMenuItem);

        addBugsMenuItem.setText("Add bugs");
        addBugsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBugsMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(addBugsMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        String path = null;
        DatasetOpenDialog openDialog = new DatasetOpenDialog(null, this);
        openDialog.setVisible(true);
        try {
            path = openDialog.getCurrentDirectory();
            if (path != null) {
                BasicFilesParserCSV parser = new BasicFilesParserCSV(path);
                parser.fillData();
                dataset = parser.getDataset();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        dispose();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void startMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMenuItemActionPerformed
        start = false;
        if (dataset != null) {
            world = new World(dataset, 100);
            canvas = new CanvasWorld(world);
            this.canvasPanel.removeAll();
            this.canvasPanel.add(canvas);
            start = true;
            // Starts simulation
            thread = new sinkThread();
            thread.start();
            this.validate();
        }

    }//GEN-LAST:event_startMenuItemActionPerformed

    private void stopMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopMenuItemActionPerformed
        start = false;
    }//GEN-LAST:event_stopMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        try {
            List<Bug> population = world.getPopulation();
            for (Bug bug : population) {
                if (bug.getSensitivity() > 0.70 && bug.getSpecificity() > 0.5 && bug.getAge() > 100) {
                    System.out.println("statistics: " + bug.getAge() + " - " + bug.getClassifierType());
                    for (PeakListRow row : bug.getRows()) {
                        this.addId(row.getID());
                    }

                  //  this.classify(ids);
                  //  this.prediction(ids);
                    for (PeakListRow row : bug.getRows()) {
                        System.out.println(row.getID());
                    }
                    System.out.println("----------------");
                }
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void purgeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purgeMenuItemActionPerformed
        this.world.purgeBugs();
    }//GEN-LAST:event_purgeMenuItemActionPerformed

    private void addBugsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBugsMenuItemActionPerformed
        world.addMoreBugs();
    }//GEN-LAST:event_addBugsMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addBugsMenuItem;
    private javax.swing.JPanel canvasPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem purgeMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem startMenuItem;
    private javax.swing.JMenuItem stopMenuItem;
    // End of variables declaration//GEN-END:variables

    public class sinkThread extends Thread {

        public boolean stopSignal;

        public sinkThread() {
        }

        @Override
        public void run() {

            while (start) {
                // Paint the graphics
                canvas.update(canvas.getGraphics());
                world.cicle();

            }

        }
    }

    public void addId(int id) {
        this.ids.add(id);
    }

    private void classify(List<Integer> ids) {
        try {

            FastVector attributes = new FastVector();

            for (int i = 0; i < ids.size(); i++) {
                Attribute weight = new Attribute("weight" + i);
                attributes.addElement(weight);
            }

            FastVector labels = new FastVector();

            labels.addElement("1");
            labels.addElement("2");
            Attribute type = new Attribute("class", labels);

            attributes.addElement(type);

            //Creates the dataset
            Instances data = new Instances("Dataset", attributes, 0);
            // int numberOfPeaks = this.rowList.get(0).getNumberPeaks();
            int numberForTraining = 287;// (int) (numberOfPeaks * 0.6);
            for (int i = 0; i < numberForTraining; i++) {
                double[] values = new double[data.numAttributes()];
                String sampleName = validate.getAllColumnNames().elementAt(i);
                int cont = 0;
                for (Integer id : ids) {
                    for (PeakListRow row : validate.getRows()) {
                        if (row.getID() == id) {
                            values[cont++] = (Double) row.getPeak(sampleName);
                        }
                    }
                }
                values[cont] = data.attribute(data.numAttributes() - 1).indexOfValue(this.dataset.getType(sampleName));

                Instance inst = new SparseInstance(1.0, values);
                data.add(inst);
            }

            data.setClass(type);
            classifier = new RandomSubSpace();
            classifier.buildClassifier(data);
        } catch (Exception ex) {
        }
    }

    private void prediction(List<Integer> ids) {
        try {

            FastVector attributes = new FastVector();

            for (int i = 0; i < ids.size(); i++) {
                Attribute weight = new Attribute("weight" + i);
                attributes.addElement(weight);
            }

            FastVector labels = new FastVector();

            labels.addElement("1");
            labels.addElement("2");
            Attribute type = new Attribute("class", labels);

            attributes.addElement(type);

            //Creates the dataset
            Instances train = new Instances("Dataset", attributes, 0);

            int numberForTraining = 287;// (int) (numberOfPeaks * 0.6);
            for (int i = numberForTraining; i < dataset.getNumberCols(); i++) {
                double[] values = new double[train.numAttributes()];
                String sampleName = dataset.getAllColumnNames().elementAt(i);
                int cont = 0;
                for (Integer id : ids) {
                    for (PeakListRow row : dataset.getRows()) {
                        if (row.getID() == id) {
                            values[cont++] = (Double) row.getPeak(sampleName);
                        }
                    }
                }
                values[cont] = train.attribute(train.numAttributes() - 1).indexOfValue(this.dataset.getType(sampleName));

                Instance inst = new SparseInstance(1.0, values);
                train.add(inst);
            }

            train.setClass(type);
            System.out.println("# - actual - predicted - distribution");
            for (int i = numberForTraining; i < dataset.getNumberCols(); i++) {
                try {
                    double pred = classifier.classifyInstance(train.instance(i));
                    double[] dist = classifier.distributionForInstance(train.instance(i));
                    /*System.out.print((i + 1) + " - ");
                    System.out.print(train.instance(i).toString(train.classIndex()) + " - ");
                    System.out.print(train.classAttribute().value((int) pred) + " - ");
                    System.out.println(Utils.arrayToString(dist));*/

                    if (train.instance(i).toString(train.classIndex()).equals("1")) {
                        this.totalspec++;
                        if (train.classAttribute().value((int) pred).equals("1")) {
                            this.spec++;
                        }
                    } else {
                        this.totalsen++;
                        if (train.classAttribute().value((int) pred).equals("2")) {
                            this.sen++;
                        }
                    }
                } catch (Exception eeee) {
                }
            }

            double specificity = spec / totalspec;
            double sensitivity = sen / totalsen;
            System.out.println("spec: " + specificity + " sen: " + sensitivity);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

