package com;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yzx.db.UserInfoDBManager;
import com.yzx.im_demo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Tangjianrong on 2018/11/16.
 */
public class JRMange {


    public static OSS oss;
    private static String ossBucketName = "kaixuanfile";
    private static String ossKeyUrl = "http://as.kaixuantx.com:37080/";
    public static boolean ossDownIsOk = false;
    public static OSSAsyncTask task;
    public static String keyClock = "";
    public static String ossBaseFilePath = UserInfoDBManager.getInstance()
            .getDefaultLoginUser(false).getId() + File.separator +
            UserInfoDBManager.getInstance().getDefaultLoginUser(false)
                    .getId() + File.separator;

    public static String sendVideoFlag = "UCSVideo";
    public static String sendfileFlag = "UCSFile";//图片和文件
    public static int selectVideoFlag = 10086;
    public static int selectfileFlag = 10010;
    public static String selectSoundFlag = "UCSSound";
    public static String selectVideoChatFlag = "UCSVideoChat";

    public static String appFilePath;//app文件路径
    public static String appFilePath_video;//app视频预览图路径


    public static void initFile(Context context) {
        appFilePath = Environment.getExternalStorageDirectory() +
                File.separator + context.getString(R.string.app_name) + File.separator + "JR" + File.separator;
        appFilePath_video = appFilePath + File.separator + "videoThumb" + File.separator;
        File file = new File(appFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File videoThumbfile = new File(appFilePath_video);
        if (!videoThumbfile.exists()) {
            videoThumbfile.mkdirs();
        }
    }

    public static void init(Context context) {
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(ossKeyUrl);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        oss = new OSSClient(context, endpoint, credentialProvider);
    }

    public static void down(final boolean isScale, boolean isShowDialog, Context context, final String keyUrl, final String name, final ResultDoing resultDoing) {
// 构造下载文件请求

        if (keyClock.equals(keyUrl)) {
            return;
        }
        keyClock = keyUrl;
        final ProgressDialog progressDialog = new ProgressDialog(context);
        GetObjectRequest get = new GetObjectRequest(ossBucketName, keyUrl);
        if (isScale) {
            String style = "image/resize,l_300";
            get.setxOssProcess(style);
        }
        progressDialog.setTitle("正在下载");
        if (isShowDialog) {
            progressDialog.show();
        }
        ossDownIsOk = false;
        final OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                LogUtil.e("ok");
                ossDownIsOk = true;
                InputStream inputStream = result.getObjectContent();
                File newfile = new File(appFilePath + isScale  + name);
                try {
                    newfile.createNewFile();
                    inputstreamtofile(inputStream, newfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                keyClock = "";
                progressDialog.dismiss();
                resultDoing.result();

            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                progressDialog.dismiss();
                ossDownIsOk = true;
                // 请求异常
                if (clientExcepion != null) {
                    LogUtil.e(clientExcepion);
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    LogUtil.e(serviceException);
                    // 服务异常
                }
                keyClock = "";
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!ossDownIsOk) {
                    task.cancel();
                }

            }
        });
    }

    public static void upSingleFile(Context context, File file, final ResultUrl resultUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("正在发送中");
        progressDialog.show();
        final String name = System.currentTimeMillis() + file.getName();
        final String values = ossBaseFilePath + name;
        PutObjectRequest put = new PutObjectRequest(ossBucketName, values, file.getPath());
        ossDownIsOk = false;
       /* put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                progressDialog.setMessage(currentSize / totalSize * 100 + "%");
            }
        });*/
        progressDialog.show();
        final OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                ossDownIsOk = true;
                resultUrl.result(values, name);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                progressDialog.dismiss();
                ossDownIsOk = true;
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                }
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!ossDownIsOk) {
                    task.cancel();
                }

            }
        });
    }

    public static void upTwoFile(Context context, File file1, File file2, final ResultTwoUrl resultUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("正在发送中");
        progressDialog.show();
        final String name1 = System.currentTimeMillis() + file1.getName();
        final String values1 = ossBaseFilePath + name1;
        final String name2 = System.currentTimeMillis() + file2.getName();
        final String values2 = ossBaseFilePath + name2;
        ossDownIsOk = false;
        PutObjectRequest put = new PutObjectRequest(ossBucketName, values1, file1.getPath());
        final PutObjectRequest put2 = new PutObjectRequest(ossBucketName, values2, file2.getPath());
        task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                //  resultUrl.result(values1, name1);
                task = oss.asyncPutObject(put2, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        ossDownIsOk = true;
                        progressDialog.dismiss();
                        resultUrl.result(values1, name1, values2, name2);
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        ossDownIsOk = true;
                        progressDialog.dismiss();
                        if (clientExcepion != null) {
                            clientExcepion.printStackTrace();
                        }
                        if (serviceException != null) {
                            clientExcepion.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                ossDownIsOk = true;
                progressDialog.dismiss();
                // 请求异常
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    clientExcepion.printStackTrace();
                }
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!ossDownIsOk) {
                    task.cancel();
                }

            }
        });
    }

    public interface ResultUrl {
        public void result(String url, String name);
    }

    public interface ResultTwoUrl {
        public void result(String url, String name, String url2, String name2);
    }

    public interface ResultDoing {
        public void result();
    }

    public static void inputstreamtofile(InputStream ins, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
    }


    public static void openFile(Context context, File file) {
        //Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), getMIMEType(file.getPath()));
        //跳转
        context.startActivity(intent);
    }


    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }


    public static File createVideoThumbFile(String filepath) {
        MediaMetadataRetriever retriever;
        retriever = new MediaMetadataRetriever();
        File newfile = new File(appFilePath_video + System.currentTimeMillis() + ".png");
        Bitmap bitmap = null;
        try {
            newfile.createNewFile();
            retriever.setDataSource(filepath);
            bitmap = retriever.getFrameAtTime(MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap == null) {
                bitmap = retriever.getFrameAtTime(0);
            }
            retriever.release();
        } catch (Exception ex) {
        }
        if (bitmap != null) {
            bitmapTofile(bitmap, newfile);
        }
        return newfile;
    }

    public static String bitmapTofile(Bitmap bitmap, File mainfile) {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mainfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return mainfile.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
