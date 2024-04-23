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
package com.maehem.mangocad.view.library.symbol.node;

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.element.basic.Pin;
import com.maehem.mangocad.model.element.enums.PinField;
import com.maehem.mangocad.model.element.enums.PinFunction;
import static com.maehem.mangocad.model.element.enums.PinVisible.*;
import com.maehem.mangocad.model.util.Rotation;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.library.LibraryElementNode;
import static com.maehem.mangocad.view.library.LibraryElementNode.FONT_PATH;
import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class PinNode extends Group implements ElementListener {

    //private final Color ORIGIN_CIRCLE_COLOR = new Color(1.0, 1.0, 1.0, 0.2);
    private final double ORIGIN_CIRCLE_RADIUS = 0.635;
    private final double ORIGIN_CIRCLE_LINE_WIDTH = 0.07;

    //private final Color PAD_NAME_COLOR = new Color(0.8, 0.8, 0.2, 0.8);
    //private final Color PAD_COLOR_GHOST = new Color(0.8, 0.8, 0.2, 0.2);
    private final double PAD_TEXT_ASCEND = 0.4;

    private final double PIN_NAME_MARGIN = 1.8;
    private final double PIN_STROKE_WIDTH = 0.1524; // 6 mil
    private final double PIN_FONT_SIZE = 2.4;
    //private final Color PIN_NAME_COLOR = new Color(0.8, 0.8, 0.8, 0.8);
    //private final Color PIN_COLOR_GHOST = new Color(0.9, 0.9, 0.9, 0.1);
    //private final Color PIN_DIR_SWAP_COLOR = new Color(0.3, 1.0, 0.3, 0.2);

    private final double DOT_CIRCLE_RADIUS = 0.7;
    private final double DOT_CIRCLE_LINE_WIDTH = PIN_STROKE_WIDTH * 1.7;
    private final double CLK_SIZE = DOT_CIRCLE_RADIUS * 2.0;

    private final double PAD_FONT_SIZE = PIN_FONT_SIZE * 0.8;
    private final double DS_FONT_SIZE = PIN_FONT_SIZE * 0.6;

    private final Pin pin;
    private double parentRot;
    private boolean parentMir;
    private boolean showDetails;
    private Color symbolColor; // Pin Wire color
    private Color pinColor; // Pin IO Text color
    private Color pinGhostColor; // if show details but pintext wouldn't be shown
    private Color nameColor; // Pin name text color
    private Color nameGhostColor;
    private final Line pinLine = new Line();
    private final Circle dotC = new Circle(DOT_CIRCLE_RADIUS);
    private final Line clkLine1 = new Line();
    private final Line clkLine2 = new Line();
    private final Text pinName = new Text();
    private final Text padName = new Text();
    private final Text dirSwap = new Text();
    private final Circle originCirc = new Circle(ORIGIN_CIRCLE_RADIUS, Color.TRANSPARENT);

    private final Translate translate = new Translate();
    private final Rotate rotTransform = new Rotate();


    public PinNode(Pin p, Color symbolColor, Color pinColor, Color nameColor, Rotation parentRotation, boolean showDetails) {
        this.pin = p;
        setSymbolColor(symbolColor);
        setPinColor(pinColor);
        setNameColor(nameColor);
        setParentRot(parentRotation);

        this.showDetails = showDetails;

        Font font = Font.loadFont(
                PinNode.class.getResourceAsStream(FONT_PATH),
                PIN_FONT_SIZE);
        pinName.setFont(font);
        pinName.setStrokeWidth(PIN_FONT_SIZE * 0.08);
        pinName.setStrokeLineJoin(StrokeLineJoin.ROUND);

        Font padFont = Font.loadFont(
                PinNode.class.getResourceAsStream(FONT_PATH),
                PAD_FONT_SIZE
        );
        padName.setFont(padFont);
        padName.setStrokeLineJoin(StrokeLineJoin.ROUND);
        padName.setStrokeWidth(PAD_FONT_SIZE * 0.08);


        dirSwap.setStrokeWidth(DS_FONT_SIZE * 0.08);
        Font dirSwapFont = Font.loadFont(
                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
                DS_FONT_SIZE
        );
        dirSwap.setFont(dirSwapFont);

        getChildren().addAll(pinLine,
                dotC, clkLine1, clkLine2,
                pinName, padName,
                originCirc, dirSwap
        );

        getTransforms().addAll(rotTransform, translate);

        pinLine.setStartX(0);
        pinLine.setStartY(0);
        pinLine.setStrokeLineCap(StrokeLineCap.BUTT);
        pinLine.setStrokeWidth(PIN_STROKE_WIDTH);

        dotC.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
        dotC.setFill(Color.TRANSPARENT);
        dotC.setLayoutY(0);
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

        p.addListener(this);
}

    private void updateLine() {
        double rawPinLen = pin.getLength().lenMM();
        double pinLen = rawPinLen - getDotRadius() * 2.0;

        //LOGGER.log(Level.SEVERE, "PinLen: raw:{0}   act:{1}    dotRad:{2}", new Object[]{rawPinLen, pinLen, getDotRadius()});
        // Pin Wire
        pinLine.setEndX((pin.isMirror() ? -pinLen : pinLen));
        pinLine.setEndY(0);
        pinLine.setStroke(symbolColor);

        // Dot Circle - Transparent if not visible.
        //double symbX = pin.getX() + (pin.isMirror() ? -rawPinLen : rawPinLen); // Symbol Outline X
        dotC.setLayoutX(getSymbolX() + (pin.isMirror() ? getDotRadius() : -getDotRadius()));
        if (getDotRadius() > 0.0) {
            dotC.setStroke(symbolColor);
        } else {
            dotC.setStroke(Color.TRANSPARENT);
        }

    }

    private void updateClockLines() {
        if (pin.getFunction() == PinFunction.CLK || pin.getFunction() == PinFunction.DOTCLK) {
            double symbX = getSymbolX();
            clkLine1.setStartX(symbX);
            clkLine1.setStartY(-CLK_SIZE / 2.0);
            clkLine1.setEndX(symbX + (pin.isMirror() ? -CLK_SIZE : CLK_SIZE));
            clkLine1.setEndY(0);

            clkLine1.setStroke(symbolColor);

            clkLine2.setStartX(symbX);
            clkLine2.setStartY(CLK_SIZE / 2.0);
            clkLine2.setEndX(symbX + (pin.isMirror() ? -CLK_SIZE : CLK_SIZE));
            clkLine2.setEndY(0);

            clkLine2.setStroke(symbolColor);

        } else {
            clkLine1.setStroke(Color.TRANSPARENT);
            clkLine2.setStroke(Color.TRANSPARENT);
        }
    }

    private void updatePinData() {
        double vizPinRot = (pin.getRot() + getParentRot()) % 360;

        // Direction and Swap-Level  ( ex.   io 0  )
        dirSwap.setText(pin.getDirection().code() + "  " + pin.getSwapLevel());
        dirSwap.setStroke(showDetails ? pinColor : Color.TRANSPARENT);
        dirSwap.setFill(pinColor);
        double dsWidth = dirSwap.getBoundsInLocal().getWidth() - DS_FONT_SIZE * 0.4;
        //double dsHeight = dirSwap.getBoundsInLocal().getHeight();
        double xyOffset = ORIGIN_CIRCLE_RADIUS * 0.71;
        dirSwap.setLayoutX(-(pin.isMirror() ? 0.0 : dsWidth) + (pin.isMirror() ? xyOffset : -xyOffset));
        double dsX = (pin.isMirror() ? xyOffset : (-xyOffset - dsWidth));
        dirSwap.setLayoutX(dsX);
        dirSwap.setLayoutY(-xyOffset);
        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
            //if (vizPinRot > 90.0) { // Flip Text
            Rotate r = new Rotate(180, dsWidth / 2.0, xyOffset);
            dirSwap.getTransforms().add(r);
        }
        if (pin.isMirror() ^ parentMir) {
            Scale sc = new Scale(-1.0, 1.0, dsWidth / 2.0, 0.0);
            dirSwap.getTransforms().add(sc);
        }

        // Pin Origin Circle
        originCirc.setStroke(showDetails ? pinColor : Color.TRANSPARENT);
    }

    private void updatePadPinVisible() {
        switch (pin.getVisible()) {
            case BOTH -> {
                pinName.setFill(nameColor);
                padName.setFill(pinColor);
            }
            case PAD -> {
                pinName.setFill(showDetails ? pinGhostColor : Color.TRANSPARENT);
                padName.setFill(pinColor);
            }
            case PIN -> {
                pinName.setFill(nameColor);
                padName.setFill(showDetails ? nameGhostColor : Color.TRANSPARENT);
            }
            case OFF -> {
                pinName.setFill(showDetails ? pinGhostColor : Color.TRANSPARENT);
                padName.setFill(showDetails ? nameGhostColor : Color.TRANSPARENT);
            }
        }
    }

    private void updatePadPin() {
        Color pinNameColor = nameColor;
        Color padColor = nameColor;
        double vizPinRot = (pin.getRot() + getParentRot()) % 360;

        updatePadPinVisible();

        // Pin Name (inside component, pin function name)
        pinName.setText(pin.getName());
        pinName.setFill(pinNameColor);
        pinName.setStroke(pinNameColor);
        double pinNameTextWidth = pinName.getBoundsInLocal().getWidth();
        double pinNameTextHeight = pinName.getBoundsInLocal().getHeight();

        // TODO: Re-test this.
        //pinName.setLayoutX(getSymbolX() + (pin.isMirror() ? -PIN_NAME_MARGIN : PIN_NAME_MARGIN) + (pin.isMirror() ? -pinNameTextWidth : 0.0));
        pinName.setLayoutX(getSymbolX() + (pin.isMirror() ? -PIN_NAME_MARGIN : PIN_NAME_MARGIN));
        pinName.setLayoutY(pinNameTextHeight * 0.25);
        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
            Rotate r = new Rotate(180, pinNameTextWidth / 2.0, -pinNameTextHeight * 0.3);
            pinName.getTransforms().add(r);
        }
        if (pin.isMirror() ^ parentMir) {
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
        padName.setFill(padColor);
        padName.setStroke(padColor);
        double padWidth = padName.getBoundsInLocal().getWidth() - PAD_FONT_SIZE * 0.47;
        //double padHeight = padName.getBoundsInLocal().getHeight();

        padName.setLayoutX(-(pin.isMirror() ? padWidth : 0.0));

        padName.setLayoutY(-PAD_TEXT_ASCEND);
        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
            Rotate r = new Rotate(180, padWidth / 2.0, PAD_TEXT_ASCEND);
            padName.getTransforms().add(r);
        }
        if (pin.isMirror() ^ parentMir) {
            Scale sc = new Scale(-1.0, 1.0, padWidth / 2.0, 0.0);
            padName.getTransforms().add(sc);
        }

    }

    private void updateRotation() {
        rotTransform.setPivotX(pin.getX());
        rotTransform.setPivotY(-pin.getY());
        //if (pin.getRot() != 0) {
        //Rotate r = new Rotate(-pin.getRot(), pin.getX(), pin.getY());
        rotTransform.setAngle(-pin.getRot());
        //getTransforms().add(r);
        //}
    }

    private void updatePosition() {
        translate.setX(pin.getX());
        translate.setY(-pin.getY());
    }

    private double getSymbolX() {
        return pin.isMirror() ? -pin.getLength().lenMM() : pin.getLength().lenMM(); // Symbol Outline X
    }

    private double getDotRadius() {
        if (pin.getFunction() == PinFunction.DOT || pin.getFunction() == PinFunction.DOTCLK) {
            return DOT_CIRCLE_RADIUS;
        }
        return 0.0;
    }

