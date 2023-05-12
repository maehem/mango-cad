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

import com.maehem.mangocad.model.library.Library;
import java.util.ArrayList;
import com.maehem.mangocad.model.library.element.Description;
import com.maehem.mangocad.model.library.element.DeviceSet;
import com.maehem.mangocad.model.library.element.LibraryElement;
import com.maehem.mangocad.model.library.element.Footprint;
import com.maehem.mangocad.model.library.element.PackageInstance3d;
import com.maehem.mangocad.model.library.element.Package3d;
import com.maehem.mangocad.model.library.element.Symbol;
import com.maehem.mangocad.model.library.element.device.Attribute;
import com.maehem.mangocad.model.library.element.device.Connection;
import com.maehem.mangocad.model.library.element.device.Device;
import com.maehem.mangocad.model.library.element.device.DevicePackageInstance3d;
import com.maehem.mangocad.model.library.element.device.Technology;
import com.maehem.mangocad.model.library.element.quantum.Circle;
import com.maehem.mangocad.model.library.element.quantum.Gate;
import com.maehem.mangocad.model.library.element.quantum.Hole;
import com.maehem.mangocad.model.library.element.quantum.PadSMD;
import com.maehem.mangocad.model.library.element.quantum.PadTHD;
import com.maehem.mangocad.model.library.element.quantum.Pin;
import com.maehem.mangocad.model.library.element.quantum.Polygon;
import com.maehem.mangocad.model.library.element.quantum.Rectangle;
import com.maehem.mangocad.model.library.element.quantum.Text;
import com.maehem.mangocad.model.library.element.quantum.Vertex;
import com.maehem.mangocad.model.library.element.quantum.Via;
import com.maehem.mangocad.model.library.element.quantum.Wire;
import com.maehem.mangocad.model.library.element.quantum.enums.PinDirection;
import com.maehem.mangocad.model.library.element.quantum.enums.PinFunction;
import com.maehem.mangocad.model.library.element.quantum.enums.PinLength;
import com.maehem.mangocad.model.library.element.quantum.enums.PinVisible;
import com.maehem.mangocad.model.library.element.quantum.enums.TextAlign;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class EagleCADIngest {
    private static final Logger LOGGER = Logger.getLogger(EagleCADIngest.class.getSimpleName());

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

    public static void ingestSymbols(Node node, ArrayList<Symbol> symbols) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("symbol")) {
                continue;
            }
            Symbol symbol = new Symbol();
            symbol.setName(item.getAttributes().getNamedItem("name").getNodeValue());

            ingestSymbolElements(item.getChildNodes(), symbol);

            symbols.add(symbol);
        }
    }

    public static void ingestDeviceSets(Node node, ArrayList<DeviceSet> deviceSets) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("deviceset")) {
                continue;
            }
            DeviceSet deviceSet = new DeviceSet();
            deviceSet.setName(item.getAttributes().getNamedItem("name").getNodeValue());
            deviceSet.setPrefix(item.getAttributes().getNamedItem("prefix").getNodeValue());
            deviceSet.setUservalue(item.getAttributes().getNamedItem("uservalue").getNodeValue().equals("yes"));

            ingestDeviceSetElements(item.getChildNodes(), deviceSet);

            deviceSets.add(deviceSet);
        }
    }

    private static void ingestPackageElements(NodeList nodes, Footprint pkg) throws EagleCADLibraryFileException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case "#text" -> {
                    continue; // skip this element
                }
                case "description" -> // Description
                    // Gets put into 'descriptions' instead of 'elements'.
                    ingestDescription(pkg, node);
                case "wire" -> // Wire
                    ingestWire(pkg, node);
                case "smd" -> // SMD
                    ingestPadSmd(pkg, node);
                case "pad" -> // Pad
                    ingestPadThd(pkg, node);
                case "text" -> // Text
                    ingestText(pkg, node);
                case "polygon" -> // Polygon
                    ingestPolygon(pkg, node);
                case "rectangle" -> // Rectangle
                    ingestRectangle(pkg, node);
                case "circle" -> // Circle
                    ingestCircle(pkg, node);
                case "hole" -> // Hole
                    ingestHole(pkg, node);
                case "via" -> // Via
                    ingestVia(pkg, node);
                default ->
                    throw new EagleCADLibraryFileException("Unknown Package element encountered: " + node.getNodeName());
            }
        }
    }

    private static void ingestPackage3dElements(NodeList nodes, Package3d pkg) throws EagleCADLibraryFileException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case "#text" -> {
                    continue; // skip this element
                }
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
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case "#text":
                    continue; // skip this element
                case "description": // Description
                    // Gets put into 'descriptions' instead of 'elements'.
                    ingestDescription(symbol, node);
                    break;
                case "wire":        // Wire
                    ingestWire(symbol, node);
                    break;
                case "text":        // Text
                    ingestText(symbol, node);
                    break;
                case "polygon":     // Polygon
                    ingestPolygon(symbol, node);
                    break;
                case "rectangle":   // Rectangle
                    ingestRectangle(symbol, node);
                    break;
                case "circle":      // Circle
                    ingestCircle(symbol, node);
                    break;
                case "pin":      // Circle
                    ingestPin(symbol, node);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Unknown Package element encountered: " + node.getNodeName());
            }
        }
    }

    private static void ingestDeviceSetElements(NodeList nodes, DeviceSet deviceSet) throws EagleCADLibraryFileException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case "#text":
                    continue; // skip this element
                case "description": // Description
                    // Gets put into 'descriptions' list instead of 'elements'.
                    ingestDescription(deviceSet, node);
                    break;
                case "gates":        // Gates
                    ingestGates(deviceSet, node);
                    break;
                case "devices":        // Devices
                    ingestDevices(deviceSet, node);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Unknown Package element encountered: " + node.getNodeName());
            }
        }
    }

    private static void ingestDescription(LibraryElement libElement, Node node) {
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

    private static void ingestWire(LibraryElement pkg, Node node) throws EagleCADLibraryFileException {
        Wire wire = new Wire();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "cap":
                    wire.setCap(value);
                    break;
                case "curve":
                    wire.setCurve(Double.parseDouble(value));
                    break;
                case "layer":
                    wire.setLayer(Integer.parseInt(value));
                    break;
                case "style":
                    wire.setStyle(value);
                    break;
                case "width":
                    wire.setWidth(Double.parseDouble(value));
                    break;
                case "x1":
                    wire.setX(Double.parseDouble(value));
                    break;
                case "x2":
                    wire.setX2(Double.parseDouble(value));
                    break;
                case "y1":
                    wire.setY(Double.parseDouble(value));
                    break;
                case "y2":
                    wire.setY2(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Wire has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        pkg.getElements().add(wire);
    }

    private static void ingestPadSmd(Footprint pkg, Node node) throws EagleCADLibraryFileException {
        PadSMD smd = new PadSMD();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "cream":
                    smd.setCream(value.equals("yes"));
                    break;
                case "stop":
                    smd.setStopmask(value.equals("yes"));
                    break;
                case "thermals":
                    smd.setThermals(value.equals("yes"));
                    break;
                case "layer":
                    smd.setLayer(Integer.parseInt(value));
                    break;
                case "name":
                    smd.setName(value);
                    break;
                case "rot":
                    // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    smd.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                case "roundness":
                    smd.setRoundness(Integer.parseInt(value));
                    break;
                case "x":
                    smd.setX(Double.parseDouble(value));
                    break;
                case "y":
                    smd.setY(Double.parseDouble(value));
                    break;
                case "dx":
                    smd.setWidth(Double.parseDouble(value));
                    break;
                case "dy":
                    smd.setHeight(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("SMD has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        pkg.getElements().add(smd);
    }

    private static void ingestPadThd(Footprint pkg, Node node) throws EagleCADLibraryFileException {
        PadTHD smd = new PadTHD();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name":
                    smd.setName(value);
                    break;
                case "first":
                    smd.setFirst(value.equals("yes"));
                    break;
                case "stop":
                    smd.setStopmask(value.equals("yes"));
                    break;
                case "thermals":
                    smd.setThermals(value.equals("yes"));
                    break;
// Layer not supported on THD pads.                    
//                case "layer":
//                    attribute.setLayer(Integer.parseInt(value));
//                    break;
                case "shape":
                    smd.setShape(value);
                    break;
                case "rot":
                    // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    smd.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                case "diameter":
                    smd.setDiameter(Double.parseDouble(value));
                    break;
                case "drill":
                    smd.setDrill(Double.parseDouble(value));
                    break;
                case "x":
                    smd.setX(Double.parseDouble(value));
                    break;
                case "y":
                    smd.setY(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Pad has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        pkg.getElements().add(smd);
    }

    private static void ingestText(LibraryElement element, Node node) throws EagleCADLibraryFileException {
        Text text = new Text();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x":
                    text.setX(Double.parseDouble(value));
                    break;
                case "y":
                    text.setY(Double.parseDouble(value));
                    break;
                case "align":
                    text.setAlign(TextAlign.fromCode(value));
                    break;
                case "rot":
                    // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    text.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                case "distance":
                    text.setDistance(Integer.parseInt(value));
                    break;
                case "ratio":
                    text.setWidth(Integer.parseInt(value));
                    break;
                case "size":
                    text.setSize(Double.parseDouble(value));
                    break;
                case "layer":
                    text.setLayer(Integer.parseInt(value));
                    break;
                case "font":
                    // Font is ignored
                    break;
                default:
                    throw new EagleCADLibraryFileException("Text has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        text.setValue(node.getChildNodes().item(0).getNodeValue());

        element.getElements().add(text);
    }

    private static void ingestPolygon(LibraryElement libElement, Node node) throws EagleCADLibraryFileException {
        Polygon poly = new Polygon();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "width":
                    poly.setWidth(Double.parseDouble(value));
                    break;
                case "layer":
                    poly.setLayer(Integer.parseInt(value));
                    break;
                case "spacing":
                    poly.setSpacing(Double.parseDouble(value));
                    break;
                case "pour":
                    poly.setPour(value);
                    break;
                case "isolate":
                    poly.setIsolate(Double.parseDouble(value));
                    break;
                case "orphans":
                    poly.setOrphans(value.equals("yes"));
                    break;
                case "thermals":
                    poly.setThermals(value.equals("yes"));
                    break;
                case "rank":
                    poly.setRank(Integer.parseInt(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Polygon has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("vertex")) {
                continue;
            }
            Vertex v = new Vertex();
            NamedNodeMap att = item.getAttributes();
            for (int j = 0; j < att.getLength(); j++) {
                Node it = att.item(j);
                String value = it.getNodeValue();
                switch (it.getNodeName()) {
                    case "x":
                        v.setX(Double.parseDouble(value));
                        break;
                    case "y":
                        v.setY(Double.parseDouble(value));
                        break;
                    case "curve":
                        v.setCurve(Double.parseDouble(value));
                        break;
                    default:
                        throw new EagleCADLibraryFileException("Polygon has unknown attribute: [" + item.getNodeName() + "]");
                }

            }
            poly.getVertices().add(v);
        }

        libElement.getElements().add(poly);
    }

    private static void ingestRectangle(LibraryElement libElement, Node node) throws EagleCADLibraryFileException {
        Rectangle rect = new Rectangle();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "layer":
                    rect.setLayer(Integer.parseInt(value));
                    break;
                case "x1":
                    rect.setX(Double.parseDouble(value));
                    break;
                case "x2":
                    rect.setX2(Double.parseDouble(value));
                    break;
                case "y1":
                    rect.setY(Double.parseDouble(value));
                    break;
                case "y2":
                    rect.setY2(Double.parseDouble(value));
                    break;
                case "rot":
                    rect.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Rectangle has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        libElement.getElements().add(rect);
    }

    private static void ingestCircle(LibraryElement libElement, Node node) throws EagleCADLibraryFileException {
        Circle circ = new Circle();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "layer":
                    circ.setLayer(Integer.parseInt(value));
                    break;
                case "x":
                    circ.setX(Double.parseDouble(value));
                    break;
                case "y":
                    circ.setY(Double.parseDouble(value));
                    break;
                case "radius":
                    circ.setRadius(Double.parseDouble(value));
                    break;
                case "width":
                    circ.setWidth(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Circle has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        libElement.getElements().add(circ);
    }

    private static void ingestHole(Footprint pkg, Node node) throws EagleCADLibraryFileException {
        Hole hole = new Hole();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x":
                    hole.setX(Double.parseDouble(value));
                    break;
                case "y":
                    hole.setY(Double.parseDouble(value));
                    break;
                case "drill":
                    hole.setDrill(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Hole has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        pkg.getElements().add(hole);
    }

    private static void ingestVia(Footprint pkg, Node node) throws EagleCADLibraryFileException {
        Via via = new Via();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "x":
                    via.setX(Double.parseDouble(value));
                    break;
                case "y":
                    via.setY(Double.parseDouble(value));
                    break;
                case "extent":
                    via.setExtent(Double.parseDouble(value));
                    break;
                case "diameter":
                    via.setDiameter(Double.parseDouble(value));
                    break;
                case "shape":
                    via.setShape(value);
                    break;
                case "stop":
                    via.setStop(value.equals("yes"));
                    break;
                case "drill":
                    via.setDrill(Double.parseDouble(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Via has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        pkg.getElements().add(via);
    }

    private static void ingestPin(Symbol symbol, Node node) throws EagleCADLibraryFileException {
        Pin pin = new Pin();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name":
                    pin.setName(value);
                    break;
                case "x":
                    pin.setX(Double.parseDouble(value));
                    break;
                case "y":
                    pin.setY(Double.parseDouble(value));
                    break;
                case "visible":
                    pin.setVisible(PinVisible.fromCode(value));
                    break;
                case "length":
                    pin.setLength(PinLength.fromCode(value));
                    break;
                case "direction":
                    pin.setDirection(PinDirection.fromCode(value));
                    break;
                case "function":
                    pin.setFunction(PinFunction.fromCode(value));
                    break;
                case "swaplevel":
                    pin.setSwapLevel(Integer.parseInt(value));
                    break;
                case "rot":
                    pin.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Pin has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        symbol.getElements().add(pin);
    }

    private static void ingestGates(DeviceSet deviceSet, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("gate")) {
                continue;
            }

            ingestGate(deviceSet, item);
        }
    }

    private static void ingestDevices(DeviceSet deviceSet, Node node) throws EagleCADLibraryFileException {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!item.getNodeName().equals("device")) {
                continue;
            }

            ingestDevice(deviceSet, item);
        }
    }

    private static void ingestGate(DeviceSet deviceSet, Node node) throws EagleCADLibraryFileException {
        Gate gate = new Gate();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
// 'layer' is not supported in 'device'
//                case "layer":
//                    device.setLayer(Integer.parseInt(value));
//                    break;
                case "name":
                    gate.setName(value);
                    break;
                case "x":
                    gate.setX(Double.parseDouble(value));
                    break;
                case "y":
                    gate.setY(Double.parseDouble(value));
                    break;
                case "symbol":
                    gate.setSymbol(value);
                    break;
                case "addlevel":
                    gate.setAddlevel(value);
                    break;
                case "swaplevel":
                    gate.setSwapLevel(Integer.parseInt(value));
                    break;
                default:
                    throw new EagleCADLibraryFileException("Pin has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        deviceSet.getGates().add(gate);
    }

    private static void ingestDevice(DeviceSet deviceSet, Node node) throws EagleCADLibraryFileException {
        Device device = new Device();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
// 'layer' is not supported in 'device'
//                case "layer":
//                    device.setLayer(Integer.parseInt(value));
//                    break;
                case "name":
                    device.setName(value);
                    break;
                case "package":
                    device.setPackage(value);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Device has unknown attribute: [" + item.getNodeName() + "]");
            }

        }

        NodeList childNodes = node.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node subItem = childNodes.item(j);
            switch (subItem.getNodeName()) {
                case "#text":
                    // Ignore for now.
                    break;
                case "connects":
                    ingestConnections(subItem, device);
                    break;
                case "technologies":
                    ingestTechnologies(subItem, device);
                    break;
                case "package3dinstances":
                    ingestDevicePackagesInstances3d(subItem, device);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Device childNode has unknown attribute: [" + subItem.getNodeName() + "]");
            }
        }

        deviceSet.getDevices().add(device);
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
            if (!item.getNodeName().equals("technology")) {
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
                case "gate":
                    connection.setGate(value);
                    break;
                case "pin":
                    connection.setPin(value);
                    break;
                case "pad":
                    connection.setPad(value);
                    break;
                case "route":
                    connection.setRoute(value);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Connect has unknown attribute: [" + item.getNodeName() + "]");
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
                case "name":
                    technology.setName(value);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Technology has unknown attribute: [" + item.getNodeName() + "]");
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node attributeNode = childNodes.item(j);
                if (!attributeNode.getNodeName().equals("attribute")) {
                    continue;
                }

                ingestAttribute(technology, attributeNode);
            }
        }

        device.getTechnologies().add(technology);
    }

    private static void ingestDevicePackageInstance3d(Device device, Node node) throws EagleCADLibraryFileException {
        DevicePackageInstance3d packageInstance = new DevicePackageInstance3d();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "package3d_urn":
                    packageInstance.setPackage3dUrn(value);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Device PackageInstance3D has unknown attribute: [" + item.getNodeName() + "]");
            }
        }
        device.getPackageInstances().add(packageInstance);
    }

    private static void ingestAttribute(Technology technology, Node node) throws EagleCADLibraryFileException {
        Attribute attribute = new Attribute();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            String value = item.getNodeValue();
            switch (item.getNodeName()) {
                case "name":
                    attribute.setName(value);
                    break;
                case "value":
                    attribute.setValue(value);
                    break;
                case "x":
                    attribute.setX(Double.parseDouble(value));
                    break;
                case "y":
                    attribute.setY(Double.parseDouble(value));
                    break;
                case "rot":
                    // Eagle 'rot' attribute has the letter 'R' prefixing it.
                    attribute.setRotation(Double.parseDouble(value.substring(1)));
                    break;
                case "ratio":
                    attribute.setWidth(Integer.parseInt(value));
                    break;
                case "size":
                    attribute.setSize(Double.parseDouble(value));
                    break;
                case "layer":
                    attribute.setLayer(Integer.parseInt(value));
                    break;
                case "display":
                    attribute.setDisplay(value);
                    break;
                case "constant":
                    attribute.setConstant(value.equals("yes"));
                    break;
                case "font":
                    // Font is ignored
                    break;
                case "align":
                    attribute.setAlign(value);
                    break;
                default:
                    throw new EagleCADLibraryFileException("Attribute has unknown attribute: [" + item.getNodeName() + "]");
            }
        }

        technology.getAttributes().add(attribute);
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
                        packages.getPackageInstances().add(new PackageInstance3d(attr.getNodeValue())
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
     * description tags.  The XML parser tends to DOM it all out, but we
     * need it raw to render in our content areas properly.
     * 
     * Lifted from StackOverflow
     * https://stackoverflow.com/questions/8873393/get-node-raw-text
     * 
     * 
     * @param doc node to transform
     * @return raw HTML content of the doc node.
     */
    public static String serializeDoc(Node doc) {
        StringWriter outText = new StringWriter();
        StreamResult sr = new StreamResult(outText);
        Properties oprops = new Properties();
        oprops.put(OutputKeys.METHOD, "html");
        //oprops.put(OutputKeys.METHOD, "xml");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
            t.setOutputProperties(oprops);
            t.transform(new DOMSource(doc), sr);
        } catch (Exception e) {
            System.out.println(e);
        }
        return outText.toString();
    }
}
