package hs.smartlayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A layout manager which works with groups of components.  The user of this layout manager
 * must divide components in logical groups (either horizontal with a specified number of rows
 * or vertical with a specified number of columns) and provide constraints for components.<p>
 *
 * A full explanation of this system is beyond the scope of this javadoc documentation.
 */
public class SmartLayout implements LayoutManager2 {

//  private static final Comparator<Block> SPAN_X_COMPARATOR = new Comparator<Block>() {
//    public int compare(Block o1, Block o2) {
//      return o1.getUserConstraints().getSpanX() - o2.getUserConstraints().getSpanX();
//    }
//  };

  private static final Comparator<Block> SPAN_Y_COMPARATOR = new Comparator<Block>() {
    @Override
    public int compare(Block o1, Block o2) {
      return o1.getUserConstraints().getSpanY() - o2.getUserConstraints().getSpanY();
    }
  };

  private final boolean vertical;
  private final int lines;
  private int horizontalSpacing;
  private int verticalSpacing;

  private final List<Block> blocks = new ArrayList<Block>();
  private final Map<Integer, Block> matrix = new HashMap<Integer, Block>();

  private Dimension minimum;
  private Dimension maximum;
  private boolean layoutConstraintsValid = false;
  private LineLimit[] columnLimit;
  private LineLimit[] rowLimit;

  private int insertPosition = 0;

  public SmartLayout(boolean vertical, int lines, int horizontalSpacing, int verticalSpacing) {
    this.vertical = vertical;
    this.lines = lines;
    this.horizontalSpacing = horizontalSpacing;
    this.verticalSpacing = verticalSpacing;
  }

  public SmartLayout(boolean vertical, int lines) {
    this(vertical, lines, 5, 5);
  }

  public SmartLayout(boolean vertical) {
    this(vertical, 1, 5, 5);
  }

  public int getHorizontalSpacing() {
    return horizontalSpacing;
  }

  public void setHorizontalSpacing(int spacing) {
    this.horizontalSpacing = spacing;
    layoutConstraintsValid = false;
  }

  public int getVerticalSpacing() {
    return verticalSpacing;
  }

  public void setVerticalSpacing(int spacing) {
    this.verticalSpacing = spacing;
    layoutConstraintsValid = false;
  }

  @Override
  public void addLayoutComponent(String name, Component c) {
    throw new UnsupportedOperationException("Components can only be added with constraints");
  }

  @Override
  public void addLayoutComponent(Component component, Object o) {
//    System.out.println("AddLayoutComponent " + component);
    Constraints c = o == null ? new Constraints() : (Constraints)o;
    int gridX = vertical ? insertPosition % lines : insertPosition / lines;
    int gridY = vertical ? insertPosition / lines : insertPosition % lines;

    /*
     * Sanity checks are done first before making any modifications so the state is
     * consistent even when an exception is thrown.
     */

    for(int y = gridY; y < gridY + c.getSpanY(); y++) {
      for(int x = gridX; x < gridX + c.getSpanX(); x++) {
        int position = vertical ? y * lines + x : x * lines + y;

        if(vertical && x >= lines) {
          throw new IllegalArgumentException("position + span size for " + component + " exceeds number of lines in group");
        }
        else if(!vertical && y >= lines) {
          throw new IllegalArgumentException("position + span size for " + component + " exceeds number of lines in group");
        }

        if(matrix.containsKey(position)) {
          throw new IllegalArgumentException("component overlaps existing component (" + matrix.get(position) + "): " + component);
        }
      }
    }

    /*
     * Add a new block now:
     */

    Block block = new Block(component, c, gridX, gridY);

    blocks.add(block);

    for(int y = gridY; y < gridY + c.getSpanY(); y++) {
      for(int x = gridX; x < gridX + c.getSpanX(); x++) {
        int position = vertical ? y * lines + x : x * lines + y;

        matrix.put(position, block);
      }
    }

    /*
     * Calculate next insert position
     */

    while(matrix.containsKey(++insertPosition)) {
    }

    layoutConstraintsValid = false;
  }

