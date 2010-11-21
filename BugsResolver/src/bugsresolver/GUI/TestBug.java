/*
 * Copyright 2007-2010 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package bugsresolver.GUI;

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
 *
 * @author SCSANDRA
 */
public class TestBug {

    BugDataset dataset;
    private Classifier classifier;
    double spec = 0, sen = 0, totalspec = 0, totalsen = 0;
    List<Integer> ids;
    Bug bug;

    public TestBug(Bug bug) {
        this.bug = bug;
        BasicFilesParserCSV parser = new BasicFilesParserCSV("/home/bicha/Escritorio/test2.csv");
        parser.fillData();
        dataset = parser.getDataset();
        ids = new ArrayList<Integer>();


        for(PeakListRow row : bug.getRows()){
            this.addId(row.getID());
        }

       // this.classify(ids);
        //this.prediction(ids);
    }

    public void addId(int id){
        this.ids.add(id);
    }

   /* private void classify(List<Integer> ids) {
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
                String sampleName = dataset.getAllColumnNames().elementAt(i);
                int cont = 0;
                for (Integer id : ids) {
                    for (PeakListRow row : dataset.getRows()) {
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
            classifier = setClassifier();
            classifier.buildClassifier(data);
        } catch (Exception ex) {
        }
    }
       private Classifier setClassifier() {
        switch (bug.getClassifierType()) {
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
         //   case MultilayerPerceptron:
         //       return new MultilayerPerceptron();
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
                return new RandomTree();
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
                   /* System.out.print((i + 1) + " - ");
                    System.out.print(train.instance(i).toString(train.classIndex()) + " - ");
                    System.out.print(train.classAttribute().value((int) pred) + " - ");
                    System.out.println(Utils.arrayToString(dist));*/

                   /* if (train.instance(i).toString(train.classIndex()).equals("1")) {
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
    }*/
}
