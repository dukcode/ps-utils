package org.dukcode.psutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

/**
 * Marks a method as a problem solving test case.
 *
 * <p>Must be used within a class annotated with {@link PSTest}.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @PSTestCase(
 *     input = """
 *         3
 *         7
 *         7 1 5 9 6 7 3
 *         """,
 *     output = "20"
 * )
 * void testCase1() {}
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
public @interface PSTestCase {

  /**
   * Input data to be fed to System.in.
   *
   * @return input string
   */
  String input();

  /**
   * Expected output from System.out.
   *
   * @return expected output string
   */
  String output();
}
