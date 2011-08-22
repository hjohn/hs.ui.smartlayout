package hs.smartlayout;

/**
 * Enum for specifying the anchoring of GUI objects 
 */
public enum Anchor {
  CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST;
  
  public boolean isEast() {
    return this == EAST || this == NORTH_EAST || this == SOUTH_EAST;
  }
  
  public boolean isWest() {
    return this == WEST || this == NORTH_WEST || this == SOUTH_WEST;
  }
  
  public boolean isNorth() {
    return this == NORTH || this == NORTH_WEST || this == NORTH_EAST;
  }

  public boolean isSouth() {
    return this == SOUTH || this == SOUTH_WEST || this == SOUTH_EAST;
  }
}
