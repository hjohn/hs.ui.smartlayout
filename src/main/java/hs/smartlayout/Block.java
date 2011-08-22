package hs.smartlayout;

import java.awt.Component;

/**
 * Represents an object at grid location x,y used by {@link SmartLayout}
 */
public class Block {
  private final Component component;
  private final Constraints constraints;
  private final LayoutConstraints layoutConstraints = new LayoutConstraints();
  private final int x;
  private final int y;

  public Block(Component component, Constraints constraints, int x, int y) {
    if(component == null) {
      throw new IllegalArgumentException("component cannot be null");
    }
    if(constraints == null) {
      throw new IllegalArgumentException("constraints cannot be null");
    }

    this.component = component;
    this.constraints = constraints;
    this.x = x;
    this.y = y;
  }

  public Component getComponent() {
    return component;
  }

  public Constraints getUserConstraints() {
    return constraints;
  }

  public LayoutConstraints getLayoutConstraints() {
    return layoutConstraints;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getMinWidth() {
    return constraints.getMinWidth() != Constraints.UNSET ? constraints.getMinWidth() : component.getPreferredSize().width;
  }

  public int getMinHeight() {
    return constraints.getMinHeight() != Constraints.UNSET ? constraints.getMinHeight() : component.getPreferredSize().height;
  }

  public int getMaxWidth() {
    return constraints.getMaxWidth() != Constraints.UNSET ? constraints.getMaxWidth() : component.getMaximumSize().width;
  }

  public int getMaxHeight() {
    return constraints.getMaxHeight() != Constraints.UNSET ? constraints.getMaxHeight() : component.getMaximumSize().height;
  }

  @Override
  public String toString() {
    return "Block at (" + x + ", " + y + ") size (" + constraints.getSpanX() + ", " + constraints.getSpanY() + ")";
  }

  public LineLimit getLimitsX() {
    return new LineLimit(layoutConstraints.minWidth.get(), layoutConstraints.minWidth.get(), layoutConstraints.maxWidth.get(), constraints.getWeightX());
  }

  public LineLimit getLimitsY() {
    return new LineLimit(layoutConstraints.minHeight.get(), layoutConstraints.minHeight.get(), layoutConstraints.maxHeight.get(), constraints.getWeightY());
  }
}
