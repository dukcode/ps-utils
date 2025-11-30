package org.dukcode.psutils;

import java.time.Duration;

public class Assertions {

  private static final Duration MAXIMUM_TIMEOUT = Duration.ofSeconds(10L);

  public static void assertTimeout(double failureTimeout,
      org.junit.jupiter.api.function.Executable executable) {
    org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(MAXIMUM_TIMEOUT, () -> {
      org.junit.jupiter.api.Assertions.assertTimeout(
          Duration.ofMillis((long) failureTimeout * 1000),
          executable);
    });
  }
}
