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
package com.maehem.mangocad.view.settings;

import com.maehem.mangocad.model.ColorPalette;
import com.maehem.mangocad.view.ColorUtils;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import com.maehem.mangocad.view.settings.widgets.SwatchGridListener;
import com.maehem.mangocad.view.settings.widgets.SwatchGridPane;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class ColorChooserPanel extends VBox implements SwatchGridListener {

    private final static int HS_SIZE = 208;
    private final static double CROSSHAIR_SIZE = 10.0;

    //private final int startColorIndex;
    private BorderPane colorRamp;
    private final ColorPalette palette;
    private Color currentColor;

    private Slider slider;
    private final Node crosshair = initCrosshair();
    private final Rectangle hsBrightness = new Rectangle(HS_SIZE, HS_SIZE);

    private final Spinner<Integer> hueSpinner = new Spinner<>(0, 360, 180);
    private final Spinner<Integer> satSpinner = new Spinner<>(0, 255, 127);
    private final Spinner<Integer> valSpinner = new Spinner<>(0, 255, 127);
    private final Spinner<Integer> redSpinner = new Spinner<>(0, 255, 127);
    private final Spinner<Integer> greenSpinner = new Spinner<>(0, 255, 127);
    private final Spinner<Integer> blueSpinner = new Spinner<>(0, 255, 127);
    private final Spinner<Integer> alphaSpinner = new Spinner<>(0, 255, 127);
    private final Rectangle colorSwatch = new Rectangle(64, 64);

    public ColorChooserPanel(ColorPalette palette, int startColorIndex) {
        this.palette = palette;
        //this.startColorIndex = startColorIndex;
        //this.currentIndex = startColorIndex;
        currentColor = ColorUtils.getColor(palette.getHex(startColorIndex));

        setSpacing(8);
        HBox topRow = new HBox();
        topRow.setSpacing(24);
        //topRow.setPadding(new Insets(CROSSHAIR_SIZE / 2.0));
        SwatchGridPane swatchGrid = new SwatchGridPane(palette, startColorIndex);
        swatchGrid.addListener(this);

        topRow.getChildren().add(swatchGrid);
        Pane hueSatRegion = initHueSatRegion();
        //hueSatRegion.setClip(new Rectangle(HS_SIZE, HS_SIZE));
        topRow.getChildren().add(hueSatRegion);
        topRow.getChildren().add(initSlider());
        getChildren().add(topRow);

        HBox bottomRow = new HBox();
        bottomRow.setSpacing(20);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        bottomRow.getChildren().add(initColorSwatch());
        bottomRow.getChildren().add(initColorSpinners());

        getChildren().add(bottomRow);

        swatchIndexChanged(startColorIndex);
    }

    private Pane initHueSatRegion() {

        int size = 64;
        int pixelSize = 2;

        Pane p = new Pane();
        p.setPrefSize(size * pixelSize, size * pixelSize);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Rectangle r = new Rectangle(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                r.setFill(xyToHSColor(x, y, size));
                //r.setFill(Color.hsb(360.0 * x / (double) size, 1.0 - (double) y / size, 1.0));
                p.getChildren().add(r);
            }
        }
        ImageView imageView = new ImageView(snap(p, size * pixelSize, size * pixelSize));
        imageView.setFitHeight(HS_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Pane regionPane = new Pane(imageView, hsBrightness, crosshair);
        regionPane.setPrefWidth(HS_SIZE + 20);
        regionPane.setPrefHeight(HS_SIZE + 20);
        imageView.setLayoutX(10);
        imageView.setLayoutY(10);
        hsBrightness.setLayoutX(10);
        hsBrightness.setLayoutY(10);

        //regionPane.setPadding(new Insets(CROSSHAIR_SIZE / 2.0));
        hsBrightness.addEventFilter(MouseEvent.MOUSE_PRESSED, (final MouseEvent mouseEvent) -> {
            //LOGGER.log(Level.SEVERE, "Press: {0},{1}", new Object[]{mouseEvent.getX(), mouseEvent.getY()});
            mouseEvent.consume();
            updateCrosshair(mouseEvent.getX(), mouseEvent.getY());
            currentColor = xySliderToColor(
                    (int) mouseEvent.getX(),
                    (int) mouseEvent.getY(),
                    HS_SIZE
            );
            updateSpinners();
            colorSwatch.setFill(currentColor);
        });

        hsBrightness.addEventFilter(MouseEvent.MOUSE_DRAGGED, (final MouseEvent mouseEvent) -> {
            //LOGGER.log(Level.SEVERE, "Drag: {0},{1}", new Object[]{mouseEvent.getX(), mouseEvent.getY()});
            mouseEvent.consume();
            updateCrosshair(mouseEvent.getX(), mouseEvent.getY());
            currentColor = xySliderToColor(
                    (int) mouseEvent.getX(),
                    (int) mouseEvent.getY(),
                    HS_SIZE
            );
            updateSpinners();
            colorSwatch.setFill(currentColor);
        });

        return regionPane;
    }

    private Color xySliderToColor(int x, int y, int range) {
        double brightness = slider.getValue();
        if (brightness > 1.0) {
            brightness = 0.999;
        }
        if (brightness < 0.0) {
            brightness = 0.001;
        }
        return Color.hsb(xToHue(x, range), yToSat(y, range), brightness);
    }

    private Color xyToHSColor(int x, int y, int range) {
        return Color.hsb(xToHue(x, range), yToSat(y, range), 1.0);
    }

    private double xToHue(double x, int range) {
        return 360.0 * x / (double) range;
    }

    private double yToSat(double y, int range) {
        double sat = 1.0 - y / range;
        if (sat < 0.0) {
            sat = 0.0;
        }
        if (sat > 0.99) {
            sat = 0.99;
        }

        return sat;
    }

    private Node initBrightnessRamp() {
        int height = 64;
        int width = 12;

        Pane p = new Pane();
        p.setPrefSize(width, height);

        for (int y = 0; y < height; y++) {
            Rectangle r = new Rectangle(0, y, 12, 1);
            r.setFill(new Color(0.0, 0.0, 0.0, y / (double) height));
            p.getChildren().add(r);
        }
        ImageView imageView = new ImageView(snap(p, width, height));
        imageView.setFitHeight(HS_SIZE);
        imageView.setFitWidth(12);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        return imageView;
    }

    private Node initSlider() {
        HBox box = new HBox();
        box.setFillHeight(true);

        colorRamp = new BorderPane();

        colorRamp.setMaxSize(12, HS_SIZE);
        colorRamp.setMinWidth(12);
        updateColorRamp();
//        colorRamp.setBackground(new Background(new BackgroundFill(
//                currentColor,
//                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRamp.getChildren().add(initBrightnessRamp());

        slider = new Slider(0.0, 1.0, 0.7) {

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                Polygon poly = new Polygon(
                        0.0, 7.0,
                        7.0, 0.0,
                        7.0, 14.0
                );
                poly.setFill(Color.DARKGREY);
                StackPane thumb = (StackPane) lookup(".thumb");
                thumb.setBackground(Background.EMPTY);
                thumb.getChildren().clear();
                thumb.getChildren().add(poly);
                thumb.setPadding(Insets.EMPTY);
                //thumb.setPadding(new Insets(10));
                ///thumb.getChildren().add(text);

                StackPane track = (StackPane) lookup(".track");
                track.setBackground(Background.EMPTY);

            }
        };
        slider.setOrientation(Orientation.VERTICAL);
        box.getChildren().addAll(colorRamp, slider);
        HBox.setMargin(colorRamp, new Insets(10, 0, 0, 0));
        HBox.setMargin(slider, new Insets(10, 0, 10, 0));

        // Adding Listener to value property.
        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            updateHsBrightness(1.0 - newValue.doubleValue());
            currentColor = Color.hsb(
                    currentColor.getHue(),
                    currentColor.getSaturation(),
                    newValue.doubleValue(),
                    alphaSpinner.getValue() / 255.0
            );
            colorSwatch.setFill(currentColor);
            updateSpinners();
        });

        return box;
    }

    private void updateHue(double newHue) {
        currentColor = Color.hsb(
                Math.clamp(newHue, 0.0, 360.0),
                currentColor.getSaturation(),
                currentColor.getBrightness(),
                currentColor.getOpacity()
        );
    }

    private void updateSaturation(double newValue) {
        currentColor = Color.hsb(
                currentColor.getHue(),
                Math.clamp(newValue, 0.0, 1.0),
                currentColor.getBrightness(),
                currentColor.getOpacity()
        );
    }

    private void updateBrightness(double newValue) {
        currentColor = Color.hsb(
                currentColor.getHue(),
                currentColor.getSaturation(),
                Math.clamp(newValue, 0.0, 1.0),
                currentColor.getOpacity()
        );
    }

    private void updateAlpha(double newValue) {
        currentColor = Color.hsb(
                currentColor.getHue(),
                currentColor.getSaturation(),
                currentColor.getBrightness(),
                Math.clamp(newValue, 0.0, 1.0)
        );
    }

    private void updateRed(double newValue) {
        currentColor = new Color(
                Math.clamp(newValue, 0.0, 1.0),
                currentColor.getGreen(),
                currentColor.getBlue(),
                currentColor.getOpacity()
        );
    }

    private void updateGreen(double newValue) {
        currentColor = new Color(
                currentColor.getRed(),
                Math.clamp(newValue, 0.0, 1.0),
                currentColor.getBlue(),
                currentColor.getOpacity()
        );
    }

    private void updateBlue(double newValue) {
        currentColor = new Color(
                currentColor.getRed(),
                currentColor.getGreen(),
                Math.clamp(newValue, 0.0, 1.0),
                currentColor.getOpacity()
        );
    }

    private static Image snap(Pane p, int w, int h) {
        WritableImage wi = new WritableImage(w, h);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        return p.snapshot(sp, wi);
    }

    private void updateSlider(Color c) {
        slider.setValue(c.getBrightness());
        //updateHsBrightness(c.getBrightness());
        //updateSpinners();
    }

