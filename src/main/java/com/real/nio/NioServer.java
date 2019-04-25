package com.real.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Nio 服务器端
 *
 * @author: mabin
 * @create: 2019/4/25 17:36
 */
public class NioServer {

    /**
     * 启动服务器端方法
     */
    public void start() throws IOException {
        //1.创建selector
        Selector selector = Selector.open();
        //2.通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.位channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        //4.设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //5.将channel注册到selector上，监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功");
        //6.循环等待新接入的连接
        for (; ; ) {
            //TODO 获取可以的channel数量
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
                //如果是 接入事件
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                //如果是 可读事件
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }

            }
        }
    }

    private void broadCast(Selector selector,SocketChannel sourceChannel,String request){
        //获取所有已接入的客户端channel
        Set<SelectionKey> selectionKeySet = selector.keys();

        //循环向所有channel广播信息
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();
            //剔除发消息的客户端
            try {
                //将消息发送到targetChannel客户端
                if (targetChannel instanceof SocketChannel && targetChannel!=sourceChannel){
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 处理接入事件
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //创建SocketChannel，与服务器端建立连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        //将channel设置为非阻塞模式
        socketChannel.configureBlocking(false);
        //将channel注册到selector上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //回复客户端提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("已连接到聊天室中"));
    }

    /**
     * 处理可读事件
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        //从selectionKey中获取已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //使用buffer循环读取客户端请求信息
        String request = "";
        while (socketChannel.read(buffer) > 0) {
            //切换为读模式
            buffer.flip();
            //读取buffer中的内容
            request += Charset.forName("UTF-8").decode(buffer);
        }
        //将channel再次注册到selector上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将客户端发送的请求信息广播给其他客户端
        if (request.length() > 0) {
            //广播给其他客户端
            broadCast(selector,socketChannel,request);
            System.out.println(" :" + request);
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
