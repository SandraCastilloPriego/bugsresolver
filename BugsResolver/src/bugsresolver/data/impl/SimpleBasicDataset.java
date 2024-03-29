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
package bugsresolver.data.impl;

import bugsresolver.data.BugDataset;
import bugsresolver.data.PeakListRow;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Basic data set implementation.
 *
 * @author SCSANDRA
 */
public class SimpleBasicDataset implements BugDataset {

    String datasetName;
    Vector<PeakListRow> peakList;
    Vector<String> columnNames;
    Hashtable<String, String> type;
    String infoDataset = "";
    private int ID;
    private int numberRows = 0;

    /**
     *
     * @param datasetName Name of the data set
     */
    public SimpleBasicDataset(String datasetName) {
        this.datasetName = datasetName;
        this.peakList = new Vector<PeakListRow>();
        this.columnNames = new Vector<String>();
        this.type = new Hashtable<String, String>();
    }

    /**
     * Returns true when the list of column names contain the parameter <b>columnName</b>.
     *
     * @param columnName Name of the column to be searched
     * @return true or false depending if the parameter <b>columnName</b> is in the name of any column
     */
    public boolean containtName(String columnName) {
        for (String name : this.columnNames) {
            if (name.compareTo(columnName) == 0) {
                return true;
            }
            if (name.matches(".*" + columnName + ".*")) {
                return true;
            }
            if (columnName.matches(".*" + name + ".*")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true when any row of the data set contains the String <b>str</b> into a
     * column called "Name"
     *
     * @param str
     * @return true or false depending if the parameter <b>str</b> is in the column called "Name"
     */
    public boolean containRowName(String srt) {
        for (PeakListRow row : this.getRows()) {
            if (((String) row.getPeak("Name")).contains(srt) || srt.contains((CharSequence) row.getPeak("Name"))) {
                return true;
            }
        }
        return false;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public void addRow(PeakListRow peakListRow) {
        this.peakList.addElement(peakListRow);
    }

    public void addColumnName(String nameExperiment) {
        this.columnNames.addElement(nameExperiment);
    }

    public PeakListRow getRow(int i) {
        return (PeakListRow) this.peakList.elementAt(i);
    }

    public Vector<PeakListRow> getRows() {
        return this.peakList;
    }

    public int getNumberRows() {
        return this.peakList.size();
    }

    public int getNumberRowsdb() {
        return this.numberRows;
    }

    public void setNumberRows(int numberRows) {
        this.numberRows = numberRows;
    }

    public int getNumberCols() {
        return this.columnNames.size();
    }

    public Vector<String> getAllColumnNames() {
        return this.columnNames;
    }

    public void setNameExperiments(Vector<String> experimentNames) {
        this.columnNames = experimentNames;
    }

    public void removeRow(PeakListRow row) {
        try {
            this.peakList.removeElement(row);

        } catch (Exception e) {
            System.out.println("No row found");
        }
    }

    public void addColumnName(String nameExperiment, int position) {
        this.columnNames.insertElementAt(datasetName, position);
    }

    public String getInfo() {
        return infoDataset;
    }

    public void setInfo(String info) {
        this.infoDataset = info;
    }

    @Override
    public SimpleBasicDataset clone() {
        SimpleBasicDataset newDataset = new SimpleBasicDataset(this.datasetName);
        for (String experimentName : this.columnNames) {
            newDataset.addColumnName(experimentName);
        }
        for (PeakListRow peakListRow : this.peakList) {
            newDataset.addRow(peakListRow.clone());
        }

        return newDataset;
    }

    public void setType(String sampleName, String type) {
        this.type.put(sampleName, type);
    }

    public String getType(String sampleName) {
        return type.get(sampleName);
    }
}
       
