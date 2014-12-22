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

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class MigrationToolTest {

    @DataProvider
    public Object[][] resources() throws Exception {

        File resources = new File("src/test/resources");
        File verified = new File(resources, "verified");

        File[] files = resources.listFiles(new JsonFilter());
        Object[][] result = new Object[files.length][2];

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            result[i][0] = file;
            File expected = new File(verified, file.getName());
            assertThat(expected).exists();
            result[i][1] = expected;
        }

        return result;
    }

    @DataProvider
    public Object[][] onlyOneResource() throws Exception {
        String filename = "simple-login-form.json";
        File resources = new File("src/test/resources");
        File verified = new File(resources, "verified");
        return new Object[][] {
                { new File(resources, filename), new File(verified, filename) }
        };
    }

    @Test(dataProvider = "resources")
    public void shouldMigrateOpenIG30Configurations(final File source, final File expected) throws Exception {
        Main main = new Main();
        main.setSources(Collections.singletonList(source.getPath()));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        main.execute(stream);

        assertThatJson(new String(stream.toByteArray()))
                .isEqualTo(toString(expected));

    }

    private static String toString(final File expected) throws IOException {
        return new String(Files.readAllBytes(expected.toPath()));
    }

    private static class JsonFilter implements FileFilter {
        @Override
        public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".json");
        }
    }
}
