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

package org.forgerock.openig.migrate.action;

import java.util.HashMap;
import java.util.Map;

import org.forgerock.openig.migrate.Action;
import org.forgerock.openig.migrate.action.model.ObjectModel;
import org.forgerock.openig.migrate.action.model.RouteModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 15/12/14.
 */
public class DeprecatedAttributesAction extends AbstractRouteModelAction {

    private Map<String, Map<String, String>> types = new HashMap<>();

    public void addDeprecatedReplacement(String type, Map<String, String> attributes) {
        types.put(type, attributes);
    }

    @Override
    protected ObjectNode doMigrate(final RouteModel route, final ObjectNode configuration) {
        for (ObjectModel model : route.getObjects()) {
            ObjectNode config = model.getConfig();
            if (config == null) {
                // Move to the next model if there is no config to process
                continue;
            }

            // Process config node if there are deprecated attributes
            Map<String, String> attributes = types.get(model.getType().getName());
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    if (config.has(entry.getKey())) {
                        // Found deprecated node
                        config.set(entry.getValue(), config.remove(entry.getKey()));
                    }
                }
            }
        }
        return configuration;
    }
}
