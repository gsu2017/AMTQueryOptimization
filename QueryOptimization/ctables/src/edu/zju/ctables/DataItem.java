package edu.zju.ctables;

import java.util.AbstractMap;
import java.util.HashMap;

/**
 * Created by TLD7040A on 2017/5/31.
 */
//store complete data and missing information of a data object
public class DataItem
{
    public int id; //data object id
    public int[] data; //normalized data(complete)
    public int[] realData; //real data(complete)
    public int[] missingValuesIndicator;//-1: missing;
    public HashMap.Entry<String,Integer> keyPair;
//    public int dominatingNum; //number of data objects which are dominated by this object
//    public int dominatedNum; // number of data objects which dominate this object
//    public int incomparableNum; // number of incomparable objects

    public DataItem(int oid,int dimension,String name,int year)
    {
        id=oid;
        data = new int[dimension];
        realData=new int[dimension];
        missingValuesIndicator=new int[dimension];
        keyPair = new AbstractMap.SimpleEntry<String,Integer>(name,year);
    }
}
