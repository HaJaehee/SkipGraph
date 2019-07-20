# # Test Documentation

This document describes the methods we used to write integration tests for our
SkipGraph implementation, and how can one add more tests.


## # SkipNode Tests

 


### Conducting SearchByNumID Test

In this test case we are testing the SearchByNumID method which does the
following:
- Given a numeric ID of a Skip Graph Node, it will perform a search by
  delegating the search to the most optimal nodes (nodes which are probably close
  to our search target)
- Will return Information about the target node (if it was found).

The test cases are constructed using a synthesized Lookup table which will be given to
a test node. Then a listener is used to monitor all the RMI calls made by
the test node, and compare it to a given test table.

> Definitions:
>
> - synthesized lookup table: a lookup table (similar to a phone book), given to our test node as the source of truth
> - mock node: a monitor for all activities done by our test node



For example, a synthesized lookup table would look like this

```java
NodeInfo lookupTable[][] = {
    { new NodeInfo(mockURL, 13, "10101"), new NodeInfo(mockURL, 16, "00101") },
    { new NodeInfo(mockURL, 11, "01100"), new NodeInfo(mockURL, 20, "01110") },
    { new NodeInfo(mockURL, 12, "00001"), new NodeInfo(mockURL, 19, "00101") },
    { new NodeInfo(mockURL, 13, "00111"), new NodeInfo(mockURL, 17, "00110") },
    { new NodeInfo(mockURL, 10, "01011"), new NodeInfo(mockURL, 31, "01001") },
};
```

The `mockURL` is a variable containing the URL of the listener which will be
used to monitor the RMI calls made by the test node.
For this given lookupTable, writing a test table from the `TestCase` class is trivial:

```java
    TestCase tt[] = {
        //           targetNumID,  expected Return value, level
        new TestCase(40,           31,                    4),
        new TestCase(5,            10,                    4),
        ...
    };
```

Finally, this test table is given to an instant of mockNode, and then running the
tests is a matter of calling the `runTests` method of the mockNode:

```java
// Constructor arguments:
// 1. test table
// 2. lookupTable
// 3. server node port
// 4. server node nameID
// 5. server node numID
// 6. mock node port
MockSearchNode mock = new MockSearchNode(tt, lookupTable, serverPORT, "01010", 15, mockPORT);
mock.runTests();
```

### Conducting InsertNode Tests

In this test case we are testing the Insert method which does the
following:
- Given a information about the node itself, the node contacts the introducer
  node (which handles inserting the nodes). The introducer then will insert the
  node into the SkipGraph appropriately.

To test node insertion, one can construct a cluster of nodes, each with a known
numID and URL, connected them to one introducer, and call the insert method on
them. This will result in the nodes using the  RMI protocol asking the introducer
to add them to the skipGraph. After the insertion of all the nodes is complete.
By comparing the introducer's lookup table with the expected lookup table, we can
insure that the method is working correctly.

```java
//               introducer,       address,     port,  nameID,  numID
SkipNode nodes[] = {
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1234,  "00000", 27),
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1235,  "10000", 35),
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1236,  "11000", 43),
    new SkipNode("none",           "127.0.0.1", 1237,  "11100", 51),
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1238,  "11110", 59),
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1239,  "11111", 67),
    new SkipNode("127.0.0.1:1237", "127.0.0.1", 1240,  "11011", 75),
};
```

If the above group of nodes were to form a skip graph, it would look like this
one:

```yaml
level 0: 27 35 43 51 59 67 75
level 1: 27 35 43 51 59-67 75
level 2: 27 35 43-75 51-59-67
level 3: 27 35 43-51-59-67-75
level 4: 27 35-43-51-59-67-75
level 5: 27-35-43-51-59-67-75
```

We can write down the expected right and left nodes of node with numID: 51 for
example at every level. This will look like the following:

```java
NodeInfo lookupTable[][] = {
    { new NodeInfo("127.0.0.1: 1234", 43, "11000"), new NodeInfo("127.0.0.1:1234", 59, "11110") },
    { new NodeInfo("127.0.0.1: 1234", 43, "11000"), new NodeInfo("127.0.0.1:1234", 59, "11110") },
    { null,                                         new NodeInfo("127.0.0.1:1234", 59, "11110") },
    { null,  null},
    { null,  null},
};
```
after inserting the nodes, we can check if the resultant naighbors of node 51 are
as expected. This is done by creating a test handler, and running it.

```java
// Constructing the test handler, and runnning it.
// arguments:
// 1. List of nodes
// 2. List of expected changes after each insertion
// 3. Final lookup table in the target node
// 4. The target node that will contain the final lookup table
InsertTestCase tc = new InsertTestCase(nodes, nodeExpectedPos, lookupTable, 51);
tc.RunTest();
```

