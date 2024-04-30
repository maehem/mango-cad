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

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementListener;
import com.maehem.mangocad.model.element.basic.ElementText;
import com.maehem.mangocad.model.element.drawing.Layers;
import com.maehem.mangocad.model.element.enums.ElementTextField;
import static com.maehem.mangocad.model.element.enums.ElementTextField.VALUE;
import static com.maehem.mangocad.model.element.enums.TextAlign.*;
import com.maehem.mangocad.model.element.misc.LayerElement;
import com.maehem.mangocad.model.util.Rotation;
import com.maehem.mangocad.view.ColorUtils;
import com.maehem.mangocad.view.ViewUtils;
import static com.maehem.mangocad.view.ViewUtils.FONT_SCALE;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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
public class TextNode extends ArrayList<Shape> implements ElementListener {

    private final double CROSS_SIZE = 0.5; // Crosshairs size
    private final double CROSS_WIDTH = 0.01;
    private final double borderW = 0.05;

    //private static final double FONT_ASC_PCT = 0.61; // 53%   (0.0 - 1.0)
    private static final double FONT_ASC_PCT = 0.47; // 53%   (0.0 - 1.0)  == 450/960 from font.

    private ElementText textElement; // Holds the text data.

    // Text
    private final Text text = new Text();
    private Font font = ViewUtils.getDefaultFont(1.0);
    private final Translate textAlignTransform = new Translate();
    private final Translate textTranslate = new Translate();
    private final Translate parentMirrorTranslate = new Translate();
    private final Translate ratioTranslate = new Translate();

    private final Scale mirrorTransform = new Scale(1.0, 1.0);
    private final Rotate rTTG = new Rotate(); // Text rotate
    private final Rotate tR = new Rotate(); // Spin flag
    private final Rotate debugRotate = new Rotate();
    private final Scale debugScale = new Scale();
    private final Translate debugTranslate = new Translate();

    // Debug Box
    private final Rectangle debugBox = new Rectangle();
    // Border
    private final Rectangle border = new Rectangle();
    // Crosshairs
    private final Line ch = new Line(-CROSS_SIZE, 0, CROSS_SIZE, 0);
    private final Line cv = new Line(0, -CROSS_SIZE, 0, CROSS_SIZE);
    private double fontAsc;
    private boolean barOver;
    private final String altText;
    private final Layers layers;
    private final ColorPalette palette;
    private final Rotation parentRotation;
    private boolean showCrosshair;

    public TextNode(ElementText et, String altText, Layers layers, ColorPalette palette, Rotation parentRotation, boolean showCrossHair) {
        this.textElement = et;
        this.altText = altText;
        this.layers = layers;
        this.palette = palette;
        this.parentRotation = parentRotation;
        this.showCrosshair = showCrossHair;

        add(text);
        add(debugBox);
        add(border);
        add(ch);
        add(cv);

        debugBox.setStroke(new Color(1.0, 0.7, 0.2, 0.3));
        debugBox.setStrokeWidth(0.05);
        debugBox.setStrokeType(StrokeType.OUTSIDE);
        debugBox.setFill(null);
        debugBox.getTransforms().add(debugRotate);
        debugBox.getTransforms().add(debugScale);
        debugBox.getTransforms().add(debugTranslate);
        debugRotate.setPivotX(0);
        debugRotate.setPivotY(0);

        ch.setStroke(Color.WHITE);
        ch.setStrokeWidth(CROSS_WIDTH);
        cv.setStroke(Color.WHITE);
        cv.setStrokeWidth(CROSS_WIDTH);

        text.setStrokeType(StrokeType.CENTERED);
        text.setStrokeLineJoin(StrokeLineJoin.ROUND);
        text.setTextOrigin(VPos.BASELINE);
        text.getTransforms().addAll(
                textAlignTransform,
                textTranslate,
                ratioTranslate,
                rTTG,
                tR,
                parentMirrorTranslate
        );

        updateValue();

        updateLocation();
        updateLayer();
        updateFont();
        updateRatio();
        updateAlignRotation();
        updateSpin();
        updateDistance();

        updateDebugBox();

        Platform.runLater(() -> {
            textElement.addListener(this);
        });
    }

