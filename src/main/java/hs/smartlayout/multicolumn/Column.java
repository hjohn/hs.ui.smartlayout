package hs.smartlayout.multicolumn;

public class Column {
  public final int index;

  public int size;
  public int minimumSize;
  public double weight;
  public double weightOffset;

  public Column(int index) {
    this.index = index;
  }

  public double getStandardWeight() {
    return minimumSize / weight;
  }
}