  @Override
  public float getLayoutAlignmentX(Container c) {
    return 0.0f;
  }

  @Override
  public float getLayoutAlignmentY(Container c) {
    return 0.0f;
  }

  @Override
  public void invalidateLayout(Container c) {
    layoutConstraintsValid = false;
    // System.out.println("invalidatelayout" + System.currentTimeMillis());
  }

  private void ensureLayoutConstraintsAreValid(Container container) {
    if(!layoutConstraintsValid) {
      calculate(container);
    }
  }

  @Override
  public void layoutContainer(Container container) {
    ensureLayoutConstraintsAreValid(container);

    // System.out.println("SMARTLAYOUT: LayoutContainer(" + container + ")");

    if(!blocks.isEmpty()) {

      /* Layout can be either horizontally based or vertically based.

         Horizontal layout means that the number of rows is fixed and objects
         are evenly distributed among the rows.  Below an example with 2 rows:

         1 3 5 7 .
         2 4 6 . .

         Vertical layout means that the number of columns is fixed and objects
         are evenly distributed among the columns.  Below an example with 2
         columns:

         1 2
         3 4
         5 6
         7 .
         . .

         Depending on the horizontal or vertical layout, the number of lines
         which the user specified respectively indicates the number of fixed
         rows or columns. */

      int columns = vertical ? lines : (insertPosition + lines - 1) / lines;
      int rows = vertical ? (insertPosition + lines - 1) / lines : lines;

      /* Get information about the dimensions of the layout area */

      Insets insets = container.getInsets();

      /* Prepare to calculate the sizes of the rows and columns */

      LayoutRequirements[] lrX = createLayoutRequirements(columns, columnLimit);
      LayoutRequirements[] lrY = createLayoutRequirements(rows, rowLimit);

      /* Calculate the sizes of the rows and columns */

      int containerContentWidth = container.getWidth() - (insets.left + insets.right) - ((columns - 1) * horizontalSpacing);
      int containerContentHeight = container.getHeight() - (insets.top + insets.bottom) - ((rows - 1) * verticalSpacing);

//      System.out.println("container = " + container.getWidth() + "x" + container.getHeight() + " --> " + containerContentWidth + "x" + containerContentHeight);

      int[] spansX = LayoutRequirements.calculateTiledPositions(containerContentWidth, lrX);
      int[] spansY = LayoutRequirements.calculateTiledPositions(containerContentHeight, lrY);

//      System.out.println(container.getName() + ": lrX = " + Arrays.toString(lrX));
//      System.out.println(container.getName() + ": lrY = " + Arrays.toString(lrY));
//
//      System.out.println(container.getName() + ": xspans = " + Arrays.toString(spansX));
//      System.out.println(container.getName() + ": yspans = " + Arrays.toString(spansY));

      /* Perform the layout */

      for(Block block : blocks) {
        Constraints uc = block.getUserConstraints();
        LayoutConstraints lc = block.getLayoutConstraints();
        int x = block.getX();
        int y = block.getY();

        // TODO this can be more efficient
        // For example, Spans could contain a cumulative value
        int offsetX = sumArray(spansX, 0, x) + horizontalSpacing * x;
        int offsetY = sumArray(spansY, 0, y) + verticalSpacing * y;

        int w = calculateSize(spansX, x, uc.getSpanX(), horizontalSpacing);
        int h = calculateSize(spansY, y, uc.getSpanY(), verticalSpacing);

        int boxW = w;
        int boxH = h;

        if(w > lc.maxWidth.get()) {
          w = lc.maxWidth.get();
        }

        if(h > lc.maxHeight.get()) {
          h = lc.maxHeight.get();
        }

        /*
         * When a group only contains a single row and/or column, boxW and boxH need to be adjusted to use the size
         * of the group for the respective dimension.
         */

        if(columns == 1 && boxW < container.getWidth()) {
          boxW = containerContentWidth;
        }
        if(rows == 1 && boxH < container.getHeight()) {
          boxH = containerContentHeight;
        }


//        System.out.println("Box size = (" + boxW + ", " + boxH + "), w, h = (" + w + ", " + h + ")");

        Dimension anchorOffset = uc.getAnchorOffset(w, h, boxW, boxH);

//        System.out.println(block.getComponent().getClass() + " -- " + block.getComponent().getParent().getClass());

//        if(block.getComponent() instanceof JScrollPane) {
//          System.out.println(block.getComponent().getClass() + " -- " + block.getComponent().getParent().getClass());
//          w = 500;
//          h = 500;
//        }

        block.getComponent().setBounds(insets.left + offsetX + anchorOffset.width, insets.top + offsetY + anchorOffset.height, w, h);
      }
    }
  }

