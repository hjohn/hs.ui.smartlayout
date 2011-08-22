package hs.smartlayout;

public class LineLimit {
  public int minimum;
  public int preferred;
  public int maximum;
  public double weight;

  public LineLimit(int minimum, int preferred, int maximum, double weight) {
    this.minimum = minimum;
    this.preferred = preferred;
    this.maximum = maximum;
    this.weight = weight;
  }

  public LineLimit() {
    this(0, 0, 0, 0.0);
  }

  public void increaseWeight(double weight) {
    if(weight > this.weight) {
       this.weight = weight;
    }
  }

  public void increaseMinimum(int m) {
    if(m > minimum) {
      minimum = m;
      if(minimum > maximum) {
        maximum = minimum;
      }
      if(minimum > preferred) {
        preferred = minimum;
      }
    }
  }

  public void increasePreferred(int p) {
    if(p > preferred) {
      if(p < minimum) {
        preferred = minimum;
      }
      else {
        preferred = p;
      }
    }
  }

  public void decreaseMaximum(int m) {
    if(m < maximum) {
      if(m < minimum) {
        maximum = minimum;
      }
      else {
        maximum = m;
      }
    }
  }

  public void increaseMaximum(int m) {
    if(m > maximum) {
      maximum = m;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + maximum;
    result = prime * result + minimum;
    result = prime * result + preferred;
    long temp;
    temp = Double.doubleToLongBits(weight);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    final LineLimit other = (LineLimit) obj;
    if(maximum != other.maximum) {
      return false;
    }
    if(minimum != other.minimum) {
      return false;
    }
    if(preferred != other.preferred) {
      return false;
    }
    if(Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "[" + minimum + ", " + preferred + ", " + maximum + ", " + weight + "]";
  }
}