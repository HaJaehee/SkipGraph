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
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1234, "00000", 27),
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1235, "10000", 35),
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1236, "11000", 43),
      new SkipNode("none", "127.0.0.1", 1237, "11100", 51),
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1238, "11110", 59),
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1239, "11111", 67),
      new SkipNode("127.0.0.1:1237", "127.0.0.1", 1240, "11011", 75),
    };

    // nodeExpectedPos is a collection of expected changes after the insertion of the nodes above
    // one by one.
    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(nodes[0].getNumID(), new ExpectedPos(0, 0));
    nodeExpectedPos.put(nodes[1].getNumID(), new ExpectedPos(1, 0));
    nodeExpectedPos.put(nodes[2].getNumID(), new ExpectedPos(2, 0));
    nodeExpectedPos.put(nodes[3].getNumID(), null);
    nodeExpectedPos.put(nodes[4].getNumID(), new ExpectedPos(3, 1));
    nodeExpectedPos.put(nodes[5].getNumID(), null);
    nodeExpectedPos.put(nodes[6].getNumID(), null);

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

    NodeInfo lookupTable[][] = {
      {
        new NodeInfo("127.0.0.1:" + 2237, 50, "00011"),
        new NodeInfo("127.0.0.1:" + 2237, 52, "00010")
      },
      {
        new NodeInfo("127.0.0.1:" + 2237, 40, "10000"),
        new NodeInfo("127.0.0.1:" + 2237, 80, "11111")
      },
      {
        new NodeInfo("127.0.0.1:" + 2237, 10, "11110"),
        new NodeInfo("127.0.0.1:" + 2237, 80, "11111")
      },
      {
        new NodeInfo("127.0.0.1:" + 2237, 10, "11110"),
        new NodeInfo("127.0.0.1:" + 2237, 80, "11111")
      },
      {null, null},
      {null, null},
    };

    SkipNode nodes[] = {
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2234, "00011", 50),
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2235, "10000", 40),
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2236, "11000", 0),
      new SkipNode("none", "127.0.0.1", 2237, "11100", 51),
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2238, "11110", 10),
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2239, "11111", 80),
      new SkipNode("127.0.0.1:2237", "127.0.0.1", 2240, "00010", 52),
    };

    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(nodes[0].getNumID(), new ExpectedPos(0, 0));
    nodeExpectedPos.put(nodes[1].getNumID(), new ExpectedPos(1, 0));
    nodeExpectedPos.put(nodes[2].getNumID(), new ExpectedPos(2, 0));
    nodeExpectedPos.put(nodes[3].getNumID(), null);
    nodeExpectedPos.put(nodes[4].getNumID(), new ExpectedPos(3, 0));
    nodeExpectedPos.put(nodes[5].getNumID(), new ExpectedPos(3, 1));
    nodeExpectedPos.put(nodes[6].getNumID(), new ExpectedPos(0, 1));

    InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 51);

    tc.RunTest();
  }

  @Test
  public void InsertTest3() throws RemoteException {

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
      new SkipNode("127.0.0.1:1336", "127.0.0.1", 1338, "11110", 59),
      new SkipNode("127.0.0.1:1338", "127.0.0.1", 1334, "00000", 27),
      new SkipNode("127.0.0.1:1336", "127.0.0.1", 1335, "10000", 35),
      new SkipNode("none", "127.0.0.1", 1336, "11000", 43),
      new SkipNode("127.0.0.1:1336", "127.0.0.1", 1337, "11100", 51),
      new SkipNode("127.0.0.1:1338", "127.0.0.1", 1339, "11111", 67),
      new SkipNode("127.0.0.1:1338", "127.0.0.1", 1340, "11011", 75),
    };

    HashMap<Integer, ExpectedPos> nodeExpectedPos = new HashMap<>();

    nodeExpectedPos.put(nodes[0].getNumID(), new ExpectedPos(2, 1));
    nodeExpectedPos.put(nodes[1].getNumID(), new ExpectedPos(0, 0));
    nodeExpectedPos.put(nodes[2].getNumID(), new ExpectedPos(1, 0));
    nodeExpectedPos.put(nodes[3].getNumID(), null);
    nodeExpectedPos.put(nodes[4].getNumID(), new ExpectedPos(2, 1));
    nodeExpectedPos.put(nodes[5].getNumID(), null);
    nodeExpectedPos.put(nodes[6].getNumID(), new ExpectedPos(3, 1));

    InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 43);

    tc.RunTest();
  }
}
