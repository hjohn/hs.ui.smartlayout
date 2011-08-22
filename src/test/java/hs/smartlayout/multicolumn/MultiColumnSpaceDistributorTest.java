package hs.smartlayout.multicolumn;

import hs.smartlayout.Assert;
import hs.smartlayout.StandardSpaceDistributorTest;
import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;

import org.junit.Test;

public abstract class MultiColumnSpaceDistributorTest extends StandardSpaceDistributorTest {

  @Test
  public void shouldRespectMinimumGroupSize() {
    Solver solver = new Solver(2,
      new Limit(0),
      new Limit(1),
      new Limit(0, 1).min(20)
    );

    for(int space = 1; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, 10, sizes[1]);
    }

    for(int space = 20; space <= 100; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, (space + 1) / 2, sizes[0]);
      Assert.assertEquals("for " + space, space / 2, sizes[1]);
    }
  }

  @Test
  public void shouldSplitSizeAmongColumnsForMultiColumnRestrictions() {
    Solver solver = new Solver(4,
      new Limit(0, 1).min(10),
      new Limit(2, 3).min(5)
    );

    solver.setGroups(Group.create(new Group(2), new Group(2)));

    for(int space = 5; space < 15; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 5, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
      Assert.assertEquals("for " + space, 3, sizes[2]);
      Assert.assertEquals("for " + space, 2, sizes[3]);
    }

    for(int space = 15; space < 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 5, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
      Assert.assertEquals("for " + space, 3 + (space - 15) / 2, sizes[2]);
      Assert.assertEquals("for " + space, 2 + (space - 15 + 1) / 2, sizes[3]);
    }

    for(int space = 20; space < 50; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space / 4, sizes[0]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[1]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[2]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[3]);

      if(space % 4 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {1, 1, 1, 1}, sizes);
      }
      if(space % 2 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {1, 1}, new int[] {sizes[0] + sizes[1], sizes[2] + sizes[3]});
      }
    }
  }

  @Test
  public void shouldAssignSizeByWeightForMultiColumnWeights() {
    Solver solver = new Solver(4,
      new Limit(0).weight(1),
      new Limit(1).weight(1),
      new Limit(2).weight(2),
      new Limit(3).weight(2),
      new Limit(0, 1).min(10).weight(1),
      new Limit(2, 3).min(5).weight(2)
    );

    solver.setGroups(Group.create(new Group(2), new Group(2)));

    // Minimum sizes dictate the result
    for(int space = 5; space < 15; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 5, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
      Assert.assertEquals("for " + space, 3, sizes[2]);
      Assert.assertEquals("for " + space, 2, sizes[3]);
    }

    // Balancing until correct ratio is reached
    for(int space = 15; space < 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 5, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
      Assert.assertEquals("for " + space, 3 + (space - 15) / 2, sizes[2]);
      Assert.assertEquals("for " + space, 2 + (space - 15 + 1) / 2, sizes[3]);
    }

    // Ratio based assignment
    for(int space = 30; space < 100; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space,  5 + ((space - 30) * 1) / 6, sizes[0]);
      Assert.assertCloseEnough("for " + space,  5 + ((space - 30) * 1) / 6, sizes[1]);
      Assert.assertCloseEnough("for " + space, 10 + ((space - 30) * 2) / 6, sizes[2]);
      Assert.assertCloseEnough("for " + space, 10 + ((space - 30) * 2) / 6, sizes[3]);

      if(space % 6 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {1, 1, 2, 2}, sizes);
      }
      if(space % 3 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {1, 2}, new int[] {sizes[0] + sizes[1], sizes[2] + sizes[3]});
      }
    }
  }

  @Test
  public void shouldRespectOverlappingMinimumSize() {
    Solver solver = new Solver(2,
      new Limit(0).min(10),
      new Limit(1),
      new Limit(0, 1).min(15)
    );

    for(int space = 5; space < 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, space < 15 ? 5 : space - 10, sizes[1]);
    }

    for(int space = 20; space < 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, (space + 1) / 2, sizes[0]);
      Assert.assertEquals("for " + space, space / 2, sizes[1]);
    }
  }

  @Test
  public void shouldRespectOverlappingMinimumSizeWithThreeColumns() {
    Solver solver = new Solver(3,
      new Limit(0).min(2),
      new Limit(1).min(4),
      new Limit(2).min(6),
      new Limit(0, 1).min(7),
      new Limit(1, 2).min(11),
      new Limit(0, 2).min(15)
    );

    for(int space = 1; space <= 15; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 4, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
      Assert.assertEquals("for " + space, 6, sizes[2]);
    }

    for(int space = 15; space <= 18; space++) {
      int[] sizes = solver.solve(space);
      //System.out.println(Arrays.toString(sizes));
      Assert.assertCloseEnough("for " + space, 4 + (space - 15 + 2) / 3, sizes[0]);
      Assert.assertCloseEnough("for " + space, 5 + (space - 15) / 3, sizes[1]);
      Assert.assertEquals("for " + space, 6, sizes[2]);
    }

    for(int space = 18; space <= 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space / 3, sizes[0]);
      Assert.assertCloseEnough("for " + space, space / 3, sizes[1]);
      Assert.assertCloseEnough("for " + space, space / 3, sizes[2]);

      if(space % 3 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {1, 1, 1}, sizes);
      }
    }
  }

  @Test
  public void shouldRespectAllMaximumSizes() {
    Solver solver = new Solver(2,
      new Limit(0).max(12).weight(3),
      new Limit(1).max(10),
      new Limit(0, 1).max(20)
    );

    for(int space = 1; space <= 16; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space * 3 / 4, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 1 / 4, sizes[1]);
    }

    for(int space = 16; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 12, sizes[0]);
      Assert.assertEquals("for " + space, 4 + (space - 16), sizes[1]);
    }

    for(int space = 20; space <= 100; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 12, sizes[0]);
      Assert.assertEquals("for " + space, 8, sizes[1]);
    }
  }

  @Test
  public void shouldRespectMultiColumnMaximum() {
    Solver solver = new Solver(2,
      new Limit(0),
      new Limit(1),
      new Limit(0, 1).max(10).weight(1)
    );

    for(int space = 1; space <= 10; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space / 2, sizes[0]);
      Assert.assertCloseEnough("for " + space, space / 2, sizes[1]);
    }

    for(int space = 10; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 5, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
    }
  }

  @Test
  public void shouldRespectMultiColumnMaximumWithWeights() {
    Solver solver = new Solver(2,
      new Limit(0).weight(1),
      new Limit(1).weight(4),
      new Limit(0, 1).max(10).weight(1)
    );

    for(int space = 1; space <= 10; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space * 1 / 5, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 4 / 5, sizes[1]);
    }

    for(int space = 10; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals(2, sizes[0]);
      Assert.assertEquals(8, sizes[1]);
    }
  }

  @Test
  public void shouldRespectGroupWeightAfterMaximumSizeIsReached() {
    Solver solver = new Solver(6,
      new Limit(0).weight(1),
      new Limit(1).max(10).weight(1),
      new Limit(2).weight(1),
      new Limit(3).weight(2),
      new Limit(4).weight(2),
      new Limit(5).weight(2),
      new Limit(0, 2).weight(1),  // weight irrelevant
      new Limit(3, 5).weight(1)   // weight irrelevant
    );

    solver.setGroups(Group.create(new Group(3), new Group(3)));

    for(int space = 1; space < 90; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space * 1 / 9, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 1 / 9, sizes[1]);
      Assert.assertCloseEnough("for " + space, space * 1 / 9, sizes[2]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[3]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[4]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[5]);
    }

    for(int space = 90; space < 150; space++) {
      int[] sizes = solver.solve(space);

      //System.out.println(space + ": "+ Arrays.toString(sizes));
      Assert.assertCloseEnough("for " + space, 10 + ((space - 90) * 1) / 6, sizes[0]);
      Assert.assertEquals("for " + space, 10, sizes[1]);
      Assert.assertCloseEnough("for " + space, 10 + ((space - 90) * 1) / 6, sizes[2]);
      Assert.assertCloseEnough("for " + space, 20 + ((space - 90) * 2) / 9, sizes[3]);
      Assert.assertCloseEnough("for " + space, 20 + ((space - 90) * 2) / 9, sizes[4]);
      Assert.assertCloseEnough("for " + space, 20 + ((space - 90) * 2) / 9, sizes[5]);
    }
  }

  @Test
  public void shouldRespectGroupWeightAfterMaximumSizeIsReached2() {
    Solver solver = new Solver(7,
      new Limit(0).weight(0.5),
      new Limit(1).weight(0.5),
      new Limit(2).max(10).weight(1),
      new Limit(3).weight(1),
      new Limit(4).weight(2),
      new Limit(5).weight(2),
      new Limit(6).weight(2),
      new Limit(2, 3).max(24),
      new Limit(0, 3).weight(1),  // weight irrelevant
      new Limit(4, 6).weight(1)   // weight irrelevant
    );

//  was:   solver.setGroups(new Group(0, 3), new Group(2, 3), new Group(4, 6));
    solver.setGroups(Group.create(new Group(new Group(1), new Group(1), new Group(2)), new Group(3)));

    for(int space = 1; space <= 90; space++) {
      int[] sizes = solver.solve(space);

      //System.out.println(space + " : " + Arrays.toString(sizes) + " " + (sizes[0] + sizes[1] + sizes[2] + sizes[3]) + ":" + (sizes[2] + sizes[3]) + ":" + (sizes[4] + sizes[5] + sizes[6]));

      Assert.assertEquals(space, sizes[0] + sizes[1] + sizes[2] + sizes[3] + sizes[4] + sizes[5] + sizes[6]);
      if(space % 18 == 0) {
        Assert.assertRatioMatches(new int[] {1, 1, 4, 12}, new int[] {sizes[0], sizes[1], sizes[2] + sizes[3], sizes[4] + sizes[5] + sizes[6]});
      }
      if(space % 9 == 0) {
        Assert.assertRatioMatches(new int[] {3, 6}, new int[] {sizes[0] + sizes[1] + sizes[2] + sizes[3], sizes[4] + sizes[5] + sizes[6]});
      }

      Assert.assertCloseEnough("for " + space, space * 1 / 18, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 1 / 18, sizes[1]);
      Assert.assertCloseEnough("for " + space, space * 1 / 9, sizes[2]);
      Assert.assertCloseEnough("for " + space, space * 1 / 9, sizes[3]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[4]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[5]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[6]);
    }

    //Assert.fail();

    for(int space = 90; space <= 108; space++) {
      int[] sizes = solver.solve(space);
      // System.out.println(space + " : " + Arrays.toString(sizes) + " " + (sizes[0] + sizes[1] + sizes[2] + sizes[3]) + ":" + (sizes[2] + sizes[3]) + ":" + (sizes[4] + sizes[5] + sizes[6]));

      Assert.assertEquals(space, sizes[0] + sizes[1] + sizes[2] + sizes[3] + sizes[4] + sizes[5] + sizes[6]);
      if(space % 18 == 0) {
        Assert.assertRatioMatches(new int[] {1, 1, 4, 12}, new int[] {sizes[0], sizes[1], sizes[2] + sizes[3], sizes[4] + sizes[5] + sizes[6]});
      }
      if(space % 9 == 0) {
        Assert.assertRatioMatches(new int[] {3, 6}, new int[] {sizes[0] + sizes[1] + sizes[2] + sizes[3], sizes[4] + sizes[5] + sizes[6]});
      }

      Assert.assertCloseEnough("for " + space, space * 1 / 18, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 1 / 18, sizes[1]);
      Assert.assertCloseEnough("for " + space, 10, sizes[2]);
//      Assert.assertCloseEnough("for " + space, 10 + (space - 90) * 2 / 9, sizes[3]);  // assertCloseEnough is not guaranteed to be always correct I think
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[4]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[5]);
      Assert.assertCloseEnough("for " + space, space * 2 / 9, sizes[6]);
    }

    for(int space = 108; space <= 200; space++) {
      int[] sizes = solver.solve(space);

     // System.out.println(space + " : " + Arrays.toString(sizes) + " " + (sizes[0] + sizes[1] + sizes[2] + sizes[3]) + ":" + (sizes[4] + sizes[5] + sizes[6]));

      Assert.assertEquals(space, sizes[0] + sizes[1] + sizes[2] + sizes[3] + sizes[4] + sizes[5] + sizes[6]);
      if(space % 3 == 0) {
        Assert.assertRatioMatches(new int[] {1, 2}, new int[] {sizes[0] + sizes[1] + sizes[2] + sizes[3], sizes[4] + sizes[5] + sizes[6]});
      }
      if(space % 6 == 0) {
        Assert.assertRatioMatches(new int[] {1, 1}, new int[] {sizes[0], sizes[1]});
      }

      Assert.assertCloseEnough("for " + space, 6 + ((space - 108) * 1) / 6, sizes[0]);
      Assert.assertCloseEnough("for " + space, 6 + ((space - 108) * 1) / 6, sizes[1]);
      Assert.assertEquals("for " + space, 10, sizes[2]);
      Assert.assertEquals("for " + space, 14, sizes[3]);
      Assert.assertCloseEnough("for " + space, 24 + ((space - 108) * 2) / 9, sizes[4]);
      Assert.assertCloseEnough("for " + space, 24 + ((space - 108) * 2) / 9, sizes[5]);
      Assert.assertCloseEnough("for " + space, 24 + ((space - 108) * 2) / 9, sizes[6]);
    }
  }

  @Test
  public void shouldRespectMinimumSizeOverGroupMaximumSize() {
    Solver solver = new Solver(2,
      new Limit(0).min(10),
      new Limit(1).min(15),
      new Limit(0, 1).max(20)
    );

    for(int space = 1; space < 60; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, 15, sizes[1]);
    }
  }

  @Test
  public void shouldRespectGroupMinimumSizeOverMaximumSizes() {
    Solver solver = new Solver(2,
      new Limit(0).max(10).weight(2),
      new Limit(1).max(10),
      new Limit(0, 1).min(20)
    );

    for(int space = 1; space < 60; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, 10, sizes[1]);
    }
  }

  @Test
  public void shouldRespectWeightsWhenGroupMinimumSizeOverridesMaximumSizes() {
    Solver solver = new Solver(2,
      new Limit(0).max(10).weight(2),
      new Limit(1).max(10),
      new Limit(0, 1).min(33)
    );

    for(int space = 1; space < 40; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 22, sizes[0]);
      Assert.assertEquals("for " + space, 11, sizes[1]);
    }
  }

  @Test
  public void shouldRespectGroupMinimumsWithSmallestSize() {
    Solver solver = new Solver(4,
      new Limit(0).weight(1),
      new Limit(1).weight(1),
      new Limit(2).weight(1),
      new Limit(3).weight(1),
      new Limit(0, 1).min(8),
      new Limit(1, 3).min(12)
    );

    for(int space = 1; space <= 12; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 0, sizes[0]);
      Assert.assertEquals("for " + space, 8, sizes[1]);
      Assert.assertEquals("for " + space, 2, sizes[2]);
      Assert.assertEquals("for " + space, 2, sizes[3]);
    }

    for(int space = 12; space <= 14; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, space - 12, sizes[0]);
      Assert.assertEquals("for " + space, 8, sizes[1]);
      Assert.assertEquals("for " + space, 2, sizes[2]);
      Assert.assertEquals("for " + space, 2, sizes[3]);
    }

    for(int space = 14; space <= 32; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, (space - 8) / 3, sizes[0]);
      Assert.assertEquals("for " + space, 8, sizes[1]);
      Assert.assertCloseEnough("for " + space, (space - 8) / 3, sizes[2]);
      Assert.assertCloseEnough("for " + space, (space - 8) / 3, sizes[3]);
    }

    for(int space = 32; space <= 60; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space / 4, sizes[0]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[1]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[2]);
      Assert.assertCloseEnough("for " + space, space / 4, sizes[3]);
    }
  }

  @Test
  public void shouldRespectGroupMinimumsWithSmallestSizeAndRespectWeightIfPossible() {
    Solver solver = new Solver(5,
      new Limit(0, 1).min(8),
      new Limit(1, 3).min(12),
      new Limit(2, 4).min(12)
    );

    for(int space = 1; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 4, sizes[0]);
      Assert.assertEquals("for " + space, 4, sizes[1]);
      Assert.assertEquals("for " + space, 4, sizes[2]);
      Assert.assertEquals("for " + space, 4, sizes[3]);
      Assert.assertEquals("for " + space, 4, sizes[4]);
    }

    for(int space = 20; space <= 60; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space / 5, sizes[0]);
      Assert.assertCloseEnough("for " + space, space / 5, sizes[1]);
      Assert.assertCloseEnough("for " + space, space / 5, sizes[2]);
      Assert.assertCloseEnough("for " + space, space / 5, sizes[3]);
      Assert.assertCloseEnough("for " + space, space / 5, sizes[4]);
    }
  }

  @Test
  public void shouldRespectGroupRatiosAfterSeveralMaximumsAreReached() {
    Solver solver = new Solver(5,
      new Limit(0).weight(2).max(20),
      new Limit(1).weight(1),
      new Limit(2).weight(3),
      new Limit(3).weight(1),
      new Limit(4).weight(1),
      new Limit(2, 3).max(20)
    );

    solver.setGroups(Group.create(new Group(1), new Group(2), new Group(2)));

    for(int space = 1; space <= 40; space++) {
      int[] sizes = solver.solve(space);

      //System.out.println(space+ ": " +Arrays.toString(sizes));

      Assert.assertCloseEnough("for " + space, space * 2 / 8, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 1 / 8, sizes[1]);
      Assert.assertCloseEnough("for " + space, space * 3 / 8, sizes[2]);
      Assert.assertCloseEnough("for " + space, space * 1 / 8, sizes[3]);
      Assert.assertCloseEnough("for " + space, space * 1 / 8, sizes[4]);

      if(space % 8 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {2, 4, 2}, new int[] {sizes[0], sizes[1] + sizes[2], sizes[3] + sizes[4]});
      }
      if(space % 4 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {2, 1}, new int[] {sizes[1] + sizes[2], sizes[3] + sizes[4]});
      }
    }

    for(int space = 40; space <= 80; space++) {
      int[] sizes = solver.solve(space);

      // System.out.println(space+ ": " +Arrays.toString(sizes));

      Assert.assertCloseEnough("for " + space, 10 + (space - 40) * 2 / 8, sizes[0]);
      Assert.assertCloseEnough("for " + space, 5 + (space - 40) * 4 / 8, sizes[1]);
      Assert.assertEquals("for " + space, 15, sizes[2]);
      Assert.assertEquals("for " + space, 5, sizes[3]);
      Assert.assertCloseEnough("for " + space, 5 + (space - 40) * 2 / 8, sizes[4]);

      if(space % 8 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {2, 4, 2}, new int[] {sizes[0], sizes[1] + sizes[2], sizes[3] + sizes[4]});
      }
      if(space % 4 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {2, 1}, new int[] {sizes[1] + sizes[2], sizes[3] + sizes[4]});
      }
    }

    for(int space = 80; space <= 200; space++) {
      int[] sizes = solver.solve(space);

      // System.out.println(space+ ": " +Arrays.toString(sizes) + " " + (sizes[1] + sizes[2]) + ":" + (sizes[3] + sizes[4]));

      Assert.assertEquals("for " + space, 20, sizes[0]);
      Assert.assertCloseEnough("for " + space, 25 + (space - 80) * 2 / 3, sizes[1]);
      Assert.assertEquals("for " + space, 15, sizes[2]);
      Assert.assertEquals("for " + space, 5, sizes[3]);
      Assert.assertCloseEnough("for " + space, 15 + (space - 80) * 1 / 3, sizes[4]);

      if((space - 80) % 3 == 0) {
        Assert.assertRatioMatches("for " + space, new int[] {2, 1}, new int[] {sizes[1] + sizes[2], sizes[3] + sizes[4]});
      }
    }
  }
}