    private double getTextWidth() {
        // JavaFX getBoundsInLocal().getWidth() for text always
        // reports +0.99 more than actual width, so we subtract it.
        double textWidth = text.getBoundsInLocal().getWidth();
        if (textWidth > 0.99) {
            textWidth -= 0.99;
        }

        return textWidth;
    }

    private double getStackHeight() {
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        long lineCount = text.getText().lines().count();

        return (lineCount * size) + (lineCount - 1) * lineSpace;
    }

    private void updateValue() {
        String textString = altText != null ? altText : textElement.getValue();

        // Replace exclamation with bar over text.
        if (textString.startsWith("!")) {
            textString = textString.substring(1);
            barOver = true;
        } else {
            barOver = false;
        }

//        // JavaFX can't to font character spacing. So we do this ugly hack.
//        StringBuilder spaceStr = new StringBuilder();
//        for (int i = 18; i < textElement.getRatio(); i += 18) {
//            spaceStr.append(" ");
//        }
//        StringBuilder sb = new StringBuilder();
//        for (char c : textString.toCharArray()) {
//            sb.append(c).append(spaceStr.toString());
//        }
//
//        text.setText(sb.toString());
        text.setText(textString);
    }

    private void updateDebugBox() {
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        long lineCount = text.getText().lines().count();

        double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;

        debugBox.setWidth(getTextWidth());
        debugBox.setHeight(stackHeight);
    }

    private void updateLocation() {
        //double stroke = textElement.getDerivedStroke();
        text.setLayoutX(textElement.getX());
        text.setLayoutY(textElement.getY());

        ch.setLayoutX(textElement.getX());
        ch.setLayoutY(textElement.getY());
        cv.setLayoutX(textElement.getX());
        cv.setLayoutY(textElement.getY());

        border.setLayoutX(textElement.getX());
        border.setLayoutY(textElement.getY());

        debugBox.setLayoutX(textElement.getX());
        debugBox.setLayoutY(textElement.getY());
    }

    private void updateLayer() {
        LayerElement layer = layers.get(textElement.getLayerNum());
        Color c = ColorUtils.getColor(palette.getHex(layer.getColorIndex()));
        // Get new Color based on layer.
        text.setStroke(c);
        text.setFill(c);
    }

    private void updateFont() {

        // TODO: Implement TextElement.getFont()
        // Fixed mangoCAD font for now.
        // 1 Point == 1/72   inch == 0.013888 inch == 0.35277 mm
        // 1 point == 1/25.4 inch ==  0.039 inch == 1 mm
        // MM to points   1 to 2.835
        double fontSize = textElement.getSize();
        //double fontSizeMult = 0.60; //0.7272; // INCH to Point ratio

        fontSize *= ViewUtils.FONT_SIZE_MULT;

        // Makes font fit requested height regardless of boldness.
        // Higher ratio text will be reduced in font size to make height fit size.
        fontSize *= ((100 - textElement.getRatio()) * 0.01);
        fontSize *= FONT_SCALE; // Font specific.
        //LOGGER.log(Level.SEVERE, "Font Size: " + fontSize);
        font = ViewUtils.getDefaultFont(fontSize);

        text.setFont(font);
    }

    private void updateRatio() {
        double stroke = textElement.getDerivedStroke();
        text.setStrokeWidth(stroke);

        // Effects text line and stack height, so adjust font scale.
    }

