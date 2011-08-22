package hs.smartlayout;

/**
 * Class for storing constraints calculated by the layout engine.
 */
class LayoutConstraints {
  LayoutConstraint minWidth = new LayoutConstraint(1);
  LayoutConstraint minHeight = new LayoutConstraint(1);
  LayoutConstraint maxWidth = new LayoutConstraint(Integer.MAX_VALUE);
  LayoutConstraint maxHeight = new LayoutConstraint(Integer.MAX_VALUE);

  void reset() {
    minWidth.set(1);  // TODO this may need to be 0
    minHeight.set(1);
    maxWidth.set(Integer.MAX_VALUE);
    maxHeight.set(Integer.MAX_VALUE);
  }

  void fixMaximumConstraints() {

    // Ensures that no maximum constrains are smaller than their minimum
    // counterparts.

    maxWidth.setIfLarger(minWidth.get());
    maxHeight.setIfLarger(minHeight.get());
  }

  @Override
  public String toString() {
    return getClass().getName() + "[(" + minWidth + ", " + minHeight + ")-(" + maxWidth + ", " + maxHeight + ")]";
  }
}
