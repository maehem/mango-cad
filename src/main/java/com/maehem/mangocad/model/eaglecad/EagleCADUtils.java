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
package com.maehem.mangocad.model.eaglecad;

import com.maehem.mangocad.model.element.drawing.Board;
import com.maehem.mangocad.model.element.drawing.Eagle;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.view.ControlPanel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class EagleCADUtils {

    // TODO: Make a logger for data model not tied to UI.
    public static final Logger LOGGER = ControlPanel.LOGGER;

    private EagleCADUtils() {
    }   // Static methods only!

    public static final Library importLBR(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        Document eagleXML = readXML(file);
        LOGGER.log(Level.SEVERE, "Library Import: {0}", file.getAbsolutePath());

        Library lib = (Library) convertEagleXMLtoLBR(eagleXML).getDrawing().getDesign();
        lib.setFilePath(file.getAbsolutePath()); // TODO: Remove. Use lib.getFile().getAbsoloutePath()
        lib.setFile(file);

        return lib;
    }

    @SuppressWarnings("unchecked")
    public static final Schematic importSCH(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        Document eagleXML = readXML(file);
        LOGGER.log(Level.SEVERE, "Schematic Import: {0}", file.getAbsolutePath());

        Schematic sch = (Schematic) convertEagleXMLtoLBR(eagleXML).getDrawing().getDesign();
        //Schematic sch = convertEagleXMLtoSCH(eagleXML);
        sch.setFilePath(file.getAbsolutePath());
        Map vars = sch.getParentDrawing().getVars();
        // Split off the last dot in the file name.
        vars.put("DRAWING_NAME", file.getName().split("\\.(?=[^\\.]+$)")[0]);
        // LAST_DATE_TIME "MM/dd/yy h:mm a"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy h:mm a");
        String lastModified = simpleDateFormat.format(new Date(file.lastModified()));
        vars.put("LAST_DATE_TIME", lastModified);

        //Text Variables TODO:
//        Help>Editor Commands>Text>Text Variables lists the following:
//          >NAME                   Component name (ev.+gate name)          1)
//          >VALUE                  Comp. value/type                        1)
//          >PART                   Component name                          2)
//          >GATE                   Gate name                               2)
//          >XREF                   Part cross-reference                    2)
//          >CONTACT_XREF           Contact cross-reference                 2)
//          >ASSEMBLY_VARIANT       Name of the current assembly variant
//          >DRAWING_NAME           Drawing name
//          >LAST_DATE_TIME         Time of the last modification
//          >PLOT_DATE_TIME         Time of the plot creation
//          >SHEETNR                Sheet number of a schematic             3)
//          >SHEETS                 Total number of sheets of a schematic   3)
//          >SHEET                  equivalent to ">SHEETNR/>SHEETS"        3)
//
//          1) Only for package or symbol
//          2) Only for symbol
//          3) Only for symbol or schematic

        return sch;
    }

    public static final Board importBRD(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        Document eagleXML = readXML(file);
        LOGGER.log(Level.SEVERE, "Board Import: {0}", file.getAbsolutePath());

        Board brd = (Board) convertEagleXMLtoLBR(eagleXML).getDrawing().getDesign();
        //Schematic sch = convertEagleXMLtoSCH(eagleXML);
        brd.setFilePath(file.getAbsolutePath());

        return brd;
    }

    public static final Document readXML(File xml) {
        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return dom;
    }

    private static Eagle convertEagleXMLtoLBR(Document eagleXML) throws IOException, EagleCADLibraryFileException {
        Element element = eagleXML.getDocumentElement();
        short nodeType = element.getNodeType();
        String nodeName = element.getNodeName();
        // This Node should be 'eagle'
        if (nodeType != Node.ELEMENT_NODE && !nodeName.equals("eagle")) {
            throw new EagleCADLibraryFileException("XML File is not an EagleCAD file!");
        }

//        Library lib = new Library();
        String eagleVersion = element.getAttributes().getNamedItem("version").getNodeValue();
        //lib.addNote("Imported from EagleCAD LBR file version: " + eagleVersion);
        LOGGER.log(Level.CONFIG, "Eagle file version: {0}\t\t\t\t\t", eagleVersion);

        Eagle eagle = new Eagle();

        NodeList nodeList = element.getChildNodes();
        for (int j = 0; j < nodeList.getLength(); j++) {
            Node subNode = nodeList.item(j);
            if (subNode.getNodeType() != 1) {
                continue;
            }
            switch (subNode.getNodeName()) {
                case "drawing" -> {
                    eagle.setDrawing(EagleCADIngest.ingestDrawing(subNode));
                }
                case "compatibility" -> {
                    EagleCADIngest.ingestNotes(eagle.getCompatibility().getNotes(), subNode);
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "Unhandled <drawing> element found: {0}", subNode.getNodeName());
                }
            }
        }
        if (eagle.getDrawing() == null) {
            throw new EagleCADLibraryFileException("Eagle ingest did not encounter a required <drawing> element!");
        }
        return eagle;
    }

}
