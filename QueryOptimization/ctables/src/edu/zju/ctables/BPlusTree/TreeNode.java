package edu.zju.ctables.BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TLD7040A on 2017/5/31.
 */

public class TreeNode implements Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    TreeNode parent;

    public ArrayList<Key> keys;

    ArrayList<TreeNode> pointers;

    int nodeType;// 0:internal; 2:leaf;

    public TreeNode(){
        super();
    }

    /*
     * factor为阶数
     */
    public TreeNode(int factor, TreeNode parent) {
        // num阶B+树有(num - 1)个键
        keys = new ArrayList<Key>(factor - 1);
		/*
		for (int i =0; i < factor - 1; i++){
			keys.add(new Key());
		}
		*/
        pointers = new ArrayList<TreeNode>(factor);
		/*
		for (int i =0; i < factor; i++){
			pointers.add(new TreeNode());
		}
		*/
        this.parent = parent;
    }

    public TreeNode(int factor, int type, TreeNode parent) {
        this(factor, parent);
        nodeType = type;
    }

    // 查找合适的下标, 按照数据结构教材定义
    public int search(Key key){
        int i = keys.size() - 1;
        while (i >= 0 && key.compareTo(keys.get(i))< 0 ){
            i--;
        }
        return i;
    }

    public int getKNum(){
        return keys.size();
    }

    public int getPNum(){
        return pointers.size();
    }

    public String toString(){
        String s= "(";
        for (int i = 0; i < getKNum() - 1; i++){
            s += keys.get(i).toString()+", ";
        }
        s+=keys.get(getKNum() - 1).toString() + ")";
        return s;
    }
}

