package SkipGraph;

import static junit.framework.Assert.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// MockSearchNode provides a way to create a monitor node of a skipnode. Which
// watches the searchNum RMI calls coming out of the skipnode and runs a
// specified set of tests. This runs the SearchNum method tests
class MockSearchNode extends UnicastRemoteObject implements RMIInterface {

  private static final long serialVersionUID = 1L;
  int counter = 0;
  SearchTestCase tt[];
  RMIInterface server;

  ArrayList<Integer[]> results = new ArrayList<>();

  // Constructor arguments:
  // 1. tt: test table consisting of SearchNumTestCases
  // 2. lookupTable: lookupTable supplied to the server node
  // 3. serverPORT: port which the server node will use
  // 4. nameID: nameID of the server node
  // 5. numID: numID of the server noed
  // 6. mockPORT: port of the mock node (monitor)
  public MockSearchNode(
      SearchTestCase tt[],
      NodeInfo lookupTable[][],
      int serverPORT,
      String nameID,
      int numID,
      int mockPORT)
      throws RemoteException {
    this.tt = tt;

    SkipNode node = new SkipNode("none", "127.0.0.1", serverPORT, nameID, numID);
    node.setLookupTable(lookupTable);

    RMIInterface server;

    try {
      server = (RMIInterface) Naming.lookup("//" + "127.0.0.1:" + serverPORT + "/RMIImpl");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    this.server = server;

    Registry reg = LocateRegistry.createRegistry(mockPORT);
    reg.rebind("RMIImpl", this);
  }

  public NodeInfo searchNum(int searchTarget, int level, int numIDOfNode) throws RemoteException {
    results.add(new Integer[] {tt[counter++].returnedNumID, numIDOfNode});
    return null;
  }

  // This methods runs the tests given to it in the test table
  public void runTests() {
    for (SearchTestCase tc : tt) {
      try {
        server.searchNum(tc.targetNumID, tc.level, server.getNumID());
      } catch (RemoteException e) {
        e.printStackTrace();
      }

      assertResults();
    }
  }

  // Makes sure the test results passed
  public void assertResults() {
    for (Integer[] res : results) {
      assertEquals(res[0], res[1]);
    }
  }

  // Everything beyond this point is just a stump needed to statisfy the
  // RMIInterface used by skipnodes
  public String getLeftNode(int level) throws RemoteException {
    return null;
  }

  public String getRightNode(int level) throws RemoteException {
    return null;
  }

  public void setLeftNode(int level, NodeInfo newNode) throws RemoteException {}

  public void setRightNode(int level, NodeInfo newNode) throws RemoteException {}

  public int getNumID() throws RemoteException {
    return 0;
  }

  public String getNameID() throws RemoteException {
    return null;
  }

  public NodeInfo searchByNameID(String targetString) throws RemoteException {
    return null;
  }

  public NodeInfo searchByNumID(int targetNum) throws RemoteException {
    return null;
  }

  public NodeInfo searchName(String searchTarget, int level, int direction) throws RemoteException {
    return null;
  }

  public Integer getLeftNumID(int level) throws RemoteException {
    return null;
  }

  public Integer getRightNumID(int level) throws RemoteException {
    return null;
  }

  public String getLeftNameID(int level) throws RemoteException {
    return null;
  }

  public String getRightNameID(int level) throws RemoteException {
    return null;
  }

  public NodeInfo insertSearch(int level, int direction, String target) throws RemoteException {
    return null;
  }
}
