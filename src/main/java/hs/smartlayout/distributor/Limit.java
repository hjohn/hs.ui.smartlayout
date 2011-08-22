package hs.smartlayout.distributor;

public class Limit {
  public final int startColumn;
  public final int endColumn;

  private int minSize = 0;
  private int maxSize = Integer.MAX_VALUE;
  private double weight = 1.0;

  public Limit(int startColumn, int endColumn) {
    this.startColumn = startColumn;
    this.endColumn = endColumn;
  }

  public Limit(int column) {
    this(column, column);
  }

  public int getMinSize() {
    return minSize;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public double getWeight() {
    return weight;
  }

  public Limit min(int min) {
    this.minSize = min;
    return this;
  }

  public Limit max(int max) {
    this.maxSize = max;
    return this;
  }

  public Limit weight(double weight) {
    this.weight = weight;
    return this;
  }

//  @Override
//  public String toString() {
//    return "Restriction[column " + startColumn + (startColumn != endColumn ? "-" + endColumn : "") + ": " + minSize + "-" + maxSize + ", w = " + weight + "]";
//  }

  public boolean contains(int column) {
    return column >= startColumn && column <= endColumn;
  }
}