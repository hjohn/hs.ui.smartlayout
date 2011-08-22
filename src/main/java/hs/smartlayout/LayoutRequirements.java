package hs.smartlayout;

import java.util.TreeSet;

/**
 * LayoutRequirements is a Class which contains mainly
 * static methods.  The Static methods are used to process
 * LayoutRequirements objects.
 */
public class LayoutRequirements {
  private final int minimum;
  private final int maximum;
  private final double weight;

  public LayoutRequirements(int min, int max, double weight) {
    minimum = min;
    maximum = Math.max(min, max);
    this.weight = minimum == maximum ? 0 : weight;
  }

  /**
   * Calculates for each of the passed LayoutRequirements the size of
   * the span they would occupy.
   */
  public static int[] calculateTiledPositions(int allocatedInput, LayoutRequirements... children) {
    double w = 0.0;
    int allocated = allocatedInput;
    int allocatedNonFixed = allocated;

    TreeSet<Sizes> sizes = new TreeSet<Sizes>();  // Doesn't allow duplicates, Sizes compareTo() never returns 0
    int[] spans = new int[children.length];

    /* Calculates for what allocated size a certain component will reach its maximum size.
       In that case the component will automatically get a span equal to its maximum size. */

    for(int i = 0; i < spans.length; i++) {
      spans[i] = children[i].minimum;

      if(children[i].weight != 0) {
        double m = children[i].maximum; // Allocated space needed for component i to reach maximum size.
        double sw = children[i].maximum / children[i].weight; // Standardized weight.

//        System.out.println("children[" + i + "].weight = " + children[i].weight + " != 0");

        for(int j = 0; j < spans.length; j++) {
          if(i != j) {
            m += Math.max(Math.min(children[j].weight * sw, children[j].maximum), children[j].minimum);
          }
        }

        if(m <= allocated) {
          // System.out.println("Maximum size met: min = " + children[i].minimum);
          // The maximum size for component i can be met.
          spans[i] = children[i].maximum;
          allocatedNonFixed -= children[i].maximum;
        }
        else {
          // System.out.println("Maximum size NOT met: min = " + children[i].minimum + " m = " + m + " allocated = " + allocated);

          w += children[i].weight;

          // Only adding components that donot have a fixed size yet
          sizes.add(new Sizes(spans[i], children[i].weight, i));
        }
      }
      else {
        spans[i] = children[i].minimum;
        allocatedNonFixed -= children[i].minimum;
      }
    }

    /* allocated         : The amount of pixels which can be used to layout
                           components.

       allocatedNonFixed : The amount of pixels which is still available for
                           components which are still scalable.  This value is
                           equal to allocated minus the sizes of all components
                           which will become their maximum size. */

    for(Sizes s : sizes) {
      int span = s.minimum;

      if(s.standardisedWeight * w < allocatedNonFixed) {
        span = (int)Math.round(allocatedNonFixed / w * s.weight);
      }
      // System.out.println("sw = " + s.standardisedWeight + "  w = " + w + "  span = " + span + "  allocatedNonFixed = " + allocatedNonFixed + " --> " + span);

      allocatedNonFixed -= span;
      w -= s.weight;

      spans[s.sequence] = span;
    }

    return spans;
  }

  @Override
  public String toString() {
    return "LayoutRequirements(min = " + minimum + ", max = " + maximum + ", w = " + weight + ")";
  }

  private static class Sizes implements Comparable<Sizes> {
    public final int minimum;
    public final double weight;
    public final double standardisedWeight;
    public final int sequence;

    public Sizes(int m, double w, int s) {
      minimum = m;
      weight = w;
      sequence = s;

      if(w != 0) {
        standardisedWeight = m / w;
      }
      else {
        standardisedWeight = 0;
      }
    }

    /**
     * Compares two Sizes objects.  <b>Note:</b> this function never returns 0 for equal
     * objects to make it possible to add duplicate Sizes objects to java sets.
     */
    @Override
    public int compareTo(Sizes sizes) {
      double d = sizes.standardisedWeight - standardisedWeight;

      if(d < 0) {
        return -1;
      }
      else if(d > 0) {
        return 1;
      }
      else {
        d = sizes.weight - weight;

        if(d < 0) {
          return -1;
        }
        else if(d > 0) {
          return 1;
        }
        else {
          return sequence - sizes.sequence;
        }
      }
    }

    @Override
    public String toString() {
      return "Sizes(" + minimum + ", " + weight + ", " + standardisedWeight + ")";
    }
  }
}
