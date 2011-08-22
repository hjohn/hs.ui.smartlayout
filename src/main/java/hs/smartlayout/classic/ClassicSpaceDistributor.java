package hs.smartlayout.classic;

import hs.smartlayout.LayoutRequirements;
import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;
import hs.smartlayout.distributor.SpaceDistributor;

import java.util.List;

public class ClassicSpaceDistributor implements SpaceDistributor {

  @Override
  public int[] distribute(int space, int columnCount, List<Limit> restrictions, Group rootGroup) {
    if(rootGroup.hasChildren()) {
      throw new IllegalArgumentException("only one group is supported, child groups are not allowed");
    }

    LayoutRequirements[] layoutRequirements = createLayoutRequirements(columnCount);

    for(Limit res : restrictions) {
      if(res.startColumn != res.endColumn) {
        throw new IllegalArgumentException("multi-column restrictions are not supported");
      }
      layoutRequirements[res.startColumn] = new LayoutRequirements(res.getMinSize(), res.getMaxSize(), res.getWeight());
    }

    return LayoutRequirements.calculateTiledPositions(space, layoutRequirements);
  }

  private static LayoutRequirements[] createLayoutRequirements(int columnCount) {
    LayoutRequirements[] layoutRequirements = new LayoutRequirements[columnCount];

    for(int i = 0; i < columnCount; i++) {
      layoutRequirements[i] = new LayoutRequirements(0, Integer.MAX_VALUE, 1.0);
    }

    return layoutRequirements;
  }
}
