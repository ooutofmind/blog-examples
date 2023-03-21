package com.ooutofmind.blog_examples;

import static java.time.ZoneOffset.UTC;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.mockito.*;

public class MockDateUtils {

  private MockDateUtils() {}

  public static void runWithCurrentDate(LocalDate localDate, Runnable runnable) {
    runWithCurrentInstant(localDate.atStartOfDay(UTC).toInstant(), runnable);
  }

  public static void runWithCurrentDateTime(LocalDateTime localDate, Runnable runnable) {
    runWithCurrentInstant(localDate.toInstant(UTC), runnable);
  }

  public static void runWithCurrentInstant(Instant desiredInstant, Runnable runnable) {
    Clock clock = Clock.fixed(desiredInstant, UTC);

    try (MockedStatic<Clock> mockedStatic = Mockito.mockStatic(Clock.class)) {
      mockedStatic.when(Clock::systemDefaultZone).thenReturn(clock);

      runnable.run();
    }
  }

}
