package com.ehooworld.imagerecognise;

import org.junit.Test;

import java.util.HashMap;
import java.util.Hashtable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        HashMap<String, String> hashMap = new HashMap<>();
        HashMap<String, String> valueMap = new HashMap<>();
        Hashtable<String, String> hashtable = new Hashtable<>();
//        hashtable.put("1",valueMap.get("1"));
        //why table connot put null but map could??
        hashMap.put(null,null);
        hashMap.put("1",valueMap.get("key"));
        System.out.println(""+hashMap.toString());
    }
    public void test(){
    }
}