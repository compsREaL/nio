# nio
nio小试牛刀（non-blocking IO）   单线程节省资源

Channel特点：双向行、非阻塞性、操作唯一性
Channel实现：文件类FileChannel，UDP类：DatagramChannel，TCP类：ServerSocketChannel，SocketChannel

NIO网络编程基本步骤

    1.创建selector
    2.通过ServerSocketChannel创建channel通道
    3.为channel通道绑定监听端口
    4.设置channel为非阻塞模式
    5.将channel注册到selector上，监听连接事件
    6.循环等待新接入的连接
    7.根据就绪状态，调用相应方法处理业务逻辑
        接入事件
        可读事件

