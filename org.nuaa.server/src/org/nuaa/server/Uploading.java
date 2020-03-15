package org.nuaa.server;

/* 实现客户端上传文件至服务器*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class Uploading implements Runnable{

    Socket socket = null;// 和本线程相关的Socket
    String dst = "";
//    Socket socket2 = null;
    public Uploading(Socket socket,String dst) {
        super();
        this.dst = dst;
        this.socket = socket;
//        this.socket2 = socket2;
    }
    
    public static boolean CheckSum(byte[] message,int len) {
		byte check = 0;
		for(int i = 0;i<len-1;i++) {
			check = (byte)((check+message[i])%256);
		}
//		if(check == message[len-1]) {
//			return true;
//		}
		return true;
	}
	public static byte[] getData(byte[] message,int len,int path_length) {
		byte[] data = new byte[len-9-path_length];
		 System.arraycopy(message, 8+path_length, data,0,len-9-path_length);
		return data;
	}
    // 响应客户端的请求
    @Override
    public void run() {
        // TODO Auto-generated method stub
        OutputStream os = null;
        PrintWriter pw = null;
		DataOutputStream dos = null;

        try {
            InputStream is=socket.getInputStream();
            // 要完成客户端文件上传到服务器的功能需要将客户机的文件通过FileInputStream进行读取，并包装成BufferedInputStream，
            //将套接字的输出流包装成BufferedOutputStream，用BufferedInputStream中的read（）方法读取文件中的数据，
            //并用 BufferedOutputStream中的write（）方法进行写入，这样文件就送入了Socket的输出流；
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);// 将BufferedInputStream与套接字的输入流进行连接
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream(dst));
            byte[] length_buf = new byte[4];
            if(-1==bufferedInputStream.read(length_buf)){
                System.out.println("流中没有数据了");
            }
            int i = bytes2Int(length_buf);
            dos =  new DataOutputStream(socket.getOutputStream());
            //第一块

            byte[] firstMsg = new byte[265+i];
            System.arraycopy(length_buf,0,firstMsg,0,length_buf.length);
            byte[] append_buf = new byte[261 + i];
            if(-1==bufferedInputStream.read(append_buf)){
                System.out.println("流中没有数据了");
            }
            System.arraycopy(append_buf,0,firstMsg,4,append_buf.length);
            byte[] data = getData(firstMsg,265+i,i);
            bufferedOutputStream.write(data,0,data.length);//写入文件
//					dos = new DataOutputStream(socket.getOutputStream());
            bufferedOutputStream.flush();// 刷新缓冲流
            dos.writeInt(0);




            byte[] buf=new byte[265+i];
            System.out.println("i = "+i);
            int len=0;
            

            while((len=bufferedInputStream.read(buf))!=-1){
                System.out.println("len = " +len);
            		if(CheckSum(buf,len)) {
					data = getData(buf,len,i);
                        System.out.println("真实写入"+data.length);
					bufferedOutputStream.write(data,0,data.length);//写入文件
//					dos = new DataOutputStream(socket.getOutputStream()); 
					bufferedOutputStream.flush();// 刷新缓冲流
					dos.writeInt(0);
				}
				else {
//					dos = new DataOutputStream(socket.getOutputStream());
//					int temp = 1;
					dos.writeInt(1);
				}		                
                
            }
            //强行写入输出流，因为有些带缓冲区的输出流要缓冲区满的时候才输出
            bufferedOutputStream.flush();// 刷新缓冲流
            socket.shutdownInput();// 关闭输入流
            os=socket.getOutputStream();
            pw=new PrintWriter(os);
            pw.println("文件已保存至服务器的/Users/wangchao/books/1.pdf");
            pw.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            // 关闭相关资源
            try {
            if (pw != null) {
                pw.close();
            }
            if (os != null) {
                os.close();
            }
            if (socket != null) {
                socket.close();
            }
            } catch (IOException e) {
            e.printStackTrace();
            }
        }
        
    }
    public static int bytes2Int(byte[] bytes )
    {
        //如果不与0xff进行按位与操作，转换结果将出错，有兴趣的同学可以试一下。
        int int1=bytes[0]&0xff;
        int int2=(bytes[1]&0xff)<<8;
        int int3=(bytes[2]&0xff)<<16;
        int int4=(bytes[3]&0xff)<<24;

        return int1|int2|int3|int4;
    }
}