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
public class Rotation {

    public static final boolean CONSTRAINED = true;
    public static final boolean UNCONSTRAINED = false;
    
    private double value = 0.0;
    private boolean constrained = false;
    private boolean spin = false;
    private boolean allowSpin = false;
    private boolean mirror = false;
    private boolean allowMirror = false;

    public Rotation() {}

    public Rotation( boolean constrained ) {
        this.constrained = constrained;
    }
    
    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        // Range checking. Round to nearest 90 degree angle.
        if (constrained) {
            if (value >= 45.0 && value < 135.0) {
                this.value = 90;
            } else if (value >= 135.0 && value < 225.0) {
                this.value = 180;
            } else if (value >= 225.0 && value < 315.0) {
                this.value = 270;
            } else {
                this.value = 0;
            }
        } else {
            this.value = value;
        }
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
        this.spin = spin;
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
        this.mirror = mirror;
    }

    /**
     * @return the allowSpin
     */
    public boolean isAllowSpin() {
        return allowSpin;
    }

    /**
     * @param allowSpin the allowSpin to set
     */
    public void setAllowSpin(boolean allowSpin) {
        this.allowSpin = allowSpin;
    }

    /**
     * @return the allowMirror
     */
    public boolean isAllowMirror() {
        return allowMirror;
    }

    /**
     * @param allowMirror the allowMirror to set
     */
    public void setAllowMirror(boolean allowMirror) {
        this.allowMirror = allowMirror;
    }

}
