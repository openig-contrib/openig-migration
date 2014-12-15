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

import org.forgerock.openig.migrate.action.model.RouteModel;
import org.forgerock.openig.migrate.meta.Before;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Created by guillaume on 15/12/14.
 */
@Before(InlineDeclarationsAction.class)
public class ObjectTypeRenameAction extends AbstractRouteModelAction {

    private final String original;
    private final String updated;

    public ObjectTypeRenameAction(final String original, final String updated) {
        this.original = original;
        this.updated = updated;
    }

    @Override
    protected ObjectNode doMigrate(final RouteModel route, final ObjectNode configuration) {
        route.getObjects()
             .stream()
             .filter(model -> original.equals(model.getType().getName()))
             .forEach(model -> model.getNode().replace("type", new TextNode(updated)));
        return configuration;
    }
}
