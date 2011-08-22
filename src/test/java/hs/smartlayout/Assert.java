package hs.smartlayout;

import java.util.Arrays;

public class Assert extends junit.framework.Assert {
  public static void assertLesserThan(int expected, int result) {
    if(result >= expected) {
      throw new AssertionError("expected: <" + expected + ", result: " + result);
    }
  }

  public static void assertAtLeast(int expected, int result) {
    if(result < expected) {
      throw new AssertionError("expected: >=" + expected + ", result: " + result);
    }
  }

  public static void assertGreaterThan(int expected, int result) {
    if(result <= expected) {
      throw new AssertionError("expected: >" + expected + ", result: " + result);
    }
  }

  public static void assertAtMost(int expected, int result) {
    if(result > expected) {
      throw new AssertionError("expected: <=" + expected + ", result: " + result);
    }
  }

  public static void assertCloseEnough(int expected, int result) {
    assertCloseEnough(null, expected, result);
  }

  public static void assertCloseEnough(String message, int expected, int result) {
    if(expected != result && expected + 1 != result) {
      throw new AssertionError(Assert.format(message, "" + expected + " || " + (expected + 1), result));
    }
  }

  public static void assertRatioMatches(int[] expectedRatios, int[] result) {
    assertRatioMatches(null, expectedRatios, result);
  }

  public static void assertRatioMatches(String message, int[] expectedRatios, int[] result) {
    double factor = (double)result[0] / (double)expectedRatios[0];

    for(int i = 0; i < result.length; i++) {
      if(result[i] / factor != expectedRatios[i]) {
        throw new AssertionError(Assert.format(message, Arrays.toString(expectedRatios), Arrays.toString(result)));
      }
    }
  }

//  public static void assertRatioMatches(double error, int[] expectedRatios, int[] result) {
//    for(int i = 0; i < result.length - 1; i++) {
//      double expected = (double)expectedRatios[i] / expectedRatios[i + 1];
//      double received = (double)result[i] / result[i + 1];
//
//      if(Math.abs(expected - received) > error) {
//        throw new AssertionError("ratio " + expectedRatios[i] + ":" + expectedRatios[i + 1] + " vs " + result[i] + ":" + result[i + 1] + " exceeds error range (" + Math.abs(expected - received) + " > " + error + "): " + Arrays.toString(expectedRatios) + ", result: " + Arrays.toString(result));
//      }
//    }
//  }
}
