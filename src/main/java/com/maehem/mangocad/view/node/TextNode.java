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
import static com.maehem.mangocad.model.element.enums.TextAlign.BOTTOM_CENTER;
import static com.maehem.mangocad.model.element.enums.TextAlign.BOTTOM_LEFT;
import static com.maehem.mangocad.model.element.enums.TextAlign.BOTTOM_RIGHT;
import static com.maehem.mangocad.model.element.enums.TextAlign.CENTER;
import static com.maehem.mangocad.model.element.enums.TextAlign.CENTER_LEFT;
import static com.maehem.mangocad.model.element.enums.TextAlign.CENTER_RIGHT;
import static com.maehem.mangocad.model.element.enums.TextAlign.TOP_CENTER;
import static com.maehem.mangocad.model.element.enums.TextAlign.TOP_LEFT;
import static com.maehem.mangocad.model.element.enums.TextAlign.TOP_RIGHT;
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

    private final Scale mirrorTransform = new Scale(1.0, 1.0);
    private final Rotate rTTG = new Rotate(); // Text rotate
    private final Rotate tR = new Rotate(); // Spin flag
    private final Rotate debugRotate = new Rotate();

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

        debugBox.setStroke(Color.MAGENTA);
        debugBox.setStrokeWidth(0.1);
        debugBox.setFill(null);
        debugBox.getTransforms().add(debugRotate);

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
                tR,
                rTTG,
                parentMirrorTranslate
        );

        updateValue();

        updateLocation();
        updateLayer();
        updateFont();
        updateRatio();
        updateRotation();
        updateAlign();
        updateDistance();

        updateDebugBox();

        Platform.runLater(() -> {
            textElement.addListener(this);
        });
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

        text.setText(textString);
    }

    private void updateDebugBox() {
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        long lineCount = text.getText().lines().count();

        double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;
        double textWidth = text.getBoundsInLocal().getWidth();

        debugBox.setWidth(textWidth);
        debugBox.setHeight(stackHeight);
    }

    private void updateLocation() {
        text.setLayoutX(textElement.getX());
        text.setLayoutY(textElement.getY());

        ch.setLayoutX(textElement.getX());
        ch.setLayoutY(textElement.getY());
        cv.setLayoutX(textElement.getX());
        cv.setLayoutY(textElement.getY());

        border.setLayoutX(textElement.getX());
        border.setLayoutY(textElement.getY());
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

    private void updateRotation() {
        Rotation rotation = textElement.getRotation();
        double rot = rotation.getValue();
        boolean mir = rotation.isMirror();
        boolean spin = rotation.isSpin();

        double textWidth = text.getBoundsInLocal().getWidth();
        long lineCount = text.getText().lines().count();
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        double stroke = textElement.getDerivedStroke();
        double sFudge = stroke * 0.099; // Nudge by 1%
        double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;
        double baselineToBottom = (lineCount - 1) * -(size + lineSpace);

        double s2 = stroke / 2.0;

        double tw2 = textWidth / 2.0;
        double sh2 = stackHeight / 2.0;

        double pivotX = 0;
        double pivotY = 0;
        double transX = 0;
        double transY = 0;

        mirrorTransform.setX(mir ? -1.0 : 1.0);

        if (!spin && (rot > 90 && rot <= 270)) {
//            Rotate tR = new Rotate(180,
//                    textWidth / 2.0 + sFudge, // + s2,
//                    lineCount > 1 ? (sh2 - size) : -size / 2.0
//            );

//            tt.getTransforms().add(tR);
            tR.setAngle(180);
            tR.setPivotX(textWidth / 2.0 + sFudge);
            tR.setPivotY(lineCount > 1 ? (sh2 - size) : -size / 2.0);

            switch (textElement.getAlign()) {
                case BOTTOM_LEFT -> {
                    transX = s2 + sFudge;
                    transY = -s2;
                    //
                    // WORKS
                    pivotX = textWidth - s2 + sFudge;
                    pivotY = -size + s2;

                }
                case BOTTOM_CENTER -> {
                    //transX = -tw2 + 0.35 * stroke;
                    transX = mir ? -transX + 0.8 * stroke : tw2 + s2;
                    // WORKS
                    pivotY = -size + s2;
                }
                case BOTTOM_RIGHT -> {
                    transX = textWidth + s2;
                    // WORKS
                    pivotX = -3 * sFudge;
                    pivotY = -size + s2;
                }
                // TODO: CENTER_* need to translate upward by approx. the stroke width,
                case CENTER_LEFT -> {
                    transY = -size / 2.0 - s2;
                    transX = s2;

                    pivotX = textWidth - s2 + 2 * sFudge;
                    //pivotY = -0.5 * (baselineToBottom + size) + s2;
                }
                case CENTER -> {
                    transX = mir ? -transX + 0.8 * stroke : tw2 + s2 + sFudge;
                    //transY = 0.5 * (baselineToBottom + size) - s2;
                    transY = -transY - stroke;

                    pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;
                }
                case CENTER_RIGHT -> {
                    transX = -textWidth + 0.66 * s2;
                    transX = -transX + 0.9 * stroke;
                    //transX = textWidth + s2;

                    transY = size / 2.0 - s2;
                    transY = -transY - stroke;

                    pivotX = /*mir ? textWidth - s2 :*/ -3.5 * sFudge;
                    //pivotX = s2;
                    ///pivotX = -transX;
                    //pivotY = -transY;
                }
                case TOP_LEFT -> {
                    transX = 1.05 * s2;
                    transY = size - s2;
                    transY = -transY - stroke;
                    //transY = -size - s2;

                    pivotX = textWidth - s2 + 1.5 * sFudge;

                    pivotY = -baselineToBottom + s2;
                    //pivotX = textWidth - s2 + sFudge;
                    //pivotY = -baselineToBottom + s2 - sFudge;// + stroke / 2.0;
                }
                case TOP_CENTER -> {  // Works already.
                    transX = mir ? -transX + 0.8 * stroke : tw2 + 1.05 * s2;
                    transY = size - s2;
                    transY = -transY - stroke;

                    pivotY = -baselineToBottom + s2;
                }
                case TOP_RIGHT -> {
                    transX = -textWidth + 0.66 * s2;
                    transX = -transX + 0.9 * stroke;
                    transY = size - s2;
                    transY = -transY - stroke;

                    pivotX = -3.5 * sFudge;
                    pivotY = -baselineToBottom + s2;
                }
            }
            textTranslate.setX(transX);
            textTranslate.setY(transY);

            double rotG = mir ? -rot : -rot;
            rTTG.setAngle(rotG); // Fix me. Always -rot ?

            rTTG.setPivotX(pivotX);
            rTTG.setPivotY(pivotY);
        } else {
            tR.setAngle(0); // No Spin
            tR.setPivotX(0);
            tR.setPivotY(0);
        }

        // TODO: Debug box
    }

    private void updateAlign() {
        double x = textElement.getX();
        double y = -textElement.getY();

        long lineCount = text.getText().lines().count();
        double size = textElement.getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        double baselineToBottom = (lineCount - 1) * -(size + lineSpace);
        double stackHeight = (lineCount * size) + (lineCount - 1) * lineSpace;

        double textWidth = text.getBoundsInLocal().getWidth();
        double stroke = textElement.getDerivedStroke();
        double sFudge = stroke * 0.099; // Nudge by 1%
        double rot = textElement.getRot();
        boolean mir = textElement.getRotation().isMirror();

        double s2 = stroke / 2.0;

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
                    textAlignTransform.setY(0.5 * baselineToBottom);
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

        switch (textElement.getAlign()) {
            case BOTTOM_LEFT -> {
                transX = s2 - sFudge;
                transY = -s2;

                pivotX = -s2 + sFudge;
                pivotY = -baselineToBottom + s2;

                debugBox.setLayoutX(x); // BL
                debugBox.setLayoutY(-y - stackHeight);  // BL

                //dr = new Rotate(-rot, 0, stackHeight); // BL
                debugRotate.setAngle(0);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(stackHeight);

            }
            case BOTTOM_CENTER -> {
                transX = -tw2 + 3.5 * sFudge;
                transY = -s2;

                // WORKS
                pivotX = tw2 - s2 + 2 * sFudge;
                pivotY = -baselineToBottom + s2;

                debugBox.setLayoutX(mir ? x + tw2 : x - tw2); // BC
                debugBox.setLayoutY(-y - stackHeight);  // BC
                //dr = new Rotate(-rot, tw2, stackHeight); // BC
                debugRotate.setAngle(-rot);
                debugRotate.setPivotX(tw2);
                debugRotate.setPivotY(stackHeight);
            }
            case BOTTOM_RIGHT -> {
                transX = -textWidth + s2 - sFudge;
                transY = -s2;

                //pivotX = mir ? 0 : textWidth - s2 + sFudge;
                pivotX = -transX;
                pivotY = -baselineToBottom + s2;

                debugBox.setLayoutX(x); // BR
                debugBox.setLayoutY(-y - stackHeight);  // BR
                //if (!mir) {
                Scale scc = new Scale(-1.0, 1.0);
                debugBox.getTransforms().add(scc);
                //}

                //dr = new Rotate(rot, 0, stackHeight); // BR
                debugRotate.setAngle(rot);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(stackHeight);
            }
            case CENTER_LEFT -> {
                transX = s2 - sFudge;
                transY = 0.5 * size - s2;

                //pivotX = mir ? textWidth : -s2 + sFudge;
                pivotX = -s2 + sFudge;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;

                debugBox.setLayoutX(x); // CL
                debugBox.setLayoutY(-y - sh2);  // CL
                //dr = new Rotate(-rot, 0, sh2); // CL
                debugRotate.setAngle(-rot);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(sh2);
            }
            case CENTER -> {
                transX = -tw2 + 0.7 * s2;
                transY = 0.5 * size - s2;

                pivotX = -transX;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;

                debugBox.setLayoutX(mir ? x + tw2 : x - tw2); // CC
                debugBox.setLayoutY(-y - sh2);  // CC
                //dr = new Rotate(-rot, textWidth / 2.0, sh2); // CC
                debugRotate.setAngle(-rot);
                debugRotate.setPivotX(textWidth / 2.0);
                debugRotate.setPivotY(sh2);
            }
            case CENTER_RIGHT -> {
                transX = -textWidth + 0.66 * s2;
                transY = 0.5 * size - s2;

                pivotX = -transX;
                pivotY = -0.5 * size - 0.5 * baselineToBottom + s2;

                //debugBox.setLayoutX(x - textWidth); // CR
                debugBox.setLayoutX(x); // CR
                debugBox.setLayoutY(-y - sh2);  // CR
                //if (!mir) {
                Scale scc = new Scale(-1.0, 1.0);
                debugBox.getTransforms().add(scc);
                //}
                //dr = new Rotate(rot, 0, sh2); // CR
                debugRotate.setAngle(rot);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(sh2);
            }
            case TOP_LEFT -> {
                transX = 0.7 * s2;
                transY = size - s2;

                pivotX = -transX;
                pivotY = -transY;

                debugBox.setLayoutX(x); // TL
                debugBox.setLayoutY(-y);  // TL

                //dr = new Rotate(-rot, 0, 0); // TL
                debugRotate.setAngle(-rot);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(0);
            }
            case TOP_CENTER -> {
                transX = -tw2 + 0.70 * s2;
                transY = size - s2;

                pivotX = -transX;
                pivotY = -transY;

                debugBox.setLayoutX(mir ? x + tw2 : x - tw2); // BC
                debugBox.setLayoutY(-y);  // BC

                //dr = new Rotate(-rot, tw2, 0); // BC
                debugRotate.setAngle(-rot);
                debugRotate.setPivotX(tw2);
                debugRotate.setPivotY(0);
            }
            case TOP_RIGHT -> { // MIR works
                transX = -textWidth + 0.66 * s2;
                transY = size - s2;

                pivotX = -transX - 0.66 * s2;
                pivotY = -transY;

                debugBox.setLayoutX(x); // TR
                debugBox.setLayoutY(-y);  // TR
                //if (!mir) {
                Scale scc = new Scale(-1.0, 1.0);
                debugBox.getTransforms().add(scc);
                //}

                //dr = new Rotate(rot, 0, 0); // TR
                debugRotate.setAngle(rot);
                debugRotate.setPivotX(0);
                debugRotate.setPivotY(0);
            }
        }

        textTranslate.setX(transX);
        textTranslate.setY(transY);

        double rotG = mir ? -rot : -rot;
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

        double size = text.getFont().getSize();
        double lineSpace = size * (textElement.getDistance() * 0.01);
        double distFactor = 0.802 - textElement.getRatio() * 0.01 * ViewUtils.FONT_SIZE_MULT;
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
            }
            case LAYER -> {
                updateLayer();
            }
            case ElementTextField.X, ElementTextField.Y -> {
                updateLocation();
            }
            case ROTATION -> {
                updateLocation();
                updateRotation();
                updateAlign();
            }
            case ALIGN -> {
                updateAlign();
            }
            case RATIO -> {
                updateRatio();
            }
            case SIZE, FONT -> {
                updateFont();
            }
        }
    }
}
