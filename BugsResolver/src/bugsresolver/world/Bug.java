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
package bugsresolver.world;

import bugsresolver.data.BugDataset;
import bugsresolver.data.PeakListRow;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 *
 * @author bicha
 */
public class Bug {

    private Cell cell;
    private int x, y;
    private List<PeakListRow> rowList;
    private int life = 100;
    private BugDataset dataset;
    private Classifier classifier;
    private Instances data;

    public Bug(int x, int y, Cell cell, PeakListRow row, BugDataset dataset) {
        this.cell = cell;
        this.dataset = dataset;
        this.x = x;
        this.y = y;
        this.rowList = new ArrayList<PeakListRow>();
        this.rowList.add(row);
        this.classify();
    }

    public Bug(Bug father, Bug mother) {
        this.cell = father.getCell();
        this.x = father.getx();
        this.y = father.gety();
        this.rowList = new ArrayList<PeakListRow>();
        for (PeakListRow row : father.getRows()) {
            this.rowList.add(row);
        }
        for (PeakListRow row : mother.getRows()) {
            this.rowList.add(row);
        }
        this.classify();
    }

    public List<PeakListRow> getRows() {
        return this.rowList;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getx() {
        return this.x;
    }

    public int gety() {
        return this.y;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return this.cell;
    }

    boolean isDead() {
        this.life--;
        if (this.life == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void classify() {
        try {

            FastVector attributes = new FastVector();
            //   Attribute type = new Attribute("type");
            //  attributes.addElement(type);
            for (int i = 0; i < rowList.size(); i++) {
                Attribute weight = new Attribute("weight" + i);
                attributes.addElement(weight);
            }

            //Creates the dataset
            data = new Instances("Dataset", attributes, 0);
            int numberOfPeaks = this.rowList.get(0).getNumberPeaks();
            int numberForTraining = (int) (numberOfPeaks * 0.6);
            for (int i = 0; i < numberForTraining; i++) {
                double[] values = new double[data.numAttributes()];
                String sampleName = dataset.getAllColumnNames().elementAt(i);
                //  values[0] = Double.parseDouble(this.dataset.getType(sampleName));
                int cont = 0;
                for (PeakListRow row : rowList) {
                    values[cont++] = (Double) row.getPeak(sampleName);
                }
                Instance inst = new SparseInstance(1.0, values);
                data.add(inst);
            }



            data.setClassIndex(data.numAttributes() - 1);
// train NaiveBayes
            classifier = new LogisticBase();//.buildClassifier(data);



            // classifier = new Logistic();
            classifier.buildClassifier(data);
        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eat() {
        String sampleName = this.cell.getSampleName();



        FastVector attributes = new FastVector();
        //   Attribute type = new Attribute("type");
        //  attributes.addElement(type);
        for (int i = 0; i < rowList.size(); i++) {
            Attribute weight = new Attribute("weight" + i);
            attributes.addElement(weight);
        }

        //Creates the dataset
        Instances train = new Instances("Train Dataset", attributes, 0);

        double[] values = new double[data.numAttributes()];

        //  values[0] = Double.parseDouble(this.dataset.getType(sampleName));
        int cont = 0;
        for (PeakListRow row : rowList) {
            values[cont++] = (Double) row.getPeak(sampleName);
        }
        Instance inst = new SparseInstance(1.0, values);
        data.add(inst);



        Evaluation eval = null;
        try {
            eval = new Evaluation(data);
        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            eval.evaluateModel(classifier, train);
            System.out.println(eval.toSummaryString("\nResults\n\n", false));
        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
