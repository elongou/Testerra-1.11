/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
 package eu.tsystems.mms.tic.testframework.report.utils;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import java.lang.reflect.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FailsAnnotationFilter
 * <p>
 * Date: 15.05.2017
 * Time: 10:49
 *
 * @author erku
 */

/**
 * @deprecated Use {@link Fails#validator()} instead
 */
public class FailsAnnotationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailsAnnotationFilter.class);

    /**
     * Divider for key value pairs
     */
    private static final String DIVIDER = "=";

    // Example: @Fails(validFor="test.foo.var=3;")

    public static boolean isFailsAnnotationValid(final String[] validFor) {

        // always valid, if String is empty
        if (validFor == null || validFor.length == 0) {
            return true;
        }

        // Split on divider
        for (final String statement : validFor) {

            final String[] keyValueSplit = statement.split(DIVIDER);

            if (keyValueSplit.length == 2) {

                final String key = keyValueSplit[0];
                final String expectedPropertyValue = keyValueSplit[1].toLowerCase();
                final String currentPropertyValue = PropertyManager.getProperty(key, "").toLowerCase();

                if (!currentPropertyValue.equals(expectedPropertyValue)) {
                    LOGGER.info("@Fails validFor is FALSE for: " + statement);
                    return false;
                }
                else {
                    LOGGER.info("@Fails validFor is TRUE for: " + statement);
                }
            }
            else {
                LOGGER.error("@Fails validFor has invalid entry: " + statement);
            }
        }

        return true;
    }
}
