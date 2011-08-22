package hs.smartlayout.oldtestcases;

import hs.smartlayout.LayoutRequirements;
import junit.framework.TestCase;

public class TilingTestCase extends TestCase {

  public void testBasic() {
    LayoutRequirements[] children = new LayoutRequirements[3];

    children[0] = new LayoutRequirements(20, Integer.MAX_VALUE, 1.0);
    children[1] = new LayoutRequirements(20, Integer.MAX_VALUE, 1.0);
    children[2] = new LayoutRequirements(20, Integer.MAX_VALUE, 1.0);
    
    // System.out.println(LayoutRequirements.getTiledLayoutRequirements(children));
    
    int[] spans = LayoutRequirements.calculateTiledPositions(99, children);

    // System.out.println(spans[0] + " - " + spans[1] + " - " + spans[2]);
    
    assertEquals(99, spans[0] + spans[1] + spans[2]);
    assertEquals(33, spans[0]);
    assertEquals(33, spans[1]);
  }
  
  public void testMaximum() {
    LayoutRequirements[] children = new LayoutRequirements[3];

    children[0] = new LayoutRequirements(0, Integer.MAX_VALUE, 1.0);
    children[1] = new LayoutRequirements(0, Integer.MAX_VALUE, 1.0);
    children[2] = new LayoutRequirements(50, 70, 1.0);
    
    // System.out.println(LayoutRequirements.getTiledLayoutRequirements(children));
    
    int[] spans = LayoutRequirements.calculateTiledPositions(300, children);

    // System.out.println(spans[0] + " - " + spans[1] + " - " + spans[2]);
    
    assertEquals(300, spans[0] + spans[1] + spans[2]);
    assertEquals(115, spans[0]);
    assertEquals(115, spans[1]);
  }
  
  public void testMinimumMaximum() {
    LayoutRequirements[] children = new LayoutRequirements[3];

    children[0] = new LayoutRequirements(1,    20, 4.0);
    children[1] = new LayoutRequirements(1, 10000, 1.0);
    children[2] = new LayoutRequirements(1, 10000, 2.0);

    int[] spans = LayoutRequirements.calculateTiledPositions(0, children);

    assertEquals(3, spans[0] + spans[1] + spans[2]);
    assertEquals(1, spans[0]);
    assertEquals(1, spans[1]);

    spans = LayoutRequirements.calculateTiledPositions(4, children);

    assertEquals(4, spans[0] + spans[1] + spans[2]);
    assertEquals(2, spans[0]);
    assertEquals(1, spans[1]);

    spans = LayoutRequirements.calculateTiledPositions(7, children);

    assertEquals(7, spans[0] + spans[1] + spans[2]);
    assertEquals(4, spans[0]);
    assertEquals(1, spans[1]);

    spans = LayoutRequirements.calculateTiledPositions(28, children);

    assertEquals(28, spans[0] + spans[1] + spans[2]);
    assertEquals(16, spans[0]);
    assertEquals(4, spans[1]);
    
    spans = LayoutRequirements.calculateTiledPositions(100, children);
    
    assertEquals(100, spans[0] + spans[1] + spans[2]);
    assertEquals(20, spans[0]);
    assertTrue(spans[1] == 26 || spans[1] == 27);
  }
  
  public void testWeight() {
    LayoutRequirements[] children = new LayoutRequirements[2];

    children[1] = new LayoutRequirements(1, Integer.MAX_VALUE, 1.0);

    children[0] = new LayoutRequirements(1, Integer.MAX_VALUE, 1.0);
    int[] spans = LayoutRequirements.calculateTiledPositions(100, children);
    
    assertEquals(100, spans[0] + spans[1]);
    assertEquals(50, spans[0]);

    children[0]=new LayoutRequirements(1, Integer.MAX_VALUE, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);
    
    assertEquals(100, spans[0] + spans[1]);
    assertTrue(spans[0] == 66 || spans[0] == 67);

    children[0]=new LayoutRequirements(50, Integer.MAX_VALUE, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);
    
    assertEquals(100, spans[0] + spans[1]);
    assertTrue(spans[0] == 66 || spans[0] == 67);

    children[0]=new LayoutRequirements(80, Integer.MAX_VALUE, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);
    
    assertEquals(100, spans[0] + spans[1]);
    assertEquals(80, spans[0]);
    
    children[0]=new LayoutRequirements(80, 80, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);

    assertEquals(100, spans[0] + spans[1]);
    assertEquals(80, spans[0]);
    
    children[0]=new LayoutRequirements(50, 60, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);

    assertEquals(100, spans[0] + spans[1]);
    assertEquals(60, spans[0]);
    
    children[0] = new LayoutRequirements(1, 60, 2.0);
    spans = LayoutRequirements.calculateTiledPositions(100, children);

    assertEquals(100, spans[0] + spans[1]);
    assertEquals(60, spans[0]);
  }
}
