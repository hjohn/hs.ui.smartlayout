package hs.smartlayout.multicolumn;

import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;

import java.util.List;

public class MinimumSolver {

  public static int fixMinimums(int[] sizes, Column[] columns, Group rootGroup, List<Limit> limits) {
    int spaceUsed = 0;

    for(Limit limit : limits) {
      if(limit.startColumn == limit.endColumn) {
        sizes[limit.startColumn] = limit.getMinSize();
        spaceUsed += limit.getMinSize();
        columns[limit.startColumn].weight = limit.getWeight();
      }
    }

    for(;;) {
      Score bestScore = new Score(0);
      int neediest = -1;
      boolean minViolated = false;

      for(int j = 0; j < columns.length; j++) {
        Score score = new Score(columns[j].weight);

        score.improveGroupWeight(Double.POSITIVE_INFINITY);

        // Only comes in play if a component is below minimum:
        for(Limit limit : limits) {
          if(limit.contains(j)) {
            int currentSize = 0;

            for(int k = limit.startColumn; k <= limit.endColumn; k++) {
              currentSize += sizes[k];
            }

            if(currentSize < limit.getMinSize()) {
              score.addToMinimum(1);
              minViolated = true;
            }
            else if(currentSize >= limit.getMaxSize()) {
              // 2nd part checks maximum
              score.setMaximumReached(true);
            }
          }
        }

        score.setColumnWeight(-sizes[j] / columns[j].weight);  // -10 / 1.0 = -1      -20 / 2.0 = -1
        score.decreaseGroupWeight(-sizes[j] / columns[j].weight);  // -15 / 2.0 = -7.5       -25 / 2.0 = -12.5

        //System.out.println("For " + j + ": " + score.minimum + " : " + score.groupWeight);

        score.decreaseGroupWeight(-LoopingSpaceDistributor.calculateGroupStandardWeight(rootGroup.getGroup(j), columns, sizes));

        if(bestScore.compareTo(score) < 0) {
          bestScore = score;
          neediest = j;
        }
      }

      if(!minViolated) {
        return spaceUsed;
      }

      //System.out.println(">> add to column " + neediest);
      sizes[neediest]++;
      spaceUsed++;
    }
  }

  private static class Score implements Comparable<Score> {
    private double minimum = 0;
    private double groupWeight = Double.NEGATIVE_INFINITY;
    private double columnWeight = Double.NEGATIVE_INFINITY;
    private boolean maximumReached;
    private final double weight;

    public Score(double weight) {
      this.weight = weight;
    }

    public void setColumnWeight(double columnWeight) {
      this.columnWeight = columnWeight;
    }

    public void addToMinimum(double value) {
      minimum += value;
    }

    public void improveGroupWeight(double score) {
      if(score > groupWeight) {
        groupWeight = score;
      }
    }

    public void decreaseGroupWeight(double score) {
      if(score < groupWeight) {
        groupWeight = score;
      }
    }

    @Override
    public int compareTo(Score o) {

      /*
       * Standard Weight = size adjusted for weight; when comparing two sizes with each other taking weight into account, the size
       *                   must first be adjusted to make comparison possible.  This adjustment by dividing by weight transforms
       *                   the size into a size that can be compared as if both sizes had equal weights.  The naming is unfortunate,
       *                   better would have been to call it StandardSize. TODO rename to standard size, or weightAdjustedSize
       *
       * The neediest column is defined as the one that:
       * - violates the most minimums as part of a group; the more groups it is part of the more minimums it could possibly violate
       * - is contained in a group that has reached its maximum; columns not having reached a maximum are preferred
       * - the lowest group standard weight; note that these are inverted, so the highest value wins TODO swap that around for clarity
       * - the lowest column standard weight; see note above
       * - the highest unadjusted weight value
       */

      int result = Double.compare(minimum, o.minimum);

      if(result == 0) {
        if(maximumReached && !o.maximumReached) {
          result = -1;
        }
        else if(!maximumReached && o.maximumReached) {
          result = 1;
        }
        else {
          result = Double.compare(groupWeight, o.groupWeight);

          if(result == 0) {
            result = Double.compare(columnWeight, o.columnWeight);

            if(result == 0) {
              result = Double.compare(weight, o.weight);
            }
          }
        }
      }

      return result;
    }

    public void setMaximumReached(boolean maximumReached) {
      this.maximumReached = maximumReached;
    }
  }
}
