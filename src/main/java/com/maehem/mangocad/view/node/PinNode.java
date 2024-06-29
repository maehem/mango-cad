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
package com.maehem.mangocad.view.node;

import com.maehem.mangocad.model.element.Element;
import com.maehem.mangocad.model.element.ElementListener;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinFunction;
import com.maehem.mangocad.model.element.enums.PinLength;
import static com.maehem.mangocad.model.element.enums.PinVisible.*;
import com.maehem.mangocad.model.element.property.CoordinateProperty;
import com.maehem.mangocad.model.element.property.Rotation;
import com.maehem.mangocad.model.element.property.RotationProperty;
import com.maehem.mangocad.model.element.property.SelectableProperty;
import com.maehem.mangocad.view.PickListener;
import com.maehem.mangocad.view.ViewUtils;
import com.maehem.mangocad.view.library.LibraryElementNode;
import static com.maehem.mangocad.view.library.LibraryElementNode.FONT_PATH;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinNode extends ViewNode implements ElementListener {

    private final double ORIGIN_CIRCLE_RADIUS = 0.635;
    private final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;

    private final double XY_OFFSET = ORIGIN_CIRCLE_RADIUS * 0.71;

    private final double PAD_TEXT_ASCEND = 0.5;

    private final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
    private final double DOT_CIRCLE_RADIUS = 1.0;
    private final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH * 1.7;
    private final double CLK_SIZE = DOT_CIRCLE_RADIUS * 1.7;

    private final double PIN_NAME_MARGIN = CLK_SIZE + 0.6;
    private final double PIN_FONT_SIZE = 2.4;

    private final double PAD_FONT_SIZE = PIN_FONT_SIZE * 0.8;
    private final double DS_FONT_SIZE = PIN_FONT_SIZE * 0.6;

    private final Pin pin;
    private double parentRot;
    private boolean parentMir;
    private boolean showDetails;
    private Color symbolColor; // Pin Wire color
    private Color dirSwapColor; // Pin IO Text color
    private Color pinGhostColor; // if show details but pintext wouldn't be shown
    private Color nameColor; // Pin name text color
    private Color nameGhostColor;
    private final Line pinLine = new Line();
    private final Circle dotC = new Circle(DOT_CIRCLE_RADIUS);
    private final Line clkLine1 = new Line();
    private final Line clkLine2 = new Line();
    private final Text pinName = new Text();
    private final Text padName = new Text();
    private final Text dirSwap = new Text("DIR-SWAP");
    private final Circle originCirc = new Circle(ORIGIN_CIRCLE_RADIUS, Color.TRANSPARENT);

    private final Translate translate = new Translate();
    private final Rotate rotTransform = new Rotate();

    private final Rotate pinNameSpin = new Rotate();
    private final Translate pinNameTranslate = new Translate();

    private final Rotate padNameSpin = new Rotate();
    private final Translate padNameTranslate = new Translate();

    private final Rotate dirSwapSpin = new Rotate();
    private final Translate dirSwapTranslate = new Translate();

    public PinNode(Pin p, Color symbolColor, Color pinColor, Color nameColor, Rotation parentRotation, boolean showDetails, PickListener pickListener) {
        super(p, pickListener);
        this.pin = p;

        setSymbolColor(symbolColor);
        setDirSwapColor(pinColor);
        setNameColor(nameColor);
        setParentRot(parentRotation);

        this.showDetails = showDetails;

        Font font = Font.loadFont(
                PinNode.class.getResourceAsStream(FONT_PATH),
                PIN_FONT_SIZE);
        pinName.setFont(font);
        pinName.setStrokeWidth(PIN_FONT_SIZE * 0.08);
        pinName.setStrokeLineJoin(StrokeLineJoin.ROUND);
        pinNameSpin.setPivotY(-PIN_FONT_SIZE / ViewUtils.FONT_SIZE_MULT / 2);
        pinNameTranslate.setY(PIN_FONT_SIZE / ViewUtils.FONT_SIZE_MULT / 2);

        Font padFont = Font.loadFont(
                PinNode.class.getResourceAsStream(FONT_PATH),
                PAD_FONT_SIZE
        );
        padName.setFont(padFont);
        padName.setStrokeLineJoin(StrokeLineJoin.ROUND);
        padName.setStrokeWidth(PAD_FONT_SIZE * 0.08);
        padNameSpin.setPivotY(-PAD_FONT_SIZE / ViewUtils.FONT_SIZE_MULT / 2);
        padNameTranslate.setY(PAD_TEXT_ASCEND);

        dirSwap.setStrokeWidth(DS_FONT_SIZE * 0.08);
        Font dirSwapFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                DS_FONT_SIZE
        );

        dirSwap.setFont(dirSwapFont);
        dirSwapSpin.setPivotY(0);

        add(pinLine);
        add(dotC);
        add(clkLine1);
        add(clkLine2);
        add(pinName);
        add(padName);
        add(originCirc);
        add(dirSwap);

        for (Shape s : this) {
            s.getTransforms().addAll(translate, rotTransform);
        }
        pinName.getTransforms().addAll(pinNameTranslate, pinNameSpin);
        padName.getTransforms().addAll(padNameTranslate, padNameSpin);
        dirSwap.getTransforms().addAll(dirSwapTranslate, dirSwapSpin);

        pinLine.setStartX(0);
        pinLine.setStartY(0);
        pinLine.setStrokeLineCap(StrokeLineCap.BUTT);
        pinLine.setStrokeWidth(PIN_STROKE_WIDTH);

        dotC.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
        dotC.setStrokeType(StrokeType.INSIDE);
        dotC.setFill(Color.TRANSPARENT);
        dotC.setCenterY(0);

        clkLine1.setStrokeLineCap(StrokeLineCap.ROUND);
        clkLine1.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
        clkLine2.setStrokeLineCap(StrokeLineCap.ROUND);
        clkLine2.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);

        originCirc.setLayoutX(0);
        originCirc.setLayoutY(0);
        originCirc.setStrokeWidth(ORIGIN_CIRCLE_LINE_WIDTH);

        updateLine(); // Line and Dot
        updateClockLines(); // CLK indicator
        updatePadPin(); // Pin Name (inside of symbol) and Pin Number
        updatePinData(); // I/O indicator and Origin indicator
        updatePosition();
        updateRotation();
        updateColors();

