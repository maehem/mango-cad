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
package com.maehem.mangocad.model.element.basic;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LayerNumberValue;
import com.maehem.mangocad.model.element.property.LockProperty;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.Rotation;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class RectangleElement extends Element implements
        LayerNumberProperty, SelectableProperty, RotationProperty,
        LockProperty,
        GrouprefsProperty, ElementValueListener {

    public static final String ELEMENT_NAME = "rectangle";

    public enum Field {
        X1("x1", Double.class), Y1("y1", Double.class),
        X2("x2", Double.class), Y2("y2", Double.class),
        ALL_XY("allXY", null);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        public String fName() {
            return fName;
        }

        public Class clazz() {
            return clazz;
        }
    }

    //private int layer;
    public final LayerNumberValue layerValue = new LayerNumberValue(1);
    public final CoordinateValue coord1 = new CoordinateValue();
    public final CoordinateValue coord2 = new CoordinateValue();
    public final LockValue lockProperty = new LockValue();
    public final Rotation rotationProperty = new Rotation(Rotation.CONSTRAINED);
    private final ArrayList<String> grouprefs = new ArrayList<>();

    private boolean selected = false;
    private int selectedCorner = 0;
    private RectangleElement snapshot = null;

    public RectangleElement() {
        layerValue.addListener(this);
        rotationProperty.setAllowSpin(false);
        rotationProperty.setAllowMirror(true);
        rotationProperty.setConstrained(true);

        coord1.addListener(this);
        coord2.addListener(this);
        rotationProperty.addListener(this);
        lockProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public LayerNumberValue getLayerNumberProperty() {
        return layerValue;
    }

    @Override
    public LockValue getLockProperty() {
        return lockProperty;
    }

    @Override
    public Rotation getRotationProperty() {
        return rotationProperty;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return coord1.x.get();
    }

    /**
     * @param val the x1 to set
     */
    public void setX1(double val) {
        coord1.x.set(val);
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return coord1.y.get();
    }

    /**
     * @param val the y1 to set
     */
    public void setY1(double val) {
        coord1.y.set(val);
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return coord2.x.get();
    }

    /**
     * @param val the x2 to set
     */
    public void setX2(double val) {
        coord2.x.set(val);
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return coord2.y.get();
    }

    /**
     * @param val the y2 to set
     */
    public void setY2(double val) {
        coord2.y.set(val);
    }

    /**
     * @return the rotationProperty
     */
    public double getRot() {
        return rotationProperty.get();
    }

    public void setAllXY(double x1, double y1, double x2, double y2) {
        coord1.x.set(x1);
        coord1.y.set(y1);
        coord2.x.set(x2);
        coord2.y.set(y2);
    }

    /**
     * Furnish a list of the polygon points with consideration for the current
     * rotationProperty/mirror of the shape.
     *
     * @return
     */
    public double[] getPoints() {
        double points[] = {0, 0, 0, 0, 0, 0, 0, 0};
        double w = Math.abs(getX1() - getX2());
        double h = Math.abs(getY1() - getY2());

        switch ((int) getRot()) {
            case 90 -> {
                points[0] = getX1() + w / 2 + h / 2;
                points[1] = getY1() + h / 2 - w / 2;

                points[2] = getX2() - w / 2 + h / 2;
                points[3] = getY1() + h / 2 + w / 2;

                points[4] = getX2() - w / 2 - h / 2;
                points[5] = getY2() - h / 2 + w / 2;

                points[6] = getX1() + w / 2 - h / 2;
                points[7] = getY2() - h / 2 - w / 2;
            }
            case 180 -> {
                points[0] = getX2();
                points[1] = getY2();

                points[2] = getX1();
                points[3] = getY2();

                points[4] = getX1();
                points[5] = getY1();

                points[6] = getX2();
                points[7] = getY1();
            }
            case 270 -> {
                points[0] = getX1() + w / 2 - h / 2;
                points[1] = getY1() + h / 2 + w / 2;

                points[2] = getX2() - w / 2 - h / 2;
                points[3] = getY1() + h / 2 - w / 2;

                points[4] = getX2() - w / 2 + h / 2;
                points[5] = getY2() - h / 2 - w / 2;

                points[6] = getX1() + w / 2 + h / 2;
                points[7] = getY2() - h / 2 + w / 2;
            }
            default -> { // zero
                points[0] = getX1();
                points[1] = getY1();

                points[2] = getX2();
                points[3] = getY1();

                points[4] = getX2();
                points[5] = getY2();

                points[6] = getX1();
                points[7] = getY2();
            }
        }
        LOGGER.log(Level.FINER, "Rectangle Points[{8}]: {0},{1}  {2},{3}  {4},{5}  {6},{7}",
                new Object[]{
                    points[0], points[1],
                    points[2], points[3],
                    points[4], points[5],
                    points[6], points[7],
                    getLayerNum()
                }
        );
        return points;
    }

    public int getSelectedCorner() {
        return selectedCorner;
    }

    public void setSelectedCorner(int corner) {
        if (corner < 0 || corner > 3) {
            selectedCorner = 0;
        } else {
            selectedCorner = corner;
        }
    }

    /**
     * @param value the rotationProperty to set
     */
    public void setRot(double value) {
//        if (getRot() != value) {
//            double oldValue = getRot();
        this.rotationProperty.set(value);
        //notifyListeners(RotationProperty.Field.VALUE, oldValue, getRot());
//        }
    }

    @Override
    public void createSnapshot() {
        snapshot = copy();
    }

    @Override
    public void restoreSnapshot() {
        setAllXY(snapshot.getX1(), snapshot.getY1(), snapshot.getX2(), snapshot.getY2());
        setLayerNum(snapshot.getLayerNum());
        setRot(snapshot.getRot());

        snapshot = null;
    }

    @Override
    public RectangleElement getSnapshot() {
        return snapshot;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            boolean oldValue = this.selected;
            this.selected = selected;
            notifyListeners(SelectableProperty.Field.SELECTED, oldValue, this.selected);
        }
    }

    public RectangleElement copy() {
        RectangleElement rectCopy = new RectangleElement();

        rectCopy.setX1(getX1());
        rectCopy.setY1(getY1());
        rectCopy.setX2(getX2());
        rectCopy.setY2(getY2());
        rectCopy.setLayerNum(getLayerNum());
        rectCopy.setRot(getRot());

        return rectCopy;
    }

    @Override
    public int getLayerNum() {
        return layerValue.get();
    }

    @Override
    public void setLayerNum(int layer) {
        layerValue.set(layer);
//        if (this.layer != layer) {
//            int oldVal = this.layer;
//            this.layer = layer;
//            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
//        }
    }

    @Override
    public ArrayList<String> getGrouprefs() {
        return grouprefs;
    }

//    @Override
//    public boolean isSpun() {
//        return false;
//    }
//
//    @Override
//    public void setSpin(boolean value) {
//    }
//
//    @Override
//    public boolean isSpin() {
//        return false;
//    }
//
//    @Override
//    public boolean isSpinAllowed() {
//        return false;
//    }
//
//    @Override
//    public void setAllowSpin(boolean value) {
//
//    }
//
//    @Override
//    public void setMirror(boolean value) {
//        rotationProperty.setMirror(value);
//    }
//
//    @Override
//    public boolean isMirrored() {
//        return rotationProperty.isMirror();
//    }
//    @Override
//    public boolean isMirrorAllowed() {
//        return rotationProperty.isMirrorAllowed();
//    }
//
//    @Override
//    public void setAllowMirror(boolean value) { // Not changable
//    }
//
//    @Override
//    public boolean isConstrained() {
//        return true;
//    }
//
//    @Override
//    public void setConstrained(boolean value) { // Not changable
//
//    }
    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(layerValue)) {
            notifyListeners(LayerNumberProperty.Field.LAYER, layerValue.getOldValue(), layerValue.get());
        } else if (newVal.equals(coord1.x)) { // TODO Coordinate needs to pass the changed value.
            notifyListeners(Field.X1, coord1.x.getOldValue(), coord1.x.get());
        } else if (newVal.equals(coord1.y)) {
            notifyListeners(Field.Y1, coord1.y.getOldValue(), coord1.y.get());
        } else if (newVal.equals(coord2.x)) {
            notifyListeners(Field.X2, coord2.x.getOldValue(), coord2.x.get());
        } else if (newVal.equals(coord2.y)) {
            notifyListeners(Field.Y2, coord2.y.getOldValue(), coord2.y.get());
        } else if (newVal.equals(rotationProperty)) {
            notifyListeners(RotationProperty.Field.VALUE, rotationProperty.getOldValue(), rotationProperty.get());
        } else if (newVal.equals(lockProperty)) {
            notifyListeners(LockProperty.Field.LOCKED, lockProperty.getOldValue(), lockProperty.isLocked());
        }
    }

    /**
     * <pre>
     * <rectangle x1="15.24" y1="10.16" x2="25.4" y2="17.78" locked="yes" layer="94" rot="R90"/>
     * </pre> @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<rectangle{0}{1}{2}{3}{4}{5}{6}/>");
        String rotValue = rotationProperty.xmlValue();

        Object[] args = {
            " x1=\"" + coord1.x.getPrecise(6) + "\"", // 0
            " y1=\"" + coord1.y.getPrecise(6) + "\"", // 1
            " x2=\"" + coord2.x.getPrecise(6) + "\"", // 2
            " y2=\"" + coord2.y.getPrecise(6) + "\"", // 3
            lockProperty.xmlValue(), // 4
            " layer=\"" + getLayerNum() + "\"", // 5
            !rotValue.equals("R0") ? " rot=\"" + rotValue + "\"" : "" // 6
        };

        return mf.format(args);
    }
}
