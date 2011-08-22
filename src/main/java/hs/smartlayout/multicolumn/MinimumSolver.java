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
