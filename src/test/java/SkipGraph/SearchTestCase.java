package SkipGraph;

// SearchTestCase Used to simplify and structure writing an integrated test for the SearchNum method
// of SkipNode
class SearchTestCase {
  public int targetNumID;
  public int returnedNumID;
  public int level;

  // Constructor arguments:
  // 1. targetNumID: the numID we will ask the server node to search for
  // 2. returnedNumId: the expected node that the server node will contact first
  // 3. level: the level in the skipgraph at which the search is conducted
  public SearchTestCase(int targetNumID, int returnedNumId, int level) {
    this.targetNumID = targetNumID;
    this.returnedNumID = returnedNumId;
    this.level = level;
  }
}
