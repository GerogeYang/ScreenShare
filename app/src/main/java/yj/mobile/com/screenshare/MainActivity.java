package yj.mobile.com.screenshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnshot;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnshot = (Button) findViewById(R.id.shot);
        imageView = (ImageView) findViewById(R.id.imageview);

        btnshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = getCaptureScreen();
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    public Bitmap getCaptureScreen() {
        FileInputStream buf = null;
        try {
            // 设置文件权限
            boolean result = ShellUtil.getInstance().rootCommand(
                    "chmod 777 /dev/graphics/fb0\n");
            buf = new FileInputStream(new File("/dev/graphics/fb0"));// 读取文件内容

            DisplayMetrics dm = new DisplayMetrics();
            Display display = getWindowManager().getDefaultDisplay();
            display.getMetrics(dm);
            int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
            int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800p）
            int pixelformat = display.getPixelFormat();
            PixelFormat localPixelFormat1 = new PixelFormat();
            PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
            int deepth = localPixelFormat1.bytesPerPixel;// 位深
            byte[] piex = new byte[screenHeight * screenWidth * deepth];// 像素
            DataInputStream dStream = new DataInputStream(buf);
            dStream.readFully(piex);
            int[] colors = new int[screenHeight * screenWidth];
            // 将rgb转为色值
            for (int m = 0; m < colors.length; m++) {
                int b = (piex[m * 4] & 0xFF);
                int g = (piex[m * 4 + 1] & 0xFF);
                int r = (piex[m * 4 + 2] & 0xFF);
                int a = (piex[m * 4 + 3] & 0xFF);
                colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
            }
            // 得到屏幕bitmap
            return Bitmap.createBitmap(colors, screenWidth, screenHeight,
                    Bitmap.Config.ARGB_4444);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
