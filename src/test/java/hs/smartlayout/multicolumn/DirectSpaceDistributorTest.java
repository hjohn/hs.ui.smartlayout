package hs.smartlayout.multicolumn;

import hs.smartlayout.distributor.SpaceDistributor;
import hs.smartlayout.multicolumn.DirectSpaceDistributor;

public class DirectSpaceDistributorTest extends MultiColumnSpaceDistributorTest {

  @Override
  protected SpaceDistributor getSpaceDistributor() {
    return new DirectSpaceDistributor();
  }

}
