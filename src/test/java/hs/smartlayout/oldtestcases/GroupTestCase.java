package hs.smartlayout.oldtestcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hs.smartlayout.Constraints;
import hs.smartlayout.JFakeLabel;
import hs.smartlayout.JFakePanel;
import hs.smartlayout.JFakeTextField;
import hs.smartlayout.LayoutRequirements;
import hs.smartlayout.SmartLayout;

import java.awt.Dimension;

import org.junit.Ignore;
import org.junit.Test;

public class GroupTestCase {

  /**
   * Tests to see if a component and (empty) group react in the same way
   * given the same constraints.
   */
  @Test
  public void testEmptyGroup() {
    JFakePanel main = new JFakePanel(new SmartLayout(true));
    main.setName("main");

    JFakePanel topBar = new JFakePanel(new SmartLayout(false));
    topBar.setName("topBar");
    topBar.add(new JFakeLabel("Test Label"), new Constraints(0, 0).setMinHeight(40));
    topBar.add(new JFakeTextField(), new Constraints(1, 0));

    // JFakeTextField view = new JFakeTextField();

    main.add(topBar, new Constraints(1, 0));
//    main.add(view, new Constraints(1, 1).setMinWidth(400).setMinHeight(400));
//
//    main.setSize(600, 600);
//    main.doLayout();
//
//    assertEquals(new Dimension(600, 40), topBar.getSize());
//    assertEquals(new Dimension(600, 555), view.getSize());
//
//    main.remove(view);

    JFakePanel view2 = new JFakePanel(new SmartLayout(true));
    view2.setName("view2");

    main.add(view2, new Constraints(1, 1).setMinWidth(400).setMinHeight(400));
    main.setSize(600, 600);
    main.doLayout();

//    System.out.println(topBar.getSize());
//    System.out.println(view2.getSize());

    assertEquals(new Dimension(600, 40), topBar.getSize());
    assertEquals(new Dimension(600, 555), view2.getSize());
  }

  /**
   * Tests if a block with certain minimum, maximum and weight values responds
   * the same as several smaller blocks with the same total values in proportion.
   */
  @Test
  public void testSubdivision() {
    JFakePanel group1 = new JFakePanel(new SmartLayout(false, 1, 0, 0));

    JFakeTextField tf1 = new JFakeTextField();
    JFakeTextField tf2 = new JFakeTextField();
    JFakeTextField b1 = new JFakeTextField();

    group1.add(tf1, new Constraints(new Dimension(20, 10)).setWeightX(0.2));
    group1.add(tf2, new Constraints(new Dimension(80, 10)).setWeightX(0.8));
    group1.add(b1, new Constraints(new Dimension(50, 10)).setWeightX(1.0));

    JFakePanel group2 = new JFakePanel(new SmartLayout(false, 1, 0, 0));

    JFakeTextField tf3 = new JFakeTextField();
    JFakeTextField b2 = new JFakeTextField();

    group2.add(tf3, new Constraints(new Dimension(100, 10)).setWeightX(1.0));
    group2.add(b2, new Constraints(new Dimension(50, 10)).setWeightX(1.0));

    for(int i = 0; i < 2000; i++) {
      group1.setSize(i, 600);
      group1.doLayout();
      group2.setSize(i, 600);
      group2.doLayout();

      assertEquals(group1.getSize().width, group2.getSize().width);
      assertTrue(tf1.getWidth() + tf2.getWidth() - tf3.getWidth() < 2);
    }
  }

  /**
   * Tests if a component correctly spans 2 columns.
   *
   * <pre>
   * 12
   * 33
   * </pre>
   */
  @Test
  public void testSpan() {
    JFakePanel group = new JFakePanel(new SmartLayout(true, 2));

    JFakeTextField tf1 = new JFakeTextField();
    JFakeTextField tf2 = new JFakeTextField();
    JFakeTextField tf3 = new JFakeTextField();

    group.add(tf1, new Constraints(0, 0));
    group.add(tf2, new Constraints(1, 0));
    group.add(tf3, new Constraints(0, 0).setSpanX(2));

    group.setSize(600, 600);
    group.doLayout();

    assertEquals(600, group.getWidth());
    assertEquals(600, tf3.getWidth());
  }

