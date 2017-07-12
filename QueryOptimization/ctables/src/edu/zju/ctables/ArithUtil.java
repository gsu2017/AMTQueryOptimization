package edu.zju.ctables;

import java.math.BigDecimal;

/**
 * Created by TLD7040A on 2017/6/27.
 */
public class ArithUtil{
    private static final int DEF_DIV_SCALE=10;

    private ArithUtil(){}
    //相加
    public static float add(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        return b1.add(b2).floatValue();

    }
    //相减
    public static float sub(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        return b1.subtract(b2).floatValue();

    }
    //相乘
    public static float mul(float d1,float d2){
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        return b1.multiply(b2).floatValue();

    }
    //相除
    public static float div(float d1,float d2){

        return div(d1,d2,DEF_DIV_SCALE);

    }

    public static float div(float d1,float d2,int scale){
        if(scale<0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1=new BigDecimal(Float.toString(d1));
        BigDecimal b2=new BigDecimal(Float.toString(d2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float log(float value, float base)
    {
        return div( (float) Math.log(value),(float) Math.log(base));
    }
}
