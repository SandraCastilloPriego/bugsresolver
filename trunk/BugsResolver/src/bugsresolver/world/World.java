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

    public World(BugDataset dataset, int cellsPerSide) {
        this.dataset = dataset;
        this.cellsPerSide = cellsPerSide;
        this.numberOfCells = cellsPerSide * cellsPerSide;
        this.population = new ArrayList<Bug>();
        this.rand = new Random();
        if (dataset != null) {
            cells = new Cell[cellsPerSide][cellsPerSide];
            Vector<String> samplesNames = dataset.getAllColumnNames();
            for (int i = 0; i < cellsPerSide; i++) {
                cells[i] = new Cell[cellsPerSide];
                for (int j = 0; j < cells[i].length; j++) {
                    cells[i][j] = new Cell();
                    this.setSamplesInCell(samplesNames, cells[i][j]);
                }
            }
            for (int i = 0; i < 3; i++) {
                for (PeakListRow row : dataset.getRows()) {
                    this.addBug(row);
                }
            }
        }
    }

    private void setSamplesInCell(Vector<String> samplesNames, Cell cell) {
        //60 % of the samples are for "training" and 40% for validation. The cells are the validation samples.
        int numberForTraining = 287;//(int) (samplesNames.size() * 0.6);
        int pos = rand.nextInt(samplesNames.size() - numberForTraining) + numberForTraining;
        String name = samplesNames.elementAt(pos);
        cell.setParameters(dataset.getType(name), name);
    }

    private void addBug(PeakListRow row) {
        boolean isInside = true;
        int cont = 0;
        while (isInside) {
            int X = this.rand.nextInt(this.cellsPerSide);
            int Y = this.rand.nextInt(this.cellsPerSide);

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
        // System.out.println(this.population.size());
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

        this.printResult();
    }

    private void movement() {
        for (Bug bug : population) {
            //  int direction = rand.nextInt(8);
            jump = rand.nextInt(100);

            int x = bug.getx();
            int y = bug.gety();
            this.setBugPosition(bug, x, y);

            /*switch (direction) {
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
            }*/

        }
    }

    private void setBugPosition(Bug bug, int x, int y) {
        int newx = rand.nextInt(this.cellsPerSide - 1);
        int newy = rand.nextInt(this.cellsPerSide - 1);
        bug.getCell().removeBug(bug);
        bug.setPosition(newx, newy);
        cells[newx][newy].addBug(bug);
        bug.setCell(cells[newx][newy]);

    }
    /* private void setBugPosition(Bug bug, int newx, int newy) {
    if (newx > this.cellsPerSide) {
    newx = (jump - (this.cellsPerSide - newx));
    } else if (newx < 0) {
    newx = this.cellsPerSide - (jump + newx);
    }
    if (newy > this.cellsPerSide) {
    newy = (jump - (this.cellsPerSide - newy));
    } else if (newy < 0) {
    newy = this.cellsPerSide - (jump + newy);
    }

    bug.getCell().removeBug(bug);
    bug.setPosition(newx, newy);
    cells[newx][newy].addBug(bug);
    bug.setCell(cells[newx][newy]);

    }*/

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

    private void printResult() {
        if (population.size() > 1500) {
            Comparator<Bug> c = new Comparator<Bug>() {

                public int compare(Bug o1, Bug o2) {
                    if ((o1.getSpecificity()) + (o1.getSensitivity()) > (o2.getSensitivity() + o2.getSpecificity())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            };
            Collections.sort(population, c);

            for (int i = 1200; i < population.size(); i++) {
                this.population.remove(i);
                System.out.println(this.population.get(i).getSensitivity());
            }
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