//    private void generateNode() {
//        getChildren().clear();
//
//        double pX = pin.getX();
//        double pY = -pin.getY();
//        boolean pinMirror = pin.getRotation().isMirror();
//        double rawPinLen = pin.getLength().lenMM();
//        double symbX = pX + (pinMirror ? -rawPinLen : rawPinLen); // Symbol Outline X
//
//        // There might be a dot on pin.
//        double dotRadius = 0;
//        if (pin.getFunction() == PinFunction.DOT || pin.getFunction() == PinFunction.DOTCLK) {
//            dotRadius = DOT_CIRCLE_RADIUS;
//        }
//        double pinLen = rawPinLen - dotRadius * 2.0;
//
//        double vizPinRot = (pin.getRot() + getParentRot()) % 360;
//
//        // Draw pin wire
//        pinLine.setStroke(symbolColor);
//        pinLine.setStrokeLineCap(StrokeLineCap.BUTT);
//        pinLine.setStrokeWidth(PIN_STROKE_WIDTH);
//        getChildren().add(pinLine);
//
//        // Pin Origin Circle
//        if (showDetails) {
//            Circle originCirc = new Circle(
//                    pX, pY, ORIGIN_CIRCLE_RADIUS, Color.TRANSPARENT
//            );
//            originCirc.setStroke(pinColor);
//            originCirc.setStrokeWidth(ORIGIN_CIRCLE_LINE_WIDTH);
//            getChildren().add(originCirc);
//        }
//
//        // Dot Function
//        if (dotRadius > 0.0) {
//            Circle dotC = new Circle(dotRadius, Color.TRANSPARENT);
//            dotC.setLayoutX(symbX + (pinMirror ? dotRadius : -dotRadius));
//            dotC.setLayoutY(pY);
//            dotC.setStroke(symbolColor);
//            dotC.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
//            getChildren().add(dotC);
//        }
    // Clock Function
