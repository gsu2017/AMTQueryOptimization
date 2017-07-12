package edu.zju.ctables.BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TLD7040A on 2017/5/31.
 */
public class PailNode implements Serializable
{

    private static final long serialVersionUID = 1L;

    public Key key;

    public ArrayList idList; //记载了键值key对应的数据对象的id（数据文件中存在重复数据值）

    public PailNode() {
        idList = new ArrayList();
    }

    public PailNode(Key k) {
        key = k;
        idList = new ArrayList();
    }

    public PailNode(Key k, ArrayList l) {
        key = k;
        idList = l;
    }


    @SuppressWarnings("unchecked")
    public void insertID(int id) {
        idList.add(id);
    }

    public String toString(){
        String s = "(";
        for (int i = 0; i < idList.size() - 1; i++){
            s += idList.get(i).toString() +",";
        }
        s += idList.get(idList.size() - 1).toString() + ")";
        return s;
    }
}

