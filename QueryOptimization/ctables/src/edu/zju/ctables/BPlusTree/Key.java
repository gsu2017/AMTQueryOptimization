package edu.zju.ctables.BPlusTree;

import java.io.Serializable;

/**
 * Created by TLD7040A on 2017/5/31.
 */

public class Key implements Comparable<Key>, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String keyString;
    //int keyNum;
    public Integer keyInteger;

    // 1:String
    // 2:Integer
    int keyType;

    public Key() {
        keyString = new String();
    }

    public Key(String keyString) {
        this.keyString = keyString;
        this.keyType = 1;
    }

    public Key(Integer i) {
        this.keyInteger = i;
        this.keyType = 2;
    }

    public int compareTo(Key key) {
        if (key.keyType == 1) {
            return keyString.compareTo(key.keyString);
        } else {
            return keyInteger.compareTo(key.keyInteger);
        }
    }

    public boolean equals(Key key) {
        if (key.keyType == 1) {
            return key.keyString.equals(keyString);
        } else {
            return key.keyInteger.equals(keyInteger);
        }
    }
    public String print(){
        if (this.keyType == 1)
            return this.keyString;
        else
            return this.keyInteger.toString();
    }

    public String toString(){
        return print();
    }
}

