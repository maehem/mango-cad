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
package com.maehem.mangocad.controller.command;

import com.maehem.mangocad.controller.Memento;
import com.maehem.mangocad.controller.command.exception.CommandException;
import com.maehem.mangocad.controller.command.exception.PinCommandException;
import static com.maehem.mangocad.view.ControlPanel.LOGGER;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public abstract class Command {

    protected Object obj;

    protected String args;

    protected Memento memento;

    public final String getArgs() {
        return args;
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract void execute();

    public abstract void unExecute();

    protected static String extractPinName(List<String> argList) throws CommandException {
        String pinName = null;
        // Find 'NAME' item in list or fail. Could be anywhere.
        ListIterator<String> argIter = argList.listIterator();
        while (argIter.hasNext()) {
            String arg = argIter.next();
            if (arg.startsWith("'") && arg.endsWith("'")) {
                pinName = arg.replaceAll("'", "");
                argIter.remove();
                break;
            }
        }

        return pinName;
    }

    protected static String extractCoords(List<String> argList) throws CommandException {
        // Find any args that are coordinates ==>  (xxx ... yyy)
        // Could be anywhere.
        String coordString = null;

        ListIterator<String> iter = argList.listIterator();

        boolean coordHasEnd = false;
        while (iter.hasNext()) {
            String arg = iter.next();
            if (coordString != null) {
                if (arg.endsWith(")")) {
                    coordString += " " + arg.substring(0, arg.lastIndexOf(")"));
                    LOGGER.log(Level.SEVERE, "Found final coordinate: ({0})", coordString);
                    iter.remove();
                    coordHasEnd = true;
                    break;
                } else {
                    coordString += " " + arg;
                    LOGGER.log(Level.SEVERE, "Found coordinate chunk: {0}", arg);
                    iter.remove();
                }
            } else if (arg.startsWith("(")) {
                coordString = arg.substring(1);
                LOGGER.log(Level.SEVERE, "Found start of coord: {0}", arg);
                if (arg.endsWith(")")) {
                    coordString = coordString.substring(0, coordString.lastIndexOf(")"));
                    coordHasEnd = true;
                    iter.remove();
                    break;
                }
                iter.remove();
            } // else ignore the arg
        }
        if (coordString != null && !coordHasEnd) {
            throw new PinCommandException("Coordinate had no end braces!");
        }

        return coordString;
    }
}
