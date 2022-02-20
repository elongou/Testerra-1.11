/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.utils;

import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import java.util.Date;
import org.testng.ITestNGMethod;

/**
 * General formatter for everything
 * @author Mike Reiche
 */
public interface Formatter {
    default String cutString(String string, int maxLength) {
        return cutString(string, maxLength, "...");
    }
    default String cutString(String string, int maxLength, String replacement) {
        if (string.length()>maxLength) {
            int first = Math.round((maxLength-replacement.length())/2);
            int second = string.length()-first;
            return String.format("%s%s%s", string.substring(0, first), replacement, string.substring(second));
        } else {
            return string;
        }
    }
    default String DATE_TIME_FORMAT() {
        return "dd.MM.yyyy-HH:mm:ss.SSS";
    }
    String logTime(Date date);

    default String toString(ITestNGMethod method) {
        StringBuilder sb = new StringBuilder();
        sb
            .append((method.isTest()?"Test":"Configuration"))
            .append("(")
            .append(method.getTestClass().getName().replace(TesterraListener.DEFAULT_PACKAGE+".", ""))
            .append(".")
            .append(method.getMethodName())
            .append(")");
        return sb.toString();
    }
}
