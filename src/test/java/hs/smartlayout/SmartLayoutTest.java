package hs.smartlayout;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;

public class SmartLayoutTest {
  private SmartLayout verticalLayout;
  private SmartLayout horizontalLayout;

  private Component compA;
  private Component compB;
  private Component compC;
  private Component compD;

  @Before
  public void before() {
    horizontalLayout = new SmartLayout(false, 3, 17, 23);
    verticalLayout = new SmartLayout(true, 3, 17, 23);
    compA = new Component() {};
    compB = new Component() {};
    compC = new Component() {};
    compD = new Component() {};
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowExceptionWhenAddingComponentByName() {
    verticalLayout.addLayoutComponent("name", new Component() {});
  }

  @Test
  public void shouldReturnCorrectSpacings() {
    Assert.assertEquals(17, verticalLayout.getHorizontalSpacing());
    Assert.assertEquals(23, verticalLayout.getVerticalSpacing());

    verticalLayout.setHorizontalSpacing(29);

    Assert.assertEquals(29, verticalLayout.getHorizontalSpacing());
    Assert.assertEquals(23, verticalLayout.getVerticalSpacing());

    verticalLayout.setVerticalSpacing(31);

    Assert.assertEquals(29, verticalLayout.getHorizontalSpacing());
    Assert.assertEquals(31, verticalLayout.getVerticalSpacing());
  }

  @Test
  public void shouldAllowNullConstraints() {
    verticalLayout.addLayoutComponent(new Component() {}, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenComponentSpanExceedsNumberOfColumns() {
    verticalLayout.addLayoutComponent(new Component() {}, new Constraints().setSpanX(5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenComponentSpanExceedsNumberOfRows() {
    horizontalLayout.addLayoutComponent(new Component() {}, new Constraints().setSpanY(5));
  }

  /*
   * Case: [A][B][C]
   *       ...[B]
   *
   * Should throw exception when component is added at insert position that would overlap
   * the B component.
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenComponentWouldOverlap() {
    verticalLayout.addLayoutComponent(new Component() {}, new Constraints());  // A
    verticalLayout.addLayoutComponent(new Component() {}, new Constraints().setSpanX(1).setSpanY(2));  // B
    verticalLayout.addLayoutComponent(new Component() {}, new Constraints());  // C
    verticalLayout.addLayoutComponent(new Component() {}, new Constraints().setSpanX(2).setSpanY(2));  // D
  }

  /*
   * Case: [A][A][B]
   *       [C]...[B]
   *
   * Should allow component to be added of two wide after C is removed.
   */
  @Test
  public void shouldAllowComponentToBeAddedAfterRemoval() {
    verticalLayout.addLayoutComponent(compA, new Constraints().setSpanX(2));
    verticalLayout.addLayoutComponent(compB, new Constraints().setSpanY(2));
    verticalLayout.addLayoutComponent(compC, new Constraints());

    verticalLayout.removeLayoutComponent(compC);

    verticalLayout.addLayoutComponent(compD, new Constraints().setSpanX(2));
  }

  @Test
  public void shouldSilentlyIgnoreRemovingComponentThatWasNeverAdded() {
    verticalLayout.removeLayoutComponent(compA);
  }
}
