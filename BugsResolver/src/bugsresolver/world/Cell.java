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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bicha
 */
public class Cell {

    String type;
    String sampleName;
    List<Bug> bugsInside;

    public void setParameters(String type, String sampleName) {
        this.type = type;
        this.sampleName = sampleName;
        this.bugsInside = new ArrayList<Bug>();
    }

    public void addBug(Bug bug){
        this.bugsInside.add(bug);
    }

    public void removeBug(Bug bug){
        this.bugsInside.remove(bug);
    }

    public String getSampleName(){
        return this.sampleName;
    }

    public Bug reproduction() {
        Bug mother, father;
        if(bugsInside.size() > 1){
            //ordenar por eficiencia y coger los dos más eficientes como padres.
        }
        return null;
    }
}
