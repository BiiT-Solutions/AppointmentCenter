package com.biit.appointment.persistence.entities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

public enum RecurrenceFrequency {
    WEEKLY,
    MONTHLY,
    YEARLY,
    WORKING_DAYS,
    MONTHLY_ON_WEEK_DAY;

    public boolean hasRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        switch (this) {
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

    private boolean weeklyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        final DayOfWeek sourceDay = DayOfWeek.of(sourceDate.get(ChronoField.DAY_OF_WEEK));
        final DayOfWeek comparedDay = DayOfWeek.of(comparedDate.get(ChronoField.DAY_OF_WEEK));
        return sourceDay == comparedDay && comparedDate.isAfter(sourceDate);
    }

    private boolean monthlyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        return sourceDate.getDayOfMonth() == comparedDate.getDayOfMonth()
                && comparedDate.isAfter(sourceDate);
    }

    private boolean yearlyRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        return sourceDate.getDayOfMonth() == comparedDate.getDayOfMonth()
                && sourceDate.getMonth() == comparedDate.getMonth()
                && comparedDate.isAfter(sourceDate);
    }

    private boolean workingDaysRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        final DayOfWeek day = DayOfWeek.of(comparedDate.get(ChronoField.DAY_OF_WEEK));
        return !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) && comparedDate.isAfter(sourceDate);
    }

    private boolean monthlyOnWeekDayRecurrence(LocalDate sourceDate, LocalDate comparedDate) {
        //Check if are on the same week number and the same week day.
        return weeklyRecurrence(sourceDate, comparedDate)
                && sourceDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH) == comparedDate.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
    }
}
