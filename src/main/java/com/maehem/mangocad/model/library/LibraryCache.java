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
package com.maehem.mangocad.model.library;

import com.maehem.mangocad.model.library.eaglecad.EagleCADLibraryFileException;
import com.maehem.mangocad.model.library.eaglecad.EagleCADUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class LibraryCache extends ArrayList<Library> {
    private static final Logger LOGGER = Logger.getLogger(LibraryCache.class.getName());

    private static LibraryCache instance = null;
    
    private LibraryCache() {
        
    }
    
    public static LibraryCache getInstance() {
        if (instance == null) {
            instance = new LibraryCache();
        }

        return instance;
    }
    
    public Library getLibrary( File f ) {
        for ( Library l: this ) {
            if ( l.getFilePath().equals(f.getAbsolutePath()) ) {
                LOGGER.log(Level.FINER, "Found cached library: " + l.getFilePath());
                return l;
            }
        }
        
        try {
            Library lbr = EagleCADUtils.importLBR(f);
            add(lbr);
            LOGGER.log(Level.FINER, "Cached new Library: " + lbr.getFilePath());
            return lbr;
        } catch (IOException ex) {
            Logger.getLogger(LibraryCache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EagleCADLibraryFileException ex) {
            Logger.getLogger(LibraryCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LOGGER.log(Level.SEVERE, "ERROR: Could find or load library for requested file!");
        return null;
    }
}
