package edu.zju.ctables.BPlusTree;

/**
 * Created by TLD7040A on 2017/5/31.
 */
import java.util.ArrayList;

public class TestForPlusTree {
    public static void main(String args[]){
        //4阶B+树
        BPlusTree b = new BPlusTree(2, 4);
		/*插入测试*/
        Key k = new Key(new Integer(47));
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(1));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(2));
        al = new ArrayList<Integer>();
        al.add(new Integer(2));
        b.insert(k, al);

        k = new Key(new Integer(3));
        al = new ArrayList<Integer>();
        al.add(new Integer(3));
        b.insert(k, al);

        k = new Key(new Integer(5));
        al = new ArrayList<Integer>();
        al.add(new Integer(4));
        b.insert(k, al);

        System.out.println(b.print());

        k = new Key(new Integer(37));
        al = new ArrayList<Integer>();
        al.add(new Integer(5));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(31));
        al = new ArrayList<Integer>();
        al.add(new Integer(6));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(41));
        al = new ArrayList<Integer>();
        al.add(new Integer(7));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(13));
        al = new ArrayList<Integer>();
        al.add(new Integer(8));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(19));
        al = new ArrayList<Integer>();
        al.add(new Integer(9));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(17));
        al = new ArrayList<Integer>();
        al.add(new Integer(10));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(43));
        al = new ArrayList<Integer>();
        al.add(new Integer(11));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(23));
        al = new ArrayList<Integer>();
        al.add(new Integer(12));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(29));
        al = new ArrayList<Integer>();
        al.add(new Integer(13));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(3));
        al = new ArrayList<Integer>();
        al.add(new Integer(14));
        b.insert(k, al);
        System.out.println(b.print());

        k = new Key(new Integer(5));
        al = new ArrayList<Integer>();
        al.add(new Integer(15));
        b.insert(k, al);
        System.out.println(b.print());

		/*查找测试*/
        System.out.println(b.search(k).toString());

		/*删除测试*/
        System.out.println("==================");
        k = new Key(new Integer(37));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(41));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(19));
        b.delete(k);
        System.out.println(b);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(29));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(17));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(13));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(31));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(23));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");

        System.out.println("==================");
        k = new Key(new Integer(43));
        b.delete(k);
        System.out.println(b.print());
        System.out.println("==================");
		/*按层打印测试*/
        System.out.println(b);
    }
}
