package SkipGraph;

import static junit.framework.Assert.assertTrue;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class MockSearchNode extends UnicastRemoteObject {

  private static final long serialVersionUID = 1L;
  int counter = 0;
  SearchTestCase tt[];
  RMIInterface server;

  public MockSearchNode(SearchTestCase tt[], RMIInterface server) throws RemoteException {
    this.tt = tt;
    this.server = server;
  }

  public NodeInfo searchNum(int searchTarget, int level, int numIDOfNode) throws RemoteException {
    assertTrue(tt[counter++].returnedNumID == numIDOfNode);
    return null;
  }

  public void runTests() {
    for (SearchTestCase tc : tt) {
      try {
        server.searchNum(tc.targetNumID, tc.level, server.getNumID());
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }
}