  /**
   * Tests the following span layout:
   *
   * <pre>
   * 1223
   * 1223
   * 4453
   * </pre>
   */
  @Ignore
  @Test
  public void testSpan2() {
    JFakePanel group = new JFakePanel(new SmartLayout(true, 4, 0, 0));

    JFakeTextField tf1 = new JFakeTextField();
    JFakeTextField tf2 = new JFakeTextField();
    JFakeTextField tf3 = new JFakeTextField();
    JFakeTextField tf4 = new JFakeTextField();
    JFakeTextField tf5 = new JFakeTextField();

    group.add(tf1, new Constraints(1, 1).setSpanY(2));
    group.add(tf2, new Constraints(1, 1).setSpanX(2).setSpanY(2));
    group.add(tf3, new Constraints(1, 1).setSpanY(3));
    group.add(tf4, new Constraints(1, 1).setSpanX(2));
    group.add(tf5, new Constraints(1, 1));

    group.setSize(600, 600);
    group.doLayout();

//    System.out.println(tf1.getSize());
//    System.out.println(tf2.getSize());
//    System.out.println(tf3.getSize());
//    System.out.println(tf4.getSize());
//    System.out.println(tf5.getSize());

    assertEquals(600, group.getWidth());
    assertEquals(600, group.getHeight());

    assertEquals(150, tf1.getWidth());
    assertEquals(400, tf1.getHeight());

    assertEquals(300, tf2.getWidth());
    assertEquals(400, tf2.getHeight());

    assertEquals(150, tf3.getWidth());
    assertEquals(600, tf3.getHeight());

    assertEquals(300, tf4.getWidth());
    assertEquals(200, tf4.getHeight());

    assertEquals(150, tf5.getWidth());
    assertEquals(200, tf5.getHeight());
  }

  /**
   * Tests how minimum sizes interact with spans using the following layout:
   *
   * <pre>
   * 12
   * 32
   * 42
   * </pre>
   */
  @Test
  public void testSpanMinimums() {
    {
      JFakePanel group = new JFakePanel(new SmartLayout(true, 2, 0, 0));

      JFakeTextField tf1 = new JFakeTextField();
      JFakeTextField tf2 = new JFakeTextField();
      JFakeTextField tf3 = new JFakeTextField();
      JFakeTextField tf4 = new JFakeTextField();

      group.add(tf1, new Constraints(new Dimension(1, 1)));
      group.add(tf2, new Constraints(new Dimension(1, 300)).setSpanY(3));
      group.add(tf3, new Constraints(new Dimension(1, 1)));
      group.add(tf4, new Constraints(new Dimension(1, 50)));

      group.setSize(300, 300);
      group.doLayout();

      assertEquals(100, tf1.getHeight());
      assertEquals(300, tf2.getHeight());
      assertEquals(100, tf3.getHeight());
      assertEquals(100, tf4.getHeight());
    }

    {
      JFakePanel group = new JFakePanel(new SmartLayout(true, 2, 0, 0));

      JFakeTextField tf1 = new JFakeTextField();
      JFakeTextField tf2 = new JFakeTextField();
      JFakeTextField tf3 = new JFakeTextField();
      JFakeTextField tf4 = new JFakeTextField();

      group.add(tf1, new Constraints(new Dimension(1, 1)));
      group.add(tf2, new Constraints(new Dimension(1, 300)).setSpanY(3));
      group.add(tf3, new Constraints(new Dimension(1, 1)));
      group.add(tf4, new Constraints(new Dimension(1, 200)));

      group.setSize(300, 300);
      group.doLayout();

      assertEquals(50, tf1.getHeight());
      assertEquals(300, tf2.getHeight());
      assertEquals(50, tf3.getHeight());
      assertEquals(200, tf4.getHeight());
    }
  }

  @Test
  public void test() {
    int size = 300;

    LayoutRequirements[] lr = new LayoutRequirements[] {
      new LayoutRequirements(0, 9999, 1.0),
      new LayoutRequirements(0, 9999, 1.0),
      new LayoutRequirements(50, 70, 1.0)
    };

    // int[] results =
    LayoutRequirements.calculateTiledPositions(size, lr);

//    System.out.println(Arrays.toString(results));
  }
}

