package hs.smartlayout;

import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;
import hs.smartlayout.distributor.SpaceDistributor;

import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public abstract class StandardSpaceDistributorTest {

  @Rule
  public static final Timeout globalTimeout = new Timeout(120000);

  protected abstract SpaceDistributor getSpaceDistributor();

  @Test
  public void shouldAssignAllSpaceToSingleComponent() {
    int[] sizes = new Solver(1, new Limit(0)).solve(15);

    Assert.assertEquals(15, sizes[0]);
  }

  @Test
  public void shouldAssignMinimumSizeToSingleComponentWithZeroWeight() {
    Solver solver = new Solver(1,
      new Limit(0).min(10).weight(0)
    );

    int[] sizes = solver.solve(15);

    Assert.assertEquals(10, sizes[0]);
  }

  @Test
  public void shouldAssignSizeByWeight() {
    Solver solver = new Solver(2,
      new Limit(0).min(10).weight(1),
      new Limit(1).min(5).weight(2)
    );

    // Minimum sizes dictate the result
    for(int space = 5; space < 15; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, 5, sizes[1]);
    }

    // Balancing until correct ratio is reached
    for(int space = 15; space < 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, space - 10, sizes[1]);
    }

    // Ratio based assignment
    for(int space = 30; space < 50; space++) {
      int[] sizes = solver.solve(space);
      //System.out.println(Arrays.toString(sizes));
      Assert.assertEquals("for " + space, 10 + ((space - 30) * 1 + 1) / 3, sizes[0]);
      Assert.assertEquals("for " + space, 20 + ((space - 30) * 2 + 1) / 3, sizes[1]);
      //Assert.assertRatioMatches(0.03, new int[] {1, 2}, sizes);
    }
  }

  @Test
  public void shouldSplitSpaceBetweenTwoComponents() {
    int[] sizes = new Solver(2,
      new Limit(0),
      new Limit(1)
    ).solve(20);

    Assert.assertEquals(10, sizes[0]);
    Assert.assertEquals(10, sizes[1]);
  }

  @Test
  public void shouldRespectMinimumSize() {
    Solver solver = new Solver(2,
      new Limit(0).min(10),
      new Limit(1)
    );

    for(int space = 5; space < 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, space < 10 ? 0 : space - 10, sizes[1]);
    }

    for(int space = 20; space < 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, (space + 1) / 2, sizes[0]);
      Assert.assertEquals("for " + space, space / 2, sizes[1]);
    }
  }

  @Test
  public void shouldRespectMinimumSizeOverWeight() {
    Solver solver = new Solver(2,
      new Limit(0).weight(1).min(10),
      new Limit(1).weight(2)
    );

    for(int space = 5; space < 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, space < 10 ? 0 : space - 10, sizes[1]);
    }

    for(int space = 20; space < 30; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
      Assert.assertEquals("for " + space, space - 10, sizes[1]);
    }

    for(int space = 30; space < 50; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10 + ((space - 30) * 1 + 1) / 3, sizes[0]);
      Assert.assertEquals("for " + space, 20 + ((space - 30) * 2 + 1) / 3, sizes[1]);
    }
  }

  @Test
  public void shouldRespectMaximumSize() {
    Solver solver = new Solver(1,
      new Limit(0).max(10).weight(1)
    );

    for(int space = 1; space <= 10; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, space, sizes[0]);
    }

    for(int space = 10; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 10, sizes[0]);
    }
  }

  @Test
  public void shouldRespectMinimumSizeOverMaximumSize() {
    Solver solver = new Solver(1,
      new Limit(0).min(15).max(10).weight(1)
    );

    for(int space = 1; space <= 20; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertEquals("for " + space, 15, sizes[0]);
    }
  }

  @Test
  public void shouldRespectMaximumSizeOverWeight() {
    Solver solver = new Solver(2,
      new Limit(0).weight(1),
      new Limit(1).max(6).weight(4)
    );

    for(int space = 1; space < 8; space++) {
      int[] sizes = solver.solve(space);

      Assert.assertCloseEnough("for " + space, space * 1 / 5, sizes[0]);
      Assert.assertCloseEnough("for " + space, space * 4 / 5, sizes[1]);
    }

    for(int space = 8; space < 90; space++) {
      int[] sizes = solver.solve(space);

      // System.out.println(Arrays.toString(sizes));

      Assert.assertEquals("for " + space, 2 + ((space - 8) * 1 + 0) / 1, sizes[0]);
      Assert.assertEquals("for " + space, 6, sizes[1]);
    }
  }

  public class Solver {
    private final Limit[] restrictions;
    private final int columnCount;

    private Group rootGroup;

    public Solver(int columnCount, Limit... restrictions) {
      this.columnCount = columnCount;
      this.restrictions = restrictions;

      rootGroup = Group.create(columnCount);
    }

    public void setGroups(Group rootGroup) {
      if(rootGroup.getColumnCount() != columnCount) {
        throw new IllegalArgumentException("group does not match number of columns: " + columnCount);
      }
      this.rootGroup = rootGroup;
    }

    public int[] solve(int space) {
      return getSpaceDistributor().distribute(space, columnCount, Arrays.asList(restrictions), rootGroup);
    }
  }
}
