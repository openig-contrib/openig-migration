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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.forgerock.openig.migrate.action.model.types.UnknownObjectType;

/**
 * Created by guillaume on 10/12/14.
 */
public class TypeRegistry {
    private final Map<String, ObjectType> types = new HashMap<String, ObjectType>();

    public void registerType(ObjectType type) {
        types.put(type.getName(), type);
    }

    public ObjectType findType(String name) {
        ObjectType type = types.get(name);
        if (type == null) {
            type = new UnknownObjectType(name);
        }
        return type;
    }

    public static TypeRegistry build() {
        TypeRegistry registry = new TypeRegistry();
        ServiceLoader<ObjectType> loader = ServiceLoader.load(ObjectType.class);
        for (ObjectType type : loader) {
            registry.registerType(type);
        }
        return registry;
    }
}
