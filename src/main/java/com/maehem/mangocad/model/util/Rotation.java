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
package com.maehem.mangocad.model.util;

import com.maehem.mangocad.model.element.property.RealValue;

/**
 * <pre>
 * Rotation -- rotation of an object; allowed range: [MSR]0..359.9
 *
 * May have 90 degree constraint.
 *
 * Autodesk packed 'mirror' and 'spin' in here without updating the XML spec.
 *
 * </pre>
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Rotation extends RealValue {

    private static final double MIN = 0.0;
    private static final double MAX = 360.0;

    public static final boolean MIRROR_NOT_ALLOWED = false;
    public static final boolean MIRROR_ALLOWED = true;
    public static final boolean SPIN_NOT_ALLOWED = false;
    public static final boolean SPIN_ALLOWED = true;
    public static final boolean CONSTRAINED = true;
    //public static final boolean UNCONSTRAINED = false;

    public static final String COMMAND_SETTING = "orientation";
    public static final String XML_SETTING = "rot";

    //private RealValue valueProperty = new RealValue(0);
    private boolean constrained = false;
    private boolean spin = false;
    private boolean mirror = false;

    private boolean allowSpin = !SPIN_ALLOWED;
    private boolean allowMirror = MIRROR_ALLOWED;

    private int prec = 1;

    public Rotation() {
        super(0.0, MIN, MAX);
    }

    public Rotation(boolean constrained) {
        this();
        this.constrained = constrained;
    }

    /**
     * @return the value
     */
    public void setValue(String strValue) {
        if (strValue.startsWith("MR")) {
            // Mirror
            setMirror(true);
            set(Double.parseDouble(strValue.substring(2)));
        } else if (strValue.startsWith("SR")) {
            // Spin
            setSpin(true);
            set(Double.parseDouble(strValue.substring(2)));
        } else if (strValue.startsWith("SMR")) {
            // Spin
            setSpin(true);
            setMirror(true);
            set(Double.parseDouble(strValue.substring(3)));
        } else {
            // Normal
            set(Double.parseDouble(strValue.substring(1)));
        }
    }

    /**
     * @param value the value to set
     */
    @Override
    public void set(double value) {
        value %= MAX;// Over-range limiting.
        if (value < 0) {
            value += MAX;
        }
        if (get() != value) {
            //double oldValue = get();

            if (constrained) {  // Round to nearest 90 degree angle.
                if (value >= 45.0 && value < 135.0) {
                    super.set(90.0);
                } else if (value >= 135.0 && value < 225.0) {
                    super.set(180.0);
                } else if (value >= 225.0 && value < 315.0) {
                    super.set(270.0);
                } else {
                    super.set(0.0);
                }
            } else {
                super.set(value);
            }
        }
        notifyValueChange();
    }

    /**
     * @return the constrained
     */
    public boolean isConstrained() {
        return constrained;
    }

    /**
     * @param constrained the constrained to set
     */
    public void setConstrained(boolean constrained) {
        this.constrained = constrained;
    }

    /**
     * @return the spin
     */
    public boolean isSpin() {
        return spin;
    }

    /**
     * @param spin the spin to set
     */
    public void setSpin(boolean spin) {
        if (isSpinAllowed() && isSpin() != spin) {
            this.spin = spin;
            notifyValueChange();
        } else {
            this.spin = false; // Ensure the rule.
        }
    }

    /**
     * @return the mirror
     */
    public boolean isMirror() {
        return mirror;
    }

    /**
     * @param mirror the mirror to set
     */
    public void setMirror(boolean mirror) {
        if (isMirrorAllowed() && isMirror() != mirror) {
            this.mirror = mirror;
            notifyValueChange();
        } else {
            this.mirror = false; // Ensure the rule.
        }
    }

    /**
     * @return the allowSpin
     */
    public boolean isSpinAllowed() {
        return allowSpin;
    }

    /**
     * @param allowSpin the allowSpin to set
     */
    public void setAllowSpin(boolean allowSpin) {
        this.allowSpin = allowSpin;
    }

    public boolean isSpun() {
        return !isSpin() && (get() > 90.0 && get() <= 270.0);
    }

    /**
     * @return the allowMirror
     */
    public boolean isMirrorAllowed() {
        return allowMirror;
    }

    /**
     * @param allowMirror the allowMirror to set
     */
    public void setAllowMirror(boolean allowMirror) {
        this.allowMirror = allowMirror;
    }

    static public Rotation copyValues(Rotation old, Rotation copy) {
        copy.set(old.get());
        copy.setMirror(old.isMirror());
        copy.setAllowMirror(old.isMirrorAllowed());
        copy.setSpin(old.isSpin());
        copy.setAllowSpin(old.isSpinAllowed());
        copy.setConstrained(old.isConstrained());

        return copy;
    }

    @Override
    public String toString() {
        return (isSpin() ? "S" : "") + (isMirror() ? "M" : "") + "R" + getPrecise(prec);
    }

    public String xmlValue() {
        return (isSpin() ? "S" : "") + (isMirror() ? "M" : "") + "R" + getPrecise(prec);
    }
}
