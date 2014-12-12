/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.openig.migrate.action.model;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonPointer;

/**
 * Created by guillaume on 10/12/14.
 */
public class Reference {
    private final ObjectModel source;
    private final ObjectModel target;
    private final JsonPointer pointer;
    private final int index;

    public Reference(final ObjectModel source, final ObjectModel target, final JsonPointer pointer) {
        this(source, target, pointer, -1);
    }

    public Reference(final ObjectModel source, final ObjectModel target, final JsonPointer pointer, int index) {
        this.source = source;
        this.target = target;
        this.pointer = pointer;
        this.index = index;
    }

    public ObjectModel getSource() {
        return source;
    }

    public ObjectModel getTarget() {
        return target;
    }

    public JsonPointer getPointer() {
        return pointer;
    }

    public int getIndex() {
        return index;
    }

    public boolean isArrayRef() {
        return index != -1;
    }

    @Override
    public String toString() {
        return format("Reference[ %s -> %s ]", source.getName(), target.getName());
    }
}
