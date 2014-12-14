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

import org.forgerock.openig.migrate.Action;
import org.forgerock.openig.migrate.meta.After;
import org.forgerock.openig.migrate.meta.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 12/12/14.
 */
@After(HeapObjectsSimplificationAction.class)
@Before(InlineDeclarationsAction.class)
public class EmptyConfigRemovalAction implements Action {
    @Override
    public ObjectNode migrate(final ObjectNode configuration) {
        ArrayNode heap = (ArrayNode) configuration.get("heap");
        for (JsonNode node : heap) {
            ObjectNode object = (ObjectNode) node;
            JsonNode config = object.get("config");
            if ((config != null)
                    && config.isContainerNode()
                    && !config.iterator().hasNext()) {
                object.remove("config");
            }
        }
        return configuration;
    }
}
