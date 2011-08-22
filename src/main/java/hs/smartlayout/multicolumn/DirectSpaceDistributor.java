package hs.smartlayout.multicolumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import hs.smartlayout.LayoutRequirements;
import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;
import hs.smartlayout.distributor.SpaceDistributor;

public class DirectSpaceDistributor implements SpaceDistributor {
  private static final boolean VERBOSE = false;

  @Override
  public int[] distribute(int space, int columnCount, List<Limit> limits, Group rootGroup) {
    Column[] columns = new Column[columnCount];
    int[] sizes = new int[columnCount];

    for(int i = 0; i < columnCount; i++) {
      columns[i] = new Column(i);
      columns[i].weight = 1.0;
    }

    space -= MinimumSolver.fixMinimums(sizes, columns, rootGroup, limits);

    for(int i = 0; i < columns.length; i++) {
      columns[i].minimumSize = sizes[i];
    }

    return new Solver(columns, rootGroup, limits).fixRest2(space);
  }

  private static class Solver {
    private final Column[] columns;
    private final Group rootGroup;
    private final List<Limit> limits;

    public Solver(Column[] columns, Group rootGroup, List<Limit> limits) {
      this.columns = columns;
      this.rootGroup = rootGroup;
      this.limits = limits;
    }

    private int[] fixRest2(int space) {
      LayoutRequirements[] requirements = new LayoutRequirements[columns.length];
      // Assign space by weight

      double totalWeight = 0;
      int assignedSpace = 0;

      for(int i = 0; i < columns.length; i++) {
        assignedSpace += columns[i].minimumSize;
        totalWeight += columns[i].weight;
        requirements[i] = new LayoutRequirements(columns[i].minimumSize, Integer.MAX_VALUE, columns[i].weight);
      }

      int[] sizes = LayoutRequirements.calculateTiledPositions(assignedSpace + space, requirements);

      for(int i = 0; i < columns.length; i++) {
        columns[i].size = sizes[i];
      }

//      solveMinimums();

      /*
       * The sizes array now contains the sizes of the columns disregarding any maximum restrictions.
       * Maximum restrictions will now be applied in order of when they would have been reached.  In
       * other words, given an expanding space, the maximum that is reached first will be applied
       * first.
       *
       * In effect, when a maximum is reached, the columns that it encompasses are adjusted to
       * reflect the maximum value.  Any space that is leftover after the adjustment is redistributed
       * amongst the group(s) encompassing the column(s).
       *
       * This process is repeated until either no restrictions are left unprocessed or no maximum was
       * reached.
       */

      List<Limit> uncheckedLimits = new ArrayList<Limit>(limits);

      for(;;) {
        Limit bestLimit = findEarliestMaximum(uncheckedLimits);

        if(bestLimit == null || !exceedsMaximumSize(bestLimit)) {
          break;
        }

        // step 3

        // a) Redivide pixels (if group) by weight and minimums -- keep track where pixels are lost
        // b) For each column that is covered by the restriction set weight to 0 and do a fixMaximums.  Assign the lost pixels based on the column.

        int[] lostPixels = lowerToMatchMaximum(bestLimit);

        uncheckedLimits.remove(bestLimit);  // remove, as minimums may cause a maximum to be violated

        redivideMaximum(bestLimit, lostPixels);
      }

      for(int i = 0; i < columns.length; i++) {
        sizes[i] = columns[i].size;
      }

      return sizes;
    }

