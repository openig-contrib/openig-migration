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

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 09/12/14.
 */
public class RouteModel {
    private final ObjectNode node;
    private final List<ObjectModel> objects;

    public RouteModel(final ObjectNode node, final List<ObjectModel> objects) {
        this.node = node;
        this.objects = objects;
    }

    public ObjectNode getNode() {
        return node;
    }

    public List<ObjectModel> getObjects() {
        return objects;
    }

    public ObjectModel findObject(final String name) {
        for (ObjectModel object : objects) {
            if (name.equals(object.getName())) {
                return object;
            }
        }
        // Either an invalid reference or pointing to a parent defined heap object
        return null;
    }
}
