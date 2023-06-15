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
package com.maehem.mangocad.model.library.eaglecad;

import com.maehem.mangocad.model.element.drawing.Eagle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.maehem.mangocad.model.element.drawing.Library;
import com.maehem.mangocad.model.element.drawing.Schematic;
import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        lib.setFilePath(file.getAbsolutePath());

        return lib;
    }

    public static final Schematic importSCH(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        Document eagleXML = readXML(file);
        LOGGER.log(Level.SEVERE, "Schematic Import: {0}", file.getAbsolutePath());

        Schematic sch = (Schematic) convertEagleXMLtoLBR(eagleXML).getDrawing().getDesign();
        //Schematic sch = convertEagleXMLtoSCH(eagleXML);
        sch.setFilePath(file.getAbsolutePath());

        return sch;
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
        LOGGER.log(Level.SEVERE, "Eagle file version: {0}\t\t\t\t\t", eagleVersion);

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

//    private static Schematic convertEagleXMLtoSCH(Document eagleXML) throws IOException, EagleCADLibraryFileException {
//        // This Node should be 'eagle'
//        Element element = eagleXML.getDocumentElement();
//        short nodeType = element.getNodeType();
//        String nodeName = element.getNodeName();
//        if (nodeType != 1 && !nodeName.equals("eagle")) {
//            throw new IOException("XML File is not an EagleCAD file!");
//        }
//
//        LOGGER.log(Level.SEVERE, "Found a Eagle file...");
//
//        Schematic sch = new Schematic();
//        String eagleVersion = element.getAttributes().getNamedItem("version").getNodeValue();
//        //sch.addNote("Imported from EagleCAD SCH file version: " + eagleVersion);
//        LOGGER.log(Level.SEVERE, "Importing Eagle Schematic with format version: {0}", eagleVersion);
//
//        NodeList nn = element.getChildNodes();
//        for (int j = 0; j < nn.getLength(); j++) {
//            Node nnn = nn.item(j);
//            if (nnn.getNodeType() != 1) {
//                continue;
//            }
//            LOGGER.log(Level.SEVERE, "Ingest Node: {0}", nnn.getNodeName());
//            switch (nnn.getNodeName()) {
//                case "drawing" -> {
//                    NodeList nodes = nnn.getChildNodes();
//                    // drawing (settings?, grid?, filters?, layers, (library | schematic | board))
//                    for (int i = 0; i < nodes.getLength(); i++) {
//                        Node node = nodes.item(i);
//                        if (node.getNodeType() != 1) {
//                            continue;
//                        }
//                        switch (node.getNodeName()) {
//                            case "settings" -> {
//                                EagleCADIngest.ingestSettings(sch.getSettings(), node);
//                            }
//                            case "grid" -> {
//                                EagleCADIngest.ingestGrid(sch.getGrid(), node);
//                            }
//                            case "filters" -> {
//                                EagleCADIngest.ingestFilters(sch.getFilters(), node);
//                            }
//                            case "layers" -> {
//                                //LOGGER.log(Level.SEVERE, "Ingest <layers>");
//                                EagleCADIngest.ingestEagleLayers(sch.getLayers(), node);
//                            }
//                            case "schematic" -> {
//                                //LOGGER.log(Level.SEVERE, "Ingest <schematic>");
//                                EagleCADIngest.ingestEagleSchematicElement(sch, node);
//                            }
//                            default ->
//                                LOGGER.log(Level.SEVERE, "Eagle Import: Unknown top node <{0}>", node.getNodeName());
//                        }
//                    }
//                }
//                default -> {
//                    LOGGER.log(Level.SEVERE, "Unknown Node found in <eagle>: {0}", nnn.getNodeName());
//                }
//            }
//        }
//        return sch;
//    }
//    private static String getTextValue(String def, Element doc, String tag) {
//        String value = def;
//        NodeList nl;
//        nl = doc.getElementsByTagName(tag);
//        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
//            value = nl.item(0).getFirstChild().getNodeValue();
//        }
//        return value;
//    }
//    private static void printNode(Node node, String indent) {
//        NodeList nodes = node.getChildNodes();
//        for (int i = 0; i < nodes.getLength(); i++) {
//            String nameText = "";
//            String valueText = "";
//            StringBuilder attributes = new StringBuilder();
//            Node item = nodes.item(i);
//
//            if (!item.getNodeName().equals("#text")) {
//                nameText = nodes.item(i).getNodeName();
//            }
//            if (item.getNodeValue() != null) {
//                valueText = nodes.item(i).getNodeValue();
//            }
//
//            if (item.hasAttributes()) {
//                NamedNodeMap att = item.getAttributes();
//                for (int j = 0; j < att.getLength(); j++) {
//                    attributes.append("  ").append(att.item(j).getNodeName()).append(":").append(att.item(j).getNodeValue());
//                }
//            }
//            System.out.println(indent + "Node: " + i + "  " + nameText + "  " + valueText + "   " + attributes);
//            printNode(item, indent + "    ");
//        }
//    }
//    private static void printNode2(Node node, String indent) {
//        String nameText = "";
//        String valueText = "";
//        StringBuilder attributes = new StringBuilder();
//
//        if (!node.getNodeName().equals("#text")) {
//            nameText = node.getNodeName();
//        }
//        if (node.getNodeValue() != null && !node.getNodeValue().equals("\n")) {
//            valueText = "     vText:[" + node.getNodeValue() + "]";
//        }
//        if (node.hasAttributes()) {
//            NamedNodeMap att = node.getAttributes();
//            for (int j = 0; j < att.getLength(); j++) {
//                attributes.append("  ").append(att.item(j).getNodeName()).append(":").append(att.item(j).getNodeValue());
//            }
//        }
//        if (!(nameText.isEmpty() && valueText.isEmpty())) {
//            System.out.println(indent + "Node: [" + nameText + "] type:[" + node.getNodeType() + "]   " + valueText + attributes);
//        }
//        NodeList nodes = node.getChildNodes();
//        for (int i = 0; i < nodes.getLength(); i++) {
//            printNode2(nodes.item(i), indent + "    ");
//        }
//
//    }
}
