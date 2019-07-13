package SkipGraph;

import static junit.framework.Assert.*;

import java.util.HashMap;

class InsertTestCase {
  public SkipNode[] nodes;
  public HashMap<Integer, NodeInfo> nodeInfos;
  public HashMap<Integer, SkipNode> nodesByID;
  public HashMap<Integer, ExpectedPos> nodeExpectedPos;
  public NodeInfo[][] LastLook;
  public Integer relativeNode;

  public InsertTestCase(
      SkipNode[] nodes,
      HashMap<Integer, ExpectedPos> nodeExpectedPos,
      NodeInfo[][] LastLook,
      Integer relativeNode) {
    this.nodeInfos = new HashMap<Integer, NodeInfo>();
    this.nodesByID = new HashMap<Integer, SkipNode>();

    this.nodes = nodes;
    this.nodeExpectedPos = nodeExpectedPos;

    this.LastLook = LastLook;
    this.relativeNode = relativeNode;

    for (SkipNode n : this.nodes) {
      this.nodeInfos.put(n.getNumID(), new NodeInfo(n.getAddress(), n.getNumID(), n.getNameID()));
    }

    for (SkipNode n : this.nodes) {
      this.nodesByID.put(n.getNumID(), n);
    }
  }

  public void RunTest() {
    int ne = 0;
    for (SkipNode n : nodes) {
      if (n.getNumID() == relativeNode) continue;
      n.insert(nodeInfos.get(n.getNumID()));
      ExpectedPos e = nodeExpectedPos.get(n.getNumID());
      if (e == null) {
        continue;
      }

      printLookUP(relativeNode);
      System.out.println("ne is : " + ne);

      if (e.direction == 0) {
        Integer le = nodesByID.get(relativeNode).getLeftNumID(e.level);
        Integer lo = n.getNumID();
        assertEquals(lo, le);
      } else {
        Integer le = nodesByID.get(relativeNode).getRightNumID(e.level);
        Integer lo = n.getNumID();
        assertEquals(lo, le);
      }
      ne++;
    }

    for (int lvl = 0; lvl < 5; lvl++) {
      try {
        if (LastLook[lvl][0] == null) {
          assertNull(nodesByID.get(relativeNode).getLeftNumID(lvl));
        } else {

          assertTrue(nodesByID.get(relativeNode).getLeftNumID(lvl) == LastLook[lvl][0].getNumID());
        }
        if (LastLook[lvl][1] == null) {
          assertNull(nodesByID.get(relativeNode).getRightNumID(lvl));
        } else {
          assertTrue(nodesByID.get(relativeNode).getRightNumID(lvl) == LastLook[lvl][1].getNumID());
        }

      } catch (Exception e) {
        System.out.println("Exception while performing Insertion Test: " + e);
      }
    }
  }

  public void printLookUP(int node) {
    SkipNode n = nodesByID.get(node);

    for (int i = 0; i < 6; i++) {
      System.out.print(n.getLeftNumID(i) + " ");
      System.out.println(n.getRightNumID(i));
    }
  }
}

class ExpectedPos {
  public int level;
  public int direction;

  public ExpectedPos(int level, int direction) {
    this.level = level;
    this.direction = direction;
  }
}
