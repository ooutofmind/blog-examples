package com.ooutofmind.blog_examples;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.truth.Truth;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CurrentDateValidatorTest {

  @ParameterizedTest
  @ValueSource(ints = {1, 10, 15, 28})
  void givenDayOfMonth_whenCheckTodayIsThisDay_thenTrue(int dayOfMonth) {
    var validator = new CurrentDateValidator();

    MockDateUtils.runWithCurrentDate(LocalDate.of(2023, 1, dayOfMonth), () -> {
      boolean actual = validator.isToday(dayOfMonth);

      Truth.assertThat(actual).isTrue();
    });
  }

}