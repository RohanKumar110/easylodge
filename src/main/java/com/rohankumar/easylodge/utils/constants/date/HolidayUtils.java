package com.rohankumar.easylodge.utils.constants.date;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

public class HolidayUtils {

    public static boolean isPublicHoliday(LocalDate date) {

        int year = date.getYear();
        Set<LocalDate> holidays = getPublicHolidays(year);

        if (holidays.contains(date)) return true;

        return isChristmasWeek(date) || isNewYearWeek(date);
    }

    public static Set<LocalDate> getPublicHolidays(int year) {

        Set<LocalDate> holidays = new HashSet<>();

        holidays.add(LocalDate.of(year, 1, 1));    // New Year's Day
        holidays.add(LocalDate.of(year, 7, 4));    // Independence Day
        holidays.add(LocalDate.of(year, 12, 25));  // Christmas Day

        holidays.add(getMartinLutherKingJrDay(year));
        holidays.add(getPresidentsDay(year));
        holidays.add(getMemorialDay(year));
        holidays.add(getLaborDay(year));
        holidays.add(getThanksgiving(year));

        return holidays;
    }

    public static boolean isChristmasWeek(LocalDate date) {

        LocalDate christmas = LocalDate.of(date.getYear(), 12, 25);
        return !date.isBefore(christmas.minusDays(2)) && !date.isAfter(christmas.plusDays(2));
    }

    public static boolean isNewYearWeek(LocalDate date) {

        Month month = date.getMonth();
        int day = date.getDayOfMonth();

        return (month == Month.DECEMBER && day >= 30) ||
                (month == Month.JANUARY && day <= 2);
    }

    public static LocalDate getThanksgiving(int year) {

        return LocalDate.of(year, Month.NOVEMBER, 1)
                .with(TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.THURSDAY));
    }

    public static LocalDate getMemorialDay(int year) {

        return LocalDate.of(year, Month.MAY, 31)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getLaborDay(int year) {

        return LocalDate.of(year, Month.SEPTEMBER, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    public static LocalDate getMartinLutherKingJrDay(int year) {

        return LocalDate.of(year, Month.JANUARY, 1)
                .with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY));
    }

    public static LocalDate getPresidentsDay(int year) {

        return LocalDate.of(year, Month.FEBRUARY, 1)
                .with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY));
    }
}