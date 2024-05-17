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

import com.maehem.mangocad.model.Element;
import com.maehem.mangocad.model.ElementSelectable;
import com.maehem.mangocad.model.element.enums.ElementRectangleField;
import com.maehem.mangocad.model.element.enums.RotationField;
import com.maehem.mangocad.model.util.Rotation;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub)
 */
public class ElementRectangle extends Element implements ElementSelectable {
    public static final String ELEMENT_NAME = "rectangle";

    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private final Rotation rotation = new Rotation();

    private boolean selected = false;
    private int selectedCorner = 0;
    private final double[] snapshot = {0, 0, 0, 0};

    @Override
    public String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double x1) {
        if (this.x1 != x1) {
            double oldVal = this.x1;
            this.x1 = x1;
            notifyListeners(ElementRectangleField.X1, oldVal, this.x1);
        }
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double y1) {
        if (this.y1 != y1) {
            double oldVal = this.y1;
            this.y1 = y1;
            notifyListeners(ElementRectangleField.Y1, oldVal, this.y1);
        }
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return x2;
    }

    /**
     * @param x2 the x2 to set
     */
    public void setX2(double x2) {
        if (this.x2 != x2) {
            double oldVal = this.x2;
            this.x2 = x2;
            notifyListeners(ElementRectangleField.X2, oldVal, this.x2);
        }
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return y2;
    }

    /**
     * @param y2 the y2 to set
     */
    public void setY2(double y2) {
        if (this.y2 != y2) {
            double oldVal = this.y2;
            this.y2 = y2;
            notifyListeners(ElementRectangleField.Y2, oldVal, this.y2);
        }
    }

    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the rotation
     */
    public double getRot() {
        return rotation.getValue();
    }

    public void setAllXY(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        notifyListeners(ElementRectangleField.ALL_XY, null, null);
    }

    /**
     * Furnish a list of the polygon points with consideration for the current
     * rotation/mirror of the shape.
     *
     * @return
     */
    public double[] getPoints() {
        double points[] = {0, 0, 0, 0, 0, 0, 0, 0};
        double w = Math.abs(x1 - x2);
        double h = Math.abs(y1 - y2);

        switch ((int) getRot()) {
            case 90 -> {
                points[0] = getX1() + w / 2 - h / 2;
                points[1] = getY1() + h / 2 + w / 2;

                points[2] = points[0];
                points[3] = points[1] - w;

                points[4] = points[0] + h;
                points[5] = points[1] - w;

                points[6] = points[0] + h;
                points[7] = points[1];
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
                points[0] = getX1() + w / 2 + h / 2;
                points[1] = getY1() + h / 2 - w / 2;

                points[2] = points[0];
                points[3] = points[1] + w;

                points[4] = points[0] + h;
                points[5] = points[1] + w;

                points[6] = points[0] + h;
                points[7] = points[1];
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
        LOGGER.log(Level.SEVERE, "Points[{8}]: {0},{1}  {2},{3}  {4},{5}  {6},{7}",
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
     * @param value the rotation to set
     */
    public void setRot(double value) {
        if (getRot() != value) {
            double oldValue = getRot();
            this.rotation.setValue(value);
            notifyListeners(RotationField.VALUE, oldValue, getRot());
        }
    }

    @Override
    public void createSnapshot() {
        snapshot[0] = getX1();
        snapshot[1] = getY1();
        snapshot[2] = getX2();
        snapshot[3] = getY2();
    }

    @Override
    public void restoreSnapshot() {
        setAllXY(snapshot[0], snapshot[1], snapshot[2], snapshot[3]);
    }

    @Override
    public double[] getSnapshot() {
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
            notifyListeners(ElementRectangleField.SELECTED, oldValue, this.selected);
        }
    }
}
