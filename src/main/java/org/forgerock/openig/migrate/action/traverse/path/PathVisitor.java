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

package org.forgerock.openig.migrate.action.traverse.path;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

import org.forgerock.openig.migrate.action.traverse.NodeVisitor;

/**
 * Created by guillaume on 11/12/14.
 */
public class PathVisitor implements NodeVisitor {

    private final Pattern pattern;
    private final List<Match> matches = new ArrayList<>();

    public PathVisitor(final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void visitNode(final JsonNode node, final JsonPointer pointer) {
        if (pattern.matcher(pointer.toString()).matches()) {
            matches.add(new Match(node, pointer));
        }
    }

    public List<Match> getMatches() {
        return matches;
    }

    public static class Match {
        private final JsonNode node;
        private final JsonPointer pointer;

        public Match(final JsonNode node, final JsonPointer pointer) {
            this.node = node;
            this.pointer = pointer;
        }

        public JsonNode getNode() {
            return node;
        }

        public JsonPointer getPointer() {
            return pointer;
        }
    }

}
