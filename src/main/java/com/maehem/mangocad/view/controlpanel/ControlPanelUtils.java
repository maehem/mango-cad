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
package com.maehem.mangocad.view.controlpanel;

import com.maehem.mangocad.model.LibraryCache;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.highlevel.DeviceSet;
import com.maehem.mangocad.model.element.highlevel.Footprint;
import com.maehem.mangocad.model.element.highlevel.Package3d;
import com.maehem.mangocad.model.element.highlevel.Symbol;
import com.maehem.mangocad.view.ControlPanel;
import com.maehem.mangocad.view.controlpanel.listitem.ControlPanelListItem;
import com.maehem.mangocad.view.controlpanel.listitem.ProjectSubFolderItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ControlPanelUtils {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    /**
     * Search in this directory for the DESCRIPTION.md
     *
     * @param dir that might contain a Description file.
     * @return first line of the Description file (DESCRIPTION.md)
     */
    public static String getFolderDescriptionShort(File dir) {
        String description = "";

        File descFile = new File(dir, "DESCRIPTION.md");

        // Is it really the description file?
        if (descFile.exists() && descFile.isFile() && descFile.canRead()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(descFile));
                description = br.readLine(); // We just need the first line here.
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            LOGGER.log(Level.FINER, "Description not found.");
        }

        return description;
    }

    public static String getItemDescriptionFull(ControlPanelListItem item) {
        File fileOrDir = item.getFile();
        if (fileOrDir != null) {
            LOGGER.log(Level.FINER, "getFolderDesc for: " + fileOrDir.getName());
        }
        if (fileOrDir != null && fileOrDir.isDirectory()) {
            File descFile = new File(fileOrDir, "DESCRIPTION.md");
            if (!descFile.exists() || descFile.isDirectory() || !descFile.canRead()) {
                return null;
            }
            try {
                String readString = Files.readString(descFile.toPath());
                if (readString == null) {
                    return "";
                }
                return readString;
            } catch (IOException ex) {
                Logger.getLogger(ProjectSubFolderItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (fileOrDir != null && fileOrDir.isFile() && fileOrDir.canRead()) {
            String fName = fileOrDir.getName();
            if (fName.endsWith(".lbr")) {
                Library library = LibraryCache.getInstance().getLibrary(fileOrDir);
                if (item.getName().equals(fName)) {
                    // Get description from file.
                    if (library != null) {
                        return library.getDescription();
                    } else {
                        LOGGER.log(Level.SEVERE, "Library requested was null: " + fileOrDir.getName());
                    }
                } else { // One of the sub-items
                    // TODO: Include Parent library information.
                    for (Symbol s : library.getSymbols()) {
                        if (s.getName().equals(item.getName())) {
                            return s.getDescription();
                        }
                    }
                    for (Footprint f : library.getPackages()) {
                        if (f.getName().equals(item.getName())) {
                            //LOGGER.log(Level.SEVERE, "Package raw desc.: " + f.getDescription());
                            return f.getDescription();
                        }
                    }
                    for (Package3d p : library.getPackages3d()) {
                        if (p.getName().equals(item.getName())) {
                            return p.getDescription();
                        }
                    }
                    for (DeviceSet ds : library.getDeviceSets()) {
                        if (ds.getName().equals(item.getName())) {
                            return ds.getDescription();
                        }
                    }
                }
            }
        }

        return "";
    }

}
