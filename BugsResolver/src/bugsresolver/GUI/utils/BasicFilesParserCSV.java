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
package bugsresolver.GUI.utils;

import bugsresolver.data.BugDataset;
import bugsresolver.data.PeakListRow;
import bugsresolver.data.impl.SimpleBasicDataset;
import bugsresolver.data.impl.SimplePeakListRowOther;
import com.csvreader.CsvReader;

import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class BasicFilesParserCSV {

    private String datasetPath;
    private SimpleBasicDataset dataset;
    private int rowsNumber;
    private int rowsReaded;

    public BasicFilesParserCSV(String datasetPath) {
        if (datasetPath != null) {
            this.rowsNumber = 0;
            this.rowsReaded = 0;
            this.datasetPath = datasetPath;
            this.dataset = new SimpleBasicDataset(this.getDatasetName());
            countNumberRows();
        }
    }

    public String getDatasetName() {
        try {
            Pattern pat = Pattern.compile("[\\\\/]");
            Matcher matcher = pat.matcher(datasetPath);
            int index = 0;
            while (matcher.find()) {
                index = matcher.start();
            }
            String n = datasetPath.substring(index + 1, datasetPath.length() - 4);
            return n;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public float getProgress() {
        return (float) rowsReaded / rowsNumber;
    }

    public void fillData() {
        try {
            CsvReader reader = new CsvReader(new FileReader(datasetPath));
            reader.readHeaders();
            String[] header = reader.getHeaders();
            reader.readRecord();
            String[] types = reader.getValues();

            // Set types
            for (int i = 1; i < types.length; i++) {
                this.dataset.setType(header[i], types[i]);
            }

            while (reader.readRecord()) {
                getData(reader.getValues(), header);
                rowsReaded++;
            }
            setExperimentsName(header);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData(String[] sdata, String[] header) {
        try {
            PeakListRow lipid = new SimplePeakListRowOther();
            for (int i = 0; i < sdata.length; i++) {
                try {
                    if (i == 0) {
                        lipid.setID(Integer.parseInt(sdata[i]));
                    } else {
                        double value = 0.0;
                        if (!sdata[i].contains("N/A")) {
                            try {
                                value = Double.parseDouble(sdata[i]);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        lipid.setPeak(header[i], value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.dataset.addRow(lipid);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public BugDataset getDataset() {
        return this.dataset;
    }

    private void setExperimentsName(String[] header) {
        try {
            for (int i = 1; i < header.length; i++) {
                this.dataset.addColumnName(header[i]);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void countNumberRows() {
        try {
            CsvReader reader = new CsvReader(new FileReader(datasetPath));
            while (reader.readRecord()) {
                this.rowsNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
