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
import com.maehem.mangocad.model.element.ElementField;
import com.maehem.mangocad.model.element.ElementValueListener;
import com.maehem.mangocad.model.element.enums.PinDirection;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import com.maehem.mangocad.model.element.enums.PinVisible;
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.CoordinateValue;
import com.maehem.mangocad.model.element.property.ElementValue;
import com.maehem.mangocad.model.element.property.Rotation;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.model.element.property.StringValue;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * pin EMPTY
 *    ATTR
 *      name          %String;       #REQUIRED
 *      x             %Coord;        #REQUIRED
 *      y             %Coord;        #REQUIRED
 *      visible       %PinVisible;   "both"
 *      length        %PinLength;    "long"
 *      direction     %PinDirection; "io"
 *      function      %PinFunction;  "none"
 *      swaplevel     %Int;          "0"
 *      rot           %Rotation;     "R0"
 * </pre>
 *
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class Pin extends Element implements
        CoordinateProperty, RotationProperty, SelectableProperty,
        ElementValueListener {

    public enum Field implements ElementField {
        NAME("name", String.class),
        VISIBLE("visible", PinVisible.class),
        LENGTH("length", PinLength.class),
        DIRECTION("direction", PinDirection.class),
        FUNCTION("function", PinFunction.class),
        SWAPLEVEL("swapLevel", Integer.class),
        PAD_VALUE("padValue", String.class);

        private final String fName;
        private final Class clazz;

        private Field(String name, Class clazz) {
            this.fName = name;
            this.clazz = clazz;
        }

        @Override
        public String fName() {
            return fName;
        }

        @Override
        public Class clazz() {
            return clazz;
        }

    }

    public static final String ELEMENT_NAME = "pin";
    // 'layer' is not used.

    public static final String SWAPLEVEL_KEY = "swaplevel"; // TODO Use PinField enum.
    public static final List<String> BASIC_ROTATIONS = Arrays.asList("R0", "R90", "R180", "R270"); // TODO: Make a fixed-ROT enum

    public final StringValue nameProperty = new StringValue("A");
    public final CoordinateValue coordinate = new CoordinateValue();
    public final Rotation rotation = new Rotation(Rotation.CONSTRAINED);

    private boolean selected = false;
    private boolean picked = false;
    private Pin snapshot2 = null;

    private PinVisible visible = PinVisible.BOTH;
    private PinLength length = PinLength.MIDDLE;
    private PinDirection direction = PinDirection.IO;
    private PinFunction function = PinFunction.NONE;
    private int swapLevel = 0;

    // Lookup
    private String padValue = null;

    public Pin() {
        //rotation.setConstrained(true);

        nameProperty.addListener(this);
        coordinate.addListener(this);
        rotation.addListener(this);
    }

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    public CoordinateValue getCoordinateProperty() {
        return coordinate;
    }

    @Override
    public Rotation getRotationProperty() {
        return rotation;
    }

    /**
     * @return the name
     */
    public String getName() {
        return nameProperty.get();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
//        if (this.nameProperty.get() == null || !this.nameProperty.get().equals(name)) {
//            String oldName = this.nameProperty.get();
        this.nameProperty.set(name);
//            notifyListeners(PinField.NAME, oldName, name);
//        }
    }

    /**
     * @return the x
     */
    public double getX() {
        return coordinate.x.get();
    }

    /**
     * @param val the x to set
     */
    public void setX(double val) {
        coordinate.x.set(val);
    }

    /**
     * @return the y
     */
    public double getY() {
        return coordinate.y.get();
    }

    /**
     * @param val the y to set
     */
    public void setY(double val) {
        coordinate.y.set(val);
    }

    /**
     * Pad Value, usually pin number assigned in DeviceSet Might be null when
     * viewed as symbol.
     *
     * @return
     */
    public String getPadValue() {
        return padValue;
    }

    public void setPadValue(String value) {
        if (value == null || (this.padValue != null && !this.padValue.equals(value))) {
            String oldVal = this.padValue;
            this.padValue = value;
            notifyListeners(Field.PAD_VALUE, oldVal, padValue);
        }
    }

    /**
     *
     * @return code for visible
     */
    public PinVisible getVisible() {
        return visible;
    }

    /**
     * Set pin/pad visibility
     *
     * @param visible
     */
    public void setVisible(PinVisible visible) {
        if (this.visible != visible) {
            PinVisible oldVisible = this.visible;
            this.visible = visible;
            notifyListeners(Field.VISIBLE, oldVisible, this.visible);
        }
    }

    /**
     * @return the length
     */
    public PinLength getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(PinLength length) {
        if (this.length != length) {
            PinLength oldVal = this.length;
            this.length = length;
            notifyListeners(Field.LENGTH, oldVal, this.length);
        }
    }

    /**
     * @return the direction
     */
    public PinDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(PinDirection direction) {
        if (this.direction != direction) {
            PinDirection oldVal = this.direction;
            this.direction = direction;
            notifyListeners(Field.DIRECTION, oldVal, this.direction);
        }
    }

    /**
     * @return the function
     */
    public PinFunction getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(PinFunction function) {
        if (this.function != function) {
            PinFunction oldVal = this.function;
            this.function = function;
            notifyListeners(Field.FUNCTION, oldVal, this.function);
        }
    }

    /**
     * @return the swapLevel
     */
    public int getSwapLevel() {
        return swapLevel;
    }

    /**
     * @param swapLevel the swapLevel to set
     */
    public void setSwapLevel(int swapLevel) {
        if (this.swapLevel != swapLevel) {
            int oldVal = this.swapLevel;
            this.swapLevel = swapLevel;
            notifyListeners(Field.SWAPLEVEL, oldVal, this.swapLevel);
        }
    }

    @Override
    public void createSnapshot() {
        snapshot2 = copy();
    }

    @Override
    public void restoreSnapshot() {
        if (snapshot2 != null) {
            Rotation snapRotation = snapshot2.rotation;
            setX(snapshot2.getX());
            setY(snapshot2.getY());
            //rotation.setAllowMirror(snapRotation.isMirrorAllowed());
            rotation.setMirrorStyle(snapRotation.getMirrorStyle());
            rotation.setConstrained(snapRotation.isConstrained());
            setDirection(snapshot2.getDirection());
            setFunction(snapshot2.getFunction());
            setLength(snapshot2.getLength());
            setName(snapshot2.getName());
            setPadValue(snapshot2.getPadValue());
            setSwapLevel(snapshot2.getSwapLevel());

            snapshot2 = null;
        }
    }

    @Override
    public Element getSnapshot() {
        if (snapshot2 == null) {
            createSnapshot();
        }
        return snapshot2;
    }

    /**
     * A copy of most settings. Maybe used by the "Copy" tool? Used by internal
     * snapshot but can be used by other tools if needed.
     *
     * @return
     */
    public Pin copy() {
        Pin copyPin = new Pin();
        copyPin.setX(getX());
        copyPin.setY(getY());
        //copyPin.rotation.setAllowMirror(rotation.isMirrorAllowed());
        copyPin.rotation.setMirrorStyle(rotation.getMirrorStyle());
        copyPin.rotation.setConstrained(rotation.isConstrained());
        copyPin.setDirection(getDirection());
        copyPin.setFunction(getFunction());
        copyPin.setLength(getLength());
        copyPin.setName(getName());
        copyPin.setPadValue(getPadValue());
        copyPin.setSwapLevel(getSwapLevel());

        return copyPin;
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

    @Override
    public boolean isPicked() {
        return picked;
    }

    @Override
    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    @Override
    public void modify(double xDist, double yDist, boolean ephemeral) {
        //LOGGER.log(Level.SEVERE, "Move elementXY.");
        if (getSnapshot() instanceof CoordinateProperty snapXY) {
            //LOGGER.log(Level.SEVERE, "    Move relative to snapXY.");
            getCoordinateProperty().setX(snapXY.getCoordinateProperty().getX() + xDist);
            getCoordinateProperty().setY(snapXY.getCoordinateProperty().getY() + yDist);
        }
    }

    @Override
    public void elementValueChanged(ElementValue newVal) {
        if (newVal.equals(nameProperty)) {
            notifyListeners(Field.NAME, nameProperty.getOldValue(), nameProperty.get());
        } else if (newVal.equals(coordinate.x)) {
            notifyListeners(CoordinateProperty.Field.X, coordinate.x.getOldValue(), newVal);
        } else if (newVal.equals(coordinate.y)) {
            notifyListeners(CoordinateProperty.Field.Y, coordinate.y.getOldValue(), newVal);
        } else if (newVal.equals(rotation)) {
            notifyListeners(RotationProperty.Field.ALL, rotation, rotation);
        }
    }

    /**
     * <pre>
     * <pin name="B1" x="12.7" y="12.7" length="middle" rot="R180"/>
     * <pin name="A8" x="-12.7" y="-5.08" length="middle"/>
     * <pin name="CLK" x="-10.16" y="2.54" length="short" function="clk"/>
     * <pin name="VDD" x="-15.24" y="20.32" visible="pad" length="short" direction="pwr" rot="R270"/>
     * <pin name="!CLK" x="-7.62" y="5.08" visible="pad" length="short" direction="in" function="dotclk"/>
     * <pin name="A" x="-5.08" y="2.54" visible="pad" length="short" direction="in" swaplevel="1"/>
     * <pin name="FOOBAR" x="-15.24" y="12.7" visible="off" length="short" direction="pwr" function="dotclk" swaplevel="3" rot="R90"/>
     *
     * </pre>
     *
     * @return
     */
    @Override
    public String toXML() {
        MessageFormat mf = new MessageFormat("<pin name=\"{0}\"{1}{2}{3}{4}{5}{6}{7}{8}/>");

        String rotValue = rotation.xmlValue();

        Object[] args = {
            nameProperty.get(), // 0
            " x=\"" + coordinate.x.getPrecise(6) + "\"", // 1
            " y=\"" + coordinate.y.getPrecise(6) + "\"", // 2
            !visible.equals(PinVisible.BOTH) ? " visible=\"" + visible.code() + "\"" : "", // 3
            !getLength().equals(PinLength.LONG) ? " length=\"" + length.code() + "\"" : "", // 4
            !getDirection().equals(PinDirection.IO) ? " direction=\"" + direction.code() + "\"" : "", // 5
            !getFunction().equals(PinFunction.NONE) ? " function=\"" + function.code() + "\"" : "", // 6
            getSwapLevel() > 0 ? " swaplevel=\"" + getSwapLevel() + "\"" : "", // 7
            !rotValue.equals("R0") ? " rot=\"" + rotValue + "\"" : "" // 8
        };

        return mf.format(args);
    }

}
