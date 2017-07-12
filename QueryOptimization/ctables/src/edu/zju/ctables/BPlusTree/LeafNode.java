package edu.zju.ctables.BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TLD7040A on 2017/5/31.
 */
public class LeafNode extends TreeNode implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ArrayList<PailNode> pointers;

    public LeafNode next;

    public LeafNode() {
        super();
    }

    public LeafNode(int factor, TreeNode parent) {
        // nodetype = 2,leafnode
        super(factor, 2, parent);
        pointers = new ArrayList<PailNode>(factor - 1);
		/*
		for (int i = 0; i < factor - 1; i++) {
			pointers.add(new PailNode());
		}
		*/
    }

    public int getPNum(){
        return pointers.size();
    }

}

