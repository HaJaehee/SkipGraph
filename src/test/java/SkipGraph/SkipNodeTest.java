package SkipGraph;

import static junit.framework.Assert.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SkipNodeTest {

  public void SearchNumIDTest() throws RemoteException {

    int PORT = 2039;
    int serverPORT = 1999;

    NodeInfo lookupTable[][] = {
      {
        new NodeInfo("127.0.0.1:" + PORT, 13, "10101"),
        new NodeInfo("127.0.0.1:" + PORT, 16, "00101")
      },
      {
        new NodeInfo("127.0.0.1:" + PORT, 11, "01100"),
        new NodeInfo("127.0.0.1:" + PORT, 20, "01110")
      },
      {
        new NodeInfo("127.0.0.1:" + PORT, 12, "00001"),
        new NodeInfo("127.0.0.1:" + PORT, 19, "00101")
      },
      {
        new NodeInfo("127.0.0.1:" + PORT, 13, "00111"),
        new NodeInfo("127.0.0.1:" + PORT, 17, "00110")
      },
      {
        new NodeInfo("127.0.0.1:" + PORT, 10, "01011"),
        new NodeInfo("127.0.0.1:" + PORT, 31, "01001")
      },
    };

    SkipNode node = new SkipNode("none", "127.0.0.1", serverPORT, "01010", 15);

    RMIInterface server;

    try {
      server = (RMIInterface) Naming.lookup("//" + "127.0.0.1:" + serverPORT + "/RMIImpl");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    SearchTestCase tt[] = {
      new SearchTestCase(40, 31, 4),
      new SearchTestCase(40, 17, 3),
      new SearchTestCase(40, 19, 2),
      new SearchTestCase(40, 20, 1),
      new SearchTestCase(40, 16, 0),
      new SearchTestCase(5, 10, 4),
      new SearchTestCase(5, 13, 3),
      new SearchTestCase(5, 12, 2),
      new SearchTestCase(5, 11, 1),
      new SearchTestCase(5, 13, 0),
      new SearchTestCase(12, 13, 4),
      new SearchTestCase(12, 13, 3),
      new SearchTestCase(12, 12, 2),
      new SearchTestCase(12, 13, 1),
      new SearchTestCase(12, 13, 0),
      new SearchTestCase(20, 17, 4),
      new SearchTestCase(11, 13, 4),
      new SearchTestCase(16, 16, 4),
    };

    MockSearchNode mock = new MockSearchNode(tt, server);
    Registry reg = LocateRegistry.createRegistry(PORT);
    reg.rebind("RMIImpl", mock);

    node.setLookupTable(lookupTable);
    mock.runTests();
  }

  // TODO: to be converted later
  // NodeInfo lookupTable2[][] = {
  //     { new NodeInfo("127.0.0.1:" + PORT, 14, "01100"), new NodeInfo("127.0.0.1:" + PORT, 16,
  // "01110") },
  //     { new NodeInfo("127.0.0.1:" + PORT, 13, "00001"), new NodeInfo("127.0.0.1:" + PORT, 40,
  // "00101") },
  //     { new NodeInfo("127.0.0.1:" + PORT, 11, "00111"), new NodeInfo("127.0.0.1:" + PORT, 27,
  // "00110") },
  //     { new NodeInfo("127.0.0.1:" + PORT, 11, "10101"), new NodeInfo("127.0.0.1:" + PORT, 28,
  // "00101") },
  //     { new NodeInfo("127.0.0.1:" + PORT, 10, "01011"), new NodeInfo("127.0.0.1:" + PORT, 31,
  // "01001") }, };

  // NodeInfo lookupTable3[][] = {};

  // NodeInfo lookupTable4[][] = {
  //     { new NodeInfo("127.0.0.1:" + PORT, 5, "01011"), new NodeInfo("127.0.0.1:" + PORT, 50,
  //             "01001") }, };
  // node.setLookupTable(lookupTable2);
  // mock.setTT(tt2);
  // mock.runTests();
  // node.setLookupTable(lookupTable3);
  // mock.setTT(tt3);
  // node.setLookupTable(lookupTable4);
  // mock.setTT(tt4);
  //     NodeInfo lookupTable2[][] = {
  //         { new NodeInfo("127.0.0.1:" + PORT, 14, "01100"), new NodeInfo("127.0.0.1:" + PORT, 16,
  // "01110") },
  //         { new NodeInfo("127.0.0.1:" + PORT, 13, "00001"), new NodeInfo("127.0.0.1:" + PORT, 40,
  // "00101") },
  //         { new NodeInfo("127.0.0.1:" + PORT, 11, "00111"), new NodeInfo("127.0.0.1:" + PORT, 27,
  // "00110") },
  //         { new NodeInfo("127.0.0.1:" + PORT, 11, "10101"), new NodeInfo("127.0.0.1:" + PORT, 28,
  // "00101") },
  //         { new NodeInfo("127.0.0.1:" + PORT, 10, "01011"), new NodeInfo("127.0.0.1:" + PORT, 31,
  // "01001") }, };

  //     NodeInfo lookupTable3[][] = {};

  //     NodeInfo lookupTable4[][] = {
  //         { new NodeInfo("127.0.0.1:" + PORT, 5, "01011"), new NodeInfo("127.0.0.1:" + PORT, 50,
  //                 "01001") }, };

  //     SearchTestCase tt2[] = {
  //         new SearchTestCase(16, 16, 4),
  //         new SearchTestCase(17, 16, 4),
  //         new SearchTestCase(14, 14, 4),
  //         new SearchTestCase(14, 14, 3),
  //         new SearchTestCase(14, 14, 2),
  //         new SearchTestCase(14, 14, 1),
  //         new SearchTestCase(14, 14, 0),
  //     };

  //     SearchTestCase tt3[] = {
  //         new SearchTestCase(40, 15, 4),
  //         new SearchTestCase(0,  15, 4),
  //     };

  //     SearchTestCase tt4[] = {
  //         new SearchTestCase(60, 15, 4),
  //         new SearchTestCase(0,  5,  4),
  //     };

  // public static void main(String args[]) {
  //     try {
  //         SearchNumIDTest();
  //     	InsertTest();
  //     } catch (Exception e) {
  //         e.printStackTrace();
  //     }
  // }
}
