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

            for (PeakListRow row : dataset.getRows()) {
                this.addBug(row);
            }
        }
    }

    private void setSamplesInCell(Vector<String> samplesNames, Cell cell) {
        //60 % of the samples are for "training" and 40% for validation. The cells are the validation samples.
        int numberForTraining = 258;//(int) (samplesNames.size() * 0.6);
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
        System.out.println(this.population.size());
        movement();
        eat();

        for (Cell[] cellArray : cells) {
            for (Cell cell : cellArray) {
                Bug child = cell.reproduction();
                if (child != null) {
                    cell.addBug(child);
                    this.population.add(child);
                }
            }
        }

        death();
    }

    private void movement() {
        for (Bug bug : population) {
            int direction = rand.nextInt(8);
            int jump = rand.nextInt(10);

            int x = bug.getx();
            int y = bug.gety();

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

    private void setBugPosition(Bug bug, int newx, int newy) {
        if (newx > this.cellsPerSide - 1) {
            newx = 1;
        } else if (newx < 0) {
            newx = this.cellsPerSide - 1;
        }
        if (newy > this.cellsPerSide - 1) {
            newy = 1;
        } else if (newy < 0) {
            newy = this.cellsPerSide - 1;
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
            bug.eat();
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
        for(Bug bug : deadBugs){
            this.population.remove(bug);
        }
    }
}
