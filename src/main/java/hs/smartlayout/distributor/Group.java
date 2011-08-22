package hs.smartlayout.distributor;

public class Group {
  public int start;
  public int end;

  private final Group[] children;
  private final int columnCount;

  private Group parent;

  public static Group create(Group... groups) {
    Group root = new Group(groups);
    initialize(root, 0);
    return root;
  }

  public static Group create(int columnCount) {
    Group root = new Group(columnCount);
    initialize(root, 0);
    return root;
  }

  private static void initialize(Group group, int offset) {
    group.start = offset;
    group.end = offset + group.columnCount - 1;

    for(Group child : group.children) {
      initialize(child, offset);
      child.parent = group;
      offset += child.columnCount;
    }
  }

  public Group(Group... groups) {
    this.children = groups.clone();
    int count = 0;

    for(Group child : children) {
      count += child.columnCount;
    }

    this.columnCount = count;
  }

  public Group(int columnCount) {
    this.columnCount = columnCount;
    this.children = new Group[] {};
  }

  public Group getParent() {
    return parent;
  }

  private boolean contains(int column) {
    return column >= start && column <= end;
  }

  public int getColumnCount() {
    return columnCount;
  }

  public boolean hasChildren() {
    return children.length > 0;
  }

  public Group getGroup(int column) {
    for(Group child : children) {
      if(child.contains(column)) {
        return child.getGroup(column);
      }
    }

    return this;
  }
}