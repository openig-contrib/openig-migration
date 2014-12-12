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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 10/12/14.
 */
public class ModelBuilder {

    private TypeRegistry registry = TypeRegistry.build();

    public RouteModel buildRoute(ObjectNode node) {
        List<ObjectModel> objects = new ArrayList<>();
        int index = 0;
        for (JsonNode value : node.get("heap")) {
            objects.add(buildObject((ObjectNode) value, index++));
        }
        return new RouteModel(node, objects);
    }

    public ObjectModel buildObject(ObjectNode node, final int index) {
        String name = node.get("name").asText();
        String typeName = node.get("type").asText();
        ObjectType type = registry.findType(typeName);
        return new ObjectModel(node, index, name, type);
    }
}
