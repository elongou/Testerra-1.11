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
 package eu.tsystems.mms.tic.testframework.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @deprecated Please use Java Date library
 */
@Deprecated
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");

    /**
     * Duration
     */
    private static String duration = "unknown";

    private DateUtils() {

    }

    @Deprecated
    public static String getDate() {

        return SIMPLE_DATE_FORMAT.format(new Date());
    }

    @Deprecated
    public static String getDate(final SimpleDateFormat simpleDateFormat) {

        return simpleDateFormat.format(new Date());
    }

    @Deprecated
    public static String getDate(final SimpleDateFormat simpleDateFormat, final Date date) {

        return simpleDateFormat.format(date);
    }

    @Deprecated
    public static XMLGregorianCalendar getXMLGregorianCalender(final Date date) throws DatatypeConfigurationException {

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        return xgc;
    }

    @Deprecated
    public static Date getDateWithDiff(int monthDiff, int dayOfMonthDiff, int dayOfWeekDiff, int hourDiff, int minuteDiff, int secondDiff) {

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthDiff);
        calendar.add(Calendar.DAY_OF_MONTH, dayOfMonthDiff);
        calendar.add(Calendar.DAY_OF_WEEK, dayOfWeekDiff);
        calendar.add(Calendar.HOUR, hourDiff);
        calendar.add(Calendar.MINUTE, minuteDiff);
        calendar.add(Calendar.SECOND, secondDiff);
        return calendar.getTime();
    }

    @Deprecated
    public static Date getDate(XMLGregorianCalendar xmlGregorianCalendar) {

        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    @Deprecated
    public static String getFormattedDuration(long durationInMS, boolean showMS) {

        String zero = "0s";

        if (durationInMS < 0) {
            duration = zero;
            return duration;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(durationInMS);
        durationInMS -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMS);
        durationInMS -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMS);
        durationInMS -= TimeUnit.SECONDS.toMillis(seconds);
        long millis = TimeUnit.MILLISECONDS.toMillis(durationInMS);

        if (showMS)
            duration = String.format("%02dh %02dm %02ds %02dms",
                    hours, minutes, seconds, millis);
        else if (!showMS)
            duration = String.format("%02dh %02dm %02ds",
                    hours, minutes, seconds);
        return duration;
    }
}
