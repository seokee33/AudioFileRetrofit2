package com.seokee.audiofileretrofit2;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadVoiceFileTask extends AsyncTask<String, Void, String> {
    private static final String SERVER_URL = "http://192.168.45.130/upload.php";
    private static final String LINE_END = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY = "*****";

    private String filePath;
    private File mFile;
    private Context context;

    public UploadVoiceFileTask(String filePath, Context context,File mFile) {
        this.filePath = filePath;
        this.context = context;
        this.mFile = mFile;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP 요청 설정
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            // 파일 업로드 설정
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"voicefile\";filename=\"" + filePath + "\"" + LINE_END);
            outputStream.writeBytes(LINE_END);

            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            outputStream.writeBytes(LINE_END);
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
            outputStream.flush();
            outputStream.close();

            // HTTP 응답 받기
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();
                return response.toString();
            } else {
                return "HTTP 요청 실패";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
