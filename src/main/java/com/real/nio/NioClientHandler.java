package com.real.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，专门用来接收服务器端响应信息
 * @author: mabin
 * @create: 2019/4/25 18:42
 */
public class NioClientHandler implements Runnable{

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }
                //获取可用channel集合
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();

                Iterator iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    //获取SelectionKey实例
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    //移除Set中的当前SelectionKey实例
                    iterator.remove();

                    //7.根据就绪状态，调用相应方法处理业务逻辑
                    //判断是否是 可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        //从selectionKey中获取已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //循环读取服务其端响应信息
        String response = "";
        while (socketChannel.read(buffer) > 0) {
            //切换为读模式
            buffer.flip();
            //读取buffer中的内容
            response += Charset.forName("UTF-8").decode(buffer);
        }
        //将channel再次注册到selector上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将服务器端响应信息打印到本地
        if (response.length() > 0) {
            //广播给其他客户端
            System.out.println(response);
        }
    }
}
