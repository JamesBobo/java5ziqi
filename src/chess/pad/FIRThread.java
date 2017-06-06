package chess.pad;

import java.io.IOException;
import java.util.StringTokenizer;

public class FIRThread extends Thread {
	FIRPad currPad;
	
	public FIRThread(FIRPad currPad){
		this.currPad=currPad;
	}
	
	public void dealWithMsg(String msgReceived)
	{
		if(msgReceived.startsWith("/chess")){
			StringTokenizer userMsgToken=new StringTokenizer(msgReceived," ");
			
			String[] chessInfo={"-1","-1","0"};
			int i=0;
			String chessInfoToken;
			
			while(userMsgToken.hasMoreTokens()){
				chessInfoToken=(String)userMsgToken.nextToken(" ");
				if(i>=1&&i<=3){
					chessInfo[i-1]=chessInfoToken;
				}
				i++;
			}
			currPad.paintNetFirPoint(Integer.parseInt(chessInfo[0]), Integer.parseInt(chessInfo[1]), Integer.parseInt(chessInfo[2]));
			
		}
		else if(msgReceived.startsWith("/yourname")){
			currPad.chessSelfName=msgReceived.substring(10);
		}
		else if(msgReceived.startsWith("/error")){
			currPad.statusText.setText("用户名不存在！");
		}
		
	}
	public  void sendMessage(String sndMessage){
		try {
			currPad.outputData.writeUTF(sndMessage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void run()
	{
		String msgReceived = "";
		try
		{
			while (true)
			{ // 等待信息输入
				msgReceived = currPad.inputData.readUTF();
				dealWithMsg(msgReceived);
			}
		}
		catch (IOException es){}
	}
}
