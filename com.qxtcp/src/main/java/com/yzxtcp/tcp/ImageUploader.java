package com.yzxtcp.tcp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.DataPacket;
import com.yzxtcp.tools.tcp.packet.PackContent;
/**
 * 图片上传者
 * @author zhuqian
 */
public class ImageUploader {

	private String iClientMsgId;
	private TcpConnection  tcpConnection;
	private TCPManager tcpManager;
	
	private CountDownLatch mLatch;
	
	private boolean isDone = false;
	public ImageUploader(String iClientMsgId,TCPManager tcpManager,TcpConnection tcpConnection){
		this.iClientMsgId = iClientMsgId;
		this.tcpManager = tcpManager;
		this.tcpConnection = tcpConnection;
	}
	public CountDownLatch getmLatch() {
		return mLatch;
	}
	
	public void uploadImage(final PackContent content){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TCPLog.d("ImgPackSize: " + content.tImgPackSize);
					String packsize[] = content.tImgPackSize.split("@@@");
					TCPLog.d("packsize length: " +  packsize.length);
					byte[] buff = content.pack_content;
					int tempSize = 0;
					int i = 0;
					DataPacket imgPack = new DataPacket() {
					};
					while(!isDone && i < packsize.length){
						mLatch = new CountDownLatch(1);
						//拷贝到DataPacket中
						imgPack.buf = new byte[Integer.valueOf(packsize[i])];
						System.arraycopy(buff,tempSize, imgPack.buf, 0,Integer.valueOf(packsize[i]));
						sendPackage(imgPack);
						tempSize = tempSize + Integer.valueOf(packsize[i]);
						i++;
						//等待图片返回，注意最后一个数据包不需要等待
						if(i < packsize.length){
							boolean isTimeOut = mLatch.await(10, TimeUnit.SECONDS);
							if(!isTimeOut){
								TCPLog.e("ImageUploader uploadImage msgId = "+iClientMsgId+" ,isTimeOut");
								isDone = true;
							}
						}
					}
					if(!isDone && i == packsize.length){
						TCPLog.e("上传图片完成");
					}
					TCPLog.e("removeImageUploader return is = "+tcpManager.removeImageUploader(iClientMsgId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private void sendPackage(DataPacket imgPack){
		tcpConnection.sendPacket(imgPack);
	}
	/**
	 * 是否继续发送下一个包
	 * @param isContinue true 表示继续 false表示终止发送
	 */
	public void notifySendNext(boolean isContinue){
		isDone = !isContinue;
		TCPLog.e("notifySendNext msg id = "+iClientMsgId+",IisContinue = "+isContinue);
		if(mLatch != null){
			mLatch.countDown();
		}
	}
}
