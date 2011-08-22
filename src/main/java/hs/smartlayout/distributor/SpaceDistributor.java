package hs.smartlayout.distributor;

import java.util.List;

public interface SpaceDistributor {
  int[] distribute(int space, int columnCount, List<Limit> limits, Group rootGroup);
}
