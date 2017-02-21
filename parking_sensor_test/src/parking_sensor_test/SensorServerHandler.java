package parking_sensor_test;

import java.util.LinkedList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.cdtemplar.parking_sensor.*;

public class SensorServerHandler extends SimpleChannelInboundHandler<String> {
    
	LinkedList<CNetgate> cngList = new LinkedList<CNetgate>();
	LinkedList<CSensorValues> csvList = new LinkedList<CSensorValues>();
	CNetgate GetTheNetgate(int nID)
	{
		for(int i=0; i<cngList.size(); i++)
		{
			CNetgate ng = cngList.get(i);
			if(ng.CID == nID)
				return ng;
		}
		return null;
	}
	CSensorValues GetTheSensorValues(int nID)
	{
		for(int i=0; i<csvList.size(); i++)
		{
			CSensorValues ng = csvList.get(i);
			if(ng.ID == nID)
				return ng;
		}
		return null;
	}
    /*
     * 
     * ���� channelActive ���� ��channel�����õ�ʱ�򴥷� (�ڽ������ӵ�ʱ��)
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        
        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        
        ctx.writeAndFlush( "OK\r\n");
        
        super.channelActive(ctx);
    }

	@Override
	protected void messageReceived(ChannelHandlerContext arg0, String arg1) throws Exception {
		// TODO �Զ����ɵķ������
		 //System.out.println("Rec : " + arg1 );
		 int nStart = arg1.indexOf("{");
		 if(nStart >= 0)
			 arg1 = arg1.substring(nStart);
		 else
			 return;
		 CNetGateMsg smv2 = SensorInterface.GetSiteMsg(arg1);
		if(smv2 != null)
		{
			if(smv2.VAR == 0)	//�汾0,ֻ����OK
			{
				arg0.writeAndFlush("OK\r\n");
			}
			else			//�°汾������,�������ݴ洢����
			{
				CNetgate ng = GetTheNetgate(smv2.CID);		//���ص�ǰ��Ϣ,һ��Ӧ���������ݿ���,����������ֱ�ӷ������ڴ���,�����Ե�һ�ε�������Ϊ���ص�ָ��ֵ,�����ȡ��ʷ����
				if(ng == null)
				{
					ng = new CNetgate();
					ng.CID = smv2.CID;
					ng.POT = smv2.POT + smv2.SensorNum();
					cngList.add(ng);
					
					if(smv2.CRC != -1)
					{
						arg0.writeAndFlush(SensorInterface.GetReadString(-1));	
					}
				}
				else
				{
					if(smv2.CRC != -1)
					{
						if(smv2.PD == ng.POT)
						{
							arg0.writeAndFlush(SensorInterface.GetReadString(-1));
							ng.POT += smv2.SensorNum();
							ng.POT %= SensorInterface.getMaxPoint();	//��ֹ�������ֵ
							
							arg0.writeAndFlush(SensorInterface.GetReadString(-1));
						}
						else
						{
							arg0.writeAndFlush(SensorInterface.GetReadString(ng.POT));
							return;			//�����ӵ����ݲ�������
						}


					}
				}
				ng.POT %= SensorInterface.getMaxPoint();	//��ֹ�������ֵ
				arg0.writeAndFlush(SensorInterface.GetReadString(ng.POT));
			}
			
			System.out.println("\r\n���ݽ��================================== " + smv2.CID);
			for(int i=0; i<smv2.SensorNum(); i++)
			{
				
				CSensorValues csvGet = smv2.getSensorValues(i);		//��������Ϣ,һ��Ӧ���������ݿ���,����������ֱ�ӷ������ڴ���,�����Ե�һ�ε�������Ϊ�������ĳ�ֵ
				if(csvGet.ID > 0)	//ֻ����ID>0������
				{
					CSensorValues csv = GetTheSensorValues(csvGet.ID);
					
					if(csv == null)
					{
						csv = csvGet;
						csv.X0 = csv.X;
						csv.Y0 = csv.Y;
						csv.Z0 = csv.Z;
					
						csv.BusyRate = 0;
						csvList.add(csv);
					}
					else
					{
						csv.OnUpdate(csvGet.X, csvGet.Y, csvGet.Z, csvGet.D);	//���³�������Ϣ,�������г�����
					}
					System.out.println("\r\n����������:" + csv);
				}
			}
			System.out.println("\r\n========================================");
		}
	}

}
