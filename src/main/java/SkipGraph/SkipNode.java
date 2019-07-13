package SkipGraph;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class SkipNode extends UnicastRemoteObject implements RMIInterface {

  private final long serialVersionUID = 1L;
  private String address;
  private String nameID;
  private int numID;
  private String IP;
  private NodeInfo[][] lookup;
  private final int maxLevels = 5;
  private String introducer;
  public Scanner in = new Scanner(System.in);
  private boolean inserted = false;
  private NodeInfo thisNode;
  private int RMIPort = 1099;

  /*
   * Constructor for SkipNode class needed for RMI setup
   */
  protected SkipNode(String introducer, String address, int port, String nameID, int numID)
      throws RemoteException {
    super();
    try {
      this.introducer = introducer;
      this.address = address;
      this.nameID = nameID;
      this.numID = numID;
      this.RMIPort = port;
      lookup = new NodeInfo[maxLevels + 1][2];
      thisNode = new NodeInfo(address + ":" + RMIPort, numID, nameID);
      String st = Inet4Address.getLocalHost().getHostAddress();

      System.setProperty("java.rmi.server.hostname", st);
      Registry reg = LocateRegistry.createRegistry(RMIPort);
      reg.rebind("RMIImpl", this);
      System.out.println("RMI Server proptery set. Inet4Address: " + st);
    } catch (UnknownHostException e) {
      System.err.println(
          "Unknown Host Exception in constructor. Please terminate the program and try again.");
    }
  }

  public void printMenu() throws IOException {
    log("Node at the address: " + address);
    log("Name ID: " + nameID + " Number ID: " + numID);
    log("Choose a query by entering it's code and then press Enter");
    log("1-Insert");
    log("2-Search By Name ID");
    log("3-Search By Number ID");
    log("4-Print the Lookup Table");
  }

  /*
   * Gets the type of operation to be executed from the user
   * and executes the corresponding operation.
   */
  public void ask() {
    String input = get();
    if (!input.matches("[1-6]")) {
      log("Invalid query. Please enter the number of one of the possible operations");
      return;
    }
    int query = Integer.parseInt(input);
    if (query == 1) { // insert node
      if (!introducer.equalsIgnoreCase("none")
          && !inserted) // if there are no inserted nodes yet then, then we insert the current node
      insert(new NodeInfo(address, numID, nameID));
      else log("Already Inserted");
    } else if (query == 2) { // search by name ID
      log("Please Enter the name ID to be searched");
      String name = get();
      while (!name.matches("[0-1]+")) { // Makes sure the name is a binary string
        log("Name ID should be a binary string. Please enter a valid Name ID:");
        name = get();
      }
      NodeInfo result = null;
      try {
        result = searchByNameID(name);
      } catch (RemoteException e) {
        e.printStackTrace();
        log("Remote Exception in query.");
      }
      log("The result of search by name ID is: " + result.getAddress());
    } else if (query == 3) { // search by num ID
      log("Please Enter the numeric ID to be searched");
      String numInput = get();
      while (!numInput.matches("0|[1-9][0-9]*")) {
        log("Invalid number entered. Please enter a valid number");
        numInput = get();
      }
      int num = Integer.parseInt(numInput);
      NodeInfo result = searchByNumID(num);
      log("The result of search by numberic ID is: " + result.getAddress());
    } else if (query == 4) { // print the lookup table of the current node
      printLookup();
    }
  }
  /*
   * This method is a helper method for insert() method
   * It is used to make the insert() operation recursive per level.
   * It receives the level of insertion and the direction of search
   * if direction == 1, then the search is to the right, and the method returns the right neighbor
   * if direction != 1, then the search is to the left, and the method returns the left neighbor
   * This method is also directly accessed by other nodes using RMI if a search should pass through it
   * @see RMIInterface#insertSearch(int, int, int, java.lang.String)
   */
  public NodeInfo insertSearch(int level, int direction, String target) throws RemoteException {

    // If the current node and the inserted node have common bits more than the current level,
    // then this node is the neighbor so we return it
    if (commonBits(target) > level) return thisNode;
    // If search is to the right then delegate the search to right neighbor if it exists
    // If the right neighbor is null then at this level the right neighbor of the inserted node is
    // null
    if (direction == 1) {
      if (lookup[level][1] == null) return null;
      RMIInterface rRMI = getRMI(lookup[level][1].getAddress());
      return rRMI.insertSearch(level, direction, target);
    } else {
      // If search is to the left then delegate the search to the left neighbor if it exists
      // If the left neighbor is null, then the left neighbor of the inserted node at this level is
      // null.
      if (lookup[level][0] == null) return null;
      RMIInterface lRMI = getRMI(lookup[level][0].getAddress());
      return lRMI.insertSearch(level, direction, target);
    }
  }

  /*
   * This method inserts either the current node to the skip graph of the introducer,
   * or it is used to insert a data node.
   */
  public void insert(NodeInfo node) {

    if (node == null) return;

    try {
      String left = null;
      String right = null;

      // We search through the introducer node to find the node with
      // the closest num ID
      NodeInfo position;
      if (introducer.equalsIgnoreCase("none")) {
        position = searchByNumID(node.getNumID());
      } else {
        RMIInterface introRMI = getRMI(introducer);
        position = introRMI.searchByNumID(node.getNumID());
      }

      if (position == null) {
        log("The address resulting from the search is null");
        log("Please check the introducer's IP address and try again.");
        return;
      } else log("The address resulting from the search is: " + position.getAddress());

      RMIInterface posRMI = getRMI(position.getAddress());
      if (posRMI == null) {
        log("RMI registry lookup at address: " + position + " failed. Insert operation stopped.");
        return;
      }

      // First, we insert the node at level 0

      int posNum = position.getNumID(); // numID of the closest node
      String posName = position.getNameID(); // nameID of the closest node
      int leftNum = -1; // numID of left node
      int rightNum = -1; // numID of right node

      if (posNum > node.getNumID()) { // if the closest node is to the right

        right = position.getAddress();
        left = posRMI.getLeftNode(0); // the left of my right will be my left
        rightNum = position.getNumID(); // we need the numID to be able to access it

        if (left
            != null) { // insert the current node in the lookup table of my left node if it exists
          RMIInterface leftRMI = getRMI(left);
          leftNum = posRMI.getLeftNumID(0);
          lookup[0][0] = new NodeInfo(left, leftNum, posRMI.getLeftNameID(0));
          leftRMI.setRightNode(0, node);
        }

        lookup[0][1] = new NodeInfo(right, posNum, posName);
        posRMI.setLeftNode(
            0, node); // insert the current node in the lookup table of its right neighbor

      } else { // if the closest node is to the left

        right = posRMI.getRightNode(0); // the right of my left is my right
        left = position.getAddress();
        leftNum = position.getNumID(); // we need the numID to be able to access it

        if (right
            != null) { // insert current node in the lookup table of its right neighbor if it exists
          RMIInterface rightRMI = getRMI(right);
          rightNum = posRMI.getRightNumID(0);
          lookup[0][1] = new NodeInfo(right, rightNum, posRMI.getRightNameID(0));
          rightRMI.setLeftNode(0, node);
        }

        lookup[0][0] = new NodeInfo(left, posNum, posName);
        posRMI.setRightNode(0, node);
      }

      // Now, we insert the node in the rest of the levels
      // In level i , we make a recursive search for the nodes that will be
      // the neighbors of the inserted nodes at level i+1

      int level = 0;
      while (level < maxLevels) {

        if (left != null) {

          RMIInterface leftRMI = getRMI(left);
          NodeInfo lft = leftRMI.insertSearch(level, -1, node.getNameID()); // start search left
          lookup[level + 1][0] = lft;

          // set left and leftNum to default values (null,-1)
          // so that if the left neighbor is null then we no longer need
          // to search in higher levels to the left
          left = null;
          leftNum = -1;

          if (lft != null) {
            RMIInterface lftRMI = getRMI(lft.getAddress());
            lftRMI.setRightNode(level + 1, node);
            left = lft.getAddress();
            leftNum = lft.getNumID();
          }
        }
        if (right != null) {

          RMIInterface rightRMI = getRMI(right);
          NodeInfo rit = rightRMI.insertSearch(level, 1, node.getNameID()); // start search right
          lookup[level + 1][1] = rit;

          // set right and rightNum to default values (null,-1)
          // so that if the right neighbor is null then we no longer need
          // to search in higher levels to the right
          right = null;
          rightNum = -1;

          if (rit != null) {
            RMIInterface ritRMI = getRMI(rit.getAddress());
            ritRMI.setLeftNode(level + 1, node);
            right = rit.getAddress();
            rightNum = rit.getNumID();
          }
        }
        level++;
      }
    } catch (RemoteException e) {
      e.printStackTrace();
      log("Remote Exception thrown in insert function.");
    }
  }

  /*
   * This method is a hepler method for searchByNumID()
   * It recieves the target numID and the level it is searching in,
   * and it routes the search through the skip graph recursively using RMI
   * @see RMIInterface#searchNum(int, int)
   */
  public NodeInfo searchNum(int targetInt, int level) {

    log("Search at: " + address); // we use this to see when a search has passed through this node

    if (numID == targetInt) return thisNode;

    // If the target is greater than the current node then we should search right
    if (numID < targetInt) {

      // Keep going down levels as long as there is either no right neighbor
      // or the right neighbor has a numID greater than the target
      while (level >= 0 && (lookup[level][1] == null || lookup[level][1].getNumID() > targetInt))
        level--;
      // If there are no more levels to go down to return the current node
      if (level < 0) return thisNode;
      // delegate the search to the right neighbor
      RMIInterface rightRMI = getRMI(lookup[level][1].getAddress());
      try {
        return rightRMI.searchNum(targetInt, level);
      } catch (Exception e) {
        log("Exception in searchNum. Target: " + targetInt);
      }
    } else { // If the target is less than the current node then we should search left
      // Keep going down levels as long as there is either no right neighbor
      // or the left neighbor has a numID greater than the target
      while (level >= 0 && (lookup[level][0] == null || lookup[level][0].getNumID() < targetInt))
        level--;
      // If there are no more levels to go down to return the current node
      if (level < 0) return thisNode;
      // delegate the search to the left neighbor
      RMIInterface leftRMI = getRMI(lookup[level][0].getAddress());
      try {
        return leftRMI.searchNum(targetInt, level);
      } catch (Exception e) {
        log("Exception in searchNum. Target: " + targetInt);
      }
    }
    return thisNode;
  }

  /*
   * Executes a search through the skip graph by numeric id and returns the a NodeInfo object
   * which contains the address, numID, and nameID of the node with closest numID to the target
   * It starts the search from last level of the current node
   * @see RMIInterface#searchByNumID(java.lang.String)
   */
  public NodeInfo searchByNumID(int searchTarget) {

    int level = maxLevels;
    if (lookup[0][0] == null && lookup[0][1] == null) return thisNode;
    return searchNum(searchTarget, level);
  }

  /*
   * This method is a helper method for searchByNameID()
   * It receives the target nameID and the level it is searching in, and also the direction
   * of search, and it routes the search through the skip graph recursively using RMI
   * It return the most similar node if the node itself is not found
   * The similarity is defined to be the the maximum number of common bits
   * If direction == 1, then search is to the right
   * If direction == 0, then search is to the left
   * @see RMIInterface#searchName(java.lang.String, int, int)
   */
  public NodeInfo searchName(String searchTarget, int level, int direction) throws RemoteException {

    if (nameID.equals(searchTarget)) // if the current node hold the same nameID, return it.
    return thisNode;
    // calculate common bits to find to which level the search must be routed
    int newLevel = commonBits(searchTarget);

    // If the number of common bits is not more than the current level
    // then we continue the search in the same level in the same direction
    if (newLevel <= level) {
      if (lookup[level][direction]
          == null) // If no more nodes in this direction return the current node
      return thisNode;
      RMIInterface rightRMI = getRMI(lookup[level][direction].getAddress());
      return rightRMI.searchName(searchTarget, level, direction);
    }
    // If the number of common bits is more than the current level
    // then the search will be continued on the new level
    // so we start a search in both directions in the new level

    NodeInfo result = thisNode; // we initialize the result to current node

    // First we start the search on the same given direction and wait for the result it returns
    if (lookup[newLevel][direction] != null) {
      RMIInterface rightRMI = getRMI(lookup[newLevel][direction].getAddress());
      result = rightRMI.searchName(searchTarget, newLevel, direction);
    }
    // If it returns a result that differs from the current node then we check it
    if (result != null && !result.equals(thisNode)) {
      RMIInterface resultRMI = getRMI(result.getAddress());
      // If this is the result we want return it, otherwise continue the search in the opposite
      // direction
      if (resultRMI.getNameID().contains(searchTarget)) return result;
    }
    // Continue the search on the opposite direction
    if (lookup[newLevel][1 - direction] != null) {
      RMIInterface leftRMI = getRMI(lookup[newLevel][1 - direction].getAddress());
      NodeInfo k = leftRMI.searchName(searchTarget, newLevel, 1 - direction);
      if (result == null
          || commonBits(k.getNameID(), thisNode.getNameID())
              > commonBits(result.getNameID(), thisNode.getNameID())) result = k;
    }
    return result;
  }

  /*
   * This methods starts a search by nameID, and returns the node as an instance
   * of NodeInfo class which contains (address, numID, nameID) of the node,
   * such that the nameID of the returned node is the most similar with the searchTarget.
   * Similarity is defined to be the maximum number of common bits between the two strings
   * @see RMIInterface#searchByNameID(java.lang.String)
   */
  public NodeInfo searchByNameID(String searchTarget) throws RemoteException {

    int newLevel = commonBits(searchTarget);
    NodeInfo result = thisNode;

    // First execute the search in the right direction and see the result it returns
    if (lookup[newLevel][1] != null) {
      RMIInterface rightRMI = getRMI(lookup[newLevel][1].getAddress());
      result = rightRMI.searchName(searchTarget, newLevel, 1);
    }
    // If the result is not null and is different from the default value we check it
    if (result != null && !result.equals(thisNode)) {
      RMIInterface resultRMI = getRMI(result.getAddress());
      // If this is the result we want return it, otherwise continue searching to the left
      if (resultRMI.getNameID().contains(searchTarget)) return result;
    }
    // If the desired result was not found try to search to the left
    if (lookup[newLevel][0] != null) {
      RMIInterface leftRMI = getRMI(lookup[newLevel][0].getAddress());
      NodeInfo k = leftRMI.searchName(searchTarget, newLevel, 0);
      if (commonBits(k.getNameID(), thisNode.getNameID())
          > commonBits(result.getNameID(), thisNode.getNameID())) result = k;
    }
    return result;
  }
  /*
   * getters and setters for lookup table and numID and nameID
   *
   */
  public String getLeftNode(int level) throws RemoteException {
    if (lookup[level][0] == null) return null;
    return lookup[level][0].getAddress();
  }

  public String getRightNode(int level) throws RemoteException {
    if (lookup[level][1] == null) return null;
    return lookup[level][1].getAddress();
  }

  public void setLeftNode(int level, NodeInfo newNode) throws RemoteException {
    log("LeftNode at level " + level + " set to: " + newNode.getAddress());
    lookup[level][0] = newNode;
  }

  public void setRightNode(int level, NodeInfo newNode) throws RemoteException {
    log("RightNode at level " + level + " set to: " + newNode.getAddress());
    lookup[level][1] = newNode;
  }

  public int getNumID() {
    return numID;
  }

  public String getNameID() {
    return nameID;
  }

  protected String getAddress() {
    return address + ":" + RMIPort;
  }

  protected void setNumID(int num) {
    numID = num;
  }

  protected void setNameID(String s) {
    nameID = s;
  }

  public Integer getLeftNumID(int level) {
    if (lookup[level][0] == null) return null;
    return lookup[level][0].getNumID();
  }

  public Integer getRightNumID(int level) {
    if (lookup[level][1] == null) return null;
    return lookup[level][1].getNumID();
  }

  public String getLeftNameID(int level) {
    if (lookup[level][0] == null) return null;
    return lookup[level][0].getNameID();
  }

  public String getRightNameID(int level) {
    if (lookup[level][1] == null) return null;
    return lookup[level][1].getNameID();
  }

  /*
   * This method returns an RMI instance of the node with the given address
   */
  public RMIInterface getRMI(String adrs) {
    if (validateIP(adrs))
      try {
        return (RMIInterface) Naming.lookup("//" + adrs + "/RMIImpl");
      } catch (Exception e) {
        log("Exception while attempting to lookup RMI located at address: " + adrs + ":" + e);
      }
    else {
      log("Error in looking up RMI. Address: " + adrs + " is not a valid address.");
    }
    return null;
  }

  /*
   * This method validates the ip and makes sure its of the form xxx.xxx.xxx.xxx
   */
  private boolean validateIP(String adrs) {
    int colonIndex = adrs.indexOf(':');
    String ip = adrs;
    if (colonIndex != -1) ip = adrs.substring(0, colonIndex);
    String[] parts = ip.split("\\.");
    if (parts.length != 4) {
      return false;
    }
    try {
      for (String el : parts) {
        int num = Integer.parseInt(el);
        if (num < 0 || num > 255) return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    if (ip.endsWith(".")) return false;
    return true;
  }

  // This method calculate the length of the common prefix
  // between the nameID of the current node and the given name
  public int commonBits(String name) {
    if (name.length() != nameID.length()) return -1;
    int i = 0;
    for (i = 0; i < name.length() && name.charAt(i) == nameID.charAt(i); i++) ;
    log("Common Prefix for " + nameID + " and " + name + " is: " + i);
    return i;
  }
  /*
   * This method returns the length of the common prefix between two given strings
   */
  public int commonBits(String name1, String name2) {
    if (name1 == null || name2 == null) {
      return -1;
    }
    if (name1.length() != name2.length()) return -1;
    int i = 0;
    for (i = 0; i < name1.length() && name1.charAt(i) == name2.charAt(i); ++i) ;
    log("Common Prefix for " + name1 + " and " + name2 + " is: " + i);
    return i;
  }
  /*
   * A shortcut for printing to console
   */
  public void log(String s) {
    System.out.println(s);
  }

  public void logLine(String s) {
    System.out.print(s);
  }
  /*
   * Print the contents of the lookup table
   */
  public void printLookup() {
    System.out.println("\n");
    for (int i = maxLevels - 1; i >= 0; i--) {
      for (int j = 0; j < 2; j++)
        if (lookup[i][j] == null) logLine("null\t");
        else logLine(lookup[i][j].getAddress() + "\t");
      log("\n\n");
    }
  }
  /*
   * A shortcut for getting input from user
   */
  public String get() {
    String response = in.nextLine();
    return response;
  }
}