//    private void updateHsBrightness(Color c) {
//        updateHsBrightness(c.getBrightness());
//    }
    private void updateHsBrightness(double v) {
        hsBrightness.setFill(new Color(0, 0, 0, v));
    }

    private static Node initCrosshair() {
        Color COLOR = Color.BLACK;
        double CS2 = CROSSHAIR_SIZE / 2.0;
        double STROKE_W = 1.2;
        double C_RAD = 4.0;

        Group g = new Group();

        Line hLine1 = new Line(-CS2, 0, -C_RAD, 0);
        hLine1.setStroke(COLOR);
        hLine1.setStrokeWidth(STROKE_W);
        Line hLine2 = new Line(C_RAD, 0, CS2, 0);
        hLine2.setStroke(COLOR);
        hLine2.setStrokeWidth(STROKE_W);
        Line vLine1 = new Line(0, -CS2, 0, -C_RAD);
        vLine1.setStroke(COLOR);
        vLine1.setStrokeWidth(STROKE_W);
        Line vLine2 = new Line(0, C_RAD, 0, CS2);
        vLine2.setStroke(COLOR);
        vLine2.setStrokeWidth(STROKE_W);
        Circle c = new Circle(C_RAD, Color.TRANSPARENT);
        c.setStrokeWidth(STROKE_W);
        c.setStroke(COLOR);
        g.getChildren().addAll(hLine1, hLine2, vLine1, vLine2, c);

        return g;
    }

    private void updateCrosshair(double x, double y) {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x >= HS_SIZE) {
            x = HS_SIZE - 1;
        }
        if (y >= HS_SIZE) {
            y = HS_SIZE - 1;
        }

        crosshair.setLayoutX(x + 10);
        crosshair.setLayoutY(y + 10);
    }

    private void updateCrosshair(Color c) {
        double hue = c.getHue();  //  0 - 360.0
        double saturation = c.getSaturation();  // 0 - 1.0

        crosshair.setLayoutX(hue / 360.0 * HS_SIZE + 10);
        crosshair.setLayoutY((1.0 - saturation) * HS_SIZE + 10);
    }

    private Node initColorSpinners() {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(4);

        Label hueLabel = new Label("Hue:");
        Label satLabel = new Label("Sat:");
        Label valLabel = new Label("Val:");
        Label redLabel = new Label("Red:");
        Label greenLabel = new Label("Green:");
        Label blueLabel = new Label("Blue:");
        Label alphaLabel = new Label("Alpha Channel:");

        GridPane.setHalignment(hueLabel, HPos.RIGHT);
        GridPane.setHalignment(satLabel, HPos.RIGHT);
        GridPane.setHalignment(valLabel, HPos.RIGHT);
        GridPane.setHalignment(redLabel, HPos.RIGHT);
        GridPane.setHalignment(greenLabel, HPos.RIGHT);
        GridPane.setHalignment(blueLabel, HPos.RIGHT);
        GridPane.setHalignment(alphaLabel, HPos.RIGHT);

        final int SPIN_W = 72;
        hueSpinner.setPrefWidth(SPIN_W);
        satSpinner.setPrefWidth(SPIN_W);
        valSpinner.setPrefWidth(SPIN_W);
        redSpinner.setPrefWidth(SPIN_W);
        greenSpinner.setPrefWidth(SPIN_W);
        blueSpinner.setPrefWidth(SPIN_W);
        alphaSpinner.setPrefWidth(SPIN_W);

        hueSpinner.setEditable(true);
        satSpinner.setEditable(true);
        valSpinner.setEditable(true);
        redSpinner.setEditable(true);
        greenSpinner.setEditable(true);
        blueSpinner.setEditable(true);
        alphaSpinner.setEditable(true);

        g.add(hueLabel, 0, 0);
        g.add(hueSpinner, 1, 0);
        g.add(satLabel, 0, 1);
        g.add(satSpinner, 1, 1);
        g.add(valLabel, 0, 2);
        g.add(valSpinner, 1, 2);

        g.add(redLabel, 2, 0);
        g.add(redSpinner, 3, 0);
        g.add(greenLabel, 2, 1);
        g.add(greenSpinner, 3, 1);
        g.add(blueLabel, 2, 2);
        g.add(blueSpinner, 3, 2);
        g.add(alphaLabel, 0, 3, 3, 1);
        g.add(alphaSpinner, 3, 3);

        // Events
        hueSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hueSpinner.increment(0); // won't change value, but will commit editor
                updateHue(hueSpinner.getValue());
                updateCrosshair(currentColor);
                colorSwatch.setFill(currentColor);
                updateColorRamp();
            }
        });
        hueSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateHue(hueSpinner.getValue());
            updateCrosshair(currentColor);
            colorSwatch.setFill(currentColor);
            updateColorRamp();
        });

        satSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                satSpinner.increment(0); // won't change value, but will commit editor
                updateSaturation(satSpinner.getValue() / 255.0);
                updateCrosshair(currentColor);
                colorSwatch.setFill(currentColor);
                updateColorRamp();
            }
        });
        satSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateSaturation(satSpinner.getValue() / 255.0);
            updateCrosshair(currentColor);
            updateColorRamp();
            colorSwatch.setFill(currentColor);
        });

        satSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                satSpinner.increment(0); // won't change value, but will commit editor
                updateSaturation(satSpinner.getValue() / 255.0);
                updateCrosshair(currentColor);
                updateColorRamp();
                colorSwatch.setFill(currentColor);
            }
        });
        satSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateSaturation(satSpinner.getValue() / 255.0);
            updateCrosshair(currentColor);
            updateColorRamp();
            colorSwatch.setFill(currentColor);
        });

        valSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                valSpinner.increment(0); // won't change value, but will commit editor
                updateBrightness(valSpinner.getValue() / 255.0);
                updateSlider(currentColor);
                updateHsBrightness(1.0 - currentColor.getBrightness());
                colorSwatch.setFill(currentColor);
                updateColorRamp();
            }
        });
        valSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateBrightness(valSpinner.getValue() / 255.0);
            updateSlider(currentColor);
            updateHsBrightness(1.0 - currentColor.getBrightness());
            colorSwatch.setFill(currentColor);
            updateColorRamp();
        });

        redSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                redSpinner.increment(0); // won't change value, but will commit editor
                updateRed(redSpinner.getValue() / 255.0);
                updateCrosshair(currentColor);
                updateSlider(currentColor);
                updateHsBrightness(1.0 - currentColor.getBrightness());
                colorSwatch.setFill(currentColor);
                updateColorRamp();
            }
        });
        redSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateRed(redSpinner.getValue() / 255.0);
            updateCrosshair(currentColor);
            updateSlider(currentColor);
            updateHsBrightness(1.0 - currentColor.getBrightness());
            updateColorRamp();
            colorSwatch.setFill(currentColor);
        });

        greenSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                greenSpinner.increment(0); // won't change value, but will commit editor
                updateGreen(greenSpinner.getValue() / 255.0);
                updateCrosshair(currentColor);
                updateSlider(currentColor);
                updateHsBrightness(1.0 - currentColor.getBrightness());
                updateColorRamp();
                colorSwatch.setFill(currentColor);
            }
        });
        greenSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateGreen(greenSpinner.getValue() / 255.0);
            updateCrosshair(currentColor);
            updateSlider(currentColor);
            updateHsBrightness(1.0 - currentColor.getBrightness());
            updateColorRamp();
            colorSwatch.setFill(currentColor);
        });

        blueSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                blueSpinner.increment(0); // won't change value, but will commit editor
                updateBlue(blueSpinner.getValue() / 255.0);
                updateCrosshair(currentColor);
                updateSlider(currentColor);
                updateHsBrightness(1.0 - currentColor.getBrightness());
                updateColorRamp();
                colorSwatch.setFill(currentColor);
            }
        });
        blueSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateBlue(blueSpinner.getValue() / 255.0);
            updateCrosshair(currentColor);
            updateSlider(currentColor);
            updateHsBrightness(1.0 - currentColor.getBrightness());
            updateColorRamp();
            colorSwatch.setFill(currentColor);
        });

        alphaSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                //LOGGER.log(Level.SEVERE, "Alpha focus: {0} ==> {1}", new Object[]{oldValue, newValue});
                alphaSpinner.increment(0); // won't change value, but will commit editor
                updateAlpha(alphaSpinner.getValue() / 255.0);
                colorSwatch.setFill(currentColor);
            }
        });
        alphaSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            //LOGGER.log(Level.SEVERE, "Alpha value: {0} ==> {1}", new Object[]{oldValue, newValue});
            updateAlpha(alphaSpinner.getValue() / 255.0);
            colorSwatch.setFill(currentColor);
        });

        return g;
    }

    private Node initColorSwatch() {
        //colorSwatch = new Rectangle(64, 64);
        colorSwatch.setFill(currentColor);
        return colorSwatch;
    }

    private void updateColorRamp() {
        // Ramp is a solid color with a monochrome brightness ramp
        // overlayed on it. So we want the full bright HS color only here.
        Color hs = Color.hsb(currentColor.getHue(), currentColor.getSaturation(), 1.0);
        colorRamp.setBackground(new Background(new BackgroundFill(
                hs,
                CornerRadii.EMPTY, Insets.EMPTY
        )));
    }

    @Override
    public final void swatchIndexChanged(int newIndex) {
        currentColor = ColorUtils.getColor(palette.getHex(newIndex));
        updateSpinners();
        updateColorRamp();
//        colorRamp.setBackground(new Background(new BackgroundFill(
//                currentColor,
//                CornerRadii.EMPTY, Insets.EMPTY
//        )));
        updateSlider(currentColor);
        updateCrosshair(currentColor);
        updateHsBrightness(1.0 - currentColor.getBrightness());
        colorSwatch.setFill(currentColor);
    }

    private void updateSpinners() {
        hueSpinner.getValueFactory().setValue((int) (currentColor.getHue()));
        satSpinner.getValueFactory().setValue((int) (currentColor.getSaturation() * 255));
        int bright = (int) (currentColor.getBrightness() * 255.0);

        // JavaFX Bug???? JavaFX Version 21 (04/2024)
        // For reasons I don't know why, when valSpinner has it's value updated,
        // it changes currentColor Opacity to 0x7F, but that should not be possible
        // as we are using a separate int to affect the spinner value.
        //
        // Workaround is to back up currentColor and copy it back after the
        // spinner value is set.
        //
        // Backup color
        Color c = new Color(currentColor.getRed(), currentColor.getGreen(),
                currentColor.getBlue(), currentColor.getOpacity());
        valSpinner.getValueFactory().setValue((int) (bright));

        currentColor = c;

        redSpinner.getValueFactory().setValue((int) (currentColor.getRed() * 255));
        greenSpinner.getValueFactory().setValue((int) (currentColor.getGreen() * 255));
        blueSpinner.getValueFactory().setValue((int) (currentColor.getBlue() * 255));

        LOGGER.log(Level.SEVERE, "alpha: {0}", currentColor.getOpacity());
        alphaSpinner.getValueFactory().setValue((int) (currentColor.getOpacity() * 255));
    }
}
