package hs.smartlayout;

/**
 * A wrapper around an integer value which can be shared among components.  These are
 * used to represent constraints set on a component which will also apply to all other
 * components sharing this constraint.  Example of constraints are minimum width and
 * height of a component.
 */
public class LayoutConstraint {
  private int value;

  public LayoutConstraint(int i) {
    value = i;
  }

  public void set(int i) {
    value = i;
  }

  public void setIfLarger(int i) {
    if(i > value) {
      value = i;
    }
  }

  public void setIfSmaller(int i) {
    if(i < value) {
      value = i;
    }
  }

  public int get() {
    return value;
  }

  @Override
  public String toString() {
    return "" + value;
  }
}
