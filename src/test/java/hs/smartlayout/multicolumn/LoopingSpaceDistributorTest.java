package hs.smartlayout.multicolumn;

import hs.smartlayout.distributor.SpaceDistributor;
import hs.smartlayout.multicolumn.LoopingSpaceDistributor;

public class LoopingSpaceDistributorTest extends MultiColumnSpaceDistributorTest {

  @Override
  protected SpaceDistributor getSpaceDistributor() {
    return new LoopingSpaceDistributor();
  }

}
