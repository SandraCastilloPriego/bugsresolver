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
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

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

    public Bug(int x, int y, Cell cell, PeakListRow row, BugDataset dataset) {
        this.cell = cell;
        this.dataset = dataset;
        this.x = x;
        this.y = y;
        this.rowList = new ArrayList<PeakListRow>();
        this.rowList.add(row);
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
        if(this.life == 0){
            return true;
        }else return false;
    }


    private void classify(){



      /* Dataset data = new DefaultDataset();

        int numberOfPeaks = this.rowList.get(0).getNumberPeaks();
        int numberForTraining  = (int)(numberOfPeaks * 0.6);

        for(int i = 0; i < numberForTraining; i++){
             Instance instance = new SparseInstance();
             instance.add((double)this.rowList.get(0).getPeak(dataset.getAllColumnNames().elementAt(i)));
        }*/
    }

    public void eat(){
        int numberOfPeaks = this.rowList.get(0).getNumberPeaks();
        int numberForTraining  = (int)(numberOfPeaks * 0.6);
        for(int i = 0; i < numberForTraining; i++){

        }
        //int y = x*var;


    }
}
