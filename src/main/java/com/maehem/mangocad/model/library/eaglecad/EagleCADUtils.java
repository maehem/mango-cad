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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.schematic.Schematic;
import com.maehem.mangocad.view.ControlPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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

        Library lib = convertEagleXMLtoLBR(eagleXML);
        lib.setFilePath(file.getAbsolutePath());

        return lib;
    }

    public static final Schematic importSCH(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        Document eagleXML = readXML(file);
        LOGGER.log(Level.SEVERE, "Schematic XML Read to DOM...");

        Schematic sch = convertEagleXMLtoSCH(eagleXML);
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

    private static Library convertEagleXMLtoLBR(Document eagleXML) throws IOException, EagleCADLibraryFileException {
        Element element = eagleXML.getDocumentElement();
        short nodeType = element.getNodeType();
        String nodeName = element.getNodeName();
        // This Node should be 'eagle'
        if (nodeType != 1 && !nodeName.equals("eagle")) {
            throw new EagleCADLibraryFileException("XML File is not an EagleCAD file!");
        }

        Library lib = new Library();
        String eagleVersion = element.getAttributes().getNamedItem("version").getNodeValue();
        lib.addNote("Imported from EagleCAD LBR file version: " + eagleVersion);
        LOGGER.log(Level.SEVERE, "Importing Eagle Library with version: {0}", eagleVersion);

        NodeList nn = element.getChildNodes();
        for (int j = 0; j < nn.getLength(); j++) {
            Node nnn = nn.item(j);
            if (nnn.getNodeType() != 1) {
                continue;
            }
            switch (nnn.getNodeName()) {
                case "drawing" -> {
                    // drawing (settings?, grid?, filters?, layers, (library | schematic | board))
                    LOGGER.log(Level.SEVERE, "Ingest <drawing>");
                    NodeList nodes = nnn.getChildNodes();
                    // 'eagle' node should have these nodes.
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        if (node.getNodeType() != 1) {
                            continue;
                        }
                        switch (node.getNodeName()) {
                            case "settings", "grid", "filters" -> // Ignore 'settings', 'grid', 'filters' for now.
                                LOGGER.log(Level.SEVERE, "*******  Ignoring Library <drawing> child <{0}>", node.getNodeName());
                            case "layers" -> {
                                LOGGER.log(Level.SEVERE, "        <layers>");                                
                                EagleCADIngest.ingestEagleLayers(lib.getLayers(), node);
                            }
                            case "library" ->
                                ingestEagleLibraryElement(lib, node);
                            default ->
                                LOGGER.log(Level.SEVERE, "Eagle LBR:  Unknown node <{0}>", node.getNodeName());
                        }
                    }
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "Unhandled <drawing> element found: {0}", nnn.getNodeName());
                }
            }
        }
        return lib;
    }

    private static Schematic convertEagleXMLtoSCH(Document eagleXML) throws IOException, EagleCADLibraryFileException {
        // This Node should be 'eagle'
        Element element = eagleXML.getDocumentElement();
        short nodeType = element.getNodeType();
        String nodeName = element.getNodeName();
        if (nodeType != 1 && !nodeName.equals("eagle")) {
            throw new IOException("XML File is not an EagleCAD file!");
        }

        LOGGER.log(Level.SEVERE, "Found a Eagle file...");

        Schematic sch = new Schematic();
        String eagleVersion = element.getAttributes().getNamedItem("version").getNodeValue();
        sch.addNote("Imported from EagleCAD SCH file version: " + eagleVersion);

        NodeList nn = element.getChildNodes();
        for (int j = 0; j < nn.getLength(); j++) {
            Node nnn = nn.item(j);
            if (nnn.getNodeType() != 1) {
                continue;
            }
            LOGGER.log(Level.SEVERE, "Ingest Node: {0}", nnn.getNodeName());
            switch (nnn.getNodeName()) {
                case "drawing" -> {
                    NodeList nodes = nnn.getChildNodes();
                    // drawing (settings?, grid?, filters?, layers, (library | schematic | board))
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node = nodes.item(i);
                        if (node.getNodeType() != 1) {
                            continue;
                        }
                        switch (node.getNodeName()) {
                            case "settings", "grid", "filters" -> // Ignore 'settings', 'grid', 'filters' for now
                                LOGGER.log(Level.SEVERE, "Schematic <drawing> ignoring {0}", node.getNodeName());
                            case "layers" -> {
                                LOGGER.log(Level.SEVERE, "Ingest <layers>");
                                EagleCADIngest.ingestEagleLayers(sch.getLayers(), node);
                            }
                            case "schematic" -> {
                                LOGGER.log(Level.SEVERE, "Ingest <schematic>");
                                ingestEagleSchematicElement(sch, node);
                            }
                            default ->
                                LOGGER.log(Level.SEVERE, "Eagle Import: Unknown top node [{0}]", node.getNodeName());
                        }
                    }
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "Unknown Node found in <eagle>: {0}", nnn.getNodeName());
                }
            }
        }
        return sch;
    }

    /**
     *
     * name: Only in libraries used inside boards or schematics urn: Only in
     * online libraries used inside boards or schematics
     *
     *
     * @param lib
     * @param node
     * @throws EagleCADLibraryFileException
     */
    public static void ingestEagleLibraryElement(Library lib, Node node) throws EagleCADLibraryFileException {
        NamedNodeMap attributes = node.getAttributes();
        // Attributes:   name, urn
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            //LOGGER.log(Level.SEVERE, "Ingest Attribute: " + item.getNodeName() + ": " + item.getNodeValue());
            switch (item.getNodeName()) {
                case "name" -> {
                    lib.setName(value);
                }
                case "urn" -> {
                    lib.setUrn(value);
                }
                default ->
                    throw new EagleCADLibraryFileException("Schematic has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() != 1) {
                continue;
            }
            // <library> sub-nodes:  description, packages, packages3d, symbols, devicesets
            switch (child.getNodeName()) {
                case "description" ->
                    EagleCADIngest.ingestLibraryDescription(lib, child);
                case "packages" ->
                    EagleCADIngest.ingestPackages(child, lib.getPackages());
                case "symbols" ->
                    EagleCADIngest.ingestSymbols(child, lib.getSymbols());
                case "devicesets" ->
                    EagleCADIngest.ingestDeviceSets(child, lib.getDeviceSets());
                case "packages3d" ->
                    EagleCADIngest.ingestPackages3d(child, lib.getPackages3d());
                default ->
                    throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
            }
        }
    }

    private static void ingestEagleSchematicElement(Schematic sch, Node node) throws EagleCADLibraryFileException {
        // Handle attributes
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            LOGGER.log(Level.SEVERE, "Ingest Node: {0}", item.getNodeName());
            switch (item.getNodeName()) {
                case "xreflabel" -> {
                    sch.setXrefLabel(value);
                }
                case "xrefpart" -> {
                    sch.setXrefPart(value);
                }
                default ->
                    throw new EagleCADLibraryFileException("Schematic has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        // Handle sub nodes.
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() != 1) {
                continue;
            }
            LOGGER.log(Level.SEVERE, "Ingest <schematic> node: {0}", child.getNodeName());

            // schematic->drawing children:  
            //      description, libraries, attributes, variantdefs, 
            //      classes, modules, groups, parts, sheets, errors
            switch (child.getNodeName()) {
                case "description" ->
                    EagleCADIngest.ingestDescription(sch.getDescription(), child);
                case "libraries" ->
                    EagleCADIngest.ingestSchematicLibraries(sch.getLibraries(), child);
                case "attributes" ->
                    EagleCADIngest.ingestSchematicAttributes(sch.getAttributes(), child);
                case "variantdefs" ->
                    EagleCADIngest.ingestSchematicVariantDefs(sch.getVariantDefs(), child);
                case "classes" ->
                    EagleCADIngest.ingestSchematicClasses(sch.getNetClasses(), child);
                case "modules" ->
                    EagleCADIngest.ingestSchematicModules(sch.getModules(), child);
                case "groups" ->
                    EagleCADIngest.ingestSchematicGroups(sch.getGroups(), child);
                case "parts" ->
                    EagleCADIngest.ingestSchematicParts(sch.getParts(), child);
                case "sheets" ->
                    EagleCADIngest.ingestSchematicSheets(sch.getSheets(), child);
                case "errors" ->
                    EagleCADIngest.ingestApprovedErrors(sch.getErrors(), child);
                default ->
                    throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
            }

        }
        LOGGER.log(Level.SEVERE, "Finished <schematic>");

    }

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
//    public static void writeYAML(String filePath) throws IOException {
//        Library library = new Library();
//        library.addNote("Imported from EagleCAD " + filePath);
//        Description de = new Description();
//        de.setValue("Resistors, Inductors and Capacitors");
//        library.getDescriptions().add(de);
//        de = new Description();
//        de.setLocale("es_419"); // Mexico
//        de.setValue("Resistencia, Condensador, Inductor");
//        library.getDescriptions().add(de);
//
//        Footprint pkg = new Footprint();
//        pkg.setName("E45-JJ");
//        de = new Description();
//        de.setValue("My package element");
//        pkg.getDescriptions().add(de);
//
//        //pkg.setDisplayUnits("MM");
//        //pkg.setElements(new ArrayList<_AQuantum>());
//        Wire wire = new Wire();
//        wire.setX1(1.4);
//        wire.setY1(2.3);
//        wire.setX2(10.444);
//        wire.setY2(3.33333);
//        wire.setCurve(22.44);
//        wire.setWidth(0.0003);
//        wire.setLayer(33);
//        wire.setCap("round");
//        pkg.getElements().add(wire);
//        Hole hole = new Hole();
//        hole.setX(0.22);
//        hole.setY(1.332);
//        hole.setDrill(0.44);
//        pkg.getElements().add(hole);
//
//        library.getPackages().add(pkg);
//
//        YamlWriter writer = new YamlWriter(new FileWriter(filePath));
//        //writer.getConfig().setPrivateFields(true);
//        writer.getConfig().setClassTag("library", Library.class);
//        writer.getConfig().setClassTag("description", Description.class);
//        writer.getConfig().setClassTag("package", Footprint.class);
//        writer.getConfig().setClassTag("wire", Wire.class);
//        writer.getConfig().setClassTag("hole", Hole.class);
//        writer.write(library);
//        writer.close();
//    }
}
