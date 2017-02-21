package parking_sensor_test;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class SensorServer {
    
    /**
     * ����˼����Ķ˿ڵ�ַ
     */
    private static final int portNumber = 6001;
    
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new SensorServerInitializer());

            // �������󶨶˿ڼ���
            ChannelFuture f = b.bind(portNumber).sync();
            // �����������رռ���
            f.channel().closeFuture().sync();

            // ���Լ�дΪ
            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}