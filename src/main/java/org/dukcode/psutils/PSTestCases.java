package org.dukcode.psutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks a method as a parameterized problem solving test with multiple test cases.
 *
 * <p>Must be used within a class annotated with {@link PSTest}.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * @PSTest(timeout = 1.0, solution = Fence.class)
 * class FenceTest {
 *     @PSTestCases({
 *         @PSTestCase(input = "3\n7\n7 1 5 9 6 7 3", output = "20"),
 *         @PSTestCase(input = "7\n1 4 4 4 4 1 1", output = "16")
 *     })
 *     void testMultipleCases() {}
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(PSTestCasesProvider.class)
public @interface PSTestCases {

  /**
   * Array of test cases to execute.
   *
   * @return array of test cases
   */
  PSTestCase[] value();
}
