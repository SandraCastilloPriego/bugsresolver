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
package bugsresolver.data;

/**
 * Definition of a Row.
 *
 * @author SCSANDRA
 */
public interface PeakListRow {

    /**
     * Returns the identification number of the row.
     *
     * @return int with the ID of the row
     */
    public int getID();

    /**
     * Sets the identification number of the row.
     *
     * @param i ID number
     */
    public void setID(int i);

    /**
     * Adds a new Double value (called "peak") to the table.
     *
     * @param columnName Column where the value will be added
     * @param Double value
     */
    public void setPeak(String columnName, Double value);

    /**
     * Returns the value located in a concrete column.
     *
     * @param columnName Column name where the value is
     * @return Object value located in the column.
     */
    public Object getPeak(String columnName);

    /**
     * Returs all the values from the row.
     *
     * @return Array of Object values
     */
    public Object[] getPeaks();

    /**
     * Removes all the values from the row.
     *
     */
    public void removePeaks();

    /**
     * Removes the peaks which are not in the columns given by the parameter.
     *
     * @param columnName Array with the name of the columns that won't be removed
     */
    public void removeNoSamplePeaks(String[] columnName);

    /**
     * Returns the number of values in the row.
     *
     * @return int with the number of values in the row
     */
    public int getNumberPeaks();

    /**
     * Return an exact copy of itself.
     *
     * @return PeakListRow
     */
    public PeakListRow clone();

    /**
     * Return true when the checkbox in the "selection" column is selected.
     *
     * @return State of the checbox in the "selection" column
     */
    public boolean isSelected();

    /**
     * @see guineu.data.GCGCColumnName
     * @see guineu.data.LCMSColumnName
     *
     * Each column in the enum files has its own getVar() and setVar() function. They are defined in it.
     *
     * @param varName
     * @return
     */
    public Object getVar(String varName);

    /**
     * @see guineu.data.GCGCColumnName
     * @see guineu.data.LCMSColumnName
     *
     * Each column in the enum files has its own getVar() and setVar() function. They are defined in it.
     *
     * @param varName
     * @param value
     */
    public void setVar(String varName, Object value);
   
}
