package hs.smartlayout;

import java.awt.Container;
import java.awt.Dimension;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static hs.smartlayout.Assert.*;

public class SmartLayoutBasicTest {
  private Container horizontalPanel;
  private Container verticalPanel;
  private Container button1;
  private Container button2;
  private Container button3;

  @Before
  public void before() {
    button1 = new Container();
    button1.setMinimumSize(new Dimension(40, 10));
    button1.setPreferredSize(new Dimension(40, 10));
    //button1.setMaximumSize(new Dimension(40, 10));

    button2 = new Container();

    button3 = new Container();

    verticalPanel = new Container();
    verticalPanel.setLayout(new SmartLayout(true));

    horizontalPanel = new Container();
    horizontalPanel.setLayout(new SmartLayout(false));
  }

  @Test
  public void shouldRespectMinimumWidth() {
    verticalPanel.add(button1, new Constraints().setMinWidth(500));
    verticalPanel.doLayout();

    assertEquals(500, button1.getWidth());
    assertEquals(500, verticalPanel.getPreferredSize().width);
  }

  @Test
  public void shouldRespectMaximumWidth() {
    verticalPanel.add(button1, new Constraints().setMaxWidth(100));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(100, button1.getWidth());
  }

  @Test
  public void shouldRespectMaximumWidthEvenIfAnotherComponentUsesMoreSpace() {
    verticalPanel.add(button1, new Constraints().setMaxWidth(100));
    verticalPanel.add(button2, new Constraints().setMinWidth(200));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(100, button1.getWidth());
    assertEquals(1000, button2.getWidth());
  }

  @Test
  public void shouldRespectMinimumHeight() {
    verticalPanel.add(button1, new Constraints().setMinHeight(500));
    verticalPanel.doLayout();

    assertEquals(500, button1.getHeight());
    assertEquals(500, verticalPanel.getPreferredSize().height);
  }

  @Test
  public void shouldRespectMaximumHeight() {
    verticalPanel.add(button1, new Constraints().setMaxHeight(100));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(100, button1.getHeight());
  }

  @Test
  public void shouldRespectMaximumHeightEvenIfAnotherComponentUsesMoreSpace() {
    horizontalPanel.add(button1, new Constraints().setMaxHeight(100));
    horizontalPanel.add(button2, new Constraints().setMinHeight(200));
    horizontalPanel.setSize(1000, 1000);
    horizontalPanel.doLayout();

    assertEquals(100, button1.getHeight());
    assertEquals(1000, button2.getHeight());
  }

  @Test
  public void shouldRespectAnchorEast() {
    verticalPanel.add(button1, new Constraints(Anchor.EAST, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(960, button1.getX());
    assertEquals(495, button1.getY());
  }

  @Test
  public void shouldRespectAnchorCenter() {
    verticalPanel.add(button1, new Constraints(Anchor.CENTER, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(480, button1.getX());
    assertEquals(495, button1.getY());
  }

  @Test
  public void shouldRespectAnchorSouth() {
    verticalPanel.add(button1, new Constraints(Anchor.SOUTH, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(480, button1.getX());
    assertEquals(990, button1.getY());
  }

  @Test
  public void shouldRespectAnchorSouthEast() {
    verticalPanel.add(button1, new Constraints(Anchor.SOUTH_EAST, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(960, button1.getX());
    assertEquals(990, button1.getY());
  }

  @Test
  public void shouldRespectAnchorSouthWest() {
    verticalPanel.add(button1, new Constraints(Anchor.SOUTH_WEST, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(0, button1.getX());
    assertEquals(990, button1.getY());
  }

  @Test
  public void shouldRespectAnchorNorthWest() {
    verticalPanel.add(button1, new Constraints(Anchor.NORTH_WEST, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(0, button1.getX());
    assertEquals(0, button1.getY());
  }

  @Test
  public void shouldRespectAnchorNorth() {
    verticalPanel.add(button1, new Constraints(Anchor.NORTH, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(480, button1.getX());
    assertEquals(0, button1.getY());
  }

  @Test
  public void shouldRespectAnchorNorthEast() {
    verticalPanel.add(button1, new Constraints(Anchor.NORTH_EAST, 0, 0));
    verticalPanel.setSize(1000, 1000);
    verticalPanel.doLayout();

    assertEquals(960, button1.getX());
    assertEquals(0, button1.getY());
  }

  @Test
  public void shouldIgnoreGap() {
    verticalPanel.setLayout(new SmartLayout(true, 2));
    verticalPanel.add(button1, new Constraints());
    verticalPanel.add(button2, new Constraints());
    verticalPanel.add(button3, new Constraints());
  }


//  @Test // Not sure if this actually tests SmartLayout
//  public void shouldRespectBorderSize() {
//    panel.add(button1, new Constraints().setMinWidth(500).setMinHeight(500));
//    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//    panel.doLayout();
//
//    assertEquals(500, button1.getWidth());
//    assertEquals(500, button1.getHeight());
//    assertEquals(530, panel.getPreferredSize().width);
//    assertEquals(530, panel.getPreferredSize().height);
//  }
}