    /*
     * Traverses all limits and returns the one which would reach its maximum first.  Also checks
     * if maximum was actually reached... which is a 2nd thing this function does.
     */
    private Limit findEarliestMaximum(List<Limit> limits) {
      Limit bestLimit = null;
      double lowestStandardWeight = Double.MAX_VALUE;  // TODO Double.POSITIVE_INFINITY ??

      for(Limit limit : limits) {
        double weight = 0;
        int fixedSize = 0;

        for(int k = limit.startColumn; k <= limit.endColumn; k++) {
          weight += columns[k].weight;
          if(columns[k].weight == 0) {
            fixedSize += columns[k].size;
          }
        }

        double standardWeight = (limit.getMaxSize() - fixedSize) / weight;
        if(VERBOSE) {
          System.out.println("SW for " + limit + ": " + limit.getMaxSize() + " / " + weight + " = " + standardWeight);
        }

        if(standardWeight < lowestStandardWeight) {
          bestLimit = limit;
          lowestStandardWeight = standardWeight;
        }
      }

      return bestLimit;
    }

    private boolean exceedsMaximumSize(Limit limit) {
      int size = 0;

      for(int k = limit.startColumn; k <= limit.endColumn; k++) {
        size += columns[k].size;
      }

      return size > limit.getMaxSize();
    }

    /*
     * Takes a restriction and adjusts the sizes of the columns in encompasses so they match
     * the maximum of this restriction.  Minimums and weights are taken into account during
     * this adjustment.  It returns an array which contains the space removed from each
     * column this restriction encompasses.
     */
    private int[] lowerToMatchMaximum(Limit limit) {
      if(VERBOSE) {
        System.out.println("lowerToMatchMaximum(" + limit + "; sizes = " + Arrays.toString(columns) + ")");
      }
      // 1:1   [15, 15]   min [10, 0]   max-total[12] --> [10, 2]

      int[] pixelsLost = new int[limit.endColumn - limit.startColumn + 1];

      int spaceLeft = limit.getMaxSize();
      double totalWeight = 0;

      for(int k = limit.startColumn; k <= limit.endColumn; k++) {
        totalWeight += columns[k].weight;
      }

      Column[] sorted = Arrays.copyOfRange(columns, limit.startColumn, limit.endColumn + 1);
      Arrays.sort(sorted, new Comparator<Column>() {
        @Override
        public int compare(Column o1, Column o2) {
          return Double.compare(o2.getStandardWeight(), o1.getStandardWeight());
        }
      });

      // smallest weight and biggest minimum size first

      for(Column column : sorted) {
        int desired = (int)(spaceLeft / totalWeight * column.weight);

        if(desired < column.minimumSize) {
          desired = column.minimumSize;
        }

        if(VERBOSE) {
          System.out.println("sizes[" + column.index + "] = " + columns[column.index].size + "; desired = " + desired);
        }
        assert columns[column.index].size - desired >= 0;

        pixelsLost[column.index - limit.startColumn] = columns[column.index].size - desired;
        columns[column.index].size = desired;
        spaceLeft -= desired;
        totalWeight -= column.weight;

        if(VERBOSE) {
          System.out.println("Assigned " + desired + " to column " + column.index + ": weight left = " + totalWeight + "; spaceLeft = " + spaceLeft);
        }
      }

      return pixelsLost;
    }

    private void redivideMaximum(Limit limit, int[] leftOverPixels) {
      if(VERBOSE) {
        System.out.println("redivideMaximum(" + limit + ")");
      }

      for(int columnNumber = limit.startColumn; columnNumber <= limit.endColumn; columnNumber++) {
        Column columnToFix = columns[columnNumber];

        Group group = findSmallestUnfixedGroup(limit, columnToFix.index);

        if(group != null) {
          distributeOverflow(limit, leftOverPixels[columnToFix.index - limit.startColumn], columnToFix, group);
        }

        columnToFix.weight = 0;
        columnToFix.weightOffset = 0;
        columnToFix.minimumSize = columnToFix.size;
      }
    }

    /*
     * Finds the group for the given column which is the smallest group that still has some weight
     * to work with.  Returns null if there is no such group; since there always is a group
     * encompassing all columns, this last case only occurs if all columns reached their maximums.
     */
    private Group findSmallestUnfixedGroup(Limit limitToAvoid, int columnIndex) {
      Group group = rootGroup.getGroup(columnIndex);

      do {
        for(int i = group.start; i <= group.end; i++) {
          if(!limitToAvoid.contains(i) && columns[i].weight != 0) {
            return group;
          }
        }

        group = group.getParent();
      } while(group != null);

      return null;
    }

