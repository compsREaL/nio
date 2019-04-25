package com.real.nio;

import java.io.IOException;

/**
 * @author: mabin
 * @create: 2019/4/25 21:30
 */
public class CClient {
    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient();
        client.start("CClient");
    }
}
