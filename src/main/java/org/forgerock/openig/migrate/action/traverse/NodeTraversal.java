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

package org.forgerock.openig.migrate.action.traverse;

import static com.fasterxml.jackson.core.JsonPointer.*;
import static java.lang.String.format;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by guillaume on 24/11/14.
 */
public class NodeTraversal {

    public void traverse(JsonNode node, NodeVisitor visitor) {
        if (node == null) {
            return;
        }
        traverse(node, compile(null), visitor);
    }

    private void traverse(JsonNode node, JsonPointer pointer, NodeVisitor visitor) {
        if (node.isArray()) {
            int index = 0;
            for (JsonNode child : node) {
                traverse(child,
                         compile(format("%s/%d", pointer.toString(), index++)),
                         visitor);
            }
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                traverse(entry.getValue(),
                         compile(format("%s/%s", pointer.toString(), entry.getKey())),
                         visitor);

            }
        }
        visitor.visitNode(node, pointer);
    }
}
