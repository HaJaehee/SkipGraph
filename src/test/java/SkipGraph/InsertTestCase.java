package SkipGraph;

import static junit.framework.Assert.*;

import java.util.HashMap;

// InsertTestCase is used to simplify and structure writing an integrated test for the Insert method
// of SkipNode
class InsertTestCase {
  public SkipNode[] nodes;
  public HashMap<Integer, NodeInfo> nodeInfos;
  public HashMap<Integer, SkipNode> nodesByID;
  public HashMap<Integer, ExpectedPos> nodeExpectedPos;
  public NodeInfo[][] LastLook;
  public Integer relativeNode;

  // Constructor arguments:
  //  1. nodes:  the group of nodes present in the system (the nodes which will form the skipGraph
  // by inserting themselves)
  //  2. nodeExpectedPos: a map of numIDs to the effect expected on the lookupTable of the
  // relativeNode just after insertion
  //  3. LastLook: the final lookupTable of the relativeNode
  //  4. relativeNode:  the node which we will monitor its lookupTable for changes
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

  // RunTest start off a group of tests using nodeExpectedPos and the
  // final lookupTable
  public void RunTest() {
    for (SkipNode n : nodes) {
      if (n.getNumID() == relativeNode) continue;
      n.insert(nodeInfos.get(n.getNumID()));
      ExpectedPos e = nodeExpectedPos.get(n.getNumID());
      if (e == null) {
        continue;
      }

      if (e.direction == 0) {
        Integer le = nodesByID.get(relativeNode).getLeftNumID(e.level);
        Integer lo = n.getNumID();
        assertEquals(lo, le);
      } else {
        Integer le = nodesByID.get(relativeNode).getRightNumID(e.level);
        Integer lo = n.getNumID();
        assertEquals(lo, le);
      }
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
}

//  ExpectedPos contains the change that is expected to happen after the insertion of a
//  specific node.
class ExpectedPos {
  public int level;
  public int direction;

  // Constructor  arguments
  // 1. level: The level at which the change occur
  // 2. direction: the direction it takes in the lookupTable (left 0, right 1)
  public ExpectedPos(int level, int direction) {
    this.level = level;
    this.direction = direction;
  }
}
