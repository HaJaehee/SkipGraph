package SkipGraph;

import java.rmi.RemoteException;
import java.util.HashMap;
import org.junit.Test;

// InsertTest contains the integrated test methods conserning testing the
// insert method of skipnodes
// Each method is by its own a test, in order to add new tests, add new methods.
public class InsertTest {

  @Test
  public void InsertTest1() throws RemoteException {
    // Testing a general case of insertion with one introducer

    // lookupTable is the final lookup table expected to be formed in the introducer node after
    // insertion
    NodeInfo lookupTable[][] = {
      {
        new NodeInfo("127.0.0.1:" + 1236, 43, "11000"),
        new NodeInfo("127.0.0.1:" + 1238, 59, "11110")
      },
      {
        new NodeInfo("127.0.0.1:" + 1236, 43, "11000"),
        new NodeInfo("127.0.0.1:" + 1238, 59, "11110")
      },
      {
        new NodeInfo("127.0.0.1:" + 1236, 43, "11000"),
        new NodeInfo("127.0.0.1:" + 1238, 59, "11110")
      },
      {null, new NodeInfo("127.0.0.1:" + 1238, 59, "11110")},
      {null, null},
      {null, null},
    };

    // nodes[] is a group of nodes which if inserted in this order would result in the above
    // lookup table in the introducer node.
    SkipNode nodes[] = {
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1234,
          "00000",
          27), // node 27 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1235,
          "10000",
          35), // node 35 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1236,
          "11000",
          43), // node 43 will be at the left in the introducer's lookuptable at level 2 and below
      new SkipNode("none", "127.0.0.1", 1237, "11100", 51),
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1238,
          "11110",
          59), // node 59 will be at the right in the introducer's lookuptable at level 3 and below
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1239,
          "11111",
          67), // node 67 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1237",
          "127.0.0.1",
          1240,
          "11011",
          75), // node 75 will not be present in the introducer's final lookuptable
    };

    // nodeExpectedPos is a collection of expected changes after the insertion of the nodes above
    // one by one.
    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(
        nodes[0].getNumID(),
        new ExpectedPos(
            0,
            0)); // when first inserted, node 27 will be in level 0 on the left in the introducer's
                 // lookuptable
    nodeExpectedPos.put(
        nodes[1].getNumID(),
        new ExpectedPos(
            1,
            0)); // when first inserted, node 35 will be in level 1 on the left in the introducer's
                 // lookuptable
    nodeExpectedPos.put(
        nodes[2].getNumID(),
        new ExpectedPos(
            2,
            0)); // when first inserted, node 43 will be in level 2 on the left in the introducer's
                 // lookuptable
    nodeExpectedPos.put(nodes[3].getNumID(), null);
    nodeExpectedPos.put(
        nodes[4].getNumID(),
        new ExpectedPos(
            3,
            1)); // when first inserted, node 59 will be in level 3 on the right in the introducer's
                 // lookuptable
    nodeExpectedPos.put(
        nodes[5].getNumID(),
        null); // when first inserted, node 67 will not change the introducer's lookuptable
    nodeExpectedPos.put(
        nodes[6].getNumID(),
        null); // when first inserted, node 75 will not change the introducer's lookuptable

    // Constructing the test handler, and runnning it.
    // arguments:
    // 1. List of nodes
    // 2. List of expected changes after each insertion
    // 3. Final lookup table in the target node
    // 4. The target node that will contain the final lookup table
    InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 51);
    tc.RunTest();
  }

  @Test
  public void InsertTest2() throws RemoteException {
    // Testing continous displacement in the introducer's lookuptable due to
    // insertion

    NodeInfo lookupTable[][] = {
      {new NodeInfo("127.0.0.1:" + 2239, 50, "11101"), null},
      {new NodeInfo("127.0.0.1:" + 2239, 50, "11101"), null},
      {new NodeInfo("127.0.0.1:" + 2239, 50, "11101"), null},
      {new NodeInfo("127.0.0.1:" + 2239, 50, "11101"), null},
      {new NodeInfo("127.0.0.1:" + 2239, 50, "11101"), null},
      {null, null},
    };

    SkipNode nodes[] = {
      new SkipNode(
          "none",
          "127.0.0.1",
          2237,
          "11100",
          51), // node 51 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:2237",
          "127.0.0.1",
          2234,
          "00000",
          10), // node 10 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:2237",
          "127.0.0.1",
          2235,
          "10000",
          20), // node 20 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:2237",
          "127.0.0.1",
          2236,
          "11000",
          30), // node 30 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:2237",
          "127.0.0.1",
          2238,
          "11110",
          40), // node 40 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:2237",
          "127.0.0.1",
          2239,
          "11101",
          50), // node 50 will be present in level 4 and below in the introducer's final lookuptable
    };

    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(nodes[0].getNumID(), null);
    nodeExpectedPos.put(
        nodes[1].getNumID(),
        new ExpectedPos(
            0,
            0)); // when first inserted, node 10 will be present in level 0 on the left in the
                 // introducer's final lookuptable
    nodeExpectedPos.put(
        nodes[2].getNumID(),
        new ExpectedPos(
            1,
            0)); // when first inserted, node 20 will be present in level 1 on the left in the
                 // introducer's final lookuptable
    nodeExpectedPos.put(
        nodes[3].getNumID(),
        new ExpectedPos(
            2,
            0)); // when first inserted, node 30 will be present in level 2 on the left in the
                 // introducer's final lookuptable
    nodeExpectedPos.put(
        nodes[4].getNumID(),
        new ExpectedPos(
            3,
            0)); // when first inserted, node 40 will be present in level 2 on the left in the
                 // introducer's final lookuptable
    nodeExpectedPos.put(
        nodes[5].getNumID(),
        new ExpectedPos(
            4,
            0)); // when first inserted, node 50 will be present in level 4 on the left in the
                 // introducer's final lookuptable

    InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 51);

    tc.RunTest();
  }

  @Test
  public void InsertTest3() throws RemoteException {
    // Testing Insertion on a network with two introducers

    NodeInfo lookupTable[][] = {
      {
        new NodeInfo("127.0.0.1:" + 1335, 35, "10000"),
        new NodeInfo("127.0.0.1:" + 1337, 51, "11100")
      },
      {
        new NodeInfo("127.0.0.1:" + 1335, 35, "10000"),
        new NodeInfo("127.0.0.1:" + 1337, 51, "11100")
      },
      {null, new NodeInfo("127.0.0.1:" + 1337, 51, "11100")},
      {null, new NodeInfo("127.0.0.1:" + 1340, 75, "11011")},
      {null, null},
      {null, null},
    };

    SkipNode nodes[] = {
      new SkipNode(
          "127.0.0.1:1336",
          "127.0.0.1",
          1338,
          "11110",
          59), // node 59 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1338",
          "127.0.0.1",
          1334,
          "00000",
          27), // node 27 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1336",
          "127.0.0.1",
          1335,
          "10000",
          35), // node 35 will be at the left in the introducer's lookuptable at level 1 and below
      new SkipNode("none", "127.0.0.1", 1336, "11000", 43),
      new SkipNode(
          "127.0.0.1:1336",
          "127.0.0.1",
          1337,
          "11100",
          51), // node 51 will be at the right in the introducer's lookuptable at level 2 and below
      new SkipNode(
          "127.0.0.1:1338",
          "127.0.0.1",
          1339,
          "11111",
          67), // node 67 will not be present in the introducer's final lookuptable
      new SkipNode(
          "127.0.0.1:1338",
          "127.0.0.1",
          1340,
          "11011",
          75), // node 75 will be at the right in the introducer's lookuptable at level 3
    };

    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(
        nodes[0].getNumID(),
        new ExpectedPos(
            2,
            1)); // when first inserted, node 59 will appear at level 2 on the right in the
                 // introducer's lookupTable
    nodeExpectedPos.put(
        nodes[1].getNumID(),
        new ExpectedPos(
            0,
            0)); // when first inserted, node 27 will appear at level 0 on the left in the
                 // introducer's lookupTable
    nodeExpectedPos.put(
        nodes[2].getNumID(),
        new ExpectedPos(
            1,
            0)); // when first inserted, node 35 will appear at level 1 on the left in the
                 // introducer's lookupTable
    nodeExpectedPos.put(nodes[3].getNumID(), null);
    nodeExpectedPos.put(
        nodes[4].getNumID(),
        new ExpectedPos(
            2,
            1)); // when first inserted, node 51 will appear at level 2 on the right in the
                 // introducer's lookupTable replacing node 59
    nodeExpectedPos.put(
        nodes[5].getNumID(),
        null); // when first inserted, node 67 will not affect the introducer's lookupTable
    nodeExpectedPos.put(
        nodes[6].getNumID(),
        new ExpectedPos(
            3,
            1)); // when first inserted, node 75 will appear at level 3 on the right in the
                 // introducer's lookupTable

    InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 43);

    tc.RunTest();
  }
}
