package com.xx.platform.core.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.xx.platform.core.SearchContext;

/**
 * 查看是否有节点程序在运行
 * @author 线点科技
 *
 */
public class CheckHasNode extends java.util.TimerTask{
	Logger LOG=Logger.getLogger("com.xx.platform.core.task.CheckHasNode");
	public void run(){
		PrintWriter writer=null;
    	Socket socket=null;
    	try {
			socket=new Socket("127.0.0.1",SearchContext.CHECK_LOCALE_COUNT_PORT);
			socket.setSoTimeout(3000);
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			writer.println("XDCHECK");
			writer.flush();
			String back=in.readLine();
			if(back!=null&&back.startsWith(SearchContext.socketInfoPre)){
				boolean hasNode=socket.isConnected();
				boolean isTogether=SearchContext.CONTROL.isTogether();
				if(!isTogether&&hasNode){
					LOG.info("节点与调度程序不能在同一台服务器上，系统将在10秒后关闭！");
					try {
						for(int i=0;i<10;i++){
							System.out.print("........"+(10-i));
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
					}
					if(SearchContext.server8432!=null){
						if(!SearchContext.server8432.isClosed()){
							SearchContext.server8432.close();
						}
					}
					System.exit(0);
				}
			}
    	} catch (UnknownHostException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}finally{
			if(writer!=null){
				writer.close();
			}
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
