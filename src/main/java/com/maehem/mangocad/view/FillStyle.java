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
package com.maehem.mangocad.view;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class FillStyle {

    private static final int PATTERN_SIZE = 24;
    private static final int N_DIAG_LINES = 6; // Must be even number for tiling pattern.
    private static final int N_HV_LINES = 5;

    public static Paint getPaint(int fillStyle, Color c) { // TODO add scale?
        switch (fillStyle) {
            case 0 -> { // Transparent
                return Color.TRANSPARENT;
            }
            case 1 -> {  // Solid
                return c;
            }
            case 2 -> { // H Stripes. Independent of Zoom ( 2 on, 2 off).
                return makeHatchHorizLines(5, c);
            }
            case 3 -> {
                return makeDiagHatch(N_DIAG_LINES, 0.15, true, c, false);
            }
            case 4 -> {
                return makeDiagHatch(N_DIAG_LINES, 0.4, true, c, false);
            }
            case 5 -> {
                return makeDiagHatch(N_DIAG_LINES, 0.4, false, c, false);
            }
            case 6 -> {
                return makeDiagHatch(N_DIAG_LINES, 0.15, false, c, false);
            }
            case 7 -> { // H-V Cross at 0.2
                return makeCrossHatch(0.2, c);
            }
            case 8 -> { // Both way diag  0.1
                return makeDiagHatch(N_DIAG_LINES, 0.15, true, c, true);
            }
            case 9 -> { // Diag Lines 2 on, 2 off
                return makeDiagHatch(N_DIAG_LINES * 2, 0.15, true, c, true);
            }
            case 10 -> { // Diag Dots, 2 on, 11 off
                return makeDots(4, true, c);
            }
            case 11 -> { // Diag Dots, 2 on, 6 off
                return makeDots(6, true, c);
            }
            case 12, 13, 14, 15 -> { // H-V Dots, 10%
                return makeDots(9, false, c);
            }

        }

        return c;
    }

    public static Shape getSwatch(int fillStyle, Color c, double size) {
        Rectangle mask = new Rectangle(size, size);

        mask.setStrokeWidth(size / 30.0);
        mask.setStroke(c);
        //ImagePattern maskPattern = makeDiagHatchOld(10, true, c);
        //mask.setFill(maskPattern);
        mask.setFill(getPaint(fillStyle, c));

        return mask;
    }

    private static ImagePattern makeHatchHorizLines(int nLines, Color c) {
        Pane p = patternPane();

        // Horizontal
        double inc = (double) PATTERN_SIZE / nLines;
        double strokeWidth = inc * 0.5;

        for (int i = 0; i < nLines; i++) {
            Line l = new Line(0, i * inc + strokeWidth, PATTERN_SIZE, i * inc + strokeWidth);

            l.setStrokeWidth(strokeWidth);
            l.setStroke(c);
            p.getChildren().add(l);
        }

        return snapPattern(p);
    }

    private static ImagePattern makeDiagHatch(int nLines, double thickPct, boolean dir, Color c, boolean cross) {
        Pane p = patternPane();

        applyDiag(p, nLines, thickPct, dir, c);
        if (cross) {
            applyDiag(p, nLines, thickPct, !dir, c);
        }

        return snapPattern(p);
    }

    private static ImagePattern makeCrossHatch(double thickPct, Color c) {
        Pane p = patternPane();

        // Horizontal
        double inc = (double) PATTERN_SIZE / N_HV_LINES;
        double strokeWidth = inc * thickPct;

        for (int i = 0; i < N_HV_LINES; i++) {
            Line l;
            // HORIZ
            l = new Line(0, i * inc + inc / 2.0, PATTERN_SIZE, i * inc + inc / 2.0);
            l.setStrokeWidth(strokeWidth);
            l.setStroke(c);
            p.getChildren().add(l);

            // VERT
            l = new Line(i * inc + inc / 2.0, 0, i * inc + inc / 2.0, PATTERN_SIZE);
            l.setStrokeWidth(strokeWidth);
            l.setStroke(c);
            p.getChildren().add(l);
        }

        return snapPattern(p);
    }

    private static ImagePattern makeDots(int nRows, boolean stagger, Color c) {
        Pane p = patternPane();

        applyDots(p, nRows, stagger, c);

        return snapPattern(p);
    }

    private static void applyDiag(Pane p, int nLines, double thickPct, boolean dir, Color c) {
        // Diagonal  -- Four Lines, Five Gaps
        double inc = (double) PATTERN_SIZE / nLines * 2.0;
        double thick = inc * thickPct / 2.0;
        for (int i = 0; i < nLines; i++) {
            Line l = new Line(
                    i * inc - (dir ? 0 : PATTERN_SIZE) + inc / 2.0, 0,
                    i * inc - (dir ? PATTERN_SIZE : 0) + inc / 2.0, PATTERN_SIZE);
            l.setStrokeWidth(thick);
            l.setStroke(c);
            p.getChildren().add(l);
        }
    }

    private static void applyDots(Pane p, int nRows, boolean stagger, Color c) {
        double inc = (double) PATTERN_SIZE / nRows;
        double shift = inc / 3.0;

        for (int i = 0; i < nRows; i++) {
            double staggerX = ((stagger && i % 2 > 0) ? inc / 2.0 : 0);
            for (int j = 0; j < nRows; j++) {

                Circle cc = new Circle(
                        j * inc + staggerX + shift,
                        i * inc + shift,
                        PATTERN_SIZE / 30, c);

                //Line l = new Line(0, i * inc + strokeWidth, PATTERN_SIZE, i * inc + strokeWidth);
                //cc.setStrokeWidth(strokeWidth);
                //cc.setStroke(c);
                p.getChildren().add(cc);
            }
        }

    }

    private static Pane patternPane() {
        Pane p = new Pane(); // Group?
        p.setBackground(Background.EMPTY);
        p.setClip(new Rectangle(PATTERN_SIZE, PATTERN_SIZE));

        return p;
    }

    private static ImagePattern snapPattern(Pane p) {
        WritableImage wi = new WritableImage(PATTERN_SIZE, PATTERN_SIZE);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        return new ImagePattern(p.snapshot(sp, wi), 0, 0, PATTERN_SIZE, PATTERN_SIZE, false);
    }

}
