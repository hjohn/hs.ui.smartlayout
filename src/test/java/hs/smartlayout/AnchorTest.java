package hs.smartlayout;

import org.junit.Test;

public class AnchorTest {

  @Test
  public void shouldBeEast() {
    Assert.assertTrue(Anchor.EAST.isEast());
    Assert.assertTrue(Anchor.SOUTH_EAST.isEast());
    Assert.assertTrue(Anchor.NORTH_EAST.isEast());
  }

  @Test
  public void shouldNotBeEast() {
    Assert.assertFalse(Anchor.WEST.isEast());
    Assert.assertFalse(Anchor.SOUTH.isEast());
    Assert.assertFalse(Anchor.SOUTH_WEST.isEast());
    Assert.assertFalse(Anchor.NORTH.isEast());
    Assert.assertFalse(Anchor.NORTH_WEST.isEast());
  }

  @Test
  public void shouldBeWest() {
    Assert.assertTrue(Anchor.WEST.isWest());
    Assert.assertTrue(Anchor.SOUTH_WEST.isWest());
    Assert.assertTrue(Anchor.NORTH_WEST.isWest());
  }

  @Test
  public void shouldNotBeWest() {
    Assert.assertFalse(Anchor.EAST.isWest());
    Assert.assertFalse(Anchor.SOUTH.isWest());
    Assert.assertFalse(Anchor.SOUTH_EAST.isWest());
    Assert.assertFalse(Anchor.NORTH.isWest());
    Assert.assertFalse(Anchor.NORTH_EAST.isWest());
  }

  @Test
  public void shouldBeNorth() {
    Assert.assertTrue(Anchor.NORTH.isNorth());
    Assert.assertTrue(Anchor.NORTH_EAST.isNorth());
    Assert.assertTrue(Anchor.NORTH_WEST.isNorth());
  }

  @Test
  public void shouldNotBeNorth() {
    Assert.assertFalse(Anchor.SOUTH.isNorth());
    Assert.assertFalse(Anchor.SOUTH_WEST.isNorth());
    Assert.assertFalse(Anchor.SOUTH_EAST.isNorth());
    Assert.assertFalse(Anchor.WEST.isNorth());
    Assert.assertFalse(Anchor.EAST.isNorth());
  }

  @Test
  public void shouldBeSouth() {
    Assert.assertTrue(Anchor.SOUTH.isSouth());
    Assert.assertTrue(Anchor.SOUTH_EAST.isSouth());
    Assert.assertTrue(Anchor.SOUTH_WEST.isSouth());
  }

  @Test
  public void shouldNotBeSouth() {
    Assert.assertFalse(Anchor.NORTH.isSouth());
    Assert.assertFalse(Anchor.NORTH_WEST.isSouth());
    Assert.assertFalse(Anchor.NORTH_EAST.isSouth());
    Assert.assertFalse(Anchor.WEST.isSouth());
    Assert.assertFalse(Anchor.EAST.isSouth());
  }
}
