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
import bugsresolver.utils.Range;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author bicha
 */
public class World {

    BugDataset dataset;
    Cell[][] cells;
    List<Bug> population;
    Random rand;
    int cellsPerSide;
    int numberOfCells;
    int jump = 1;
    int cicleNumber = 0;

    public World(BugDataset dataset, int cellsPerSide) {
        this.dataset = dataset;
        this.cellsPerSide = cellsPerSide;
        this.numberOfCells = cellsPerSide * cellsPerSide;
        this.population = new ArrayList<Bug>();
        this.rand = new Random();
        if (dataset != null) {
            cells = new Cell[cellsPerSide][cellsPerSide];
            for (int i = 0; i < cellsPerSide; i++) {
                cells[i] = new Cell[cellsPerSide];
                for (int j = 0; j < cells[i].length; j++) {
                    cells[i][j] = new Cell();
                }
            }
            int numCellSubWorld = cellsPerSide / 10;

            for (int i = 0; i < 10; i++) {
                createCity(numCellSubWorld, i);
            }
            for (int i = 0; i < 3; i++) {
                for (PeakListRow row : dataset.getRows()) {
                    this.addBug(row);
                }
            }
        }
    }

    private void createCity(int numCellSubWorld, int x) {
        int initX = x * numCellSubWorld;
        Range range = new Range(x * 10, (x * 10) + 10);
        for (int i = initX; i < numCellSubWorld + initX; i++) {
            for (int e = 0; e < this.cellsPerSide; e++) {
                this.setSamplesInCell(dataset.getAllColumnNames(), cells[i][e], range);
            }
        }
    }

    private void setSamplesInCell(Vector<String> samplesNames, Cell cell, Range range) {
        int pos = range.getRandom();
        String name = samplesNames.elementAt(pos);
        cell.setParameters(name, range, dataset.getType(name));
    }

    public synchronized void addMoreBugs() {
        for (PeakListRow row : dataset.getRows()) {
            this.addBug(row);
        }
    }

    private void addBug(PeakListRow row) {
        boolean isInside = true;
        int cont = 0;
        while (isInside) {
            int X = this.rand.nextInt(this.cellsPerSide - 1);
            int Y = this.rand.nextInt(this.cellsPerSide - 1);
            Bug bug = new Bug(X, Y, cells[X][Y], row, dataset);
            cells[X][Y].addBug(bug);
            this.population.add(bug);
            isInside = false;
            cont++;
            if (cont > numberOfCells) {
                break;
            }
        }
    }

    public void cicle() {
        //System.out.println(this.population.size());
        movement();
        eat();

        for (Cell[] cellArray : cells) {
            for (Cell cell : cellArray) {
                List<Bug> childs = cell.reproduction();
                if (childs != null) {
                    for (Bug child : childs) {
                        cell.addBug(child);
                        this.population.add(child);
                    }
                }
            }
        }

        death();

        if (population.size() > 2000) {
            this.purgeBugs();
        }

        this.cicleNumber++;

        /* if (cicleNumber > 2000) {
        this.addMoreBugs();
        cicleNumber = 0;
        }



        /* for(Bug bug : population){

        }*/
    }

    private void movement() {
        for (Bug bug : population) {
            int direction = rand.nextInt(8);

            int x = bug.getx();
            int y = bug.gety();
            // this.setBugPosition(bug, x, y);

            switch (direction) {
                case 0:
                    this.setBugPosition(bug, x + jump, y);
                    break;
                case 1:
                    this.setBugPosition(bug, x, y);
                    break;
                case 2:
                    this.setBugPosition(bug, x, y + jump);
                    break;
                case 3:
                    this.setBugPosition(bug, x, y - jump);
                    break;
                case 4:
                    this.setBugPosition(bug, x + jump, y + jump);
                    break;
                case 5:
                    this.setBugPosition(bug, x + jump, y - jump);
                    break;
                case 6:
                    this.setBugPosition(bug, x - jump, y + jump);
                    break;
                case 7:
                    this.setBugPosition(bug, x - jump, y - jump);
                    break;
            }

        }
    }

