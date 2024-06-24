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
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.GrouprefsProperty;
import com.maehem.mangocad.model.element.property.LayerNumberProperty;
import com.maehem.mangocad.model.element.property.LockProperty;
import com.maehem.mangocad.model.element.property.LockValue;
import com.maehem.mangocad.model.element.property.RealValue;
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
        //SELECTED("selected", Boolean.class);

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

    private int layer;
    public final RealValue x1Property = new RealValue(0);
    public final RealValue y1Property = new RealValue(0);
    public final RealValue x2Property = new RealValue(0);
    public final RealValue y2Property = new RealValue(0);
    public final LockValue lockProperty = new LockValue();
    public final Rotation rotationProperty = new Rotation(Rotation.CONSTRAINED);
    private final ArrayList<String> grouprefs = new ArrayList<>();

    private boolean selected = false;
    private int selectedCorner = 0;
    private RectangleElement snapshot = null;

    public RectangleElement() {
        rotationProperty.setAllowSpin(false);
        rotationProperty.setAllowMirror(true);
        rotationProperty.setConstrained(true);

        x1Property.addListener(this);
        y1Property.addListener(this);
        x2Property.addListener(this);
        y2Property.addListener(this);
        rotationProperty.addListener(this);
        lockProperty.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
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
        return x1Property.get();
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double x1) {
//        if (getX1() != x1) {
//            double oldVal = getX1();
        x1Property.set(x1);
//            notifyListeners(RectangleElement.Field.X1, oldVal, getX1());
//        }
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1Property.get();
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double y1) {
//        if (getY1() != y1) {
//            double oldVal = getY1();
        y1Property.set(y1);
//            notifyListeners(RectangleElement.Field.Y1, oldVal, getY1());
//        }
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return x2Property.get();
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double x2) {
//        if (getX2() != x2) {
//            double oldVal = getX2();
        x2Property.set(x2);
//            notifyListeners(RectangleElement.Field.X2, oldVal, getX2());
//        }
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return y2Property.get();
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double y2) {
//        if (getY2() != y2) {
//            double oldVal = getY2();
            y2Property.set(y2);
//            notifyListeners(RectangleElement.Field.Y2, oldVal, getY2());
//        }
    }

    /**
     * @return the rotationProperty
     */
    public double getRot() {
        return rotationProperty.get();
    }

    public void setAllXY(double x1, double y1, double x2, double y2) {
        x1Property.set(x1);
        y1Property.set(y1);
        x2Property.set(x2);
        y2Property.set(y2);

        //notifyListeners(RectangleElement.Field.ALL_XY, null, null);
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
        if (getRot() != value) {
            double oldValue = getRot();
            this.rotationProperty.set(value);
            //notifyListeners(RotationProperty.Field.VALUE, oldValue, getRot());
        }
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
        return layer;
    }

    @Override
    public void setLayerNum(int layer) {
        if (this.layer != layer) {
            int oldVal = this.layer;
            this.layer = layer;
            notifyListeners(LayerNumberProperty.Field.LAYER, oldVal, this.layer);
        }
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
        if (newVal.equals(x1Property)) {
            notifyListeners(Field.X1, x1Property.getOldValue(), x1Property.get());
        } else if (newVal.equals(y1Property)) {
            notifyListeners(Field.Y1, y1Property.getOldValue(), y1Property.get());
        } else if (newVal.equals(x2Property)) {
            notifyListeners(Field.X2, x2Property.getOldValue(), x2Property.get());
        } else if (newVal.equals(y2Property)) {
            notifyListeners(Field.Y2, y2Property.getOldValue(), y2Property.get());
        } else if (newVal.equals(rotationProperty)) {
            notifyListeners(Rotation.Field.VALUE, rotationProperty.getOldValue(), rotationProperty.get());
        } else if (newVal.equals(lockProperty)) {
            notifyListeners(LockValue.Field.LOCKED, lockProperty.getOldValue(), lockProperty.isLocked());
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
            " x1=\"" + x1Property.getPrecise(6) + "\"", // 0
            " y1=\"" + y1Property.getPrecise(6) + "\"", // 1
            " x2=\"" + x2Property.getPrecise(6) + "\"", // 2
            " y2=\"" + y2Property.getPrecise(6) + "\"", // 3
            lockProperty.xmlValue(), // 4
            " layer=\"" + getLayerNum() + "\"", // 5
            !rotValue.equals("R0") ? " rot=\"" + rotValue + "\"" : "" // 6
        };

        return mf.format(args);
    }
}