    private void updateSpin() {
        Rotation rotation = textElement.getRotation();
        //double rot = rotation.getValue();
        boolean mir = rotation.isMirror();
        //boolean spin = rotation.isSpin();

        double stroke = textElement.getDerivedStroke();
        //double sFudge = stroke * 0.099; // Nudge by 1%
        double s2 = stroke / 2.0;
        double textWidth = getTextWidth();
        long lineCount = text.getText().lines().count();
        double size = textElement.getSize();
        //double lineSpace = size * (textElement.getDistance() * 0.01);
        //double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;
        double stackHeight = getStackHeight();
        //double baselineToBottom = (lineCount - 1) * -(size + lineSpace);

        double tw2 = textWidth / 2.0;
        double sh2 = stackHeight / 2.0;

//        double pivotX = 0;
//        double pivotY = 0;
//        double transX = 0;
//        double transY = 0;

        mirrorTransform.setX(mir ? -1.0 : 1.0);

        // TODO: Merge these into AlignRotate's switch
        if (textElement.isSpun()) {
            // Spin the text
            tR.setAngle(180);
            tR.setPivotX(tw2 - s2);
            tR.setPivotY(lineCount > 1 ? (sh2 - size + s2) : -size / 2 + s2);
        } else {
            tR.setAngle(0); // No Spin
            tR.setPivotX(0);
            tR.setPivotY(0);
        }
    }

    private void updateAlignRotation() {
        long lineCount = text.getText().lines().count();
        double stroke = textElement.getDerivedStroke();
        double s2 = stroke / 2.0;
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        double baselineToBottom = (lineCount - 1) * -(size + lineSpace);
        double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;

        double textWidth = getTextWidth();

        double rot = textElement.getRot();
        boolean mir = textElement.getRotation().isMirror();
        boolean spun = textElement.isSpun();

        double tw2 = textWidth / 2.0;
        double sh2 = stackHeight / 2.0;

        double pivotX = 0;
        double pivotY = 0;
        double transX = 0;
        double transY = 0;

        if (lineCount > 1) {
            switch (textElement.getAlign()) {
                case BOTTOM_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT -> {
                    textAlignTransform.setX(0);
                    textAlignTransform.setY(baselineToBottom);
                }
                case CENTER, CENTER_LEFT, CENTER_RIGHT -> {
                    textAlignTransform.setX(0);
                    textAlignTransform.setY(baselineToBottom);
                }
                default -> { // TOP_LEFT, TOP_CENTER, TOP_RIGHT
                    textAlignTransform.setX(0);
                    textAlignTransform.setY(0);
                }
            }
        } else {
            textAlignTransform.setX(0);
            textAlignTransform.setY(0);
        }

        double ratioTX = 0;
        double ratioTY = 0;

        switch (textElement.getAlign()) {
            case BOTTOM_LEFT -> {
                transX = mir ? -textWidth + stroke : 0;
                transY = 0;

                pivotX = mir ? textWidth - s2 : -s2;
                pivotY = -baselineToBottom + s2;

                ratioTX = s2;
                ratioTY = -s2;

                debugTranslate.setX(0);
                debugTranslate.setY(-stackHeight);
            }
            case BOTTOM_CENTER -> {
                transX = -tw2 + s2;
                transY = 0;

                pivotX = tw2 - s2;
                pivotY = -baselineToBottom + s2;

                ratioTX = 0;
                ratioTY = -s2;

                debugTranslate.setX(-tw2);
                debugTranslate.setY(-stackHeight);
            }
            case BOTTOM_RIGHT -> {
                transX = mir ? 0 : -textWidth + stroke;
                transY = 0;

                ratioTX = -s2;
                ratioTY = -s2;

                pivotX = mir ? -s2 : textWidth - s2;
                pivotY = -baselineToBottom + s2;

                debugTranslate.setX(-textWidth);
                debugTranslate.setY(-stackHeight);
            }
            case CENTER_LEFT -> {
                transX = mir ? -textWidth + stroke : 0;
                transY = sh2 - s2;

                ratioTX = s2;
                ratioTY = 0;

                pivotX = mir ? textWidth - s2 : -s2;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;

                debugTranslate.setX(0);
                debugTranslate.setY(-sh2);
            }
            case CENTER -> {
                transX = -tw2 + s2;
                transY = sh2 - s2;

                ratioTX = 0;
                ratioTY = 0;

                pivotX = tw2 - 1.0 * s2;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + 1.0 * s2;

                debugTranslate.setX(-tw2);
                debugTranslate.setY(-sh2);
            }
            case CENTER_RIGHT -> {
                transX = mir ? 0 : -textWidth + stroke;
                transY = sh2 - s2;

                ratioTX = -s2;
                ratioTY = 0;

                pivotX = mir ? -s2 : textWidth - s2;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;

                debugTranslate.setX(-textWidth);
                debugTranslate.setY(-sh2);
            }
            case TOP_LEFT -> {
                transX = mir ? -textWidth + stroke : 0;
                //transY = stackHeight - s2;
                transY = size - s2;

                ratioTX = s2;
                ratioTY = 0;

                pivotX = mir ? textWidth - s2 : -s2;
                pivotY = -size + s2;

                debugTranslate.setX(0);
                debugTranslate.setY(0);
            }
            case TOP_CENTER -> {
                transX = -tw2 + s2;
                transY = size - s2;

                ratioTX = 0;
                ratioTY = 0;

                pivotX = tw2 - s2;
                pivotY = -size + s2;

                debugTranslate.setX(-tw2);
                debugTranslate.setY(0);
            }
            case TOP_RIGHT -> { // MIR works
                transX = mir ? 0 : -textWidth + stroke;
                transY = size - s2;

                ratioTX = -s2;
                ratioTY = 0;

                pivotX = mir ? -s2 : textWidth - s2;
                pivotY = -size + s2;

                debugTranslate.setX(-textWidth);
                debugTranslate.setY(0);
            }
        }

        textTranslate.setX(transX);
        textTranslate.setY(transY);
        ratioTranslate.setX(ratioTX);
        ratioTranslate.setY(ratioTY);

        debugScale.setX(mir ? -1.0 : 1.0);

        double rotG = mir ? rot : -rot;
        debugRotate.setAngle(rotG);
        rTTG.setAngle(rotG);
        rTTG.setPivotX(pivotX);
        rTTG.setPivotY(pivotY);

        boolean parentMir = parentRotation != null ? parentRotation.isMirror() : false;
        if (parentMir) {
            double trFact = 0.0;
            if (textElement.getAlign().name().endsWith("_RIGHT")) {
                trFact = 1.0;
            } else if (textElement.getAlign().name().endsWith("_LEFT")) {
                trFact = -1.0;
            }
            parentMirrorTranslate.setX(trFact * textWidth);
            parentMirrorTranslate.setY(0);
            //Translate tr = new Translate(trFact * tt.getBoundsInLocal().getWidth(), 0);
            //tt.getTransforms().add(tr);
        }
    }

