package org.nuaa.client;


public class Protocol {
	
//		报头为上传路径长度、姓名、本次分片在整个文件中的位置
//		报尾为校验和：校验和s的计算：设要发送n字节，bi为第i个字，s=(b0+b1+…+bn) mod 256
	private int path_length;
	private String path = "/Users/wangchao/books/1.pdf";
	private int pos;
	private byte[] data;
	private int len;
	private byte checksum = 0;

	Protocol(String dstPath, int loc,byte[] data,int len){
		this.path_length = dstPath.length();
		this.path = dstPath;
		this.pos = loc;
		this.data = data;
		this.len = len;
	}
	
	public int getLen() {
		return len;
	}
	
	public int getPath_length() {
		return path_length;
	}
	public String getPath() {
		return path;
	}
	public int getPos() {
		return pos;
	}
	public byte[] getData() {
		return data;
	}
	
	public byte[] intToByte(int intnum) {
		byte[] bytenum = new byte[4];
		bytenum[3] = (byte)((intnum>>24) & 0xFF);
		bytenum[2] = (byte)((intnum>>16) & 0xFF);
		bytenum[1] = (byte)((intnum>>8) & 0xFF);
		bytenum[0] = (byte)(intnum & 0xFF);
		return bytenum;
	}
	
	public byte[] getContentData() {
		
		byte checksum = (byte)getCheckSum();
		byte[] data = getData();
		for(int i = 0;i<getLen();i++) {
			checksum = (byte)((checksum+data[i])%256);
		}
		//int temp = Integer.parseInt(path, 2);
		//path_length
		byte[] path =  getPath().getBytes();

		for(int i = 0;i<6;i++) {
			checksum = (byte)((checksum+path[i])%256);
		}
		//4
		byte[] path_length =  intToByte(getPath_length());
		for(int i = 0;i<4;i++) {
			checksum = (byte)((checksum+path_length[i])%256);
		}
		//4
		byte pos[] = intToByte(getPos());
		for(int i = 0;i<4;i++) {
			checksum = (byte)((checksum+pos[i])%256);
		}
		//1
		byte[] checksum2 = new byte[1];
		checksum2[0] = checksum;
		byte[] all = new byte[9+path.length+getLen()];
		//长度为4的path_length
		System.arraycopy(path_length, 0, all, 0, 4);
		//长度为path_length的path
		System.arraycopy(path, 0, all, 4, path.length);
		System.arraycopy(pos, 0, all, path.length+4, 4);
		System.arraycopy(data, 0, all, path.length+8, getLen());
		int a = path.length+data.length+2;
		System.arraycopy(checksum2, 0, all, path.length+getLen()+8, 1);
		return all;
		
	}
	public byte getCheckSum() {
		return checksum;
	}

}
