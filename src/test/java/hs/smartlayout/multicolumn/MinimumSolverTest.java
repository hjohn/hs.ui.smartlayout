package hs.smartlayout.multicolumn;

import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MinimumSolverTest {

  @Before
  public void before() {

  }

  @Test
  public void shouldAssignNoMinimumsWhenThereAreNoLimits() {
    Solver solver = new Solver(1);

    int[] minimums = new int[1];
    int spaceUsed = distribute(minimums, solver);

    Assert.assertEquals(0, spaceUsed);
    Assert.assertEquals(0, minimums[0]);
  }

  @Test
  public void shouldAssignSimpleMinimum() {
    Solver solver = new Solver(1,
      new Limit(0).min(5)
    );

    int[] minimums = new int[1];
    int spaceUsed = distribute(minimums, solver);

    Assert.assertEquals(5, spaceUsed);
    Assert.assertEquals(5, minimums[0]);
  }

  @Test
  public void shouldIgnoreMaximumAndAssignSimpleMinimum() {
    Solver solver = new Solver(1,
      new Limit(0).min(5).max(1)
    );

    int[] minimums = new int[1];
    int spaceUsed = distribute(minimums, solver);

    Assert.assertEquals(5, spaceUsed);
    Assert.assertEquals(5, minimums[0]);
  }

  @Test
  public void shouldAvoidGoingOverMaximum() {
    Solver solver = new Solver(1,
      new Limit(0).max(5),
      new Limit(2).max(2),
      new Limit(0, 2).min(21)
    );

    int[] minimums = new int[3];
    int spaceUsed = distribute(minimums, solver);

    Assert.assertEquals(21, spaceUsed);
    Assert.assertEquals(5, minimums[0]);
    Assert.assertEquals(14, minimums[1]);
    Assert.assertEquals(2, minimums[2]);
  }

  @Test
  public void shouldDistributeAccordingToWeightWhenAnyMaximumExceeded() {  // debatable, if one columns exceeds one maximum and another exceeds two maximums, we may want to favor one over the other
    Solver solver = new Solver(1,
      new Limit(0).max(5),
      new Limit(0, 1).max(10),
      new Limit(2).max(2),
      new Limit(0, 2).min(21)
    );

    int[] minimums = new int[3];
    int spaceUsed = distribute(minimums, solver);

    Assert.assertEquals(21, spaceUsed);
    Assert.assertEquals(7, minimums[0]);  // column max exceeded by 2
    Assert.assertEquals(7, minimums[1]);  // group max exceeded by 4
    Assert.assertEquals(7, minimums[2]);  // column max exceeded by 5
  }

  @Test
  public void shouldPreferIncreasingMinimumOfHighestWeightColumnFirstWhenAllElseEqual() {
    {
      Solver solver = new Solver(2,
        new Limit(0).min(10).weight(2),
        new Limit(1).min(5).weight(1),
        new Limit(0, 1).min(16)
      );

      int[] minimums = new int[2];
      int spaceUsed = distribute(minimums, solver);

      Assert.assertEquals(16, spaceUsed);
      Assert.assertEquals(11, minimums[0]);
      Assert.assertEquals(5, minimums[1]);
    }

    {
      Solver solver = new Solver(2,
        new Limit(0).min(5).weight(1),
        new Limit(1).min(10).weight(2),
        new Limit(0, 1).min(16)
      );

      int[] results = new int[2];
      int spaceUsed = distribute(results, solver);

      Assert.assertEquals(16, spaceUsed);
      Assert.assertEquals(5, results[0]);
      Assert.assertEquals(11, results[1]);
    }
  }

  private static int distribute(int[] sizes, Solver solver) {
    Column[] columns = new Column[sizes.length];

    for(int i = 0; i < sizes.length; i++) {
      columns[i] = new Column(i);
      columns[i].weight = 1.0;
    }

    return MinimumSolver.fixMinimums(sizes, columns, solver.rootGroup, Arrays.asList(solver.restrictions));
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
  }
}
