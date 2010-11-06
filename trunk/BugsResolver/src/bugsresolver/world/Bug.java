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
import weka.classifiers.Evaluation;
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
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.MultiScheme;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.meta.Stacking;
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
    private double life = 30;
    private BugDataset dataset;
    private Classifier classifier;
    private classifiersEnum classifierType;
    private Instances data, validation;
    private double wellClassified, total;
    private double sensitivity;
    private double specificity;
    double spec = 0, sen = 0, totalspec = 0, totalsen = 0;
    private Random rand;
    private int MAXNUMBERGENES = 3;
    Evaluation eval;
    boolean fixValue = false;

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
        // System.out.println(this.classifierType);
       /* if (this.classifier != null) {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(row.getID());
        if (ids.size() > 0) {
        this.prediction(ids);
        }
        }*/
    }

    public double getAge() {
        return this.total;
    }

    public Bug(Bug father, Bug mother, BugDataset dataset) {
        rand = new Random();
        this.dataset = dataset;
        this.cell = father.getCell();
        this.x = father.getx();
        this.y = father.gety();
        this.rowList = new ArrayList<PeakListRow>();

        this.assingGenes(mother, 0);
        this.assingGenes(father, 0);

        this.orderPurgeGenes();

        if (mother.getAreaUnderTheCurve() > father.getAreaUnderTheCurve()) {
            this.classifierType = mother.getClassifierType();
        } else {
            this.classifierType = father.getClassifierType();
        }

        this.classify();

        /* List<Integer> IDs = new ArrayList<Integer>();
        for (PeakListRow row : this.getRows()) {
        IDs.add(row.getID());
        }*/
        // this.prediction(IDs);
    }

    public void assingGenes(Bug parent, int plus) {
        for (PeakListRow row : parent.getRows()) {
            if (!this.rowList.contains(row)) {
                this.rowList.add(row);
            }
        }
    }

    public void orderPurgeGenes() {
        int removeGenes = this.rowList.size() - this.MAXNUMBERGENES;
        if (removeGenes > 0) {
            for (int i = 0; i < removeGenes; i++) {
                int index = rand.nextInt(this.rowList.size() - 1);
                this.rowList.remove(index);
            }
        }
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
        life -= ((1 - this.getAreaUnderTheCurve()) * 3);
        if (this.rowList.size() == 0) {
            life = 0;
        }
        if (this.life < 1) {
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

            int numberForTraining = 287;
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
            if (classifier != null) {
                classifier.buildClassifier(data);
            }
        } catch (Exception ex) {
            Logger.getLogger(Bug.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eat(int nBugs) {
        total++;
        if (!fixValue) {
            if (cell.type.equals("1")) {
                this.totalspec++;
            } else {
                this.totalsen++;
            }
        } else {
            this.totalsen = 1;
            this.totalspec = 1;
        }
        if (isClassify()) {
            wellClassified++;
            this.life += ((this.getAreaUnderTheCurve()) * (1 / nBugs));
            if (!fixValue) {
                if (cell.type.equals("1")) {
                    this.specificity++;
                } else {
                    this.sensitivity++;
                }
            }
        }
        if (!fixValue) {
            if (this.getAge() > 100 && this.getAreaUnderTheCurve() > 0.75) {
                /* System.out.println("statistics:  " + this.getAge() + " - " + this.getClassifierType());
                for (PeakListRow row : this.getRows()) {
                System.out.println(row.getID());
                }
                System.out.println(this.getSensitivity() + " - " + this.getSpecificity());*/
                this.prediction();
            }
        }
    }

    public void kill() {
        this.life = -1;
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

        /* for (Object prediction : eval.predictions().toArray()) {
        System.out.println(prediction);
        }
        return false;*/
    }

    private Classifier setClassifier() {
        switch (this.classifierType) {
            case Logistic:
                return new Logistic();
            case LogisticBase:
                return new LogisticBase();
            case LogitBoost:
                return new LogitBoost();
            case NaiveBayesMultinomialUpdateable:
                return new NaiveBayesMultinomialUpdateable();
            case NaiveBayesUpdateable:
                return new NaiveBayesUpdateable();
            // case MultilayerPerceptron:
            //      return new MultilayerPerceptron();
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
            case J48:
                return new J48();
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
                life = 0;
                return null;
        }

    }

    public double getAreaUnderTheCurve() {
        double value = (this.getSpecificity() + this.getSensitivity()) / 2;
        if (value != Double.NaN) {
            return value;
        } else {
            return 0;
        }
    }

    private void prediction() {
        try {

            FastVector attributes = new FastVector();

            for (int i = 0; i < this.rowList.size(); i++) {
                Attribute weight = new Attribute("weight" + i);
                attributes.addElement(weight);
            }

            FastVector labels = new FastVector();

            labels.addElement("1");
            labels.addElement("2");
            Attribute type = new Attribute("class", labels);

            attributes.addElement(type);

            //Creates the dataset
            validation = new Instances("Dataset2", attributes, 0);

            int numberForTraining = 287;// (int) (numberOfPeaks * 0.6);
            for (int i = numberForTraining; i < dataset.getNumberCols(); i++) {
                double[] values = new double[validation.numAttributes()];
                String sampleName = dataset.getAllColumnNames().elementAt(i);
                int cont = 0;

                for (PeakListRow row : this.rowList) {
                    values[cont++] = (Double) row.getPeak(sampleName);
                }

                values[cont] = validation.attribute(validation.numAttributes() - 1).indexOfValue(this.dataset.getType(sampleName));

                Instance inst = new SparseInstance(1.0, values);
                validation.add(inst);
            }

            validation.setClass(type);
            if (classifier != null) {
                eval = new Evaluation(validation);
                eval.crossValidateModel(classifier, validation, 10, new Random(1));
                //System.out.println(eval.weightedPrecision() + " - " + eval.weightedRecall());


                specificity = eval.weightedRecall();
                sensitivity = eval.weightedPrecision();


                //      if (this.getAreaUnderTheCurve() > 0.7) {
                System.out.println("statistics:  " + this.getClassifierType());
                for (PeakListRow row : this.getRows()) {
                    System.out.println(row.getID());
                }
                System.out.println(eval.weightedPrecision() + " - " + eval.weightedRecall() + " AUC: " + eval.weightedAreaUnderROC());
            }
            // }
            double sp = 0, tsp = 0, sn = 0, tsn = 0;

            for (int i = 287; i < dataset.getNumberCols(); i++) {

                try {
                    double pred = classifier.classifyInstance(validation.instance(i));

                    if (validation.instance(i).toString(validation.classIndex()).equals("1")) {
                        tsp++;
                        if (validation.classAttribute().value((int) pred).equals("1")) {
                            sp++;
                        }
                    } else {
                        tsn++;
                        if (validation.classAttribute().value((int) pred).equals("2")) {
                            sn++;
                        }
                    }
                } catch (Exception eeee) {
                }
            }
            specificity = sp / tsp;
            sensitivity = sn / tsn;
            fixValue = true;
            System.out.println("spec: " + specificity + " sen: " + sensitivity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