//        originCirc.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
//            PickListener listener = getPickListener();
//            LOGGER.log(Level.SEVERE, "Pin Picked..");
//            if (listener != null) {
//                LOGGER.log(Level.SEVERE, "Notify Pin pick listener.");
//                getPickListener().nodePicked(this, me);
//            }
//        });

        Platform.runLater(() -> {
            pin.addListener(this);
        });
    }

    private void updateLine() {
        double rawPinLen = pin.getLength().lenMM();
        double pinLen = rawPinLen - getDotRadius() * 2.0;

        // Pin Wire
        pinLine.setEndX((pinLen));
        pinLine.setEndY(0);

        // Dot Circle - Transparent if not visible.
        dotC.setCenterX(pinLen + getDotRadius());
    }

    private void updateClockLines() {
        if (pin.getFunction() == PinFunction.CLK || pin.getFunction() == PinFunction.DOTCLK) {
            double symbX = getSymbolX();
            clkLine1.setStartX(symbX);
            clkLine1.setStartY(-CLK_SIZE / 2.0);
            clkLine1.setEndX(symbX + CLK_SIZE);
            clkLine1.setEndY(0);

            //clkLine1.setStroke(getActiveColor(symbolColor));

            clkLine2.setStartX(symbX);
            clkLine2.setStartY(CLK_SIZE / 2.0);
            clkLine2.setEndX(symbX + CLK_SIZE);
            clkLine2.setEndY(0);

            //clkLine2.setStroke(getActiveColor(symbolColor));

        }
    }

    private void updatePinData() {
        //double vizPinRot = (pin.getRot() + getParentRot()) % 360;

        // Direction and Swap-Level  ( ex.   io 0  )
        dirSwap.setText(pin.getDirection().code() + "  " + pin.getSwapLevel());

        dirSwapSpin.setPivotX(getDsWidth() / 2.0);

        if (parentMir) { // TODO ???
            //Scale sc = new Scale(-1.0, 1.0, dsWidth / 2.0, 0.0);
            //dirSwap.getTransforms().add(sc);
        }

        pinNameSpin.setPivotX(getPinNameWidth() / 2.0);
    }

    private double getDsWidth() {
        return dirSwap.getBoundsInLocal().getWidth() - 1.0;
    }

    private double getPadWidth() {
        return padName.getBoundsInLocal().getWidth() - 1.0;
    }

    private double getPinNameWidth() {
        return pinName.getBoundsInLocal().getWidth() - 1.0;
    }

    private void updatePadPin() {
        //Color pinNameColor = nameColor;
        //Color padColor = nameColor;
        //double vizPinRot = (pin.getRot() + getParentRot()) % 360;
        //double rawPinLen = pin.getLength().lenMM();

        // Pin Name (inside component, pin function name)
        pinName.setText(pin.getName());
        double pinNameTextWidth = pinName.getBoundsInLocal().getWidth();

        if (parentMir) {
            Scale sc = new Scale(-1.0, 1.0, pinNameTextWidth / 2.0, 0.0);
            pinName.getTransforms().add(sc);
        }

        // Pad Name (outside component, usually a number)
        // Use the padValue from DeviceSet if it exists.
        String padValue;
        if (pin.getPadValue() != null) {
            padValue = pin.getPadValue();
        } else { // Fill padValue with string that matches pinLength
            padValue = "9";
            for (int i = 1; i < pin.getLength().ordinal(); i++) {
                padValue += "9";
            }
        }

        padName.setText(padValue);
        //padName.setFill(getActiveColor(padColor));
        //padName.setStroke(padColor);
//        double padWidth = padName.getBoundsInLocal().getWidth() - PAD_FONT_SIZE * 0.47;

//        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
//            //Rotate r = new Rotate(180, padWidth / 2.0, PAD_TEXT_ASCEND);
//            //padName.getTransforms().add(r);
//        }
//        if (parentMir) {
//            //Scale sc = new Scale(-1.0, 1.0, padWidth / 2.0, 0.0);
//            //padName.getTransforms().add(sc);
//        }
    }

    private void updateRotation() {
        double vizPinRot = (pin.rotation.get() + getParentRot()) % 360;
        rotTransform.setAngle(-pin.rotation.get());

        double rawPinLen = pin.getLength().lenMM();
        pinNameTranslate.setX(rawPinLen + PIN_NAME_MARGIN);
        dirSwapTranslate.setX(-XY_OFFSET - getDsWidth());

        if (vizPinRot == 0 || vizPinRot == 90) {
            pinNameSpin.setAngle(0);

            dirSwapSpin.setAngle(0);
            dirSwapTranslate.setY(-XY_OFFSET);

            padNameSpin.setAngle(0);
            padNameTranslate.setX(0);
            padNameTranslate.setY(-PAD_TEXT_ASCEND);
        } else if (vizPinRot == 180 || vizPinRot == 270) { // Flip Text
            pinNameSpin.setAngle(180);

            dirSwapSpin.setAngle(180);
            dirSwapTranslate.setY(XY_OFFSET);

            padNameSpin.setAngle(180);
            padNameTranslate.setX(getPadWidth());
            padNameTranslate.setY(PAD_TEXT_ASCEND + PAD_FONT_SIZE / ViewUtils.FONT_SIZE_MULT);
        }
    }

    private void updatePosition() {
        translate.setX(pin.getX());
        translate.setY(-pin.getY());
    }

    private void updateColors() {
        Color activeSymbolColor = pin.isSelected() ? symbolColor.brighter().brighter() : symbolColor;

        pinLine.setStroke(activeSymbolColor);
        if (getDotRadius() > 0.0) {
            dotC.setStroke(activeSymbolColor);
        } else {
            dotC.setStroke(Color.TRANSPARENT);
        }
        boolean showClk = pin.getFunction() == PinFunction.CLK || pin.getFunction() == PinFunction.DOTCLK;

        clkLine1.setStroke(showClk ? activeSymbolColor : Color.TRANSPARENT);
        clkLine2.setStroke(showClk ? activeSymbolColor : Color.TRANSPARENT);

        // Direction and Swap level text + origin circle.
        Color activeDirSwapColor = pin.isSelected() ? dirSwapColor.brighter().brighter() : dirSwapColor;
        dirSwap.setStroke(showDetails ? activeDirSwapColor : Color.TRANSPARENT);
        dirSwap.setFill(activeDirSwapColor);

        Color activePinPadColor = pin.isSelected() ? nameColor.brighter().brighter() : nameColor;
        switch (pin.getVisible()) {
            case BOTH -> {
                pinName.setFill(activePinPadColor);
                pinName.setStroke(activePinPadColor);
                padName.setFill(activePinPadColor);
                padName.setStroke(activePinPadColor);
            }
            case PAD -> {
                pinName.setFill(showDetails ? pinGhostColor : Color.TRANSPARENT);
                pinName.setStroke(showDetails ? pinGhostColor : Color.TRANSPARENT);
                padName.setFill(activePinPadColor);
                padName.setStroke(activePinPadColor);
            }
            case PIN -> {
                pinName.setFill(activePinPadColor);
                pinName.setStroke(activePinPadColor);
                padName.setFill(showDetails ? nameGhostColor : Color.TRANSPARENT);
                padName.setStroke(showDetails ? pinGhostColor : Color.TRANSPARENT);
            }
            case OFF -> {
                pinName.setFill(showDetails ? pinGhostColor : Color.TRANSPARENT);
                pinName.setStroke(showDetails ? pinGhostColor : Color.TRANSPARENT);
                padName.setFill(showDetails ? nameGhostColor : Color.TRANSPARENT);
                padName.setStroke(showDetails ? pinGhostColor : Color.TRANSPARENT);
            }
        }
        // Pin Origin Circle
        originCirc.setStroke(showDetails ? activeDirSwapColor : Color.TRANSPARENT);
    }

    private double getSymbolX() {
        return pin.getLength().lenMM(); // Symbol Outline X
    }

    private double getDotRadius() {
        if (pin.getLength() != PinLength.POINT
                && (pin.getFunction() == PinFunction.DOT || pin.getFunction() == PinFunction.DOTCLK)) {
            return DOT_CIRCLE_RADIUS;
        }
        return 0.0;
    }

    /**
     * @return the parentRot
     */
    public double getParentRot() {
        return parentRot;
    }

    /**
     * @param parentRot the parentRot to set
     */
    public final void setParentRot(Rotation parentRotation) {
        this.parentRot = parentRotation == null ? 0.0 : parentRotation.get();
        this.parentMir = parentRotation == null ? false : parentRotation.isMirror();
    }

    /**
     * @param showDetails the showDetails to set
     */
    public final void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    /**
     * @param symbolColor the symbolColor to set
     */
    public final void setSymbolColor(Color symbolColor) {
        this.symbolColor = symbolColor;
        updateColors();
    }

    /**
     * @param dirSwapColor the dirSwapColor to set
     */
    public final void setDirSwapColor(Color dirSwapColor) {
        this.dirSwapColor = dirSwapColor;
        updateColors();
    }

    /**
     * @param nameColor the nameColor to set
     */
    public final void setNameColor(Color nameColor) {
        this.nameColor = nameColor;
        this.nameGhostColor = new Color(nameColor.getRed(), nameColor.getGreen(), nameColor.getBlue(), 0.2);
        this.pinGhostColor = new Color(nameColor.getRed(), nameColor.getGreen(), nameColor.getBlue(), 0.2);

        //pinName.setFill(getActiveColor(nameColor));
        //pinName.setStroke(getActiveColor(nameColor));
        updateColors();
    }

