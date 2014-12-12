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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 10/12/14.
 */
public class ObjectModel {
    private final ObjectNode node;
    private final int index;
    private final String name;
    private final ObjectType type;
    private List<Reference> referencesTo = new ArrayList<>();
    private List<Reference> referencedBy = new ArrayList<>();
    private boolean inlined = false;

    public ObjectModel(final ObjectNode node, final int index, final String name, final ObjectType type) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public ObjectType getType() {
        return type;
    }

    public ObjectNode getNode() {
        return node;
    }

    public int getIndex() {
        return index;
    }

    public boolean isInlined() {
        return inlined;
    }

    public ObjectNode getConfig() {
        return (ObjectNode) node.get("config");
    }

    public void addReferenceTo(final Reference target) {
        referencesTo.add(target);
    }

    public void addReferencedBy(final Reference source) {
        referencedBy.add(source);
    }

    public List<Reference> getReferencesTo() {
        return referencesTo;
    }

    public List<Reference> getReferencedBy() {
        return referencedBy;
    }


    @Override
    public String toString() {
        return format("%s/%s in:%d out:%d", name, type.getName(), referencedBy.size(), referencesTo.size());
    }

    public void markInlined() {
        inlined = true;
    }
}
