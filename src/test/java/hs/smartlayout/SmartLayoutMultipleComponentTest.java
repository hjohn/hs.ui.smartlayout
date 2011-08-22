package hs.smartlayout;

import static org.junit.Assert.assertEquals;

import java.awt.Container;
import java.awt.Dimension;

import org.junit.Before;
import org.junit.Test;

public class SmartLayoutMultipleComponentTest {
  private Container panel;
  private Container[] buttons;

  @Before
  public void before() {
    buttons = new Container[10];

    for(int i = 0; i < buttons.length; i++) {
      buttons[i] = new Container();
      buttons[i].setMinimumSize(new Dimension(40, 10));
      buttons[i].setPreferredSize(new Dimension(40, 10));
      buttons[i].setMaximumSize(new Dimension(40, 10));
    }

    panel = new Container();
    panel.setLayout(new SmartLayout(true, 2, 3, 3));
  }

  @Test
  public void shouldRespectInterComponentSpacing() {
    panel.add(buttons[0], new Constraints());
    panel.add(buttons[1], new Constraints());
    panel.add(buttons[2], new Constraints());
    panel.doLayout();

    assertEquals(40 * 2 + 3, panel.getPreferredSize().width);
    assertEquals(10 * 2 + 3, panel.getPreferredSize().height);
  }

  @Test
  public void shouldRespectComponentWeights() {
    panel.setSize(303, 303);
    panel.add(buttons[0], new Constraints().setWeightX(1.0).setWeightY(2.0).setMaxWidth(1000).setMaxHeight(1000));
    panel.add(buttons[1], new Constraints().setWeightX(2.0).setWeightY(1.0).setMaxWidth(1000).setMaxHeight(1000));
    panel.add(buttons[2], new Constraints().setWeightX(1.0).setWeightY(1.0).setMaxWidth(1000).setMaxHeight(1000));
    panel.doLayout();

    assertEquals(100, buttons[0].getWidth());
    assertEquals(200, buttons[1].getWidth());
    assertEquals(100, buttons[2].getWidth());
    assertEquals(200, buttons[0].getHeight());
    assertEquals(200, buttons[1].getHeight());
    assertEquals(100, buttons[2].getHeight());
  }
}