//    private final Color getActiveColor(Color baseColor) {
//        if (pin.isSelected()) {
//            return baseColor.brighter();
//        } else {
//            return baseColor;
//        }
//    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {

//        LOGGER.log(Level.SEVERE,
//                "Pin properties have changed! {0}: {1} => {2}",
//                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (field) {
            case CoordinateProperty.Field.X, CoordinateProperty.Field.Y -> {
                updatePosition();
            }
            case SelectableProperty.Field.SELECTED -> {
                updateColors();
            }
            case Pin.Field.NAME, Pin.Field.PAD_VALUE -> {
                updatePadPin();
            }
            case Pin.Field.DIRECTION, Pin.Field.SWAPLEVEL -> {
                updatePinData();
            }
            case Pin.Field.VISIBLE -> {
                updateColors();
            }
            case Pin.Field.FUNCTION -> {
                updateClockLines();
                updateLine();
                updateColors();
            }
            case Pin.Field.LENGTH -> {
                updateRotation();
                updateLine();
                updateClockLines();
                updatePadPin();
                updateColors();
            }
            case RotationProperty.Field.ALL -> {
                LOGGER.log(Level.SEVERE,
                        "Pin properties have changed! {0}: {1} => {2}",
                        new Object[]{field, oldVal.toString(), newVal.toString()});
                // Adjust Rotation Transform
                updateRotation();
            }
            default -> {
            }
        }

    }

    @Override
    public String toString() {
        return "PinNode: " + pin.getName()
                + " pad:" + pin.getPadValue()
                + " dir:" + pin.getDirection().name()
                + " func:" + pin.getFunction().name()
                + " len:" + pin.getLength().name();
    }

}
