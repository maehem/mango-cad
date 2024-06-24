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

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.LibraryElement;
import com.maehem.mangocad.model.element.basic.*;
import com.maehem.mangocad.model.element.drawing.*;
import com.maehem.mangocad.model.element.enums.*;
import com.maehem.mangocad.model.element.highlevel.*;
import com.maehem.mangocad.model.element.misc.*;
import com.maehem.mangocad.model.element.property.UnitValue;
import com.maehem.mangocad.view.ControlPanel;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 *
 *
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class EagleCADIngest {

    private static final Logger LOGGER = ControlPanel.LOGGER;

    /**
     * <pre>
     *     drawing (settings?, grid?, filters?, layers, (library | schematic | board))
     * </pre>
     *
     * @param node
     * @return
     * @throws com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException
     */
    public static Drawing ingestDrawing(Node node) throws EagleCADLibraryFileException {
        Drawing drawing = new Drawing();
        // drawing (settings?, grid?, filters?, layers, (library | schematic | board))
        //LOGGER.log(Level.SEVERE, "Ingest <drawing>");

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node subNode = nodes.item(i);
            if (subNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (subNode.getNodeName()) {
                case "settings" -> {
                    ingestSettings(drawing.getSettings(), subNode);
                }
                case "grid" -> {
                    ingestGrid(drawing.getGrid(), subNode);
                }
                case "filters" -> // Ignore 'settings', 'grid', 'filters' for now.
                    LOGGER.log(Level.SEVERE, "*******  Ignoring Library <drawing> child <{0}>", subNode.getNodeName());
                case "layers" -> {
                    ingestEagleLayers(drawing.getLayers(), subNode);
                }
                case "library" -> {
                    if (drawing.getDesign() == null) {
                        drawing.setDesign(new Library());
                        ingestEagleLibraryElement((Library) drawing.getDesign(), subNode);
                        drawing.getDesign().setParentDrawing(drawing);
                    } else {
                        throw new EagleCADLibraryFileException(
                                "Tried to ingest <library> element when there was already a DesignObject assigned!");
                    }
                }
                case "schematic" -> {
                    if (drawing.getDesign() == null) {
                        drawing.setDesign(new Schematic());
                        ingestEagleSchematicElement((Schematic) drawing.getDesign(), subNode);
                        drawing.getDesign().setParentDrawing(drawing);
                        // TODO each sub-library needs parent drawing set?
                    } else {
                        throw new EagleCADLibraryFileException(
                                "Tried to ingest <schematic> element when there was already a DesignObject assigned!");
                    }
                }
                case "board" -> {
                    if (drawing.getDesign() == null) {
                        drawing.setDesign(new Board());
                        ingestEagleBoardElement((Board) drawing.getDesign(), subNode);
                        drawing.getDesign().setParentDrawing(drawing);
                        // TODO each sub-library needs parent drawing set?
                    } else {
                        throw new EagleCADLibraryFileException(
                                "Tried to ingest <board> element when there was already a DesignObject assigned!");
                    }
                }
                default ->
                    LOGGER.log(Level.SEVERE, "<drawing>:  Unknown sub-node <{0}>", subNode.getNodeName());
            }
        }

        // Post-process
        for (LayerElement le : drawing.getLayers().getElements()) {
            switch (drawing.getDesign().getType()) {
                case Board -> {
                    le.setAllowDelete(!BoardLayers.contains(le.getNumber()));
                }
                case Schematic -> {
                    le.setAllowDelete(!SchematicLayers.contains(le.getNumber()));
                }
                case Library -> {
                    le.setAllowDelete(!LibraryLayers.contains(le.getNumber()));
                }
            }
        }

        return drawing;
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
                    throw new EagleCADLibraryFileException("Library has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            // <library> sub-nodes:  description, packages, packages3d, symbols, devicesets
            switch (child.getNodeName()) {
                case "description" ->
                    ingestLibraryDescription(lib, child);
                case "packages" ->
                    ingestPackages(child, lib.getPackages());
                case "symbols" ->
                    ingestSymbols(child, lib.getSymbols());
                case "devicesets" ->
                    ingestDeviceSets(child, lib.getDeviceSets());
                case "packages3d" ->
                    ingestPackages3d(child, lib.getPackages3d());
                default ->
                    throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
            }
        }
    }

    public static void ingestEagleSchematicElement(Schematic sch, Node node) throws EagleCADLibraryFileException {
        // Handle attributes
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
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
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            // schematic->drawing children:
            //      description, libraries, attributes, variantdefs,
            //      classes, modules, groups, parts, sheets, errors
            switch (child.getNodeName()) {
                case "description" ->
                    ingestDescription(sch.getDescription(), child);
                case "libraries" ->
                    ingestSchematicLibraries(sch.getLibraries(), child);
                case "attributes" ->
                    ingestSchematicAttributes(sch.getAttributes(), child);
                case "variantdefs" ->
                    ingestSchematicVariantDefs(sch.getVariantDefs(), child);
                case "classes" ->
                    ingestSchematicClasses(sch.getNetClasses(), child);
                case "modules" ->
                    ingestSchematicModules(sch.getModules(), child);
                case "groups" ->
                    ingestSchematicGroups(sch.getGroups(), child);
                case "parts" ->
                    ingestSchematicParts(sch.getParts(), child);
                case "sheets" ->
                    ingestSchematicSheets(sch, child);
                case "errors" ->
                    ingestApprovedErrors(sch.getErrors(), child);
                default ->
                    throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
            }

        }
    }

    public static void ingestEagleBoardElement(Board brd, Node node) throws EagleCADLibraryFileException {
        // Handle attributes
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "limitedwidth" -> {
                    brd.setLimitedWidth(Double.parseDouble(value));
                }
                default ->
                    throw new EagleCADLibraryFileException("Schematic has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        // Handle sub nodes.
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            //       description?,  x
            //       fusionsync?,   x
            //       fusionteam?,   x
            //       plain?,        x
            //       libraries?,    x
            //       attributes?,   x
            //       variantdefs?,  x
            //       classes?,      x
            //       designrules?,  x
            //       autorouter?,   x
            //       groups?,       x
            //       elements?,     x
            //       signals?,      x
            //       mfgpreviewcolors?, x
            //       errors?        x
            switch (child.getNodeName()) {
                case "description" ->
                    ingestDescription(brd.getDescription(), child);
                case "libraries" ->
                    ingestSchematicLibraries(brd.getLibraries(), child);
                case "attributes" ->
                    ingestSchematicAttributes(brd.getAttributes(), child);
                case "variantdefs" ->
                    ingestSchematicVariantDefs(brd.getVariantDefs(), child);
                case "classes" ->
                    ingestSchematicClasses(brd.getNetClasses(), child);
                case "groups" ->
                    ingestSchematicGroups(brd.getGroups(), child);
                case "elements" ->
                    ingestBoardElements(brd.getElements(), child);
                case "plain" ->
                    ingestPlain(brd.getPlain(), child);
                case "errors" ->
                    ingestApprovedErrors(brd.getErrors(), child);
                case "fusionsync" ->
                    ingestFusionSync(brd.getFusionSync(), child);
                case "fusionteam" ->
                    ingestFusionTeam(brd.getFusionTeam(), child);
                case "designrules" ->
                    ingestDesignRules(brd.getDesignRules(), child);
                case "autorouter" ->
                    ingestAutorouter(brd.getAutorouter(), child);
                case "signals" ->
                    ingestSignals(brd.getSignals(), child);
                case "mfgpreviewcolors" ->
                    ingestMfgPreviewColors(brd.getMfgPreviewColors(), child);
                default ->
                    throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
            }

        }

        // Resolve Elements (Library, Part)
        EagleCADResolve.resolveElements(brd);
        // Resolve Signal->ContactRefs
        EagleCADResolve.resolveContactRefs(brd);
    }

    public static void ingestPackages(Node node, ArrayList<Footprint> packages) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("package")) {
                continue;
            }
            Footprint pkg = new Footprint();
            pkg.setName(item.getAttributes().getNamedItem("name").getNodeValue());

            ingestPackageElements(item.getChildNodes(), pkg);

            packages.add(pkg);
        }
    }

    public static void ingestPackages3d(Node node, ArrayList<Package3d> packages3d) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("package3d")) {
                continue;
            }
            Package3d pkg = new Package3d();
            pkg.setName(item.getAttributes().getNamedItem("name").getNodeValue());
            pkg.setUrn(item.getAttributes().getNamedItem("urn").getNodeValue());
            pkg.setType(item.getAttributes().getNamedItem("type").getNodeValue());

            ingestPackage3dElements(item.getChildNodes(), pkg);

            packages3d.add(pkg);
        }
    }

    /**
     * <pre>
     * symbol ( description?,
     *    (polygon | wire | text | dimension | pin | circle| rectangle | frame)* )
     * attributes
     *     name %String; #REQUIRED
     *     urn %Urn; ""
     *     locally_modified %Bool; "no"
     *     library_version %Int; ""
     *     library_locally_modified %Bool; "no"
     *
     *     library_version and library_locally_modified: Only in managed libraries
     *     inside boards or schematics
     * </pre>
     *
     * @param node
     * @param symbols
     * @throws EagleCADLibraryFileException
     */
    public static void ingestSymbols(Node node, ArrayList<Symbol> symbols) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String nodeName = item.getNodeName();
            switch (nodeName) {
                case Symbol.ELEMENT_NAME -> {
                    Symbol symbol = new Symbol();

                    NamedNodeMap attributes = item.getAttributes();
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attrItem = attributes.item(j);
                        String value = attrItem.getNodeValue();
                        switch (attrItem.getNodeName()) {
                            case "name" ->
                                symbol.setName(value);
                            case "urn" ->
                                symbol.setUrn(value);
                            case "locally_modified" ->
                                symbol.setLocallyModified(value.equalsIgnoreCase("yes"));
                            case "library_version" -> {
                                if (!value.isBlank()) {
                                    symbol.setLibraryVersion(Integer.parseInt(value));
                                }
                            }
                            case "library_locally_modified" ->
                                symbol.setLibraryLocallyModified(value.equalsIgnoreCase("yes"));
                            default ->
                                throw new EagleCADLibraryFileException("Wire has unknown attribute: [" + item.getNodeName() + "]");
                        }
                    }
                    ingestSymbolElements(item.getChildNodes(), symbol);

                    symbols.add(symbol);
                }
                default ->
                    throw new EagleCADLibraryFileException("Symbols list has unknown child: [" + item.getNodeName() + "]");

            }
        }
    }

    public static void ingestDeviceSets(Node node, ArrayList<DeviceSet> deviceSets) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case DeviceSet.ELEMENT_NAME -> {
                    ingestDeviceSet(deviceSets, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<devicesets> has unknown child: [" + item.getNodeName() + "]");
                }
            }
        }
    }

    private static void ingestDeviceSet(List<DeviceSet> list, Node node) throws EagleCADLibraryFileException {
        //  deviceset (description?, gates, devices, spice?)>
        //      ATTLIST deviceset
        //          name          %String;       #REQUIRED
        //          urn              %Urn;       ""
        //          locally_modified %Bool;      "no"
        //          prefix        %String;       ""
        //          uservalue     %Bool;         "no"
        //          library_version  %Int;       ""
        //          library_locally_modified %Bool; "no"
        //          >
        //          <!-- library_version and library_locally_modified: Only in managed libraries inside boards or schematics -->
        NamedNodeMap attributes = node.getAttributes();
        if (attributes.getNamedItem("name") == null) {
            LOGGER.log(Level.SEVERE, "Device Set did not have name set! Node:{0}", node.getTextContent());
        }

        DeviceSet deviceSet = new DeviceSet();
        for (int j = 0; j < attributes.getLength(); j++) {
            Node attrNode = attributes.item(j);
            String value = attrNode.getNodeValue();
            switch (attrNode.getNodeName()) {
                case "name" -> {
                    deviceSet.setName(value);
                }
                case "urn" -> {
                    deviceSet.setUrn(value);
                }
                case "locally_modified" -> {
                    deviceSet.setUservalue(value.equalsIgnoreCase("yes"));
                }
                case "prefix" -> {
                    deviceSet.setPrefix(value);
                }
                case "uservalue" -> {
                    deviceSet.setUservalue(value.equalsIgnoreCase("yes"));
                }
                case "library_version" -> {
                    if (!value.isBlank()) {
                        deviceSet.setLibraryVersion(Integer.parseInt(value));
                    }
                }
                case "library_locally_modified" -> {
                    deviceSet.setUservalue(value.equalsIgnoreCase("yes"));
                }
                default ->
                    throw new EagleCADLibraryFileException("<deviceset> has unknown attribute: [" + attrNode.getNodeName() + "]");
            }
        }
        // Elements
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (child.getNodeName()) {
                case "description" -> // Description
                    // Gets put into 'descriptions' list instead of 'elements'.
                    ingestDescription(deviceSet.getDescriptions(), child);
                case "gates" -> // Gates
                    ingestGates(deviceSet.getGates(), child);
                case "devices" -> // Devices
                    ingestDevices(deviceSet.getDevices(), child);
                default ->
                    throw new EagleCADLibraryFileException("Unknown DeviceSet element encountered: " + child.getNodeName());
            }
        }

        list.add(deviceSet);
    }

    private static void ingestPackageElements(NodeList nodes, Footprint pkg) throws EagleCADLibraryFileException {
        // TODO: Add a flag to check if description appears more than once.
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            List<Element> elements = pkg.getElements();
            // polygon | wire | text | dimension | circle | rectangle | frame | hole | pad | smd
            switch (node.getNodeName()) {
                case "polygon" -> // PolygonElement
                    ingestPolygon(elements, node);
                case "wire" -> // Wire
                    ingestWire(elements, node);
                case "text" -> // ElementText
                    ingestText(elements, node);
                case "dimension" ->
                    ingestDimension(elements, node);
                case "circle" -> // ElementCircle
                    ingestCircle(elements, node);
                case "rectangle" -> // ElementRectangle
                    ingestRectangle(elements, node);
                case "frame" ->
                    ingestFrame(elements, node);
                case "hole" -> // Hole
                    ingestHole(elements, node);
                case "pad" -> // Pad
                    ingestPadThd(elements, node);
                case "smd" -> // SMD
                    ingestPadSmd(elements, node);
                case "description" -> { // Zero or One descriptions
                    // Gets put into 'description' instead of 'elements'.
                    // There should only be one description.
                    ingestDescription(pkg, node);
                }
                default ->
                    throw new EagleCADLibraryFileException("Unknown Package element encountered: <" + node.getNodeName() + ">");
            }
        }
    }

    private static void ingestPackage3dElements(NodeList nodes, Package3d pkg) throws EagleCADLibraryFileException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (node.getNodeName()) {
                case "description" -> // Description
                    // Gets put into 'descriptions' instead of 'elements'.
                    ingestDescription(pkg, node);
                case "packageinstances" -> // 3D Footprint instance
                    ingestPackage3dInstances(pkg, node);
                default ->
                    throw new EagleCADLibraryFileException("Unknown Package3d element encountered: " + node.getNodeName());
            }
        }
    }

    private static void ingestSymbolElements(NodeList nodes, Symbol symbol) throws EagleCADLibraryFileException {
        // TODO: Add a flag to check if description appears more than once.
        List<Element> elements = symbol.getElements();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            // description?, (polygon | wire | text | dimension | pin | circle | rectangle | frame)*
            switch (node.getNodeName()) {
                case "description" -> // Only one description allowed.
                    ingestDescription(symbol, node);
                case "polygon" ->
                    ingestPolygon(elements, node);
                case "wire" ->
                    ingestWire(elements, node);
                case "text" ->
                    ingestText(elements, node);
                case "dimension" ->
                    ingestDimension(elements, node);
                case "pin" ->
                    ingestPin(elements, node);
                case "circle" ->
                    ingestCircle(elements, node);
                case "rectangle" ->
                    ingestRectangle(elements, node);
                case "frame" ->
                    ingestFrame(elements, node);
                default ->
                    throw new EagleCADLibraryFileException("Unknown Symbol element encountered: " + node.getNodeName());
            }
        }
    }

    private static void ingestDescription(LibraryElement libElement, Node node) throws EagleCADLibraryFileException {
        Description desc = new Description();
        Node langAttribute = node.getAttributes().getNamedItem("language");
        if (langAttribute != null) {
            if (!langAttribute.getNodeValue().equals("en")) {
                desc.setLocale(langAttribute.getNodeValue());
            } // else default is en_US so we don't set it.
        }

        String serializeDoc = serializeDoc(node);
        Pattern pattern = Pattern.compile("<description language=\"..\">(.*)</description>");

        Matcher matcher = pattern.matcher(serializeDoc);

        if (matcher.find()) {
            //LOGGER.log(Level.SEVERE, "Matcher Outout: " + matcher.group(1));
            desc.setValue(matcher.group(1));
        } else {
            desc.setValue("");
        }

        libElement.getDescriptions().add(desc);
    }

    /**
     * Ingest a description and put it into a list.
     *
     * @param list
     * @param node
     * @throws com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException
     */
    public static void ingestDescription(List<Description> list, Node node) throws EagleCADLibraryFileException {
        Description desc = new Description();
        ingestDescription(desc, node);
    }

    /**
     * Configure the supplied Description with the Node values.
     *
     * @param desc
     * @param node
     * @throws com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException
     */
    public static void ingestDescription(Description desc, Node node) throws EagleCADLibraryFileException {
        Node langAttribute = node.getAttributes().getNamedItem("language");
        if (langAttribute != null) {
            if (!langAttribute.getNodeValue().equals("en")) {
                desc.setLocale(langAttribute.getNodeValue());
            } // else default is en_US so we don't set it.
        }

        String serializeDoc = serializeDoc(node);
        Pattern pattern = Pattern.compile("<description language=\"..\">(.*)</description>");

        Matcher matcher = pattern.matcher(serializeDoc);

        if (matcher.find()) {
            //LOGGER.log(Level.SEVERE, "Matcher Outout: " + matcher.group(1));
            desc.setValue(matcher.group(1));
        } else {
            desc.setValue("");
        }

        //libElement.getDescriptions().add(desc);
    }

    public static void ingestLibraryDescription(Library library, Node node) {
        Description desc = new Description();
        Node langAttribute = node.getAttributes().getNamedItem("language");
        if (langAttribute != null) {
            if (!langAttribute.getNodeValue().equals("en")) {
                desc.setLocale(langAttribute.getNodeValue());
            } // else default is en_US so we don't set it.
        }
        if (node.getChildNodes().getLength() == 1) {
            desc.setValue(node.getChildNodes().item(0).getTextContent());
        } else {
            //desc.setValue("WARNING: Node has [" + node.getChildNodes().getLength() + "] nodes. Should be only one?");
            desc.setValue(node.getTextContent());
        }
        library.getDescriptions().add(desc);
    }

    /**
     * <pre>
     * wire EMPTY
     *    ATTLIST wire
     *      x1            %Coord;        #REQUIRED
     *      y1            %Coord;        #REQUIRED
     *      x2            %Coord;        #REQUIRED
     *      y2            %Coord;        #REQUIRED
     *      width         %Dimension;    #REQUIRED
     *      layer         %Layer;        #REQUIRED
     *      extent        %Extent;       #IMPLIED
     *      style         %WireStyle;    "continuous"
     *      curve         %WireCurve;    "0"
     *      cap           %WireCap;      "round"
     *      grouprefs     IDREFS         #IMPLIED
     *
     *      extent: Only applicable for airwires -->
     *      cap   : Only applicable if 'curve' is not zero -->
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestWire(List<Element> list, Node node) throws EagleCADLibraryFileException {
        Wire wire = new Wire();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x1" ->
                    wire.setX1(Double.parseDouble(value));
                case "x2" ->
                    wire.setX2(Double.parseDouble(value));
                case "y1" ->
                    wire.setY1(Double.parseDouble(value));
                case "y2" ->
                    wire.setY2(Double.parseDouble(value));
                case "width" ->
                    wire.setWidth(Double.parseDouble(value));
                case "layer" ->
                    wire.setLayerNum(Integer.parseInt(value));
                case "extent" ->
                    wire.setExtent(value);
                case "style" ->
                    wire.setStyle(WireStyle.fromCode(value));
                case "curve" ->
                    wire.setCurve(Double.parseDouble(value));
                case "cap" ->
                    wire.setCap(WireCap.fromCode(value));
                case "grouprefs" ->
                    wire.getGrouprefs().addAll(Arrays.asList(value.split(" ")));
                default ->
                    throw new EagleCADLibraryFileException("Wire has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(wire);
    }

    private static void ingestPadSmd(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        PadSMD smd = new PadSMD();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "cream" ->
                    smd.setCream(value.equals("yes"));
                case "stop" ->
                    smd.setStopmask(value.equals("yes"));
                case "thermals" ->
                    smd.setThermals(value.equals("yes"));
                case "layer" ->
                    smd.setLayerNum(Integer.parseInt(value));
                case "name" ->
                    smd.setName(value);
                case "rot" -> // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    smd.getRotation().setValue(value);
                case "roundness" ->
                    smd.setRoundness(Integer.parseInt(value));
                case "x" ->
                    smd.setX(Double.parseDouble(value));
                case "y" ->
                    smd.setY(Double.parseDouble(value));
                case "dx" ->
                    smd.setWidth(Double.parseDouble(value));
                case "dy" ->
                    smd.setHeight(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("SMD has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(smd);
    }

    private static void ingestPadThd(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        PadTHD thd = new PadTHD();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    thd.setName(value);
                case "first" ->
                    thd.setFirst(value.equals("yes"));
                case "stop" ->
                    thd.setStopmask(value.equals("yes"));
                case "thermals" ->
                    thd.setThermals(value.equals("yes"));
                case "shape" ->
                    thd.setShape(PadShape.fromCode(value));
                case "rot" -> // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    thd.getRotation().setValue(value);
                case "diameter" ->
                    thd.setDiameter(Double.parseDouble(value));
                case "drill" ->
                    thd.setDrill(Double.parseDouble(value));
                case "x" ->
                    thd.setX(Double.parseDouble(value));
                case "y" ->
                    thd.setY(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("Pad has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(thd);
    }

    private static void ingestText(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        ElementText text = new ElementText();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x" ->
                    text.setX(Double.parseDouble(value));
                case "y" ->
                    text.setY(Double.parseDouble(value));
                case "align" ->
                    text.setAlign(TextAlign.fromCode(value));
                case "rot" ->
                    text.rotation.setValue(value);
                case "distance" ->
                    text.setDistance(Integer.parseInt(value));
                case "ratio" ->
                    text.setRatio(Integer.parseInt(value));
                case "size" ->
                    text.setSize(Double.parseDouble(value));
                case "layer" ->
                    text.setLayerNum(Integer.parseInt(value));
                case "font" -> {
                    text.setFont(TextFont.fromCode(value));
                }
                default ->
                    throw new EagleCADLibraryFileException("Text has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        text.setValue(node.getChildNodes().item(0).getNodeValue());

        elements.add(text);
    }

    private static void ingestPolygon(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        PolygonElement poly = new PolygonElement();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "width" ->
                    poly.setWidth(Double.parseDouble(value));
                case "layer" ->
                    poly.setLayerNum(Integer.parseInt(value));
                case "spacing" -> {
                    poly.setSpacing(Double.parseDouble(value));
                } // Non-signal polygon
                case "pour" -> {  // Non-signal polygon
                    poly.setPour(value);
                }
                case "isolate" -> {
                    poly.setIsolate(Double.parseDouble(value));
                } // Non-signal polygon
                case "orphans" -> {
                    poly.setOrphans(value.equals("yes"));
                }  // Non-signal polygon
                case "thermals" -> {
                    poly.setThermals(value.equals("yes"));
                } // Non-signal polygon
                case "rank" -> {
                    poly.setRank(Integer.parseInt(value));
                }  // Non-signal polygon
                default ->
                    throw new EagleCADLibraryFileException("Polygon has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("vertex")) {
                continue;
            }
            ingestVertex(poly.getVertices(), item);
        }

        elements.add(poly);
    }

    private static void ingestSignalPolygon(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        SignalPolygon poly = new SignalPolygon();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "width" ->
                    poly.setWidth(Double.parseDouble(value));
                case "layer" ->
                    poly.setLayerNum(Integer.parseInt(value));
                case "spacing" ->
                    poly.setSpacing(Double.parseDouble(value));
                case "pour" ->
                    poly.setPour(value);
                case "isolate" ->
                    poly.setIsolate(Double.parseDouble(value));
                case "orphans" ->
                    poly.setOrphans(value.equals("yes"));
                case "thermals" ->
                    poly.setThermals(value.equals("yes"));
                case "rank" ->
                    poly.setRank(Integer.parseInt(value));
                default ->
                    throw new EagleCADLibraryFileException("SignalPolygon has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("vertex")) {
                continue;
            }
            ingestVertex(poly.getVertices(), item);
        }

        elements.add(poly);
    }

    private static void ingestRectangle(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        ElementRectangle rect = new ElementRectangle();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "layer" ->
                    rect.setLayerNum(Integer.parseInt(value));
                case "x1" ->
                    rect.setX1(Double.parseDouble(value));
                case "x2" ->
                    rect.setX2(Double.parseDouble(value));
                case "y1" ->
                    rect.setY1(Double.parseDouble(value));
                case "y2" ->
                    rect.setY2(Double.parseDouble(value));
                case "locked" ->
                    rect.lockProperty.setLocked(value.equals("yes"));
                case "rot" ->
                    rect.rotationProperty.setValue(value);
                default ->
                    throw new EagleCADLibraryFileException("Rectangle has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(rect);
    }

    /**
     * <pre>
     * circle EMPTY
     * ATTLIST circle
     * x             %Coord;        #REQUIRED
     * y             %Coord;        #REQUIRED
     * radius        %Coord;        #REQUIRED
     * width         %Dimension;    #REQUIRED
     * layer         %Layer;        #REQUIRED
     * grouprefs     IDREFS         #IMPLIED
     * </pre>
     *
     * @param elements
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestCircle(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        CircleElement circ = new CircleElement();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "layer" ->
                    circ.setLayerNum(Integer.parseInt(value));
                case "x" ->
                    circ.setX(Double.parseDouble(value));
                case "y" ->
                    circ.setY(Double.parseDouble(value));
                case "radius" ->
                    circ.setRadius(Double.parseDouble(value));
                case "width" ->
                    circ.setWidth(Double.parseDouble(value));
                case "grouprefs" ->
                    circ.getGrouprefs().addAll(Arrays.asList(value.split(" ")));
                default ->
                    throw new EagleCADLibraryFileException("Circle has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(circ);
    }

    private static void ingestHole(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        Hole hole = new Hole();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x" ->
                    hole.setX(Double.parseDouble(value));
                case "y" ->
                    hole.setY(Double.parseDouble(value));
                case "drill" ->
                    hole.setDrill(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("Hole has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(hole);
    }

    private static void ingestVia(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        Via via = new Via();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x" ->
                    via.setX(Double.parseDouble(value));
                case "y" ->
                    via.setY(Double.parseDouble(value));
                case "extent" ->
                    via.setExtent(value);
                case "drill" ->
                    via.setDrill(Double.parseDouble(value));
                case "diameter" ->
                    via.setDiameter(Double.parseDouble(value));
                case "shape" ->
                    via.setShape(ViaShape.fromCode(value));
                case "alwaysstop" ->
                    via.setAlwaysstop(value.equals("yes"));
                case "grouprefs" ->
                    via.getGrouprefs().addAll(Arrays.asList(value.split(" ")));
                default -> {
                    throw new EagleCADLibraryFileException("Via has unknown attribute: [" + item.getNodeName() + "]");
                }
            }
        }

        elements.add(via);
    }

    private static void ingestPin(List<Element> elements, Node node) throws EagleCADLibraryFileException {
        Pin pin = new Pin();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    pin.setName(value);
                case "x" ->
                    pin.setX(Double.parseDouble(value));
                case "y" ->
                    pin.setY(Double.parseDouble(value));
                case "visible" ->
                    pin.setVisible(PinVisible.fromCode(value));
                case "length" ->
                    pin.setLength(PinLength.fromCode(value));
                case "direction" ->
                    pin.setDirection(PinDirection.fromCode(value));
                case "function" ->
                    pin.setFunction(PinFunction.fromCode(value));
                case "swaplevel" ->
                    pin.setSwapLevel(Integer.parseInt(value));
                case "rot" ->
                    pin.rotation.setValue(value);
                default ->
                    throw new EagleCADLibraryFileException("Pin has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(pin);
    }

    private static void ingestLabel(List<Element> elements, Node node, String lblText) throws EagleCADLibraryFileException {
        //  label EMPTY>
        //     ATTLIST
        //          x             %Coord;        #REQUIRED
        //          y             %Coord;        #REQUIRED
        //          size          %Dimension;    #REQUIRED
        //          layer         %Layer;        #REQUIRED
        //          font          %TextFont;     "proportional"
        //          ratio         %Int;          "8"
        //          rot           %Rotation;     "R0"
        //          xref          %Bool;         "no"
        //          align         %Align;        "bottom-left"
        //          grouprefs     IDREFS         #IMPLIED
        //          >
        //          <!-- rot:  Only 0, 90, 180 or 270 -->
        //          <!-- xref: Only in <net> context -->
        LabelElement label = new LabelElement();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x" ->
                    label.setX(Double.parseDouble(value));
                case "y" ->
                    label.setY(Double.parseDouble(value));
                case "size" ->
                    label.setSize(Double.parseDouble(value));
                case "layer" ->
                    label.setLayerNum(Integer.parseInt(value));
                case "font" ->
                    label.setFont(TextFont.fromCode(value));
                case "ratio" ->
                    label.setRatio(Integer.parseInt(value));
                case "rot" ->
                    label.rotation.setValue(value);
                case "xref" ->
                    label.setXref(value.equalsIgnoreCase("yes"));
                case "align" ->
                    label.setAlign(TextAlign.fromCode(value));
                case "grouprefs" ->
                    label.getGrouprefs().addAll(Arrays.asList(value.split(" ")));

                default ->
                    throw new EagleCADLibraryFileException("Label has unknown attribute: [" + item.getNodeName() + "]");
            }
        }
        label.setValue(lblText);

        elements.add(label);
    }

    private static void ingestProbe(List<Element> elements, Node node, String netName) throws EagleCADLibraryFileException {
        // probe ( no sub-nodes )
        //    ATTLIST probe
        //        x             %Coord;        #REQUIRED
        //        y             %Coord;        #REQUIRED
        //        size          %Dimension;    #REQUIRED
        //        layer         %Layer;        #REQUIRED
        //        font          %TextFont;     "proportional"
        //        ratio         %Int;          "8"
        //        rot           %Rotation;     "R0"
        //        xref          %Bool;         "no"
        //        grouprefs     IDREFS         #IMPLIED
        //
        //      Added recently:
        //        probetype    %Int             "0"
        //
        //        <!-- rot:  Only 0, 90, 180 or 270 -->
        //        <!-- xref: Only in <net> context -->

        Probe probe = new Probe();
        probe.setValue(netName);
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x" ->
                    probe.setX(Double.parseDouble(value));
                case "y" ->
                    probe.setY(Double.parseDouble(value));
                case "size" ->
                    probe.setSize(Double.parseDouble(value));
                case "layer" ->
                    probe.setLayerNum(Integer.parseInt(value));
                case "font" ->
                    probe.setFont(TextFont.fromCode(value));
                case "ratio" ->
                    probe.setRatio(Integer.parseInt(value));
                case "rot" ->
                    probe.rotation.setValue(value);
                case "xref" ->
                    probe.setXref(value.equalsIgnoreCase("yes"));
                case "probetype" ->
                    probe.setProbeType(Integer.parseInt(value));
                case "grouprefs" ->
                    probe.getGrouprefs().addAll(Arrays.asList(value.split(" ")));

                default ->
                    throw new EagleCADLibraryFileException("Probe has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        elements.add(probe);
    }

    private static void ingestFrame(List<Element> list, Node node) throws EagleCADLibraryFileException {
        FrameElement frame = new FrameElement();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "columns" ->
                    frame.setColumns(Integer.parseInt(value));
                case "rows" ->
                    frame.setRows(Integer.parseInt(value));
                case "layer" ->
                    frame.setLayerNum(Integer.parseInt(value));
                case "border-top" ->
                    frame.setBorderTop(value.equalsIgnoreCase("yes"));
                case "border-right" ->
                    frame.setBorderRight(value.equalsIgnoreCase("yes"));
                case "border-bottom" ->
                    frame.setBorderBottom(value.equalsIgnoreCase("yes"));
                case "border-left" ->
                    frame.setBorderLeft(value.equalsIgnoreCase("yes"));
                case "x1" ->
                    frame.setX1(Double.parseDouble(value));
                case "x2" ->
                    frame.setX2(Double.parseDouble(value));
                case "y1" ->
                    frame.setY1(Double.parseDouble(value));
                case "y2" ->
                    frame.setY2(Double.parseDouble(value));
                case "grouprefs" ->
                    frame.getGroupRefs().addAll(Arrays.asList(value.split(" ")));

                default ->
                    throw new EagleCADLibraryFileException("Frame has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(frame);
    }

    private static void ingestGates(List<Gate> list, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(Gate.ELEMENT_NAME)) {
                continue;
            }

            ingestGate(list, item);
        }
    }

    private static void ingestDevices(List<Device> list, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("device")) {
                continue;
            }

            ingestDevice(list, item);
        }
    }

    private static void ingestGate(List<Gate> list, Node node) throws EagleCADLibraryFileException {
        Gate gate = new Gate();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    gate.setName(value);
                case "x" ->
                    gate.setX(Double.parseDouble(value));
                case "y" ->
                    gate.setY(Double.parseDouble(value));
                case "symbol" ->
                    gate.setSymbol(value);
                case "addlevel" ->
                    gate.setAddlevel(value);
                case "swaplevel" ->
                    gate.setSwapLevel(Integer.parseInt(value));
                default ->
                    throw new EagleCADLibraryFileException("Gate has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(gate);
    }

    private static void ingestDevice(List<Device> list, Node node) throws EagleCADLibraryFileException {
        Device device = new Device();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    device.setName(value);
                case "package" ->
                    device.setFootprint(value);
                default ->
                    throw new EagleCADLibraryFileException("Device has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node subItem = childNodes.item(j);
            if (subItem.getNodeType() != 1) {
                continue;
            }
            switch (subItem.getNodeName()) {
                case "connects" ->
                    ingestConnections(subItem, device);
                case "technologies" ->
                    ingestTechnologies(subItem, device);
                case "package3dinstances" ->
                    ingestDevicePackagesInstances3d(subItem, device);
                default ->
                    throw new EagleCADLibraryFileException("Device childNode has unknown attribute: [" + subItem.getNodeName() + "]");
            }
        }

        list.add(device);
    }

    private static void ingestConnections(Node node, Device device) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("connect")) {
                continue;
            }

            ingestConnection(device, item);
        }
    }

    private static void ingestTechnologies(Node node, Device device) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("technology")) {
                continue;
            }

            ingestTechnology(device, item);
        }
    }

    private static void ingestDevicePackagesInstances3d(Node node, Device device) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("package3dinstance")) {
                continue;
            }

            ingestDevicePackageInstance3d(device, item);
        }
    }

    private static void ingestConnection(Device device, Node node) throws EagleCADLibraryFileException {
        Connection connection = new Connection();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "gate" ->
                    connection.setGate(value);
                case "pin" ->
                    connection.setPin(value);
                case "pad" ->
                    connection.setPad(value);
                case "route" ->
                    connection.setRoute(value);
                default ->
                    throw new EagleCADLibraryFileException("Connection has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        device.getConnections().add(connection);
    }

    private static void ingestTechnology(Device device, Node node) throws EagleCADLibraryFileException {
        Technology technology = new Technology();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    technology.setName(value);
                default ->
                    throw new EagleCADLibraryFileException("Technology has unknown attribute: [" + item.getNodeName() + "]");
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node attributeNode = childNodes.item(j);
                if (!attributeNode.getNodeName().equals(Attribute.ELEMENT_NAME)) {
                    continue;
                }

                ingestAttribute(technology.getAttributes(), attributeNode);
            }
        }

        device.getTechnologies().add(technology);
    }

    private static void ingestDevicePackageInstance3d(Device device, Node node) throws EagleCADLibraryFileException {
        Package3dInstance packageInstance = new Package3dInstance();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "package3d_urn" ->
                    packageInstance.setPackage3dUrn(value);
                default ->
                    throw new EagleCADLibraryFileException("Device PackageInstance3D has unknown attribute: [" + item.getNodeName() + "]");
            }
        }
        device.getPackage3dInstances().add(packageInstance);
    }

    /**
     * Ingest an \<attribute\> node.
     * <pre>
     * attribute
     *      name        %String;        #REQUIRED
     *      value       %String;        #IMPLIED
     *      x           %Coord;         #IMPLIED
     *      y           %Coord;         #IMPLIED
     *      size        %Dimension;     #IMPLIED
     *      layer       %Layer;         #IMPLIED
     *      font        %TextFont;      #IMPLIED
     *      ratio       %Int;           #IMPLIED
     *      rot         %Rotation;      "R0"
     *      display     %AttributeDisplay; "value"
     *      constant     %Bool;          "no"
     *      align       %Align;         "bottom-left"
     *      grouprefs   IDREFS          #IMPLIED
     *      // Not in older Eagle DTD
     *      locked      %Bool;          #IMPLIED
     *
     *     display: Only in <element> or <instance> context
     *     constant:Only in <device> context
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestAttribute(List<Attribute> list, Node node) throws EagleCADLibraryFileException {
        Attribute attribute = new Attribute();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" -> {
                    list.forEach((att) -> {
                        if (att.getName().equals(value)) {
                            LOGGER.log(Level.SEVERE, "Duplicate name in attributes.");
                        }
                    });
                    attribute.setName(value);
                }
                case "value" ->
                    attribute.setValue(value);
                case "x" ->
                    attribute.setX(Double.parseDouble(value));
                case "y" ->
                    attribute.setY(Double.parseDouble(value));
                case "rot" ->
                    attribute.getRotation().setValue(value);
                case "ratio" ->
                    attribute.setRatio(Integer.parseInt(value));
                case "size" ->
                    attribute.setSize(Double.parseDouble(value));
                case "layer" ->
                    attribute.setLayerNum(Integer.parseInt(value));
                case "display" ->
                    attribute.setDisplay(value);
                case "constant" ->
                    attribute.setConstant(value.equals("yes"));
                case "font" ->
                    attribute.setFont(TextFont.fromCode(value));
                case "align" ->
                    attribute.setAlign(TextAlign.fromCode(value));
                case "locked" ->
                    attribute.setLocked(value.equals("yes"));
                default ->
                    throw new EagleCADLibraryFileException("Attribute has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(attribute);
    }

    private static void ingestPackage3dInstances(Package3d packages, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("packageinstance")) {
                continue;
            }

            NamedNodeMap attributes = item.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attr = attributes.item(j);
                switch (attr.getNodeName()) {
                    case "name" ->
                        packages.getPackageInstances().add(new PackageInstance(attr.getNodeValue())
                        );
                    default ->
                        throw new EagleCADLibraryFileException(
                                "package3dInstance has unknown attribute: [" + attr.getNodeName() + "]"
                        );
                }
            }
        }
    }

    /**
     * Used by description parser to get sub-HTML snippets used by legacy
     * description tags.The XML parser tends to DOM it all out, but we need it
     * raw to render in our content areas properly. Lifted from StackOverflow
     * https://stackoverflow.com/questions/8873393/get-node-raw-text
     *
     *
     * @param doc node to transform
     * @return raw HTML content of the doc node.
     * @throws com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException
     */
    public static String serializeDoc(Node doc) throws EagleCADLibraryFileException {
        StringWriter outText = new StringWriter();
        StreamResult sr = new StreamResult(outText);
        Properties oprops = new Properties();
        oprops.put(OutputKeys.METHOD, "html");
        //oprops.put(OutputKeys.METHOD, "xml");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t;
        try {
            t = tf.newTransformer();
            t.setOutputProperties(oprops);
            t.transform(new DOMSource(doc), sr);
        } catch (TransformerException e) {
            throw new EagleCADLibraryFileException("Could not serialize Node into a string!");
        }
        return outText.toString();
    }

    /**
     * Node has maybe multiple libraries.
     *
     * @param libraries
     * @param node
     */
    static void ingestSchematicLibraries(ArrayList<Library> libraries, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("library")) {
                // TODO: Log this exception.
                continue;
            }

            Library lib = new Library();
            ingestEagleLibraryElement(lib, item);

            libraries.add(lib);
        }
    }

    /**
     *
     * @param attributes
     * @param child
     */
    static void ingestSchematicAttributes(ArrayList<Attribute> attributes, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != 1) {
                continue;
            }
            if (!item.getNodeName().equals(Attribute.ELEMENT_NAME)) {
                // I think w3c DOM importer already handles this based on eagle.dtd. Should experiment with this.
                throw new EagleCADLibraryFileException("Unknown node <" + item.getNodeName() + "> found in <attributes>. Should only contain <attribute> type nodes.");
            }

            ingestAttribute(attributes, item);
        }
    }

    static void ingestSchematicVariantDefs(ArrayList<VariantDefinition> variantDefs, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(VariantDefinition.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestVariantDef(variantDefs, item);
        }
    }

    private static void ingestVariantDef(List<VariantDefinition> list, Node node) throws EagleCADLibraryFileException {
        VariantDefinition variantDef = new VariantDefinition();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    variantDef.setName(value);
                case "current" ->
                    variantDef.setCurrent("yes".equals(value));
                default ->
                    throw new EagleCADLibraryFileException("VariantDef has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(variantDef);
    }

    static void ingestSchematicClasses(ArrayList<NetClass> netClasses, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(NetClass.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestNetClass(netClasses, item);
        }
    }

    /**
     * number %Class; #REQUIRED name %String; #REQUIRED width %Dimension; "0"
     * drill %Dimension; "0"
     *
     * @param list to place new NetClass into.
     * @param node of attributes to configure NetClass settings.
     * @throws EagleCADLibraryFileException
     */
    private static void ingestNetClass(List<NetClass> list, Node node) throws EagleCADLibraryFileException {
        NetClass netClass = new NetClass();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "number" ->
                    netClass.setNumber(Integer.parseInt(value));
                case "name" ->
                    netClass.setName(value);
                case "width" ->
                    netClass.setWidth(Double.parseDouble(value));
                case "drill" ->
                    netClass.setDrill(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("NetClass has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(netClass);
    }

    static void ingestSchematicModules(ArrayList<CircuitModule> modules, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(CircuitModule.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestCircuitModule(modules, item);
        }
    }

    /**
     * module name %String; #REQUIRED prefix %String; "" dx %Coord; #REQUIRED dy
     * %Coord; #REQUIRED
     *
     * @param modules
     * @param node
     */
    private static void ingestCircuitModule(ArrayList<CircuitModule> modules, Node node) throws EagleCADLibraryFileException {
        CircuitModule module = new CircuitModule();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    module.setName(value);
                case "prefix" ->
                    module.setPrefix(value);
                case "x" ->
                    module.setX(Double.parseDouble(value));
                case "y" ->
                    module.setY(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("CircuitModule has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        modules.add(module);
    }

    static void ingestSchematicGroups(ArrayList<SchematicGroup> groups, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(SchematicGroup.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestSchematicGroup(groups, item);
        }
    }

    /**
     * schematic_group name ID #REQUIRED selectable %Bool; #IMPLIED width
     * %Dimension; #IMPLIED titleSize %Dimension; #IMPLIED titleFont %TextFont;
     * #IMPLIED style %WireStyle; #IMPLIED showAnnotations %Bool; #IMPLIED layer
     * %Layer; #IMPLIED grouprefs IDREFS #IMPLIED
     *
     * @param groups
     * @param node
     */
    private static void ingestSchematicGroup(ArrayList<SchematicGroup> groups, Node node) throws EagleCADLibraryFileException {
        SchematicGroup group = new SchematicGroup();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    group.setName(value);
                case "selectable" ->
                    group.setSelectable(value.equalsIgnoreCase("yes"));
                case "width" ->
                    group.setWidth(Double.parseDouble(value));
                case "titleSize" ->
                    group.setTitleSize(Double.parseDouble(value));
                case "titleFont" ->
                    group.setTitleFont(TextFont.fromCode(value));
                case "style" ->
                    group.setStyle(WireStyle.fromCode(value));
                case "showAnnotations" ->
                    group.setShowAnnotations(value.equalsIgnoreCase("yes"));
                case "layer" ->
                    group.setLayerNum(Integer.parseInt(value));
                case "grouprefs" ->
                    ingestGroupRefs(group.getGrouprefs(), value);
                default ->
                    throw new EagleCADLibraryFileException("SchematicGroup has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        groups.add(group);
    }

    private static void ingestGroupRefs(ArrayList<String> grouprefs, String value) {
        String[] split = value.split(" ");
        grouprefs.addAll(Arrays.asList(split));
    }

    static void ingestSchematicParts(ArrayList<Part> parts, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(Part.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestSchematicPart(parts, item);
        }
    }

    /**
     * part name %String; #REQUIRED library %String; #REQUIRED library_urn %Urn;
     * "" deviceset %String; #REQUIRED device %String; #REQUIRED package3d_urn
     * %Urn; "" override_package3d_urn %Urn; "" override_package_urn %Urn; ""
     * override_locally_modified %Bool; "no" technology %String; "" value
     * %String; #IMPLIED
     *
     * @param parts
     * @param node
     */
    private static void ingestSchematicPart(ArrayList<Part> parts, Node node) throws EagleCADLibraryFileException {
        Part part = new Part();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    part.setName(value);
                case "library" ->
                    part.setLibrary(value);
                case "library_urn" ->
                    part.setLibraryUrn(value);
                case "deviceset" ->
                    part.setDeviceSet(value);
                case "device" ->
                    part.setDevice(value);
                case "package3d_urn" ->
                    part.setPackage3dUrn(value);
                case "override_package3d_urn" ->
                    part.setOverridePackage3dUrn(value);
                case "override_package_urn" ->
                    part.setOverridePackageUrn(value);
                case "override_locally_modified" ->
                    part.setOverrideLocallyModified(value.equalsIgnoreCase("yes"));
                case "technology" ->
                    part.setTechnology(value);
                case "value" ->
                    part.setValue(value);

                default ->
                    throw new EagleCADLibraryFileException("SchematicPart has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "attribute" -> {
                    ingestAttribute(part.getAttributes(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Part <attributes> list has unknown child: [" + item.getNodeName() + "]");
                }

            }
        }

        parts.add(part);
    }

    /**
     *
     * @param sheets
     * @param child
     */
    static void ingestSchematicSheets(Schematic sch, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(Sheet.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            Sheet sheet = ingestSchematicSheet(item);
            sheet.setParent(sch);
            sch.getSheets().add(sheet);
            //sheet.postIngest();
        }
    }

    /**
     *
     *
     * @param sheets
     * @param node
     */
    private static Sheet ingestSchematicSheet(Node node) throws EagleCADLibraryFileException {
        Sheet sheet = new Sheet();

        // Handle sub nodes.
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {

                // sheet (description?, plain?, moduleinsts?, instances?, busses?, nets?)
                switch (child.getNodeName()) {
                    case "description" ->
                        ingestDescription(sheet.getDescription(), child);
                    case "plain" ->
                        ingestPlain(sheet.getPlain(), child);
                    case "moduleinsts" ->
                        ingestModuleInsts(sheet.getModuleInsts(), child);
                    case "instances" ->
                        ingestInstances(sheet.getInststances(), child);
                    case "busses" ->
                        ingestNets(sheet.getBusInsts(), child, true);
                    case "nets" ->
                        ingestNets(sheet.getNetInsts(), child, false);
                    default ->
                        throw new EagleCADLibraryFileException("Unknown tag [" + child.getNodeName() + "] passed at [" + node.getNodeName() + "]");
                }
            }
        }
        return sheet;
    }

    static void ingestApprovedErrors(ArrayList<Approved> errors, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(Approved.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            Approved approvedError = new Approved();

            NamedNodeMap attributes = item.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attrItem = attributes.item(j);
                String value = attrItem.getNodeValue();
                switch (attrItem.getNodeName()) {
                    case "hash" ->
                        approvedError.setHash(value);

                    default ->
                        throw new EagleCADLibraryFileException("<approved> has unknown attribute: [" + attrItem.getNodeName() + "]");
                }
            }

            errors.add(approvedError);
        }
    }

    //static void ingestEagleLayers(LayerElement[] layers, Node node) throws EagleCADLibraryFileException {
    static void ingestEagleLayers(Layers layers, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != 1) {
                continue;
            }
            switch (item.getNodeName()) {
                case "layer" -> {
                    ingestEagleLayer(layers, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<layers> list has unknown child: [" + item.getNodeName() + "]");
                }
            }
        }
    }

    static void ingestEagleLayer(Layers layers, Node node) throws EagleCADLibraryFileException {

        LayerElement layer = new LayerElement();

        // layer
        //  number        %Layer;        #REQUIRED
        //  name          %String;       #REQUIRED
        //  color         %Int;          #REQUIRED
        //  fill          %Int;          #REQUIRED
        //  visible       %Bool;         "yes"
        //  active        %Bool;         "yes"
        NamedNodeMap attributes = node.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            Node attrItem = attributes.item(j);
            String value = attrItem.getNodeValue();
            switch (attrItem.getNodeName()) {
                case "number" ->
                    layer.setNumber(Integer.parseInt(value));
                case "name" ->
                    layer.setName(value);
                case "color" ->
                    layer.setColorIndex(Integer.parseInt(value));
                case "fill" ->
                    layer.setFill(Integer.parseInt(value));
                case "visible" ->
                    layer.setVisible(value.equalsIgnoreCase(value));
                case "active" ->
                    layer.setActive(value.equalsIgnoreCase(value));

                default ->
                    throw new EagleCADLibraryFileException("<layer> has unknown attribute: [" + attrItem.getNodeName() + "]");
            }

        }
        //LOGGER.log(Level.SEVERE, layer.toString());
        //layers[layer.getNumber()] = layer;
        layers.getElements().add(layer);
    }

    /**
     * polygon | wire | text | dimension | circle | spline | rectangle | frame |
     * hole
     *
     * @param plain
     * @param node
     */
    private static void ingestPlain(List<Element> plain, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != 1) {
                continue;
            }
            switch (item.getNodeName()) {
                case "polygon" -> {
                    ingestPolygon(plain, item);
                }
                case "wire" -> {
                    ingestWire(plain, item);
                }
                case "text" -> {
                    ingestText(plain, item);
                }
                case "dimension" -> {
                    ingestDimension(plain, item);
                }
                case "circle" -> {
                    ingestCircle(plain, item);
                }
                case "spline" -> {
                    ingestSpline(plain, item);
                }
                case "rectangle" -> {
                    ingestRectangle(plain, item);
                }
                case "frame" -> {
                    ingestFrame(plain, item);
                }
                case "hole" -> {
                    ingestHole(plain, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<plain> list has unknown child: [" + item.getNodeName() + "]");
                }

            }
        }
    }

    private static void ingestNets(List<Net> netInsts, Node node, boolean asBus) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!item.getNodeName().equals(Net.ELEMENT_NAME_NET)) {
                LOGGER.log(Level.SEVERE, "Unrecognized Element in Net ingest: [{0}] ... ignoring.", item.getNodeName());
                continue;
            }
            //LOGGER.log(Level.SEVERE, "Ingest <net>");
            // net (segment) // segment list
            // ATTLIST
            //    name          %String;       #REQUIRED
            //    class         %Class;        "0"
            Net net = new Net(asBus);

            NamedNodeMap attributes = item.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attrItem = attributes.item(j);
                String value = attrItem.getNodeValue();
                switch (attrItem.getNodeName()) {
                    case "name" -> {
                        //LOGGER.log(Level.SEVERE, "    name: " + value);
                        net.setName(value);
                    }
                    case "class" -> {
                        if (!asBus) {
                            net.setNetClass(Integer.parseInt(value));
                        } else {
                            LOGGER.log(Level.SEVERE, "Bus calls out 'class' attribute, but it shouldn't have one.");
                        }
                    }

                    default ->
                        throw new EagleCADLibraryFileException("Approved has unknown attribute: [" + attrItem.getNodeName() + "]");
                }
            }

            // inget Segments
            NodeList segmentNodes = item.getChildNodes();
            for (int j = 0; j < segmentNodes.getLength(); j++) {
                Node segmentItem = segmentNodes.item(j);
                if (segmentItem.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if (!segmentItem.getNodeName().equals(Segment.ELEMENT_NAME)) {
                    LOGGER.log(Level.SEVERE, "Unrecognized Element in Segment ingest: [{0}] ... ignoring.", item.getNodeName());
                    continue;
                }

                ingestSegment(net.getSegments(), segmentItem, net.getName());
            }

            //LOGGER.log(Level.SEVERE, "Net [{0}] has {1} elements.", new Object[]{net.getName(), net.getSegments().size()});
            netInsts.add(net);
        }
        //LOGGER.log(Level.SEVERE, "Ingested " + netInsts.size() + " netInsts.");
    }

    /**
     * <pre>
     * segment (pinref | portref | wire | junction | label | probe)
     *     'pinref' and 'junction' are only valid in a <net> context
     * </pre>
     *
     * @param segmentList
     * @param node
     */
    private static void ingestSegment(List<Segment> segmentList, Node node, String netName) throws EagleCADLibraryFileException {

        Segment seg = new Segment(); // Segment is a List
        //LOGGER.log(Level.SEVERE, "        <segment>");
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            //LOGGER.log(Level.SEVERE, "            Attribute: " + item.getNodeName());
            switch (item.getNodeName()) {
                case "pinref" -> {
                    ingestPinref(seg, item);
                }
                case "portref" -> {
                    ingestPortref(seg, item);
                }
                case "wire" -> {
                    ingestWire(seg, item);
                }
                case "junction" -> {
                    ingestJunction(seg, item);
                }
                case "label" -> {
                    ingestLabel(seg, item, netName);
                }
                case "probe" -> {
                    ingestProbe(seg, item, netName);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Segment list has unknown node: [" + item.getNodeName() + "]");
                }

            }
        }
        segmentList.add(seg);
    }

    private static void ingestPinref(List<Element> list, Node node) throws EagleCADLibraryFileException {
        PinRef pinref = new PinRef();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "part" ->
                    pinref.setPart(value);
                case "gate" ->
                    pinref.setGate(value);
                case "pin" ->
                    pinref.setPin(value);
                default ->
                    throw new EagleCADLibraryFileException("PinRef has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(pinref);
    }

    /**
     * portref moduleinst %String; #REQUIRED port %String; #REQUIRED
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestPortref(List<Element> list, Node node) throws EagleCADLibraryFileException {
        PortRef pinref = new PortRef();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "moduleinst" ->
                    pinref.setModuleInst(value);
                case "port" ->
                    pinref.setPort(value);
                default ->
                    throw new EagleCADLibraryFileException("PortRef has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        list.add(pinref);
    }

    private static void ingestModuleInsts(List<ModuleInst> moduleInsts, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "moduleinst" -> {
                    ingestModuleInst(moduleInsts, node);
                }
                default -> {
                    throw new EagleCADLibraryFileException("ModuleInsts list has unknown node: [" + item.getNodeName() + "]");
                }
            }
        }
    }

    /**
     * moduleinst (attribute) name %String; #REQUIRED module %String; #REQUIRED
     * modulevariant %String; "" x %Coord; #REQUIRED y %Coord; #REQUIRED offset
     * %Int; "0" smashed %Bool; "no" rot %Rotation; "R0"
     *
     * @param moduleInsts
     * @param node
     */
    private static void ingestModuleInst(List<ModuleInst> moduleInsts, Node node) throws EagleCADLibraryFileException {
        ModuleInst moduleInst = new ModuleInst();

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    moduleInst.setName(value);
                case "module" ->
                    moduleInst.setModule(value);
                case "modulevariant" ->
                    moduleInst.setModuleVariant(value);
                case "x" ->
                    moduleInst.setX(Double.parseDouble(value));
                case "y" ->
                    moduleInst.setY(Double.parseDouble(value));
                case "offset" ->
                    moduleInst.setOffset(Integer.parseInt(value));
                case "smashed" ->
                    moduleInst.setSmashed(value.equals("yes"));
                case "rot" -> // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    moduleInst.getRotation().setValue(value);
                default ->
                    throw new EagleCADLibraryFileException("ModuleInst has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            switch (item.getNodeName()) {
                case "attribute" -> {
                    ingestAttribute(moduleInst.getAttributes(), node);
                }
                default -> {
                    throw new EagleCADLibraryFileException("ModuleInst attributes list has unknown node: [" + item.getNodeName() + "]");
                }

            }
        }
        moduleInsts.add(moduleInst);
    }

    private static void ingestInstances(List<Instance> inststances, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "instance" -> {
                    ingestInstance(inststances, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Instances list has unknown node: [" + item.getNodeName() + "]");
                }

            }
        }
    }

    /**
     * instance ( attribute(s) ) instance part %String; #REQUIRED gate %String;
     * #REQUIRED x %Coord; #REQUIRED y %Coord; #REQUIRED smashed %Bool; "no" rot
     * %Rotation; "R0" grouprefs IDREFS #IMPLIED
     *
     * rot: Only 0, 90, 180 or 270
     *
     * @param inststances
     * @param child
     */
    private static void ingestInstance(List<Instance> instances, Node node) throws EagleCADLibraryFileException {
        Instance instance = new Instance();

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            if (item.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "part" ->
                    instance.setPart(value);
                case "gate" ->
                    instance.setGate(value);
                case "x" ->
                    instance.setX(Double.parseDouble(value));
                case "y" ->
                    instance.setY(Double.parseDouble(value));
                case "smashed" ->
                    instance.setSmashed(value.equals("yes"));
                case "rot" ->
                    instance.getRotation().setValue(value);
                default ->
                    throw new EagleCADLibraryFileException("Instance has unknown XML attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "attribute" -> {
                    ingestAttribute(instance.getAttributes(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Instance Attribute list has unknown child: [" + item.getNodeName() + "]");
                }

            }
        }
        instances.add(instance);
    }

    /**
     *
     * @param list
     * @param node
     */
    private static void ingestSpline(List<Element> list, Node node) throws EagleCADLibraryFileException {
        //spline (vertex)*>
        //  Four simple (non-curve) vertices define the control points of a degree-3 spline curve
        //  ATTLIST
        //     width          %Dimension;    #REQUIRED
        //     layer           %Int%       IMPLIED    (new to 9.7)
        //     locked          %Bool%      IMPLIED    (new to 9.7)
        Spline spline = new Spline();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            if (item.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }
            String value = item.getNodeValue();

            switch (item.getNodeName()) {
                case "width" ->
                    spline.setWidth(Double.parseDouble(value));
                case "layer" ->
                    spline.setLayerNum(Integer.parseInt(value));
                case "locked" ->
                    spline.setLocked(value.equalsIgnoreCase("yes"));

                default ->
                    throw new EagleCADLibraryFileException("Spline has unknown XML attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "vertex" -> {
                    ingestVertex(spline.getVertices(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Spline Vertex list has unknown child: [" + item.getNodeName() + "]");
                }

            }
        }
        list.add(spline);
    }

    /**
     *
     * @param list
     * @param node
     */
    private static void ingestVertex(List<Vertex> list, Node node) throws EagleCADLibraryFileException {
        Vertex v = new Vertex();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "x" ->
                    v.setX(Double.parseDouble(value));
                case "y" ->
                    v.setY(Double.parseDouble(value));
                case "curve" ->
                    v.setCurve(Double.parseDouble(value));
                default ->
                    throw new EagleCADLibraryFileException("Vertex has unknown attribute: [" + node.getNodeName() + "]");
            }

        }
        list.add(v);
    }

    /**
     * <pre>
     * dimension EMPTY
     *    ATTR
     *      x1            %Coord;        #REQUIRED
     *      y1            %Coord;        #REQUIRED
     *      x2            %Coord;        #REQUIRED
     *      y2            %Coord;        #REQUIRED
     *      x3            %Coord;        #REQUIRED
     *      y3            %Coord;        #REQUIRED
     *      layer         %Layer;        #REQUIRED
     *      dtype         %DimensionType; "parallel"
     *      width         %Dimension;    "0.13"
     *      extwidth      %Dimension;    "0"
     *      extlength     %Dimension;    "0"
     *      extoffset     %Dimension;    "0"
     *      textsize      %Dimension;    #REQUIRED
     *      textratio     %Int;          "8"
     *      unit          %GridUnit;     "mm"
     *      precision     %Int;          "2"
     *      visible       %Bool;         "no"
     *      grouprefs     IDREFS         #IMPLIED
     * </pre>
     *
     * @param list
     * @param node
     */
    private static void ingestDimension(List<Element> list, Node node) throws EagleCADLibraryFileException {
        Dimension dim = new Dimension();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "x1":
                    dim.setX1(Double.parseDouble(value));
                    break;
                case "y1":
                    dim.setY1(Double.parseDouble(value));
                    break;
                case "x2":
                    dim.setX2(Double.parseDouble(value));
                    break;
                case "y2":
                    dim.setY2(Double.parseDouble(value));
                    break;
                case "x3":
                    dim.setX3(Double.parseDouble(value));
                    break;
                case "y3":
                    dim.setY3(Double.parseDouble(value));
                    break;
                case "locked":
                    dim.lockProperty.setLocked(value.equals("yes"));
                    break;
                case "layer":
                    dim.setLayerNum(Integer.parseInt(value));
                    break;
                case "dtype":
                    dim.setDtype(DimensionType.fromCode(value));
                    break;
                case "width":
                    dim.setWidth(Double.parseDouble(value));
                    break;
                case "extwidth":
                    dim.setExtwidth(Double.parseDouble(value));
                    break;
                case "extlength":
                    dim.setExtlength(Double.parseDouble(value));
                    break;
                case "extoffset":
                    dim.setExtoffset(Double.parseDouble(value));
                    break;
                case "textsize":
                    dim.setTextsize(Double.parseDouble(value));
                    break;
                case "textratio":
                    dim.setTextratio(Integer.parseInt(value));
                    break;
                case "unit":
                    dim.setUnit(UnitValue.Unit.fromCode(value));
                    break;
                case "precision":
                    dim.setPrecision(Integer.parseInt(value));
                    break;
                case "visible":
                    dim.setVisible(value.equalsIgnoreCase("yes"));
                case "grourefs":
                    dim.getGrouprefs().addAll(Arrays.asList(value.split(" ")));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Dimension has unknown attribute: [" + it.getNodeName() + "]");
            }

        }
        list.add(dim);
    }

    /**
     *
     * @param list
     * @param node
     */
    private static void ingestJunction(List<Element> list, Node node) throws EagleCADLibraryFileException {
        Junction junction = new Junction();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "x" ->
                    junction.setX(Double.parseDouble(value));
                case "y" ->
                    junction.setY(Double.parseDouble(value));
                case "grourefs" ->
                    junction.getGrouprefs().addAll(Arrays.asList(value.split(" ")));
                default ->
                    throw new EagleCADLibraryFileException("Junction has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(junction);
    }

    static void ingestSettings(List<Setting> settings, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != 1) {
                continue;
            }
            if (!item.getNodeName().equals(Setting.ELEMENT_NAME)) {
                // I think w3c DOM importer already handles this based on eagle.dtd. Should experiment with this.
                throw new EagleCADLibraryFileException("Unknown node found in <settings>. Should only contain <setting> type nodes.");
            }

            ingestSetting(settings, item);
        }
    }

    /**
     *
     * <pre>
     * setting EMPTY
     *    ATTLIST setting
     * alwaysvectorfont %Bool;         #IMPLIED
     * verticaltext     %VerticalText; "up"
     * keepoldvectorfont %Bool;        "no"
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestSetting(List<Setting> list, Node node) throws EagleCADLibraryFileException {
        Setting setting = new Setting();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "alwaysvectorfont" ->
                    setting.setAlwaysVectorFont(value.equalsIgnoreCase("yes"));
                case "verticaltext" ->
                    setting.setVerticalText(VerticalText.fromCode(value));
                case "keepoldvectorfont" ->
                    setting.setKeepOldVectorFont(value.equalsIgnoreCase("yes"));
                default ->
                    throw new EagleCADLibraryFileException("Setting has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(setting);
    }

    /**
     * <pre>
     * grid EMPTY
     * ATTRIBUTES
     * distance      %Real;         #IMPLIED
     * unitdist      %GridUnit;     #IMPLIED
     * unit          %GridUnit;     #IMPLIED
     * style         %GridStyle;    "lines"
     * multiple      %Int;          "1"
     * display       %Bool;         "no"
     * altdistance   %Real;         #IMPLIED
     * altunitdist   %GridUnit;     #IMPLIED
     * altunit       %GridUnit;     #IMPLIED
     * </pre>
     *
     * @param grid
     * @param node
     * @throws com.maehem.mangocad.model.eaglecad.EagleCADLibraryFileException
     */
    public static void ingestGrid(Grid grid, Node node) throws EagleCADLibraryFileException {
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "distance" -> // size
                    grid.setSize(Double.parseDouble(value));
                case "unitdist" -> // unit of stored number
                    grid.setSizeStoredUnit(GridUnit.fromCode(value));
                case "unit" ->  // unit for application combobox
                    grid.setSizeUnit(GridUnit.fromCode(value));
                case "style" ->
                    grid.setStyle(GridStyle.fromCode(value));
                case "multiple" ->
                    grid.setMultiple(Integer.parseInt(value));
                case "display" ->
                    grid.setDisplay(value.equalsIgnoreCase("yes"));
                case "altdistance" ->
                    grid.setAltSize(Double.parseDouble(value));
                case "altunitdist" ->
                    grid.setAltStoredUnit(GridUnit.fromCode(value));
                case "altunit" ->
                    grid.setAltUnit(GridUnit.fromCode(value));
                default ->
                    throw new EagleCADLibraryFileException("Junction has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
    }

    public static void ingestFilters(List<Filter> filters, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!item.getNodeName().equals(Filter.ELEMENT_NAME)) {
                // I think w3c DOM importer already handles this based on eagle.dtd. Should experiment with this.
                throw new EagleCADLibraryFileException("Unknown node found in <filters>. Should only contain <filter> type nodes.");
            }

            ingestFilter(filters, item);
        }
    }

    /**
     * <pre>
     * filter EMPTY>
     * ATTLIST filter
     * name          %String;       #REQUIRED
     * expression    %String;       #REQUIRED
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestFilter(List<Filter> list, Node node) throws EagleCADLibraryFileException {
        Filter filter = new Filter();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "name" ->
                    filter.setName(value);
                case "expression" ->
                    filter.setExpression(value);
                default ->
                    throw new EagleCADLibraryFileException("Filter has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(filter);
    }

    static void ingestNotes(List<Note> notes, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!item.getNodeName().equals(Note.ELEMENT_NAME)) {
                // I think w3c DOM importer already handles this based on eagle.dtd. Should experiment with this.
                throw new EagleCADLibraryFileException("Unknown node found in <compatibility>. Should only contain <note> type nodes.");
            }

            ingestNote(notes, item);
        }
    }

    /**
     * <pre>
     * note (#PCDATA)
     * ATTLIST
     * version       %Real;         #REQUIRED
     * severity      %Severity;     #REQUIRED
     *
     * version: The EAGLE file version that introduced this compatibility note
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestNote(List<Note> list, Node node) throws EagleCADLibraryFileException {
        Note note = new Note();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "version" -> {
                    note.setVersion(value);
                }
                case "minversion" -> {
                    note.setVersion(value);
                }
                case "severity" ->
                    note.setSeverity(Severity.fromCode(value));
                default ->
                    throw new EagleCADLibraryFileException("Note has unknown attribute: [" + it.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.TEXT_NODE) {
                note.setValue(item.getTextContent());
                //LOGGER.log(Level.SEVERE, "Ingest Note: \n" + note.getValue());
            } else {
                LOGGER.log(Level.SEVERE, "Note was not text????");
            }
        }

        list.add(note);
    }

    static void ingestBoardElements(ArrayList<ElementElement> elements, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals(ElementElement.ELEMENT_NAME)) {
                // TODO: Log this exception.
                continue;
            }

            ingestBoardElement(elements, item);
        }
    }

    /**
     * <pre>
     * ELEMENT element (attribute*, variant*)
     *    variant* is accepted only for compatibility with EAGLE 6.x files
     *    ATTLIST element
     *      name          %String;       #REQUIRED
     *      library       %String;       #REQUIRED
     *      library_urn   %Urn;          ""
     *      package       %String;       #REQUIRED
     *      package3d_urn %Urn;          ""
     *      override_package3d_urn %Urn; ""
     *      override_package_urn %Urn;    ""
     *      override_locally_modified %Bool; "no"
     *      value         %String;       #REQUIRED
     *      x             %Coord;        #REQUIRED
     *      y             %Coord;        #REQUIRED
     *      locked        %Bool;         "no"
     *      populate      %Bool;         "yes"
     *      smashed       %Bool;         "no"
     *      rot           %Rotation;     "R0"
     *      grouprefs     IDREFS         #IMPLIED
     *
     *      library_urn: Only in parts from online libraries
     *
     * </pre>
     *
     *
     * @param elements
     * @param node
     */
    private static void ingestBoardElement(ArrayList<ElementElement> elements, Node node) throws EagleCADLibraryFileException {
        ElementElement element = new ElementElement();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    element.setName(value);
                case "library" ->
                    element.setLibrary(value);
                case "library_urn" ->
                    element.setLibraryUrn(value);
                case "package" ->
                    element.setFootprint(value);
                case "package3d_urn" ->
                    element.setPackage3dUrn(value);
                case "override_package3d_urn" ->
                    element.setOverridePackage3dUrn(value);
                case "override_package_urn" ->
                    element.setOverridePackageUrn(value);
                case "override_locally_modified" ->
                    element.setOverrideLocallyModified(value.equalsIgnoreCase("yes"));
                case "value" ->
                    element.setValue(value);
                case "x" ->
                    element.setX(Double.parseDouble(value));
                case "y" ->
                    element.setY(Double.parseDouble(value));
                case "locked" ->
                    element.setLocked(value.equalsIgnoreCase("yes"));
                case "populate" ->
                    element.setPopulate(value.equalsIgnoreCase("yes"));
                case "smashed" ->
                    element.setSmashed(value.equalsIgnoreCase("yes"));
                case "rot" ->
                    element.getRotation().setValue(value);
                case "grourefs" ->
                    element.getGrouprefs().addAll(Arrays.asList(value.split(" ")));

                default ->
                    throw new EagleCADLibraryFileException("BoardElement has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "attribute" -> {
                    ingestAttribute(element.getAttributes(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("Element attributes list has unknown node: [" + item.getNodeName() + "]");
                }

            }
        }

        elements.add(element);
    }

    /**
     * <pre>
     * ELEMENT fusionsync EMPTY>
     *   ATTLIST fusionsync
     *      huburn                   %String;       #REQUIRED
     *      projecturn               %String;       #REQUIRED
     *      f3durn                   %String;       #REQUIRED
     *      pcbguid                  %String;       #REQUIRED
     *      lastsyncedchangeguid     %String;       #REQUIRED
     *      lastpulledtime           %String;       #REQUIRED
     *
     * </pre>
     *
     * @param fs fusion sync object to store attributes into
     * @param node XML node containing input attributes.
     * @throws EagleCADLibraryFileException
     */
    private static void ingestFusionSync(FusionSync fs, Node node) throws EagleCADLibraryFileException {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "huburn" ->
                    fs.setHubUrn(value);
                case "projecturn" ->
                    fs.setProjectUrn(value);
                case "f3durn" ->
                    fs.setF3dUrn(value);
                case "pcbguid" ->
                    fs.setPcbUid(value);
                case "lastsyncedchangeguid" ->
                    fs.setLastSyncChangeUid(value);
                case "lastpulledtime" ->
                    fs.setLastPulledTime(value);

                case "latestrevisionid" ->
                    fs.setLatestRevisionId(value);
                case "lastsyncedrevisionid" ->
                    fs.setLastSyncedRevisionId(value);
                case "lastboardhashguid" ->
                    fs.setLastBoardHashGuid(value);
                case "lastpushedtime" ->
                    fs.setLastPushedTime(value);
                case "linktopcb3d" ->
                    fs.setLinkToPcb3d(value.equalsIgnoreCase("true"));
                default ->
                    throw new EagleCADLibraryFileException("FusionSync has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

    }

    /**
     * <pre>
     * ELEMENT fusionteam EMPTY
     *    ATTLIST fusionteam
     *      huburn                   %String;       #REQUIRED
     *      projecturn               %String;       #REQUIRED
     *      folderUrn                %String;       #REQUIRED
     *      urn                      %String;       #REQUIRED
     *      versionUrn               %String;       #REQUIRED
     *      camFileUrn               %String;       #REQUIRED
     *      camFileVersionUrn        %String;       #REQUIRED
     *      lastpublishedchangeguid  %String;       #REQUIRED
     * </pre>
     *
     * @param ft FusionTeam object to store attributes into
     * @param node XML node containing input attributes.
     * @throws EagleCADLibraryFileException
     */
    private static void ingestFusionTeam(FusionTeam ft, Node node) throws EagleCADLibraryFileException {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "huburn" ->
                    ft.setHubUrn(value);
                case "projecturn" ->
                    ft.setProjectUrn(value);
                case "folderUrn" ->
                    ft.setFolderUrn(value);
                case "urn" ->
                    ft.setUrn(value);
                case "versionUrn" ->
                    ft.setVersionUrn(value);
                case "camFileUrn" ->
                    ft.setCamFileUrn(value);
                case "camFileVersionUrn" ->
                    ft.setCamFileVersionUrn(value);
                case "lastpublishedchangeguid" ->
                    ft.setLastPublishedChangeUid(value);
                default ->
                    throw new EagleCADLibraryFileException("FusionTeam has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

    }

    /**
     * <pre>
     * ELEMENT designrules (description*, param*)>
     *    ATTLIST designrules
     *      name          %String;       #REQUIRED
     * </pre>
     *
     *
     * @param dr
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestDesignRules(DesignRules dr, Node node) throws EagleCADLibraryFileException {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name" ->
                    dr.setName(value);
                default ->
                    throw new EagleCADLibraryFileException("<designrules> has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "description" -> {
                    Description desc = new Description();
                    ingestDescription(desc, item);
                    dr.getDescriptions().add(desc);
                }
                case "param" -> {
                    ingestParam(dr.getParams(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<designrules> has unknown child: <" + item.getNodeName() + ">");
                }
            }
        }
    }

    /**
     * <pre>
     * ELEMENT param EMPTY
     *   ATTLIST param
     * name          %String;       #REQUIRED
     * value         %String;       #REQUIRED
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestParam(List<Param> list, Node node) throws EagleCADLibraryFileException {
        Param param = new Param();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "name" ->
                    param.setName(value);
                case "value" ->
                    param.setValue(value);
                default ->
                    throw new EagleCADLibraryFileException("<param> has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(param);
    }

    /**
     * <pre>
     * ELEMENT designrules (description*, param*)>
     *     ATTLIST designrules
     * name          %String;       #REQUIRED
     * </pre>
     *
     *
     * @param dr
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestAutorouter(ArrayList<Pass> dr, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "pass" -> {
                    ingestPass(dr, node);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<autorouter> has unknown child: <" + item.getNodeName() + ">");
                }
            }
        }
    }

    /**
     * <pre>
     * ELEMENT pass (param)*>
     *    ATTLIST pass
     *      name          %String;       #REQUIRED
     *      refer         %String;       #IMPLIED
     *      active        %Bool;         "yes"
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestPass(List<Pass> list, Node node) throws EagleCADLibraryFileException {
        Pass pass = new Pass();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "name" ->
                    pass.setName(value);
                case "refer" ->
                    pass.setRefer(value);
                case "active" ->
                    pass.setActive(value.equalsIgnoreCase("yes"));
                default ->
                    throw new EagleCADLibraryFileException("<pass> has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(pass);
    }

    /**
     * <pre>
     * ELEMENT signals (signal*)>
     *     ATTLIST EMPTY
     * </pre>
     *
     *
     * @param dr
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestSignals(ArrayList<Signal> dr, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "signal" -> {
                    ingestSignal(dr, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<signals> has unknown child: <" + item.getNodeName() + ">");
                }
            }
        }
    }

    /**
     * <pre>
     * ELEMENT signal (contactref | polygon | wire | via)*>
     *   ATTLIST signal
     *      name          %String;       #REQUIRED
     *      class         %Class;        "0"
     *      airwireshidden %Bool;        "no"
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestSignal(List<Signal> list, Node node) throws EagleCADLibraryFileException {
        Signal signal = new Signal();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "name" ->
                    signal.setName(value);
                case "class" ->
                    signal.setNetClass(Integer.parseInt(value));
                case "airwireshidden" ->
                    signal.setAirwiresHidden(value.equalsIgnoreCase("yes"));
                default ->
                    throw new EagleCADLibraryFileException("<signal> has unknown attribute: [" + node.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "contactref", "polygon", "wire", "via" -> {
                    ingestSignalElement(signal.getElements(), item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<signal> has unknown child: [" + item.getNodeName() + "]");
                }
            }
        }

        list.add(signal);
    }

    /**
     * element: contactref | polygon | wire | via
     *
     * @param list
     * @param node
     */
    private static void ingestSignalElement(List<Element> list, Node item) throws EagleCADLibraryFileException {
        switch (item.getNodeName()) {
            case "contactref" -> {
                ingestContactRef(list, item);
            }
            case "polygon" -> {
                ingestSignalPolygon(list, item);
            }
            case "wire" -> {
                ingestWire(list, item);
            }
            case "via" -> {
                ingestVia(list, item);
            }
            default -> {
                throw new EagleCADLibraryFileException("<signal> list has unknown child: [" + item.getNodeName() + "]");
            }
        }
    }

    /**
     * <pre>
     * ELEMENT contactref EMPTY>
     *    ATTLIST contactref
     *      element       %String;       #REQUIRED
     *      pad           %String;       #REQUIRED
     *      route         %ContactRoute; "all"
     *      routetag      %String;       ""
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestContactRef(List<Element> list, Node node) throws EagleCADLibraryFileException {
        ContactRef cRef = new ContactRef();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "element" ->
                    cRef.setElement(value);
                case "pad" ->
                    cRef.setPad(value);
                case "routetag" ->
                    cRef.setRouteTag(value);
                case "route" ->
                    cRef.setRoute(ContactRoute.fromCode(value));
                default ->
                    throw new EagleCADLibraryFileException("<contactref> has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(cRef);
    }

    /**
     * <pre>
     *  mfgpreviewcolors (mfgpreviewcolor)*
     * </pre>
     *
     * @param mfgPreviewColors
     * @param child
     */
    private static void ingestMfgPreviewColors(ArrayList<MfgPreviewColor> mfgPreviewColors, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (item.getNodeName()) {
                case "mfgpreviewcolor" -> {
                    ingestMfgPreviewColor(mfgPreviewColors, item);
                }
                default -> {
                    throw new EagleCADLibraryFileException("<mfgpreviewcolors> has unknown child: [" + item.getNodeName() + "]");
                }
            }
        }
    }

    /**
     * <pre>
     * ELEMENT mfgpreviewcolor EMPTY
     *    ATTLIST
     *      name          %String;       #REQUIRED
     *      color         %String;       #REQUIRED
     * </pre>
     *
     * @param list
     * @param node
     * @throws EagleCADLibraryFileException
     */
    private static void ingestMfgPreviewColor(List<MfgPreviewColor> list, Node node) throws EagleCADLibraryFileException {
        MfgPreviewColor mpColor = new MfgPreviewColor();
        NamedNodeMap att = node.getAttributes();
        for (int j = 0; j < att.getLength(); j++) {
            Node it = att.item(j);
            String value = it.getNodeValue();
            switch (it.getNodeName()) {
                case "name" ->
                    mpColor.setName(value);
                case "color" ->
                    mpColor.setColor(value);
                default ->
                    throw new EagleCADLibraryFileException("<mfgpreviewcolor> has unknown attribute: [" + node.getNodeName() + "]");
            }
        }
        list.add(mpColor);
    }

}