  private static int sumArray(int[] array, int start, int count) {
    int sum = 0;

    for(int i = start; i < start + count; i++) {
      sum += array[i];
    }

    return sum;
  }

  private LayoutRequirements[] createLayoutRequirements(int lines, LineLimit[] lineLimits) {
    LayoutRequirements[] lr = new LayoutRequirements[lines];

    for(int i = 0; i < lines; i++) {
      lr[i] = new LayoutRequirements(lineLimits[i].minimum, lineLimits[i].maximum, lineLimits[i].weight);
    }

    return lr;
  }

  private int calculateSize(int[] spans, int spanOffset, int spanSize, int spacing) {
    int size = 0;

    for(int i = 0; i < spanSize; i++) {
      if(size != 0) {
        size += spacing;
      }
      size += spans[spanOffset + i];
    }

    return size;
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    ensureLayoutConstraintsAreValid(parent);

    return new Dimension(minimum);
  }

  @Override
  public Dimension maximumLayoutSize(Container parent) {
    ensureLayoutConstraintsAreValid(parent);

    return new Dimension(maximum);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    ensureLayoutConstraintsAreValid(parent);

    return new Dimension(minimum);
  }

  @Override
  public void removeLayoutComponent(Component comp) {
    // This would require us to recalculate the position of each Block.  This
    // functionality does not make a whole lot of sense for this kind of layout
    // as it would usually result in the entire layout significantly changing.
//    throw new UnsupportedOperationException();
//    components.remove(comp);

    List<Block> copy = new ArrayList<Block>(blocks);

    blocks.clear();
    matrix.clear();
    insertPosition = 0;

    for(Block block : copy) {
      if(!block.getComponent().equals(comp)) {
        addLayoutComponent(block.getComponent(), block.getUserConstraints());
      }
    }
  }

