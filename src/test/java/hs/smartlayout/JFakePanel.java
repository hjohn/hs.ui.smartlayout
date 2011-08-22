package hs.smartlayout;

import java.awt.Container;
import java.awt.LayoutManager;

public class JFakePanel extends Container {

  public JFakePanel(LayoutManager layoutManager) {
    setLayout(layoutManager);
  }

}
