package com.real.nio;

import java.io.IOException;

/**
 * @author: mabin
 * @create: 2019/4/25 21:30
 */
public class BClient {
    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient();
        client.start("BClient");
    }
}
