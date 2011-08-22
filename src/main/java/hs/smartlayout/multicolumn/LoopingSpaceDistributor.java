package hs.smartlayout.multicolumn;

import java.util.List;
import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;
import hs.smartlayout.distributor.SpaceDistributor;

public class LoopingSpaceDistributor implements SpaceDistributor {

  @Override
  public int[] distribute(int space, int columnCount, List<Limit> restrictions, Group rootGroup) {
    Column[] columns = new Column[columnCount];
    int[] sizes = new int[columnCount];

    for(int i = 0; i < columnCount; i++) {
      columns[i] = new Column(i);
      columns[i].weight = 1.0;
    }

    space -= MinimumSolver.fixMinimums(sizes, columns, rootGroup, restrictions);

    fixRest(sizes, columns, space, restrictions, rootGroup);

    return sizes;
  }

  private void fixRest(int[] sizes, Column[] columns, int space, List<Limit> restrictions, Group rootGroup) {
    for(;;) {
      Score bestScore = calculateBestScore(sizes, columns, restrictions, rootGroup);

      if(space <= 0 || bestScore.getColumn() == -1) {
        break;
      }

      if(bestScore.isAlternateMaximumReached() && columns[bestScore.getColumn()].weight != 0) {
        handleMaximum(rootGroup, columns, bestScore.getColumn(), sizes[bestScore.getColumn()]);
        continue; // redo calculation for this round
      }

      sizes[bestScore.getColumn()]++;
      // System.out.println(">> add to column " + bestScore.getColumn() + " : " + Arrays.toString(sizes));
      space--;
    }
  }

  private Score calculateBestScore(int[] sizes, Column[] columns, List<Limit> restrictions, Group rootGroup) {
    Score bestScore = new Score(-1, 0);
//    int totalSize = 0;
//    double totalWeight = 0;
//
//    for(int j = 0; j < columns.length; j++) {
//      totalSize += sizes[j];
//      totalWeight += columns[j].weight;
//    }

    for(int j = 0; j < columns.length; j++) {
      Score score = new Score(j, columns[j].weight);

      // Only comes in play if a component is below minimum:
      if(isMaximumReached(sizes, restrictions, j)) {
        score.setAlternateMaximumReached(true);
      }

//      double constant = totalSize / totalWeight;
//      double howMuchPixels = constant * columns[j].weight - columns[j].weightOffset;
//      double howMuchPixelsDiff = howMuchPixels - sizes[j];

      score.setColumnAndGroupWeight((-sizes[j] - columns[j].weightOffset) / columns[j].weight);  // -10 / 1.0 = -1      -20 / 2.0 = -1

      /*
       * Column weight was calculated, which is used if group weight matches to determine the best column within a group.
       *
       * Now, the smallest group for a column determines the primary group weight.  If any parent groups however have a
       * better weight, they overrule the smallest group.
       */

      // System.out.println("column = " + j + "; SW = " + (-calculateGroupStandardWeight(rootGroup.getGroup(j), columns, sizes)) + ", CW = " + ((-sizes[j] - columns[j].weightOffset) / columns[j].weight) + ", w = " + columns[j].weight + ", missing pixels = " +howMuchPixelsDiff + " g: " + calculateGroupMissingPixels(rootGroup.getGroup(j), columns, sizes, constant));

      score.decreaseGroupWeight(-calculateGroupStandardWeight(rootGroup.getGroup(j), columns, sizes));

      if(bestScore.compareTo(score) < 0) {
        bestScore = score;
      }
    }

    return bestScore;
  }

  private boolean isMaximumReached(int[] sizes, List<Limit> restrictions, int column) {
    for(Limit res : restrictions) {
      if(res.contains(column)) {
        int currentSize = 0;

        for(int k = res.startColumn; k <= res.endColumn; k++) {
          currentSize += sizes[k];
        }

        if(currentSize >= res.getMaxSize()) {
          return true;
        }
      }
    }

    return false;
  }

  private void handleMaximum(Group rootGroup, Column[] columns, int column, int columnSize) {
    // at this point, we need to rebalance weights and do this part again, or somehow assign
    // the pixel that might have been assigned to this column to another column in the same
    // group (which may also reach its maximum, etc...)

    double weightToAssign = columns[column].weight;
    double offsetToAssign = columnSize + columns[column].weightOffset;
    columns[column].weight = 0;
    columns[column].weightOffset = 0;

    Group group = rootGroup.getGroup(column);
    // System.out.println("Enter for " + column);
    while(group != null) {
      double totalWeight = 0;

      for(int i = group.start; i <= group.end; i++) {
        totalWeight += columns[i].weight;
      }

      if(totalWeight != 0) {
        for(int i = group.start; i <= group.end; i++) {
          columns[i].weightOffset += offsetToAssign / totalWeight * columns[i].weight;
          columns[i].weight += weightToAssign / totalWeight * columns[i].weight;
          // System.out.println("Assigned " + weights[i] + " (off=" + weightOffsets[i] + ") to " + i + " because " + column + " reached maximum");
        }

        break;
      }

      group = group.getParent();
    }
  }

//  private static double calculateGroupMissingPixels(Group group, Column[] columns, int[] sizes, double constant) {
//    int groupSize = 0;
//    double groupWeight = 0;
//
//    for(int k = group.start; k <= group.end; k++) {
//      groupSize += sizes[k];
//      groupWeight += columns[k].weight;
//    }
//
//    double howMuchPixels = constant * groupWeight;
//    double howMuchPixelsDiff = howMuchPixels - groupSize;
//
//    return howMuchPixelsDiff;
//  }

  public static double calculateGroupStandardWeight(Group group, Column[] columns, int[] sizes) {
    int groupSize = 0;
    double groupWeight = 0;

    for(int k = group.start; k <= group.end; k++) {
      groupSize += sizes[k];
      groupWeight += columns[k].weight;
    }

    double standardWeight = groupSize / groupWeight;

    while(group.getParent() != null) {
      group = group.getParent();

      groupSize = 0;
      groupWeight = 0;

      for(int k = group.start; k <= group.end; k++) {
        groupSize += sizes[k];
        groupWeight += columns[k].weight;
      }

      if(groupSize / groupWeight > standardWeight) {  // apparently should be ">" as otherwise parent groups cannot influence ratio
        standardWeight = groupSize / groupWeight;
      }
    }

    return standardWeight;
  }

  private static class Score implements Comparable<Score> {
    private double groupWeight = Double.NEGATIVE_INFINITY;
    private double columnWeight = Double.NEGATIVE_INFINITY;
    private boolean alternateMaximumReached;
    private final double weight;
    private final int column;

    public Score(int column, double weight) {
      this.column = column;
      this.weight = weight;
    }

    public int getColumn() {
      return column;
    }

    public void setColumnAndGroupWeight(double weight) {
      this.columnWeight = weight;
      this.groupWeight = weight;
    }

    public void decreaseGroupWeight(double score) {
      if(score < groupWeight) {
        groupWeight = score;
      }
    }

    @Override
    public int compareTo(Score o) {
      int result = Double.compare(groupWeight, o.groupWeight);

      if(result == 0) {
        result = Double.compare(columnWeight, o.columnWeight);

        if(result == 0) {
          result = Double.compare(weight, o.weight);
        }
      }

      return result;
    }

    public boolean isAlternateMaximumReached() {
      return alternateMaximumReached;
    }

    public void setAlternateMaximumReached(boolean b) {
      this.alternateMaximumReached = b;
    }
  }
}