    private void distributeOverflow(Limit restriction, int overflow, Column columnToFix, Group group) {
      double totalWeight = 0;
      double totalSize = 0;

      for(int i = group.start; i <= group.end; i++) {
        if(!restriction.contains(i) && columns[i].weight != 0) {
          totalWeight += columns[i].weight;
          totalSize += columns[i].size + columns[i].weightOffset;
        }
      }

      totalSize += overflow;

      if(VERBOSE) {
        System.out.println("For column " + columnToFix.index + ": leftOverPixels = " + overflow + "; tw = " + totalWeight);
      }

      double weightToAssign = columnToFix.weight;
      double offsetToAssign = columnToFix.size + columnToFix.weightOffset;
      double tw = totalWeight;

      for(int i = group.start; i <= group.end; i++) {
        Column column = columns[i];

        if(!restriction.contains(i) && column.weight != 0) {
          int x = (int)Math.round(totalSize / tw * column.weight - column.weightOffset) - column.size;

          column.size += x;
          totalSize -= column.size + column.weightOffset;
          tw -= column.weight;

          column.weightOffset += offsetToAssign / totalWeight * column.weight;
          column.weight += weightToAssign / totalWeight * column.weight;

          if(VERBOSE) {
            System.out.println("Changed column " + i + " to " + column.weight + " weight (off=" + column.weightOffset + ") and added " + x + " pixels to become " + column.size + " because column " + columnToFix.index + " reached maximum" );
          }
        }
      }
    }

//    private void solveMinimums() {
//      List<Restriction> uncheckedRestrictions = new ArrayList<Restriction>(restrictions);
//
//      for(;;) {
//        Restriction bestRestriction = findLatestMinimum(uncheckedRestrictions);
//
//        if(bestRestriction == null) {
//          break;
//        }
//
//        System.out.println("Need to fix minimum for " + bestRestriction);
//
//        // step 3
//
//        // a) Redivide pixels (if group) by weight and minimums -- keep track where pixels are lost
//        // b) For each column that is covered by the restriction set weight to 0 and do a fixMaximums.  Assign the lost pixels based on the column.
//
//        int[] lostPixels = increaseToMatchMinimum(bestRestriction);
//
//        uncheckedRestrictions.remove(bestRestriction);  // remove, as minimums may cause a maximum to be violated
//
//        redivideMaximum(bestRestriction, lostPixels);
//      }
//    }

    /*
     * Traverses all restrictions and returns the one which would reach its minimum last.
     */
//    private Restriction findLatestMinimum(List<Restriction> restrictions) {
//      Restriction bestRestriction = null;
//      double highestStandardWeight = Double.NEGATIVE_INFINITY;
//
//      for(Restriction res : restrictions) {
//        double weight = 0;
//        int size = 0;
//        int fixedSize = 0;
//
//        for(int k = res.startColumn; k <= res.endColumn; k++) {
//          weight += columns[k].weight;
//          size += columns[k].size;
//          if(columns[k].weight == 0) {
//            fixedSize += columns[k].size;
//          }
//        }
//
////        double standardWeight = (res.getMinSize() - fixedSize) / weight;
//        double standardWeight = res.getMinSize() / weight;
//        if(!VERBOSE) {
//          System.out.println("MinSW for " + res + ": " + res.getMinSize() + " / " + weight + " = " + standardWeight);
//        }
//
//        if(size < res.getMinSize() && standardWeight > highestStandardWeight) {
//          bestRestriction = res;
//          highestStandardWeight = standardWeight;
//        }
//      }
//
//      return bestRestriction;
//    }
  }
}
