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

import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.library.YAMLUtils;
import com.maehem.mangocad.model.library.eaglecad.EagleCADLibraryFileException;
import com.maehem.mangocad.model.library.eaglecad.EagleCADUtils;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class EagleCADLibraryConvert {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        if ( args.length != 2 ) {
//            Logger.getLogger(EagleCADLibraryConvert.class.getName()).log(Level.SEVERE,"Usage: this <input.file> <output.file>");
//        }
//        convert(new File(args[0]), new File(args[1]));
//        
//    }

    /**
     * 
     * @param in
     * @throws IOException
     * @throws FileNotFoundException
     * @throws EagleCADLibraryFileException 
     */
    public static void convert(File in) throws IOException, FileNotFoundException, EagleCADLibraryFileException {
        File out = new File(
                in.getParent() + File.separator +
                in.getName().split("\\.")[0] +
                "." + Library.FILE_EXTENSION);
        convert(in, out);
    }
    
    /**
     * Convert EagleCAD LBR file to MangoCAD format.
     * @param in EagleCAD .lbr file.
     * @param out MangoCAD .mclib file.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws EagleCADLibraryFileException 
     */
    public static void convert(File in, File out) throws IOException, FileNotFoundException, EagleCADLibraryFileException {
            // TODO code application logic here
            Library lbr = EagleCADUtils.importLBR(in);
            
            YamlWriter writer = new YamlWriter(new FileWriter(out));
            //writer.getConfig().setPrivateFields(true);
            YAMLUtils.setConfig(writer.getConfig());
            writer.write(lbr);
            writer.close();

    }
}
