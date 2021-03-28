package com.yuchao.fileclient.util;

import com.yuchao.fileclient.model.ResponseVo;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author yuchao
 * @date 2021/3/27 11:16 上午
 **/
public class HttpUtil {


    /**
     * get请求，没有savePath就不下载文件
     * @param httpurl
     * @param savePath
     * @return
     */
    public static ResponseVo doGet(String httpurl, String savePath) {

        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        ResponseVo response = new ResponseVo();
        try {
            URL url = new URL(httpurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            final int responseCode = connection.getResponseCode();
            response.setCode(responseCode);
            // 通过connection连接，获取输入流
            is = connection.getInputStream();

            // 存放数据
            // 没有本地路径
            if (savePath == null) {

                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                response.setMessage(sbf.toString());
                return response;
            } else {
                File saveDir = new File(savePath);
                if(!saveDir.exists()){
                    saveDir.mkdir();
                }
                // 中文会乱码
                String fieldValue = new String(connection.getHeaderField("Content-Disposition").getBytes("ISO-8859-1"), "UTF-8");
                System.out.println("请求头是: " + fieldValue);
                if (fieldValue == null || ! fieldValue.contains("filename=")) {
                    throw new Exception("没有文件名");
                }
                // 获取文件名
                String fileName = fieldValue.substring(fieldValue.indexOf("filename=") + 9);
                File file= new File(saveDir + File.separator + fileName);

                System.out.println("文件名是" + fileName);
                response.setMessage(fileName);
                ReadableByteChannel rbc = Channels.newChannel(is);
                fos = new FileOutputStream(file);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                return response;
            }

        } catch (Exception e) {
            //e.printStackTrace();
            if (response.getCode() == -1) {
                response.setCode(499);
            }
            response.setMessage("请求失败");
            return response;
        } finally {
            // 关闭资源
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            connection.disconnect();// 关闭远程连接
        }
    }

    /**
     * 不需要下载的get方法
     * @param httpurl
     * @return
     */
    public static ResponseVo doGet(String httpurl) {
        return doGet(httpurl, null);
    }

    /**
     * post上传文件
     * @param myurl
     * @param file
     * @return
     */
    public static ResponseVo doPost(String myurl, File file) {
        String result = "";
        FileInputStream fileInputStream = null;
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream inputStream = null;
        ResponseVo responseVo = new ResponseVo();
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "========7d4a6d158c9";
            // 服务器的域名
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());

            // 上传文件

            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName()
                    + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb.toString().getBytes());

            // 数据输入流,用于读取文件数据
            fileInputStream = new FileInputStream(file);

            byte[] bufferOut = new byte[1024*8];
            int bytes = 0;
            // 每次读8KB数据,并且将文件数据写入到输出流中
            while ((bytes = fileInputStream.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            // 最后添加换行
            out.write(newLine.getBytes());


            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
                    .getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();

            // 定义BufferedReader输入流来读取URL的响应
            inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line; //这里读取的是上边url对应的上传文件接口的返回值，读取出来后，然后接着返回到前端，实现接口中调用接口的方式
            }
            final int responseCode = conn.getResponseCode();
            responseVo.setCode(responseCode);
        } catch (Exception e) {
            //e.printStackTrace();
            if (responseVo.getCode() == -1) {
                responseVo.setCode(499);
            }
            responseVo.setMessage("请求失败");
            return responseVo;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            conn.disconnect();

        }
        responseVo.setMessage(result);
        return responseVo;

    }
}