    private void updateDistance() {
//        Text exLine = new Text("EXAMPLE");
//        exLine.setFont(font);
//        double lineHeight = exLine.getBoundsInLocal().getHeight();

        //double size = text.getFont().getSize();
        double size = textElement.getSize();
        //double lineSpace = size * (textElement.getDistance() * 0.01);
        double distFactor = 0.82 - textElement.getRatio() * 0.01 * ViewUtils.FONT_SIZE_MULT * 1.1;
        double lineSpaceFx = size * (textElement.getDistance() * 0.01 - distFactor);
        text.setLineSpacing(lineSpaceFx); // Convert mm to  pixels.
    }

    @Override
    public void elementChanged(Element e, Enum field, Object oldVal, Object newVal) {
//        LOGGER.log(Level.SEVERE,
//                "Pin properties have changed!{0}: {1} => {2}",
//                new Object[]{field, oldVal.toString(), newVal.toString()});

        switch ((ElementTextField) field) {
            case VALUE -> {
                updateValue();
                updateDistance();
                updateDebugBox();
            }
            case LAYER -> {
                updateLayer();
            }
            case ElementTextField.X, ElementTextField.Y -> {
                updateLocation();
            }
            case ROTATION -> {
                updateLocation();
                updateAlignRotation();
                updateSpin();
            }
            case ALIGN -> {
                updateAlignRotation();
            }
            case RATIO -> {
                updateRatio();
                updateValue();
                updateFont();
                updateDistance();
                updateAlignRotation();
                updateSpin();
                updateDebugBox();
            }
            case SIZE, FONT -> {
                updateFont();
                updateDistance();
                updateDebugBox();
            }
        }
    }
}
