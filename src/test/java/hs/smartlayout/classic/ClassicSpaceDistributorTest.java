package hs.smartlayout.classic;

import hs.smartlayout.StandardSpaceDistributorTest;
import hs.smartlayout.distributor.Group;
import hs.smartlayout.distributor.Limit;
import hs.smartlayout.distributor.SpaceDistributor;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class ClassicSpaceDistributorTest extends StandardSpaceDistributorTest {

  @Override
  protected SpaceDistributor getSpaceDistributor() {
    return new ClassicSpaceDistributor();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenGroupsAreUsed() {
    getSpaceDistributor().distribute(10, 4, new ArrayList<Limit>(), Group.create(new Group(2), new Group(2)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenMultiColumnRestrictionsAreUsed() {
    getSpaceDistributor().distribute(10, 4, new ArrayList<Limit>(Arrays.asList(new Limit[] {new Limit(0, 2).max(100)})), Group.create(4));
  }
}
