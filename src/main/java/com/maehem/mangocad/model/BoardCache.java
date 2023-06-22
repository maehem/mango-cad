/*
    Licensed to the Apache Software Foundation (ASF) under one or more 
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF 
    licenses this file to you under the Apache License, Version 2.0 
    (the "License"); you may not use this file except in compliance with the 
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the 
    License for the specific language governing permissions and limitations 
    under the License.
 */
package com.maehem.mangocad.model;

import com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException;
import com.maehem.mangocad.model.eaglecad.EagleCADUtils;
import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.view.ControlPanel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class BoardCache extends ArrayList<Board> {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    private static BoardCache instance = null;

    private BoardCache() {}

    public static BoardCache getInstance() {
        if (instance == null) {
            instance = new BoardCache();
        }

        return instance;
    }

    public Board getBoard(File f) {
        for (Board brd : this) {
            if (brd.getFilePath().equals(f.getAbsolutePath())) {
                LOGGER.log(Level.FINER, "Found cached Board: " + brd.getFilePath());
                return brd;
            }
        }

        try {
            Board brd = EagleCADUtils.importBRD(f);
            add(brd);
            LOGGER.log(Level.FINER, "Cached new Board: " + brd.getFilePath());
            return brd;
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (EagleCADLibraryFileException ex) {
            LOGGER.log(Level.SEVERE, ex.toString());
            LOGGER.log(Level.SEVERE, "Error importing Board: " + f.getAbsolutePath(), ex);
        }

        LOGGER.log(Level.SEVERE, "ERROR: Could not find or load requested file!");
        return null;
    }
}
