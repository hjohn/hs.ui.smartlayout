package hs.smartlayout;

import junit.framework.Assert;

import org.junit.Test;
import static org.junit.Assert.*;

public class LayoutRequirementsTest {
  
  @Test
  public void shouldRespectWeightsAndMaximums() {
    LayoutRequirements[] children = new LayoutRequirements[3];

    children[0] = new LayoutRequirements(1, 20, 4.0);
    children[1] = new LayoutRequirements(1, 80, 1.0);
    children[2] = new LayoutRequirements(1, 80, 2.0);

    assertArrayEquals(new int[] {1, 1, 1}, LayoutRequirements.calculateTiledPositions(0, children));
    assertArrayEquals(new int[] {1, 1, 1}, LayoutRequirements.calculateTiledPositions(1, children));
    assertArrayEquals(new int[] {2, 1, 1}, LayoutRequirements.calculateTiledPositions(4, children));
    assertArrayEquals(new int[] {4, 1, 2}, LayoutRequirements.calculateTiledPositions(7, children));
    assertArrayEquals(new int[] {16, 4, 8}, LayoutRequirements.calculateTiledPositions(28, children));
    assertArrayEquals(new int[] {20, 5, 10}, LayoutRequirements.calculateTiledPositions(35, children));
    assertArrayEquals(new int[] {20, 6, 12}, LayoutRequirements.calculateTiledPositions(38, children));
    assertArrayEquals(new int[] {20, 27, 54}, LayoutRequirements.calculateTiledPositions(101, children));
    assertArrayEquals(new int[] {20, 27, 54}, LayoutRequirements.calculateTiledPositions(101, children));
    assertArrayEquals(new int[] {20, 40, 80}, LayoutRequirements.calculateTiledPositions(140, children));
    assertArrayEquals(new int[] {20, 41, 80}, LayoutRequirements.calculateTiledPositions(141, children));
    assertArrayEquals(new int[] {20, 80, 80}, LayoutRequirements.calculateTiledPositions(180, children));
    assertArrayEquals(new int[] {20, 80, 80}, LayoutRequirements.calculateTiledPositions(181, children));
  }

  @Test
  public void shouldRespectWeightsAndMinimumsAndMaximums() {
    LayoutRequirements[] children = new LayoutRequirements[3];

    children[0] = new LayoutRequirements(20, 80, 4.0);
    children[1] = new LayoutRequirements(5, 80, 1.0);
    children[2] = new LayoutRequirements(1, 80, 2.0);

    assertArrayEquals(new int[] {20, 5, 1}, LayoutRequirements.calculateTiledPositions(0, children));
    assertArrayEquals(new int[] {20, 5, 1}, LayoutRequirements.calculateTiledPositions(1, children));
    assertArrayEquals(new int[] {20, 5, 1}, LayoutRequirements.calculateTiledPositions(26, children));
    assertArrayEquals(new int[] {20, 5, 2}, LayoutRequirements.calculateTiledPositions(27, children));
    assertArrayEquals(new int[] {20, 5, 3}, LayoutRequirements.calculateTiledPositions(28, children));
    assertArrayEquals(new int[] {20, 5, 10}, LayoutRequirements.calculateTiledPositions(35, children));
    assertArrayEquals(new int[] {24, 6, 12}, LayoutRequirements.calculateTiledPositions(42, children));
    assertArrayEquals(new int[] {80, 20, 40}, LayoutRequirements.calculateTiledPositions(140, children));
    assertArrayEquals(new int[] {80, 20, 41}, LayoutRequirements.calculateTiledPositions(141, children));
    assertArrayEquals(new int[] {80, 21, 42}, LayoutRequirements.calculateTiledPositions(143, children));
    assertArrayEquals(new int[] {80, 40, 80}, LayoutRequirements.calculateTiledPositions(200, children));
    assertArrayEquals(new int[] {80, 41, 80}, LayoutRequirements.calculateTiledPositions(201, children));
    assertArrayEquals(new int[] {80, 80, 80}, LayoutRequirements.calculateTiledPositions(240, children));
    assertArrayEquals(new int[] {80, 80, 80}, LayoutRequirements.calculateTiledPositions(241, children));
  } 
  
  @Test
  public void shouldReturnNonNullResult() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(10, Integer.MAX_VALUE, 1.0)
    );
    
    Assert.assertNotNull(solution);
  }
  
  @Test
  public void shouldSolveSingleComponent() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(10, Integer.MAX_VALUE, 1.0)
    );
    
    Assert.assertEquals(1, solution.length);
    Assert.assertEquals(100, solution[0]);
  }
  
  @Test
  public void shouldRespectMinimum() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(120, Integer.MAX_VALUE, 1.0)
    );
    
    Assert.assertEquals(1, solution.length);
    Assert.assertEquals(120, solution[0]);
  }
      
  @Test
  public void shouldRespectMinimumOverWeight() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(60, Integer.MAX_VALUE, 1.0),
      new LayoutRequirements(0, Integer.MAX_VALUE, 1.0)
    );
    
    Assert.assertEquals(2, solution.length);
    Assert.assertEquals(60, solution[0]);
    Assert.assertEquals(40, solution[1]);
  }
  
  @Test
  public void shouldRespectMaximum() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(0, 10, 1.0)
    );

    Assert.assertEquals(1, solution.length);
    Assert.assertEquals(10, solution[0]);
  }
  
  @Test
  public void shouldRespectMinimumOverMaximum() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(20, 10, 1.0)
    );

    Assert.assertEquals(1, solution.length);
    Assert.assertEquals(20, solution[0]);
  }
  
  @Test
  public void shouldRespectMaximumOverWeight() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(0, 70, 3.0),
      new LayoutRequirements(0, 40, 1.0)
    );

    Assert.assertEquals(2, solution.length);
    Assert.assertEquals(70, solution[0]);
    Assert.assertEquals(30, solution[1]);
  }
  
  @Test
  public void shouldRespectWeights() {
    int[] solution = LayoutRequirements.calculateTiledPositions(100, 
      new LayoutRequirements(0, Integer.MAX_VALUE, 1.0),
      new LayoutRequirements(0, Integer.MAX_VALUE, 3.0)
    );

    Assert.assertEquals(2, solution.length);
    Assert.assertEquals(25, solution[0]);
    Assert.assertEquals(75, solution[1]);
  }
}
