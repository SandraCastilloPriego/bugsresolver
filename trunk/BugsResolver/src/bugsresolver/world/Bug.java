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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author bicha
 */
public class Bug {

    private Cell cell;
    private int x, y;
    private List<PeakListRow> rowList;
    private double life = 200;
    private BugDataset dataset;
    private Classifier classifier;
    private classifiersEnum classifierType;
    private Instances data;
    private double wellClassified, total;
    private double sensitivity, totalsen;
    private double specificity, totalspec;
    private Random rand;

    public Bug(int x, int y, Cell cell, PeakListRow row, BugDataset dataset) {
        rand = new Random();
        this.cell = cell;
        this.dataset = dataset;
        this.x = x;
        this.y = y;
        this.rowList = new ArrayList<PeakListRow>();
        this.rowList.add(row);
        int n = rand.nextInt(classifiersEnum.values().length);
        this.classifierType = classifiersEnum.values()[n];
        this.classify();
    }

    public double getAge() {
        return this.total;
    }

    public Bug(Bug father, Bug mother, BugDataset dataset) {
        this.dataset = dataset;
        this.cell = father.getCell();
        this.x = father.getx();
        this.y = father.gety();
        this.rowList = new ArrayList<PeakListRow>();
        for (PeakListRow row : father.getRows()) {
            if (this.rowList.contains(row)) {
                if (this.rowList.size() == 1) {
                    this.life = 0;
                }
            } else {
                this.rowList.add(row);
            }
        }
        for (PeakListRow row : mother.getRows()) {
            if (this.rowList.contains(row)) {
                if (this.rowList.size() == 1) {
                    this.life = 0;
                }
            } else {
                this.rowList.add(row);
            }
        }
        if (mother.specificity > father.getSpecificity()) {
            this.classifierType = mother.getClassifierType();
        } else {
            this.classifierType = father.getClassifierType();
        }

        this.classify();
    }

    public classifiersEnum getClassifierType() {
        return this.classifierType;
    }

    public double getTotal() {
        return this.wellClassified / this.total;
    }

    public double getSensitivity() {
        return this.sensitivity / this.totalsen;
    }

    public double getSpecificity() {
        return this.specificity / this.totalspec;
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

    public double getLife() {
        return life;
    }

    public BugDataset getDataset() {
        return this.dataset;
    }

    boolean isDead() {
        this.life -= (1 - this.getSensitivity());
        this.life -= (1 - this.getSpecificity());
        if (this.rowList.size() == 0) {
            life = 0;
        }
        if (this.life == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void classify() {
        try {

            FastVector attributes = new FastVector();

            for (int i = 0; i < rowList.size(); i++) {
                Attribute weight = new Attribute("weight" + i);
                attributes.addElement(weight);
            }

            FastVector labels = new FastVector();

            labels.addElement("1");
            labels.addElement("2");
            Attribute type = new Attribute("class", labels);

            attributes.addElement(type);

            //Creates the dataset
            data = new Instances("Dataset", attributes, 0);
            // int numberOfPeaks = this.rowList.get(0).getNumberPeaks();
            int numberForTraining = 287;// (int) (numberOfPeaks * 0.6);
            for (int i = 0; i < numberForTraining; i++) {
                double[] values = new double[data.numAttributes()];
                String sampleName = dataset.getAllColumnNames().elementAt(i);
                int cont = 0;
                for (PeakListRow row : rowList) {
                    values[cont++] = (Double) row.getPeak(sampleName);
                }
                values[cont] = data.attribute(data.numAttributes() - 1).indexOfValue(this.dataset.getType(sampleName));

                Instance inst = new SparseInstance(1.0, values);
                data.add(inst);
            }

            data.setClass(type);

            classifier = setClassifier();
            classifier.buildClassifier(data);
        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eat(int nBugs) {
        total++;
        if (cell.type.equals("1")) {
            this.totalspec++;
        } else {
            this.totalsen++;
        }
        if (isClassify()) {
            double food;
            if(this.getSensitivity() > this.getSpecificity()){
                food = this.getSpecificity();
            }else{
                food = this.sensitivity;
            }
            this.life += food;
            wellClassified++;

            if (cell.type.equals("1")) {
                this.specificity++;
            } else {
                this.sensitivity++;
            }
        }
    }

    public boolean isClassify() {
        String sampleName = this.cell.getSampleName();
        FastVector attributes = new FastVector();
        for (int i = 0; i < rowList.size(); i++) {
            Attribute weight = new Attribute("weight" + i);
            attributes.addElement(weight);
        }
        FastVector labels = new FastVector();
        labels.addElement("1");
        labels.addElement("2");
        Attribute type = new Attribute("class", labels);
        attributes.addElement(type);
        //Creates the dataset
        Instances train = new Instances("Train Dataset", attributes, 0);
        double[] values = new double[train.numAttributes()];
        int cont = 0;
        for (PeakListRow row : rowList) {
            values[cont++] = (Double) row.getPeak(sampleName);
        }
        values[cont] = train.attribute(train.numAttributes() - 1).indexOfValue(this.dataset.getType(sampleName));
        Instance inst = new SparseInstance(1.0, values);
        train.add(inst);
        train.setClassIndex(cont);
        try {
            double pred = classifier.classifyInstance(train.instance(0));
            // System.out.print(train.instance(0).toString(train.classIndex()) + " - ");
            //System.out.println(train.classAttribute().value((int) pred));

            if (cell.type.equals(train.classAttribute().value((int) pred))) {
                return true;
            } else {
                return false;
            }


        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;


    }

    private Classifier setClassifier() {
        switch (this.classifierType) {
            case Logistic:
                return new Logistic();
            case LogisticBase:
                return new LogisticBase();
            case LogitBoost:
                return new LogitBoost();
            case LWL:
                return new LWL();
            case NaiveBayesMultinomialUpdateable:
                return new NaiveBayesMultinomialUpdateable();
            case NaiveBayesUpdateable:
                return new NaiveBayesUpdateable();
            case MultilayerPerceptron:
                return new MultilayerPerceptron();
            case RandomForest:
                return new RandomForest();
            case RandomCommittee:
                return new RandomCommittee();
            case RandomTree:
                return new RandomTree();
            case ZeroR:
                return new ZeroR();
            case Stacking:
                return new Stacking();
            case AdaBoostM1:
                return new AdaBoostM1();
            case Bagging:
                return new Bagging();            
            case ComplementNaiveBayes:
                return new ComplementNaiveBayes();            
            case IB1:
                return new IB1();
            case IBk:
                return new IBk();            
            case J48:
                return new J48();
            case JRip:
                return new JRip();
            case KStar:
                return new KStar();
            case LMT:
                return new LMT();          
            case MultiScheme:
                return new MultiScheme();
            case NaiveBayes:
                return new NaiveBayes();
            case NaiveBayesMultinomial:
                return new NaiveBayesMultinomial();
            case OneR:
                return new OneR();
            case PART:
                return new PART();
            case RandomSubSpace:
                return new RandomSubSpace();
            case REPTree:
                return new REPTree();
            case SimpleLogistic:
                return new SimpleLogistic();
            case SMO:
                return new SMO();            
            default:
                return new RandomTree();
        }

    }
}
