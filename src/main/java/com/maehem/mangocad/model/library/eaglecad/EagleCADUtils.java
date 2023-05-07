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

import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.maehem.mangocad.model.library.Library;
import com.maehem.mangocad.model.library.element.Description;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.quantum.Hole;
import com.maehem.mangocad.model.library.element.quantum.Wire;
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

    private EagleCADUtils() {
    }   // Static methods only!

    public static final Library importLBR(File file) throws FileNotFoundException, IOException, EagleCADLibraryFileException {
        //Library lib = new Library();
        //File lbr = file;

        Document eagleXML = readXML(file);
        //Element element = eagleXML.getDocumentElement();

        Library lib = convertEagleXML(eagleXML);
        lib.setFilePath(file.getAbsolutePath());

        return lib;
    }

    public static final Document readXML(File xml) {
        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
            //printNode2(dom, "");

            
            //DEBUGGING: Will print all nodes during decode.
            // get the first element
            //Element element = dom.getDocumentElement();
            //printNode2(element, "");

            /*

            role1 = getTextValue(role1, doc, "role1");
            if (role1 != null) {
                if (!role1.isEmpty()) {
                    rolev.add(role1);
                }
            }
            role2 = getTextValue(role2, doc, "role2");
            if (role2 != null) {
                if (!role2.isEmpty()) {
                    rolev.add(role2);
                }
            }
            role3 = getTextValue(role3, doc, "role3");
            if (role3 != null) {
                if (!role3.isEmpty()) {
                    rolev.add(role3);
                }
            }
            role4 = getTextValue(role4, doc, "role4");
            if (role4 != null) {
                if (!role4.isEmpty()) {
                    rolev.add(role4);
                }
            }
            return true;
             */
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        //return false;
        return dom;

    }

    private static Library convertEagleXML(Document eagleXML) throws IOException, EagleCADLibraryFileException {
        // This Node should be 'eagle'
        Element element = eagleXML.getDocumentElement();
        short nodeType = element.getNodeType();
        String nodeName = element.getNodeName();
        if (nodeType != 1 && !nodeName.equals("eagle")) {
            throw new IOException("XML File is not an EagleCAD file!");
        }

        Library lib = new Library();
        String eagleVersion = element.getAttributes().getNamedItem("version").getNodeValue();
        lib.addNote("Imported from EagleCAD LBR file version: " + eagleVersion);

        NodeList nn = element.getChildNodes();
        for (int j = 0; j < nn.getLength(); j++) {
            Node nnn = nn.item(j);
            if (nnn.getNodeName().equals("drawing")) {
                NodeList nodes = nnn.getChildNodes();
                // 'eagle' node should have these nodes.
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == 1) {
                        switch (node.getNodeName()) {
                            case "settings":  // Ignore 'settings'
                            case "layers":    // Ignore 'layers'
                            case "grid":      // Ignore 'grid'
                                break;
                            case "library":   // This is what we came for
                                ingestEagleLibraryElement(lib, node);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        return lib;
    }

    private static void ingestEagleLibraryElement(Library lib, Node node) throws EagleCADLibraryFileException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() == 1) {
                switch (child.getNodeName()) {
                    case "description":
                        EagleCADIngest.ingestLibraryDescription(lib, child);
                        break;
                    case "packages":
                        EagleCADIngest.ingestPackages(child, lib.getPackages());
                        break;
                    case "symbols":
                        EagleCADIngest.ingestSymbols(child, lib.getSymbols());
                        break;
                    case "devicesets":
                        EagleCADIngest.ingestDeviceSets(child, lib.getDeviceSets());
                        break;
                    case "packages3d":
                        EagleCADIngest.ingestPackages3d(child, lib.getPackages3d());
                        break;
                    default:
                        throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
                }
            } //else {
                //throw new EagleCADLibraryFileException("Unexpected node type [" + child.getNodeType() + "] at [" + node.getNodeName() + "]");

            //}
        }

    }

    private static String getTextValue(String def, Element doc, String tag) {
        String value = def;
        NodeList nl;
        nl = doc.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }

    private static void printNode(Node node, String indent) {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            String nameText = "";
            String valueText = "";
            StringBuilder attributes = new StringBuilder();
            Node item = nodes.item(i);

            if (!item.getNodeName().equals("#text")) {
                nameText = nodes.item(i).getNodeName();
            }
            if (item.getNodeValue() != null) {
                valueText = nodes.item(i).getNodeValue();
            }

            if (item.hasAttributes()) {
                NamedNodeMap att = item.getAttributes();
                for (int j = 0; j < att.getLength(); j++) {
                    attributes.append("  ").append(att.item(j).getNodeName()).append(":").append(att.item(j).getNodeValue());
                }
            }
            System.out.println(indent + "Node: " + i + "  " + nameText + "  " + valueText + "   " + attributes);
            printNode(item, indent + "    ");
        }
    }

    private static void printNode2(Node node, String indent) {
        String nameText = "";
        String valueText = "";
        StringBuilder attributes = new StringBuilder();

        if (!node.getNodeName().equals("#text")) {
            nameText = node.getNodeName();
        }
        if (node.getNodeValue() != null && !node.getNodeValue().equals("\n")) {
            valueText = "     vText:[" + node.getNodeValue() + "]";
        }
        if (node.hasAttributes()) {
            NamedNodeMap att = node.getAttributes();
            for (int j = 0; j < att.getLength(); j++) {
                attributes.append("  ").append(att.item(j).getNodeName()).append(":").append(att.item(j).getNodeValue());
            }
        }
        if (!(nameText.isEmpty() && valueText.isEmpty())) {
            System.out.println(indent + "Node: [" + nameText + "] type:[" + node.getNodeType() + "]   " + valueText + attributes);
        }
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            printNode2(nodes.item(i), indent + "    ");
        }

    }

    public static void writeYAML(String filePath) throws IOException {
        Library library = new Library();
        library.addNote("Imported from EagleCAD " + filePath);
        Description de = new Description();
        de.setValue("Resistors, Inductors and Capacitors");
        library.getDescriptions().add(de);
        de = new Description();
        de.setLocale("es_419"); // Mexico
        de.setValue("Resistencia, Condensador, Inductor");
        library.getDescriptions().add(de);

        Footprint pkg = new Footprint();
        pkg.setName("E45-JJ");
        de = new Description();
        de.setValue("My package element");
        pkg.getDescriptions().add(de);

        pkg.setDisplayUnits("MM");
        //pkg.setElements(new ArrayList<_AQuantum>());
        Wire wire = new Wire();
        wire.setX(1.4);
        wire.setY(2.3);
        wire.setX2(10.444);
        wire.setY2(3.33333);
        wire.setCurve(22.44);
        wire.setWidth(0.0003);
        wire.setLayer(33);
        wire.setCap("round");
        pkg.getElements().add(wire);
        Hole hole = new Hole();
        hole.setX(0.22);
        hole.setY(1.332);
        hole.setDrill(0.44);
        pkg.getElements().add(hole);

        library.getPackages().add(pkg);

        YamlWriter writer = new YamlWriter(new FileWriter(filePath));
        //writer.getConfig().setPrivateFields(true);
        writer.getConfig().setClassTag("library", Library.class);
        writer.getConfig().setClassTag("description", Description.class);
        writer.getConfig().setClassTag("package", Footprint.class);
        writer.getConfig().setClassTag("wire", Wire.class);
        writer.getConfig().setClassTag("hole", Hole.class);
        writer.write(library);
        writer.close();
    }
}