    /* private void setBugPosition(Bug bug, int x, int y) {
    int newx = rand.nextInt(this.cellsPerSide - 1);
    int newy = rand.nextInt(this.cellsPerSide - 1);
    bug.getCell().removeBug(bug);
    bug.setPosition(newx, newy);
    cells[newx][newy].addBug(bug);
    bug.setCell(cells[newx][newy]);


    }*/
    private void setBugPosition(Bug bug, int newx, int newy) {
        if (newx > this.cellsPerSide - 1) {
            newx = 1;
        } else if (newx < 0) {
            newx = 99;
        }
        if (newy > this.cellsPerSide - 1) {
            newy = 1;
        } else if (newy < 0) {
            newy = 99;
        }
        bug.getCell().removeBug(bug);
        bug.setPosition(newx, newy);
        cells[newx][newy].addBug(bug);
        bug.setCell(cells[newx][newy]);

    }

    public List<Bug> getPopulation() {
        return this.population;
    }

    public int getWorldSize() {
        return this.cellsPerSide;
    }

    private void eat() {
        for (Bug bug : population) {
            bug.eat(this.population.size());
        }
    }

    public void purgeBugs() {

        Comparator<Bug> c = new Comparator<Bug>() {

            public int compare(Bug o1, Bug o2) {
                if (o1.getAreaUnderTheCurve() < o2.getAreaUnderTheCurve()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(population, c);

        //  List<Bug> populationcopy = new ArrayList<Bug>();
        for (int i = 2000; i < this.population.size(); i++) {
            population.get(i).kill();
            // populationcopy.add(this.population.get(i));
            //System.out.println(this.population.get(i).getSensitivity());
        }

        //  this.population = populationcopy;
        /*try {
        Bug bug = this.population.get(0);

        // if (bug.getSensitivity() > 0.6 && bug.getSpecificity() > 0.6 && bug.getAge() > 100) {
        System.out.println("statistics: " + bug.getAge() + " - " + bug.getTotal() + " sensitivity: " + bug.getSensitivity() + " specificity: " + bug.getSpecificity() + " - " + bug.getClassifierType());
        for (PeakListRow row : bug.getRows()) {
        System.out.println(row.getID());
        }
        System.out.println("----------------");
        // }

        } catch (Exception e) {
        }*/

    }

    private void death() {
        List<Bug> deadBugs = new ArrayList<Bug>();


        for (Bug bug : population) {
            if (bug.isDead()) {
                deadBugs.add(bug);


                this.cells[bug.getx()][bug.gety()].removeBug(bug);


            }
        }
        for (Bug bug : deadBugs) {
            this.population.remove(bug);


        }
    }

    public void saveBugs() throws IOException {
        if (cicleNumber > 1000) {
            FileWriter fstream = new FileWriter("output.txt");
            BufferedWriter out = new BufferedWriter(fstream);



            for (Bug bug : this.population) {
                if (bug.getSensitivity() > 0.6 && bug.getSpecificity() > 0.6 && bug.getAge() > 400) {
                    out.write(bug.getClassifierType().name());
                    out.write("\n");


                    for (PeakListRow row : bug.getRows()) {
                        out.write(String.valueOf(row.getID()));
                        out.write("\n");


                    }
                    out.write(String.valueOf(bug.getSensitivity()) + " - " + String.valueOf(bug.getSpecificity()));
                    out.write("\n");
                    out.write("--------------------------------------------------------------");
                    out.write("\n");

                }
            }
            out.close();
            fstream.close();





        }



    }

    public class Population implements Comparable<Bug> {

        double specificity;

        public Population(double specificity) {
            this.specificity = specificity;
        }

        public int compareTo(Bug o) {
            if (this.specificity < o.getSpecificity()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
