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

package org.forgerock.openig.migrate;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.forgerock.openig.migrate.action.DeprecatedAttributesAction;
import org.forgerock.openig.migrate.action.EmptyConfigRemovalAction;
import org.forgerock.openig.migrate.action.HandlerObjectAction;
import org.forgerock.openig.migrate.action.HeapObjectsSimplificationAction;
import org.forgerock.openig.migrate.action.InlineDeclarationsAction;
import org.forgerock.openig.migrate.action.ObjectTypeRenameAction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by guillaume on 23/11/14.
 */
public class Main {

    public static void main(String... args) throws Exception {
        Main main = new Main();
        new JCommander(main, args);
        main.execute(System.out);
    }

    @Parameter
    private List<String> sources = new ArrayList<String>();

    void execute(OutputStream stream) throws Exception {

        // Pre-parse the original files with JSON-Simple parser (which is more lenient than Jackson's one)
        File source = new File(sources.get(0));
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(new FileReader(source));

        // Then serialize again the structure (should clean up the JSON)
        StringWriter writer = new StringWriter();
        object.writeJSONString(writer);

        // Load migration actions to apply in order
        List<Action> actions = loadActions();

        // Parse the cleaned-up content with Jackson this time
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = (ObjectNode) mapper.readTree(writer.toString());

        // Apply migration actions
        for (Action action : actions) {
            node = action.migrate(node);
        }

        // Serialize the migrated content on the given stream (with pretty printer)
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultPrettyPrinter.Lf2SpacesIndenter.instance);
        mapper.writeTree(factory.createGenerator(stream).setPrettyPrinter(prettyPrinter),
                         node);
    }

    private List<Action> loadActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(new HeapObjectsSimplificationAction());
        actions.add(new EmptyConfigRemovalAction());
        actions.add(new ObjectTypeRenameAction("RedirectFilter", "LocationHeaderFilter"));
        actions.add(buildDeprecatedAttributes());
        actions.add(new InlineDeclarationsAction());
        actions.add(new HandlerObjectAction());
        return actions;
    }

    private Action buildDeprecatedAttributes() {
        DeprecatedAttributesAction action = new DeprecatedAttributesAction();
        Map<String, String> rs = new HashMap<>();
        rs.put("enforceHttps", "requireHttps");
        rs.put("httpHandler", "providerHandler");
        rs.put("requiredScopes", "scopes");
        action.addDeprecatedReplacement("OAuth2ResourceServerFilter", rs);
        return action;
    }

    public void setSources(final List<String> sources) {
        this.sources = sources;
    }
}
