package hs.smartlayout.oldtestcases;

import hs.smartlayout.Anchor;
import hs.smartlayout.Block;
import hs.smartlayout.Constraints;
import hs.smartlayout.JFakeTextField;
import hs.smartlayout.LineLimit;
import hs.smartlayout.SmartLayout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class LimitsTestCase extends TestCase {
  private static int MAX = Integer.MAX_VALUE;

  public void test1() {
    // No special handling test
    assertTrue(test2x2( 5, MAX, 1.0,  5, MAX, 1.0,   5,  5, MAX,  5, MAX));

    // Simple minimum size tests
    assertTrue(test2x2( 5, MAX, 1.0,  5, MAX, 1.0, 100, 50, MAX, 50, MAX));
    assertTrue(test2x2(55, MAX, 1.0,  5, MAX, 1.0, 100, 55, MAX, 45, MAX));
    assertTrue(test2x2(55, MAX, 1.0, 55, MAX, 1.0, 100, 55, MAX, 55, MAX));

    // Weighted minimum size tests
    assertTrue(test2x2( 5, MAX, 1.0,  5, MAX, 3.0, 100, 25, MAX, 75, MAX));
    assertTrue(test2x2(35, MAX, 1.0,  5, MAX, 3.0, 100, 35, MAX, 65, MAX));
    assertTrue(test2x2( 5, MAX, 1.0, 70, MAX, 3.0, 100, 25, MAX, 75, MAX));
    assertTrue(test2x2(35, MAX, 1.0, 70, MAX, 3.0, 100, 35, MAX, 70, MAX));
    assertTrue(test2x2( 5, MAX, 1.0, 80, MAX, 3.0, 100, 20, MAX, 80, MAX));
    assertTrue(test2x2(35, MAX, 1.0, 80, MAX, 3.0, 100, 35, MAX, 80, MAX));
//    assertTrue(test2x2( 5, 1.0,  5, 1.0, 100, 50, 50));
//    assertTrue(test2x2(55, 1.0,  5, 1.0, 100, 55, 45));
//    assertTrue(test2x2(55, 1.0, 55, 1.0, 100, 55, 55));
  }

  public boolean test2x2(int min1, int max1, double w1, int min2, int max2, double w2, int min3, int resultMin1, int resultMax1, int resultMin2, int resultMax2) {
    LineLimit[] columnLimits = new LineLimit[2];
    LineLimit[] rowLimits = new LineLimit[2];

    SmartLayout.calculateLimits(
      createList(
        block(min1, max1, 1, MAX, w1, 1.0, 0, 0, 1, 1),
        block(min2, max2, 1, MAX, w2, 1.0, 1, 0, 1, 1),
        block(min3, MAX, 1, MAX, 1.0, 1.0, 0, 1, 2, 1)
      ),
      columnLimits,
      rowLimits
    );

    // System.out.println(Arrays.toString(columnLimits));

    return Arrays.equals(columnLimits, new LineLimit[] {
      new LineLimit(resultMin1, resultMin1, resultMax1, w1),
      new LineLimit(resultMin2, resultMin2, resultMax2, w2)
    });
  }

  private static Block block(int minX, int maxX, int minY, int maxY, double wx, double wy, int x, int y, int spanX, int spanY) {
    Constraints c = new Constraints(new Dimension(minX, minY), new Dimension(maxX, maxY), Anchor.CENTER, wx, wy);

    c.setSpanX(spanX);
    c.setSpanY(spanY);

    return new Block(new JFakeTextField(), c, x, y);
  }

  private static List<Block> createList(Block... blocks) {
    List<Block> list = new ArrayList<Block>();

    for(Block block : blocks) {
      list.add(block);
    }

    return list;
  }
}
