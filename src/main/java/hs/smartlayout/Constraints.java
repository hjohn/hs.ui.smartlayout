package hs.smartlayout;

import java.awt.Dimension;

/**
 * Represents user specified constraints on a GUI object.  Depending on the situation user
 * specified constraints can be overriden by the layout engine when components have technical
 * restrictions.
 */
public class Constraints {
  public static final int UNSET = -1;

  private int minWidth = UNSET;
  private int minHeight = UNSET;
  private int maxWidth = UNSET;
  private int maxHeight = UNSET;

  private double weightX;
  private double weightY;
  private Anchor anchor;

  private int spanX = 1;
  private int spanY = 1;

  public Constraints(Dimension minimum, Dimension maximum, Anchor anchor, double weightX, double weightY) {
    if(minimum != null) {
      minWidth = minimum.width;
      minHeight = minimum.height;
    }
    if(maximum != null) {
      maxWidth = maximum.width;
      maxHeight = maximum.height;
    }

    this.anchor = anchor;
    this.weightX = weightX;
    this.weightY = weightY;
  }

  public Constraints(Anchor anchor, double weightX, double weightY) {
    this(null, null, anchor, weightX, weightY);
  }

  public Constraints(double weightX, double weightY) {
    this(null, null, Anchor.NORTH_WEST, weightX, weightY);
  }

  public Constraints(Dimension minimum, Dimension maximum, Anchor anchor) {
    this(minimum, maximum, anchor, 1.0, 1.0);
  }

  public Constraints(Dimension minimum, Dimension maximum) {
    this(minimum, maximum, Anchor.NORTH_WEST, 1.0, 1.0);
  }

  public Constraints(Dimension minimum) {
    this(minimum, null, Anchor.NORTH_WEST, 1.0, 1.0);
  }

  public Constraints(Anchor anchor) {
    this(null, null, anchor, 1.0, 1.0);
  }

  public Constraints() {
    this(null, null, Anchor.NORTH_WEST, 1.0, 1.0);
  }

  public int getSpanX() {
    return spanX;
  }

  public Constraints setSpanX(int spanX) {
    this.spanX = spanX;
    return this;
  }

  public int getSpanY() {
    return spanY;
  }

  public Constraints setSpanY(int spanY) {
    this.spanY = spanY;
    return this;
  }

//  public Constraints setMinimum(Dimension d) {
//     setMinWidth(d.width);
//    setMinHeight(d.height);
//    return this;
//  }
//
//  public Constraints setMaximum(Dimension d) {
//     setMaxWidth(d.width);
//    setMaxHeight(d.height);
//    return this;
//  }
//
//  public Dimension getMinimum() {
//    return new Dimension(getMinWidth(), getMinHeight());
//  }
//
//   public Dimension getMaximum() {
//    return new Dimension(getMaxWidth(), getMaxHeight());
//  }

  public Constraints setMinWidth(int i) {
    minWidth = i;
    return this;
  }

  public Constraints setMinHeight(int i) {
    minHeight = i;
    return this;
  }

  public Constraints setMaxWidth(int i) {
    maxWidth = i;
    return this;
  }

  public Constraints setMaxHeight(int i) {
    maxHeight = i;
    return this;
  }

  public int getMinWidth() {
    return minWidth;
  }

  public int getMinHeight() {
    return minHeight;
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  public int getMaxHeight() {
    return maxHeight;
  }

  public double getWeightX() {
     return weightX;
  }

  public double getWeightY() {
     return weightY;
  }

  public Anchor getAnchor() {
    return anchor;
  }

  public Constraints setAnchor(Anchor a) {
    anchor = a;
    return this;
  }

  public Dimension getAnchorOffset(int sizeX, int sizeY, int spaceX, int spaceY) {
    int x = 0;
    int y = 0;

    if(anchor.isEast()) {
      x = spaceX - sizeX;
    }
    else if(!anchor.isWest()) {
      x = (spaceX - sizeX) / 2;
    }

    if(anchor.isSouth()) {
      y = spaceY - sizeY;
    }
    else if(!anchor.isNorth()) {
      y = (spaceY - sizeY) / 2;
    }

    return new Dimension(x, y);
  }

  public Constraints setWeightX(double wx) {
    weightX = wx;
    return this;
  }

  public Constraints setWeightY(double wy) {
    weightY = wy;
    return this;
  }

  @Override
  public String toString() {
    return getClass().getName() + "[(" + minWidth + ", " + minHeight + ")-(" + maxWidth + ", " + maxHeight + "), weight " + weightX + ", " + weightY + "]";
  }
}
