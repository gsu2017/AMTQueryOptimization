package edu.zju.ctables.BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BPlusTree implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    int factor;// 阶数,即非叶子结点中指针的数目

    int keyType;

    TreeNode root;

    public LeafNode leafHead;

    // FindRecord fr = new FindRecord();

    class FindRecord {
        TreeNode interNode;

        int keyInterPos;

        LeafNode leafNode;

        int keyLeafPos;
    }

    public BPlusTree() {
    }

    public BPlusTree(int keyType, int factor) {
        this.keyType = keyType;
        this.factor = factor;
        // 创建根结点,nodeType = 0;
        // 创建叶子结点, 目前还没有使用!!!
        leafHead = new LeafNode(factor, null);
        root = leafHead;
    }

    // B+树中插入新键key
    @SuppressWarnings("unchecked")
    public void insert(Key key, ArrayList idList) {
        SearchResult r = coreSearch(root, key);
        if (r.haveFound) {
            // if exists, record the id into the idlist, GUOSU
//            List L = r.q.pointers.get(r.i).idList;
//            Object o = idList.get(0);
//            if (o instanceof Comparable) {
//                Comparable c = (Comparable) o;
//                for (int i = 0; i < L.size(); i++) {
//                    if (c.compareTo(L.get(i)) < 0) {
//                        L.add(i, c);
//                        break;
//                    }
//                }
//            } else
                r.q.pointers.get(r.i).idList.add(idList.get(0));
        } else {
            LeafNode leaf = r.q;
            leaf.keys.add(r.i + 1, key);
            leaf.pointers.add(r.i + 1, new PailNode(key, idList));

            if (leaf.getKNum() > factor - 1) {
                LeafNode leaf1 = new LeafNode(factor, r.q.parent);
                for (int i = leaf.getKNum() - 1; i > (factor - 1) / 2; i--) {
                    leaf1.keys.add(0, leaf.keys.get(i));
                    leaf1.pointers.add(0, leaf.pointers.get(i));
                    leaf.keys.remove(i);
                    leaf.pointers.remove(i);
                }
                leaf1.next=leaf.next;
                leaf.next = leaf1;
                Key k1 = leaf1.keys.get(0);
                toRootSplit(leaf.parent, k1, leaf, leaf1);
            }
        }
    }

    void toRootSplit(TreeNode parent, Key k, TreeNode lchild, TreeNode rchild) {
        // 需要创建新的根节点
        if (parent == null) {
            TreeNode newRoot = new TreeNode(factor, null);
            newRoot.keys.add(k);
            newRoot.pointers.add(lchild);
            newRoot.pointers.add(rchild);
            lchild.parent = newRoot;
            rchild.parent = newRoot;
            root = newRoot;
            return;
        }

        int index = parent.search(k);
        parent.keys.add(index + 1, k);
        parent.pointers.add(index + 2, rchild);
        rchild.parent = parent;
        // 插入之后满足要求
        if (parent.getKNum() < factor) {
            return;
        }

        TreeNode parent1 = new TreeNode(factor, parent.parent);
        for (int i = parent.getKNum() - 1; i > factor / 2; i--) {
            parent1.keys.add(0, parent.keys.get(i));
            parent.keys.remove(i);
        }

        for (int i = parent.getPNum() - 1; i > factor / 2; i--) {
            parent1.pointers.add(0, parent.pointers.get(i));
            parent.pointers.remove(i);
        }

        Key k1 = parent.keys.remove(parent.getKNum() - 1);
        for (int i = 0; i <= parent1.getPNum() - 1; i++) {
            parent1.pointers.get(i).parent = parent1;
        }
        toRootSplit(parent.parent, k1, parent, parent1);

    }


    SearchResult coreSearch(TreeNode n, Key k) {
        int i = n.search(k);

        if (n.nodeType == 2) { //leaf node, jump into search
            SearchResult result = new SearchResult();
            result.i = i;
            result.q = (LeafNode) n;
            if (i >= 0)
                result.haveFound = k.equals(n.keys.get(i));
            return result;
        } else {
            return coreSearch(n.pointers.get(i + 1), k);
        }

    }

    public boolean booleanSearch(Key k) {
        return coreSearch(root, k).haveFound;
    }

    public ArrayList search(Key k) {
        SearchResult r = coreSearch(root, k);
        if (r.haveFound)
            return r.q.pointers.get(r.i).idList;
        else
            return null;
    }

    public SearchResult searchInfo(Key k){
        SearchResult r = coreSearch(root, k);
        if (r.haveFound)
            return r;
        else
            return null;
    }

    // //////////////////////////////////////////////////////////////////////
    /**
     *
     * Writen by Mei Zi...~_~
     */
    public void delete(Key k) {
        FindRecord fr = new FindRecord();
        Stack<Integer> record = new Stack<Integer>();
        rootToLeafFind(k, this.root, fr, record);
        if (fr.leafNode == null)
            return;
        // 删除,
        deleteLeafKey(fr.leafNode, fr.keyLeafPos);
        // 替换中间结点
        if (fr.interNode != null) {
            fr.interNode.keys.set(fr.keyInterPos, fr.leafNode.keys
                    .get(fr.keyLeafPos));
        }
        // 符合条件则结束
        if (fr.leafNode.getKNum() >= Math.ceil(((float) this.factor - 1) / 2))
            return;
        // 不符合则MERGE()
        // else
        // leafToRootMerge(fr.leafNode);
		/*System.out.println("+++++++++++++++++++++++++");
		System.out.println(this.print());
		System.out.println("+++++++++++++++++++++++++");*/
        leafToRootMerge(fr.leafNode, record);
    }

    // 删除pos位置处的KEY
    public void deleteLeafKey(LeafNode l, int pos) {
        l.keys.remove(pos);
        l.pointers.remove(pos);
    }

    // 从根到叶子寻找包含关键字的中间结点和叶子结点
    void rootToLeafFind(Key k, TreeNode r, FindRecord f, Stack<Integer> record) {
        int i = r.search(k);
        record.push(new Integer(i));
        if (i >= 0 && r.keys.get(i).equals(k)) {

            if (r.nodeType == 2) {
                f.leafNode = (LeafNode) r;
                f.keyLeafPos = i;
            } else {
                f.interNode = r;
                f.keyInterPos = i;
                rootToLeafFind(k, r.pointers.get(i + 1), f, record);
            }
        } else {
            if (r.nodeType != 2)
                rootToLeafFind(k, r.pointers.get(i + 1), f, record);

        }
    }

    void leafToRootMerge(LeafNode leaf, Stack<Integer> record) {
        int NumberLeaf = (int) Math.ceil(((double) (factor - 1)) / 2);

        record.pop();
        if (record.isEmpty()) {
            return;
        }
        TreeNode parent = leaf.parent;
        int i = record.pop().intValue();
        // 依靠左兄弟
        if (i >= 0 && parent.pointers.get(i).getKNum() > NumberLeaf) {
            LeafNode brother = (LeafNode) parent.pointers.get(i);

            leaf.keys.add(0, brother.keys.get(brother.getKNum() - 1));
            leaf.pointers.add(0, brother.pointers.get(brother.getPNum() - 1));

            brother.keys.remove(brother.getKNum() - 1);
            brother.pointers.remove(brother.getPNum() - 1);

            parent.keys.set(i, leaf.keys.get(0));

        }
        // 无左兄弟依靠右兄弟
        else if (i <= parent.getPNum() - 3
                && parent.pointers.get(i + 2).getKNum() > NumberLeaf) {

            LeafNode brother = (LeafNode) parent.pointers.get(i + 2);

            leaf.keys.add(brother.keys.get(0));
            leaf.pointers.add(brother.pointers.get(0));

            brother.keys.remove(0);
            brother.pointers.remove(0);

            parent.keys.set(i + 1, brother.keys.get(0));
        }
        // 和左边的兄弟合并
        else if (i >= 0) {
            LeafNode brother = (LeafNode) parent.pointers.get(i);
            parent.keys.remove(i);
            parent.pointers.remove(i + 1);

            while (leaf.getKNum() > 0) {
                brother.keys.add(leaf.keys.remove(0));
                brother.pointers.add(leaf.pointers.remove(0));
            }
            brother.next = leaf.next;
        }
        // 和右边的兄弟合并
        else if (i <= parent.getPNum() - 3) {
            LeafNode brother = (LeafNode) parent.pointers.get(i + 2);
            parent.keys.remove(i + 1);
            parent.pointers.remove(i + 2);

            while (brother.getKNum() > 0) {
                leaf.keys.add(brother.keys.remove(0));
                leaf.pointers.add(brother.pointers.remove(0));
            }
            leaf.next = brother.next;
        }
		/*System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
		System.out.println(this.print());
		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");*/
        // if(parent.getKNum() )
        internalToRootMerge(parent, record);
        // 根节点已空, 可以删除,降低一个高度
        if (root.keys.size() == 0)
            root = root.pointers.get(0);
    }

    void internalToRootMerge(TreeNode r, Stack<Integer> record) {
        int NumberNotLeaf = (int) Math.ceil(((double) factor) / 2) - 1;
        TreeNode parent = r.parent;

        if (record.isEmpty()) {
            return;
        } else if (r.keys.size() < NumberNotLeaf) {
            int i = record.pop().intValue();
            // 依靠左兄弟
            if (i >= 0 && parent.pointers.get(i).getKNum() > NumberNotLeaf) {
                TreeNode brother = parent.pointers.get(i);

                r.keys.add(0, parent.keys.get(i));

                r.pointers.add(0, brother.pointers.get(brother.getPNum() - 1));
                brother.pointers.remove(brother.getPNum() - 1);
                // 修改父节点指针
                r.pointers.get(0).parent = r;

                parent.keys.set(i, brother.keys.get(brother.getKNum() - 1));
                brother.keys.remove(brother.getKNum() - 1);
            }
            // 依靠右兄弟
            else if (i <= parent.getPNum() - 3
                    && parent.pointers.get(i + 2).getKNum() > NumberNotLeaf) {

                TreeNode brother = parent.pointers.get(i + 2);

                r.keys.add(parent.keys.get(i + 1));

                r.pointers.add(brother.pointers.get(0));
                brother.pointers.remove(0);
                // 修改父节点指针
                r.pointers.get(r.getPNum() - 1).parent = r;

                parent.keys.set(i + 1, brother.keys.get(0));
                brother.keys.remove(0);
            }
            // 和左边的兄弟合并
            else if (i >= 0) {
                TreeNode brother = parent.pointers.get(i);

                brother.keys.add(parent.keys.remove(i));
                parent.pointers.remove(i + 1);

                while (r.getKNum() > 0) {
                    brother.keys.add(r.keys.remove(0));
                }
                while (r.getPNum() > 0) {
                    // 修改父节点指针
                    r.pointers.get(0).parent = brother;
                    brother.pointers.add(r.pointers.remove(0));
                }
            }
            // 和右边的兄弟合并
            else if (i <= parent.getPNum() - 3) {
                TreeNode brother = parent.pointers.get(i + 2);
                r.keys.add(parent.keys.remove(i + 1));
                parent.pointers.remove(i + 2);

                while (brother.getKNum() > 0) {
                    r.keys.add(brother.keys.remove(0));
                }
                while (brother.getPNum() > 0) {
                    // 修改父节点指针
                    brother.pointers.get(0).parent = r;
                    r.pointers.add(brother.pointers.remove(0));
                }
            }
        } else {
            record.pop();
        }
        internalToRootMerge(parent, record);
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // 测试相关
    public String print() {
        String s = new String();
        s += printBPlusTree(this.root, 0);
        return s;
    }

    public String printBPlusTree(TreeNode r, int l) {
        // printf当前结点的内容
        String s = new String();
        s += "\n";
        s += ((Integer) l).toString() + "***";
        for (int i = 0; i < r.getKNum(); i++) {
            // if(r.keys[i]!=null)
            s += "(" + r.keys.get(i).print().toString() + ")";
            // System.out.println(s);
        }
        // print子结点的内容
        if (r.nodeType != 2) {
            if (r.getPNum() != 0)
                l++;
            for (int i = 0; i < r.getPNum(); i++) {
                // if(r.)
                s += printBPlusTree(r.pointers.get(i), l);
            }
        }
        if (r.nodeType == 2) {
            s += "\n" + "PailNode";
            for (int i = 0; i < r.getKNum(); i++)
                s += "(" + printPailNode(((LeafNode) r).pointers.get(i)) + ")";
        }
        return s;
    }

    public String printPailNode(PailNode p) {
        String s = new String();
		/*
		 * for(int i = 0;i<p.idList.size();i++) s +=
		 * ((Integer)(p.idList.get(i))).toString();
		 */
        // System.out.println(((Integer) (p.idList.get(0))).toString());
        for (int i = 0; i < p.idList.size(); i++) {
            s += ((Integer) (p.idList.get(i))).toString() + " ";
        }
        s += "; ";
        return s;
    }

    public String toString() {
        ;
        return printInLevel(root);
    }

    String printInLevel(TreeNode r) {
        class Result {
            TreeNode n;

            int level;

            Result(TreeNode n, int level) {
                this.n = n;
                this.level = level;
            }
        }

        int currentLevel = 1;
        TreeNode node;
        String s = "";
        String s1 = "";

        Queue<Result> q = new LinkedList<Result>();
        q.add(new Result(r, 1));

        while (!q.isEmpty()) {
            Result result = q.remove();
            node = result.n;

            if (currentLevel < result.level) {
                s += "\n";
                currentLevel = result.level;
            }

            s += node.toString() + " ";
            // Not leaf
            if (node.nodeType == 0) {
                for (int i = 0; i < node.getPNum(); i++) {
                    q.add(new Result(node.pointers.get(i), currentLevel + 1));
                }
            } else {
                // 需要对桶节点进行处理
                s1 += "[";
                LeafNode leaf = (LeafNode) node;
                for (int i = 0; i < leaf.getKNum() - 1; i++) {
                    s1 += leaf.pointers.get(i).toString() + ", ";
                }
                s1 += leaf.pointers.get(leaf.getPNum() - 1) + "]";
                s1 += " --> ";
            }
        }
        s1 += "NULL";
        return s + "\n" + s1;
    }
}
