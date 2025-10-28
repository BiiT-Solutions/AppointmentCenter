package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public enum RecurrenceFrequency {

    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    WORKING_DAYS,
    MONTHLY_ON_WEEK_DAY;

    public boolean hasRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        switch (this) {
            case DAILY -> {
                return dailyRecurrence(sourceDate, comparedDate);
            }
            case WEEKLY -> {
                return weeklyRecurrence(sourceDate, comparedDate);
            }
            case MONTHLY -> {
                return monthlyRecurrence(sourceDate, comparedDate);
            }
            case YEARLY -> {
                return yearlyRecurrence(sourceDate, comparedDate);
            }
            case WORKING_DAYS -> {
                return workingDaysRecurrence(sourceDate, comparedDate);
            }
            case MONTHLY_ON_WEEK_DAY -> {
                return monthlyOnWeekDayRecurrence(sourceDate, comparedDate);
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isAfter(LocalDate sourceDate, LocalDate comparedDate) {
        return sourceDate.until(comparedDate, ChronoUnit.DAYS) > 0;
    }

    private boolean dailyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        return isAfter(sourceDate, comparedDate);
    }

    private boolean weeklyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        final DayOfWeek sourceDay = DayOfWeek.of(sourceDate.get(ChronoField.DAY_OF_WEEK));
        final DayOfWeek comparedDay = DayOfWeek.of(comparedDate.get(ChronoField.DAY_OF_WEEK));
        return sourceDay == comparedDay && isAfter(sourceDate, comparedDate);
    }

    private boolean monthlyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        return sourceDate.getDayOfMonth() == comparedDate.getDayOfMonth()
                && isAfter(sourceDate, comparedDate);
    }

    private boolean yearlyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        return sourceDate.getDayOfMonth() == comparedDate.getDayOfMonth()
                && sourceDate.getMonth() == comparedDate.getMonth()
                && isAfter(sourceDate, comparedDate);
    }

    private boolean workingDaysRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        final DayOfWeek day = DayOfWeek.of(comparedDate.get(ChronoField.DAY_OF_WEEK));
        return !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) && isAfter(sourceDate, comparedDate);
    }

    private boolean monthlyOnWeekDayRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        //Check if are on the same week number and the same week day.
        return weeklyRecurrence(sourceDate, comparedDate)
                && sourceDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH) == comparedDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
    }
}