//        if (pin.getFunction() == PinFunction.CLK || pin.getFunction() == PinFunction.DOTCLK) {
//            Line line1 = new Line(symbX, pY - CLK_SIZE / 2.0, symbX + (pinMirror ? -CLK_SIZE : CLK_SIZE), pY);
//            line1.setStroke(symbolColor);
//            line1.setStrokeLineCap(StrokeLineCap.ROUND);
//            line1.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
//
//            Line line2 = new Line(symbX, pY + CLK_SIZE / 2.0, symbX + (pinMirror ? -CLK_SIZE : CLK_SIZE), pY);
//            line2.setStroke(symbolColor);
//            line2.setStrokeLineCap(StrokeLineCap.ROUND);
//            line2.setStrokeWidth(DOT_CIRCLE_LINE_WIDTH);
//
//            getChildren().addAll(line1, line2);
//        }
//        Color pinNameColor = nameColor;
//        Color padColor = nameColor;
//
//        switch (pin.getVisible()) {
//            case BOTH -> {
//            }
//            case PAD -> {
//                pinNameColor = showDetails ? pinGhostColor : Color.TRANSPARENT;
//            }
//            case PIN -> {
//                padColor = showDetails ? nameGhostColor : Color.TRANSPARENT;
//            }
//            case OFF -> {
//                pinNameColor = showDetails ? pinGhostColor : Color.TRANSPARENT;
//                padColor = showDetails ? nameGhostColor : Color.TRANSPARENT;
//            }
//        }
//        // Pin Name (inside component, pin function name)
//        pinName = new Text(pin.getName());
//
//        Font font = Font.loadFont(LibraryElementNode.class.getResourceAsStream(FONT_PATH), PIN_FONT_SIZE);
//        pinName.setFont(font);
//        pinName.setFill(pinNameColor);
//        pinName.setStroke(pinNameColor);
//        pinName.setStrokeWidth(PIN_FONT_SIZE * 0.08);
//        pinName.setStrokeLineJoin(StrokeLineJoin.ROUND);
//        double pinNameTextWidth = pinName.getBoundsInLocal().getWidth();
//        double pinNameTextHeight = pinName.getBoundsInLocal().getHeight();
//        pinName.setLayoutX(symbX + (pinMirror ? -PIN_NAME_MARGIN : PIN_NAME_MARGIN) + (pinMirror ? -pinNameTextWidth : 0.0));
//        pinName.setLayoutY(pY + pinNameTextHeight * 0.3);
//        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
//            Rotate r = new Rotate(180, pinNameTextWidth / 2.0, -pinNameTextHeight * 0.3);
//            pinName.getTransforms().add(r);
//        }
//        if (pinMirror ^ parentMir) {
//            Scale sc = new Scale(-1.0, 1.0, pinNameTextWidth / 2.0, 0.0);
//            pinName.getTransforms().add(sc);
//        }
//        getChildren().add(pinName);
//        // Pad Name (outside component, usually a number)
//        // Use the padValue from DeviceSet if it exists.
//        String padValue;
//        if (pin.getPadValue() != null) {
//            padValue = pin.getPadValue();
//        } else { // Fill padValue with string that matches pinLength
//            padValue = "9";
//            for (int i = 1; i < pin.getLength().ordinal(); i++) {
//                padValue += "9";
//            }
//        }
//        Text padName = new Text(padValue);
//        double padFontSize = PIN_FONT_SIZE * 0.8;
//        Font padFont = Font.loadFont(
//                LibraryElementNode.class.getResourceAsStream(FONT_PATH),
//                padFontSize
//        );
//        padName.setFont(padFont);
//        padName.setFill(padColor);
//        padName.setStroke(padColor);
//        padName.setStrokeLineJoin(StrokeLineJoin.ROUND);
//        padName.setStrokeWidth(padFontSize * 0.08);
//        double padWidth = padName.getBoundsInLocal().getWidth();
//        //double padHeight = padName.getBoundsInLocal().getHeight();
//        padName.setLayoutX(pX - (pinMirror ? padWidth : 0.0));
//        padName.setLayoutY(pY - PAD_TEXT_ASCEND);
//        if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
//            Rotate r = new Rotate(180, padWidth / 2.0, PAD_TEXT_ASCEND);
//            padName.getTransforms().add(r);
//        }
//        if (pinMirror ^ parentMir) {
//            Scale sc = new Scale(-1.0, 1.0, padWidth / 2.0, 0.0);
//            padName.getTransforms().add(sc);
//        }
//        getChildren().add(padName);
//
//        // Direction and Swap-Level  ( ex.   io 0  )
//        if (showDetails) {
//            Text dirSwap = new Text(pin.getDirection().code() + "  " + pin.getSwapLevel());
//            double dirSwapFontSize = PIN_FONT_SIZE * 0.6;
//            Font dirSwapFont = Font.loadFont(
//                    LibraryElementNode.class.getResourceAsStream(FONT_PATH),
//                    dirSwapFontSize
//            );
//            dirSwap.setStroke(pinColor);
//            dirSwap.setStrokeWidth(dirSwapFontSize * 0.08);
//            dirSwap.setFont(dirSwapFont);
//            dirSwap.setFill(pinColor);
//            double dsWidth = dirSwap.getBoundsInLocal().getWidth();
//            //double dsHeight = dirSwap.getBoundsInLocal().getHeight();
//            double xyOffset = ORIGIN_CIRCLE_RADIUS * 0.71;
//            dirSwap.setLayoutX(pX - (pinMirror ? 0.0 : dsWidth) + (pinMirror ? xyOffset : -xyOffset));
//            dirSwap.setLayoutY(pY - xyOffset);
//            if ((vizPinRot == 180) || (vizPinRot == 270 && !parentMir) || (vizPinRot == 90 && parentMir)) { // Flip Text
//                //if (vizPinRot > 90.0) { // Flip Text
//                Rotate r = new Rotate(180, dsWidth / 2.0, xyOffset);
//                dirSwap.getTransforms().add(r);
//            }
//            if (pinMirror ^ parentMir) {
//                Scale sc = new Scale(-1.0, 1.0, dsWidth / 2.0, 0.0);
//                dirSwap.getTransforms().add(sc);
//            }
//            getChildren().add(dirSwap);
//        }
//        if (pin.getRot() != 0) {
//            Rotate r = new Rotate(-pin.getRot(), pX, pY);
//            getTransforms().add(r);
//        }
//
//    }
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
        this.parentRot = parentRotation == null ? 0.0 : parentRotation.getValue();
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
    }

    /**
     * @param pinColor the pinColor to set
     */
    public final void setPinColor(Color pinColor) {
        this.pinColor = pinColor;
        this.pinGhostColor = new Color(pinColor.getRed(), pinColor.getGreen(), pinColor.getBlue(), 0.2);
    }

    /**
     * @param nameColor the nameColor to set
     */
    public final void setNameColor(Color nameColor) {
        this.nameColor = nameColor;
        this.nameGhostColor = new Color(nameColor.getRed(), nameColor.getGreen(), nameColor.getBlue(), 0.2);

        pinName.setFill(nameColor);
        pinName.setStroke(nameColor);
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
        PinField f = (PinField) field;

        LOGGER.log(Level.SEVERE,
                "Pin properties have changed!{0}: {1} => {2}",
                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch (f) {
            case PinField.NAME, PinField.PAD_VALUE -> {
                updatePadPin();
            }
            case PinField.X, PinField.Y -> {
                // Adjust position transform
                updatePosition();
            }
            case PinField.DIRECTION -> {
                updatePinData();
            }
            case PinField.VISIBLE -> {
                updatePadPinVisible();
            }
            case PinField.FUNCTION -> {
                updateClockLines();
                updateLine();
            }
            case PinField.LENGTH -> {
                updateLine();
            }
            case PinField.ROTATION -> {
                // Adjust Rotation Transform
                updateRotation();
            }
        }

    }

}
