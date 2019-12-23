package br.ucb.prevejo.shared.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DateAndTime {

    public static DateTimeFormatter buildFormater(String format) {
        return DateTimeFormatter.ofPattern(format).withZone(currentZone());
    }

    public static boolean isBehind(LocalDateTime time, Duration duration) {
        return isBehind(time, now(), duration);
    }

    public static boolean isBehind(LocalDateTime time, LocalDateTime from, Duration duration) {
        LocalDateTime pastTime = from.minus(duration.toMillis(), ChronoUnit.MILLIS);

        return pastTime.compareTo(time) <= 0;
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(currentZone());
    }

    public static ZoneId currentZone() {
        return ZoneId.systemDefault();
    }

    public static long minutesBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return timeBetween(startTime, endTime, ChronoUnit.MINUTES);
    }

    public static long timeBetween(LocalDateTime startTime, LocalDateTime endTime, TemporalUnit unit) {
        return startTime.until(endTime, unit);
    }

    public static List<LocalDateTime> splitInterval(LocalDateTime startTime, LocalDateTime endTime, int splitCount) {
        long timeBetween = timeBetween(startTime, endTime, ChronoUnit.MILLIS);

        splitCount += 1;

        long diference = timeBetween / splitCount;

        Stream<LocalDateTime> middleTimes = Stream.empty();

        if (diference > 0 && splitCount > 0) {
            middleTimes = IntStream.range(1, splitCount).boxed()
                    .map(val -> startTime.plus(val * diference, ChronoUnit.MILLIS));
        }

        return Stream.concat(
                Stream.of(startTime),
                Stream.concat(middleTimes, Stream.of(endTime))
        ).collect(Collectors.toList());
    }

    public static LocalDateTime middleTime(LocalDateTime startTime, LocalDateTime endTime, double fraction) {
        if (fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException("Fraction not between 0 and 1");
        }

        long timeBetween = timeBetween(startTime, endTime, ChronoUnit.MILLIS);

        long middle = (long) (timeBetween * fraction);

        if (middle == 0) {
            return startTime;
        } else if (middle == timeBetween) {
            return endTime;
        }

        return startTime.plus(middle, ChronoUnit.MILLIS);
    }

    public static String toString(TemporalAccessor time, String format) {
        return buildFormater(format).format(time);
    }

    public static String toStringTime(LocalDateTime time) {
        return buildFormater("HH:mm").format(time);
    }

    public static long toEpochMilli(LocalDateTime time) {
        return time.atZone(currentZone()).toInstant().toEpochMilli();
    }

}
