package org.nuaa.client;

import java.io.UnsupportedEncodingException;

/**
 * @author wangchao
 * @date 2020/3/13 - 11:18 上午
 */
public class UploadDemo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String src = "/Users/wangchao/books/dsaj.pdf";
        String dst = "/Users/wangchao/books/1.pdf";
        int slice = 30000;

        Client client = new Client();
        client.uploading(src,dst,slice);
    }
}
