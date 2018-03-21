package com.willkernel.www.lib;

/**
 * Created by willkernel on 2018/3/21.
 */

public class Test {
    public static void main(String[] args) {
        System.out.println(take());
    }

    private static int take() {
        try {
            System.out.println(1 / 0);
        } catch (Exception e) {
            e.printStackTrace();
//            throw e;
        } finally {
            return 1;
        }
    }
}
