package org.dukcode.psutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks a test class for problem solving tests.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @PSTest(timeout = 1.0, solution = Fence.class)
 * class FenceTest {
 *     @PSTestCase(input = "3\n7\n7 1 5 9 6 7 3", output = "20")
 *     void testCase1() {}
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(PSTestExtension.class)
public @interface PSTest {

  /**
   * Timeout in seconds for each test case.
   *
   * @return timeout value in seconds
   */
  double timeout() default 1.0;

  /**
   * The solution class containing the main method to be tested.
   *
   * @return solution class
   */
  Class<?> solution();
}
