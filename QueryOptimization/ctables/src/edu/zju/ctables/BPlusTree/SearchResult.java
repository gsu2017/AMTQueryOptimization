package edu.zju.ctables.BPlusTree;

/**
 * Created by TLD7040A on 2017/5/31.
 */
public class SearchResult {
    public LeafNode q = new LeafNode();
    public int i; // indicates ith child of leafnode q
    public boolean haveFound = false;
}

