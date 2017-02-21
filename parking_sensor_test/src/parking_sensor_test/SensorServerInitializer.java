package parking_sensor_test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class SensorServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        ByteBuf buf = Unpooled.copiedBuffer("\r".getBytes());	//�á�\r����Ϊ�ָ���
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, buf));


        // �ַ������� �� ����
        //pipeline.addLast(new LineBasedFrameDecoder(1024));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        


        // �Լ����߼�Handler
        pipeline.addLast("handler", new SensorServerHandler());
        

    }
}