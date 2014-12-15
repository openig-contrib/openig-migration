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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.forgerock.openig.migrate.action.model.ObjectModel;
import org.forgerock.openig.migrate.action.model.Reference;
import org.forgerock.openig.migrate.action.model.RouteModel;
import org.forgerock.openig.migrate.action.traverse.NodeTraversal;
import org.forgerock.openig.migrate.action.traverse.path.PathVisitor;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 23/11/14.
 */
public class InlineDeclarationsAction extends AbstractRouteModelAction {

    @Override
    protected ObjectNode doMigrate(final RouteModel route, final ObjectNode configuration) {

        ArrayNode heap = (ArrayNode) configuration.get("heap");

        // Creates references
        for (ObjectModel source : route.getObjects()) {
            for (String pointer : source.getType().getPatterns()) {

                PathVisitor visitor = new PathVisitor(Pattern.compile(pointer));
                new NodeTraversal().traverse(source.getConfig(), visitor);

                for (PathVisitor.Match match : visitor.getMatches()) {
                    JsonNode pointed = match.getNode();

                    if (pointed.isArray()) {
                        int i = 0;
                        for (JsonNode item : pointed) {
                            bindArrayReference(source, route.findObject(item.asText()), match.getPointer(), i++);
                        }
                    } else if (pointed.isTextual()) {
                        bindReference(source, route.findObject(pointed.asText()), match.getPointer());
                    }
                }
            }
        }

        // Inline references as much as possible, starting from the leafs
        // TODO Consider Moving all candidates at once, this is probably not useful to process them step by step
        List<ObjectModel> candidates = findCandidates(route);
        while (!candidates.isEmpty()) {

            for (ObjectModel candidate : candidates) {
                Reference ref = candidate.getReferencedBy().get(0);
                if (ref.isArrayRef()) {
                    ArrayNode array = (ArrayNode) ref.getSource().getConfig().at(ref.getPointer());
                    array.set(ref.getIndex(), ref.getTarget().getNode());
                } else {
                    // We'll just replace in place the value
                    ObjectNode parent = (ObjectNode) ref.getSource().getConfig().at(parentOf(ref.getPointer()));
                    parent.replace(lastSegmentOf(ref.getPointer()), ref.getTarget().getNode());
                }
                ref.getSource().getReferencesTo().remove(ref);
                ref.getTarget().getReferencedBy().remove(ref);
                ref.getTarget().markInlined();
            }

            candidates = findCandidates(route);
        }

        // Remove inlined references, Java 8 style
        Iterator<ObjectModel> iterator = route.getObjects()
                                              .stream()
                                              .filter(inlined())
                                              .sorted(byReverseIndex())
                                              .iterator();
        while (iterator.hasNext()) {
            ObjectModel next = iterator.next();
            heap.remove(next.getIndex());
        }

        return configuration;
    }

    private String lastSegmentOf(final JsonPointer pointer) {
        String ser = pointer.toString();
        int i = ser.lastIndexOf('/');
        return ser.substring(i + 1);
    }

    private JsonPointer parentOf(final JsonPointer pointer) {
        String ser = pointer.toString();
        int i = ser.lastIndexOf('/');
        return JsonPointer.compile(ser.substring(0, i));
    }

    private Comparator<? super ObjectModel> byReverseIndex() {
        return new Comparator<ObjectModel>() {
            @Override
            public int compare(final ObjectModel o1, final ObjectModel o2) {
                Integer i1 = o1.getIndex();
                Integer i2 = o2.getIndex();
                return -(i1.compareTo(i2));
            }
        };
    }

    private Predicate<? super ObjectModel> inlined() {
        return new Predicate<ObjectModel>() {
            @Override
            public boolean test(final ObjectModel objectModel) {
                return objectModel.isInlined();
            }
        };
    }

    private void bindReference(ObjectModel source, ObjectModel target, JsonPointer pointer) {
        if (target != null) {
            Reference ref = new Reference(source, target, pointer);
            source.addReferenceTo(ref);
            target.addReferencedBy(ref);
        }
    }

    private void bindArrayReference(ObjectModel source, ObjectModel target, JsonPointer pointer, final int i) {
        if (target != null) {
            Reference ref = new Reference(source, target, pointer, i);
            source.addReferenceTo(ref);
            target.addReferencedBy(ref);
        }
    }

    private List<ObjectModel> findCandidates(final RouteModel route) {
        return route.getObjects()
                    .stream()
                    .filter(this::isCandidate)
                    .collect(Collectors.toList());
    }

    private boolean isCandidate(final ObjectModel model) {
        // candidates are single-referenced nodes...
        if (!isReferencedByOnlyOne(model)) {
            return false;
        }
        // .. with references that cannot be inlined (not candidates themselves)
        for (Reference reference : model.getReferencesTo()) {
            if (isCandidate(reference.getTarget())) {
                return false;
            }
        }
        return true;
    }

    private boolean isReferencedByOnlyOne(final ObjectModel model) {
        return model.getReferencedBy().size() == 1;
    }
}
