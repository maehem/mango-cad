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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ControlPanelUtils {

    /**
     * Search in this directory for the DESCRIPTION.md
     * 
     * @param dir that might contain a Description file.
     * @return first line of the Description file (DESCRIPTION.md)
     */
    public static String getFolderDescription(File dir) {
        String description = "";

        File descFile = new File(dir, "DESCRIPTION.md");

        // Is it really the description file?
        if (descFile.exists() && descFile.isFile() && descFile.canRead()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(descFile));
                description = br.readLine(); // We just need the first line here.
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModuleList.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger("ModuleList").log(Level.SEVERE, "Description not found.");
        }

        return description;
    }
}