  private void calculate(Container parent) {
    int columns = vertical ? lines : (insertPosition + lines - 1) / lines;
    int rows = vertical ? (insertPosition + lines - 1) / lines : lines;

    columnLimit = new LineLimit[columns];
    rowLimit = new LineLimit[rows];

    if(!blocks.isEmpty()) {
      calculateLimits(blocks, columnLimit, rowLimit);

      long minHeight = 0;
      long maxHeight = 0;

      for(int r = 0; r < rows; r++) {
        minHeight += rowLimit[r].minimum;
        maxHeight += rowLimit[r].maximum;
      }

      long minWidth = 0;
      long maxWidth = 0;

      for(int c = 0; c < columns; c++) {
        minWidth += columnLimit[c].minimum;
        maxWidth += columnLimit[c].maximum;
      }

      Insets i = parent.getInsets();

      minWidth += i.left + i.right + (columns-1) * horizontalSpacing;
      minHeight += i.top + i.bottom + (rows-1) * verticalSpacing;
      maxWidth += i.left + i.right + (columns-1) * horizontalSpacing;
      maxHeight += i.top + i.bottom + (rows-1) * verticalSpacing;

      minimum = new Dimension((int)Math.min(minWidth, Integer.MAX_VALUE), (int)Math.min(minHeight, Integer.MAX_VALUE));
      maximum = new Dimension((int)Math.min(maxWidth, Integer.MAX_VALUE), (int)Math.min(maxHeight, Integer.MAX_VALUE));
    }
    else {
      minimum = new Dimension(0, 0);
      maximum = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    layoutConstraintsValid = true;
  }

  public static void calculateLimits(List<Block> blocks, LineLimit[] columnLimit, LineLimit[] rowLimit) {

    /* Builds a list of the Constraints of each object. */

    for(Block block : blocks) {
      block.getLayoutConstraints().reset();
    }

    /* The layoutConstraints portion of each Constraint object is fully shared
       at this point.  Setting the value of the layoutConstraints therefore
       should take this sharing into account -- completely overriding existing
       values is not an option. */

    for(Block block : blocks) {
      LayoutConstraints layoutConstraints = block.getLayoutConstraints();
      Component c = block.getComponent();

      layoutConstraints.minWidth.setIfLarger(block.getMinWidth());
      layoutConstraints.minHeight.setIfLarger(block.getMinHeight());
      layoutConstraints.maxWidth.setIfSmaller(block.getMaxWidth());
      layoutConstraints.maxHeight.setIfSmaller(block.getMaxHeight());

      // TODO Not sure if this is the idea..
      if(c instanceof JScrollPane && ((JScrollPane)c).getViewport().getView() instanceof JPanel) {
        layoutConstraints.minWidth = new LayoutConstraint(1);
        layoutConstraints.minHeight = new LayoutConstraint(1);
      }

      // System.out.println("Constraints maxSize = " + c.getMaximumSize() + " : "  + uc.layoutConstraints + " / " + uc + " for " + c);
      // System.out.println(parent.getName() + ": lc(" + n + ") = " + uc.layoutConstraints);
    }

    /* This last loop must be done after setting all values. */

    for(Block b : blocks) {
      b.getLayoutConstraints().fixMaximumConstraints();
    }

    /* Set values for horizontal or vertical layout */

    for(int i = 0; i < columnLimit.length; i++) {
      columnLimit[i] = new LineLimit();
    }

    for(int i = 0; i < rowLimit.length; i++) {
      rowLimit[i] = new LineLimit();
    }

    // TODO does not yet perform correct calculations for components with a span > 1

//    {
//      List<Block> sortedBlocks = new ArrayList<Block>(blocks);
//      Collections.sort(sortedBlocks, SPAN_X_COMPARATOR);
//
//      for(Block b : sortedBlocks) {
////          System.out.println("Column Processing " + b);
//        Constraints constraints = b.getUserConstraints();
//        LayoutConstraints lc = constraints.layoutConstraints;
//        int c = b.getX();
//
//        columnLimit[c].increaseMinimum(lc.minWidth.get());
//        columnLimit[c].increasePreferred(lc.minWidth.get());
//        columnLimit[c].increaseMaximum(lc.maxWidth.get());
//        columnLimit[c].increaseWeight(constraints.getWeightX());
//      }
//    }

    calculateLimits(blocks, columnLimit, true);
    calculateLimits(blocks, rowLimit, false);

//      System.out.println("cols = " + Arrays.toString(columnLimit));
//      System.out.println("rows = " + Arrays.toString(rowLimit));

//      for(int i = 0; i < blocks.size(); i++) {
//        Block b = blocks.get(i);
//
//        Constraints constraints = b.getUserConstraints();
//        int c = b.getX();
//        int r = b.getY();
//
//        LayoutConstraints lc = constraints.layoutConstraints;
//
//        columnLimit[c].increaseMinimum(lc.minWidth.get());
//        columnLimit[c].increasePreferred(lc.minWidth.get());
//        columnLimit[c].increaseMaximum(lc.maxWidth.get());
//        columnLimit[c].setWeight(constraints.getWeightX());
//
//        rowLimit[r].increaseMinimum(lc.minHeight.get());
//        rowLimit[r].increasePreferred(lc.minHeight.get());
//        rowLimit[r].increaseMaximum(lc.maxHeight.get());
//        rowLimit[r].setWeight(constraints.getWeightY());
//      }

//    for(int r = 0; r < rows; r++) {
//      for(int c = 0; c < columns; c++) {
//        int n = vertical ? r * columns + c : c * rows + r;
//
//        /* It is possible that there are less components in this
//           group than rows*columns.  In this case the remaining
//           empty spaces are ignored. */
//
//        if(n < size) {
//          Constraints constraints = userConstraints.get(n);
//          LayoutConstraints lc = constraints.layoutConstraints;
//
//          columnLimit[c].increaseMinimum(lc.minWidth.get());
//          columnLimit[c].increasePreferred(lc.minWidth.get());
//          columnLimit[c].increaseMaximum(lc.maxWidth.get());
//          // System.out.println("column, row = "+c+", "+r+": maxWidth, maxHeight = "+lc.maxHeight.get()+", "+lc.maxWidth.get());
//          columnLimit[c].setWeight(constraints.getWeightX());
//
//          rowLimit[r].increaseMinimum(lc.minHeight.get());
//          rowLimit[r].increasePreferred(lc.minHeight.get());
//          rowLimit[r].increaseMaximum(lc.maxHeight.get());
//          rowLimit[r].setWeight(constraints.getWeightY());
//        }
//      }
//    }
  }

  private static void calculateLimits(List<Block> blocks, LineLimit[] lineLimit, boolean x) {
    List<Block> sortedBlocks = new ArrayList<Block>(blocks);
    Collections.sort(sortedBlocks, SPAN_Y_COMPARATOR);

    for(Block b : sortedBlocks) {
//        System.out.println("Row Processing " + b);

      // JOHNTODO Simply, and do this for columns as well
      // JOHNTODO Minimums are handled for spans > 1, what about maximum?
      // JOHNTODO Are we happy with how weights are handled?  Span3 = 3.0, and 1.0 + 1.0 + 1.5 = 3.5...
      // JOHNTODO Is preferred used?
      // JOHNTODO What about row/column code similarity?

      LineLimit blockLimit = x ? b.getLimitsX() : b.getLimitsY();
      Constraints constraints = b.getUserConstraints();

      int span = x ? constraints.getSpanX() : constraints.getSpanY();
      int position = x ? b.getX() : b.getY();

      //LayoutConstraints lc = constraints.layoutConstraints;
      if(span == 1) {
        lineLimit[position].increaseMinimum(blockLimit.minimum);
        lineLimit[position].increasePreferred(blockLimit.minimum);
        lineLimit[position].increaseMaximum(blockLimit.maximum);
        lineLimit[position].increaseWeight(blockLimit.weight);
      }
      else {
        LayoutRequirements[] lr = new LayoutRequirements[span];

        for(int r = 0; r < span; r++) {
          LineLimit limit = lineLimit[r + position];
          lr[r] = new LayoutRequirements(limit.minimum, limit.maximum, limit.weight);
          // System.out.println(lr[r]);
        }

        int[] results = LayoutRequirements.calculateTiledPositions(blockLimit.minimum, lr);
        // System.out.println("calculateTiledPositions(" + blockLimit.minimum + ") --> " + Arrays.toString(results));

        for(int r = 0; r < span; r++) {
          LineLimit limit = lineLimit[r + position];

          limit.increaseMinimum(results[r]);
//          limit.increasePreferred(lc.minHeight.get());
  //        limit.increaseMaximum(lc.maxHeight.get());
    //      limit.increaseWeight(constraints.getWeightY());
        }
      }
    }
  }
}