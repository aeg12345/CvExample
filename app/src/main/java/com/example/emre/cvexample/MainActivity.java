package com.example.emre.cvexample;
import org.opencv.android.BaseLoaderCallback;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;


import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ImageView sonucimg;


    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("opencv", "opencv initialzation failed");
        } else {
            Log.i("opencv", "opencv initialzation success");
        }


    }

    private static final String TAG = "OCVSample::Activity";


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // mOpenCvCameraView.enableView();
                    //  mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sonucimg = (ImageView) findViewById(R.id.sonucimg);

        new MatchingDemo().run(Imgproc.TM_SQDIFF_NORMED);






/*
        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
*/
    }


    public class MatchingDemo {
        public void run(int match_method) {
            System.out.println("\nRunning Template Matching");

            Mat img = new Mat();
            Mat templ = new Mat();

            System.out.println("\nRead the images");
            /*templ= Imgcodecs.imread(imgf);
                img=Imgcodecs.imread(tf);*/

            try {
                img = Utils.loadResource(getApplicationContext(), R.drawable.ontl2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
                templ = Utils.loadResource(getApplicationContext(), R.drawable.ontl1, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("\nRead the images" + templ);
            System.out.println("\nRead the images" + img);

            // / Create the result matrix
            int result_cols = img.cols() - templ.cols() + 1;
            int result_rows = img.rows() - templ.rows() + 1;
            Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC3);
            System.out.println("\nRead the columns");
            System.out.println("\nRead the column" + result_cols);
            System.out.println("\nRead the rows" + result_rows);
            // / Do the Matching and Normalize
            Imgproc.matchTemplate(img, templ, result, match_method);
            //Imgproc.matchTemplate(img,templ,result,Imgproc.TM_CCOEFF_NORMED);
            //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
            System.out.println("\nMatch template run");
            System.out.println("\nResult" + result);
            // / Localizing the best match with minMaxLoc
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            System.out.println("\nResult after minmax" + result);
            Point matchLoc;
            if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
                matchLoc = mmr.minLoc;
            } else {
                matchLoc = mmr.maxLoc;
            }
            Mat Temp = new Mat(result_rows, result_cols, CvType.CV_8UC3);
            System.out.println("\nResults temp mat" + Temp);
            //result.convertTo(Temp, CvType.CV_8UC4);
            System.out.println("\nResults temp mat" + Temp);
            //Mat fnl=new Mat(Temp.rows(),Temp.cols(),CvType.CV_8UC1);

            //Imgproc.cvtColor(Temp, fnl,Imgproc.COLOR_BGRA2GRAY,0);
            //System.out.println("\nFinal temp mat" + fnl);
            // / Show me what you got
            Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                    matchLoc.y + templ.rows()), new Scalar(0, 255, 0));

            // Save the visualized detection.


            System.out.println("\nRead the images" + templ);
            System.out.println("\nRead the images" + img);
            double d= correlation(templ, img);

            System.out.println("\nThe correlation of the images" +String.format("%.2f", d) );
            //Imgcodecs.imwrite(oF, result);
            Bitmap resultBitmap = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(img, resultBitmap);
            sonucimg.setImageBitmap(resultBitmap);
        }
    }


    double correlation(Mat image_1, Mat image_2) {
        Mat src_base =new Mat();
        Mat hsv_base=new Mat();
        Mat src_test1=new Mat();
        Mat hsv_test1=new Mat();


        try {
            src_base = Utils.loadResource(getApplicationContext(), R.drawable.ontl2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            src_test1 = Utils.loadResource(getApplicationContext(), R.drawable.ontl1, Imgcodecs.CV_LOAD_IMAGE_COLOR);


        }
        catch (IOException e){
            e.printStackTrace();
        }
        Imgproc.cvtColor(src_base, hsv_base, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src_test1, hsv_test1, Imgproc.COLOR_BGR2HSV);

        /// Using 50 bins for hue and 60 for saturation
        int hBins = 50;
        int sBins = 60;
        MatOfInt histSize = new MatOfInt( hBins,  sBins);

        // hue varies from 0 to 179, saturation from 0 to 255
        MatOfFloat ranges =  new MatOfFloat( 0f,180f,0f,256f );

        // we compute the histogram from the 0-th and 1-st channels
        MatOfInt channels = new MatOfInt(0, 1);


        Mat histRef = new Mat();
        Mat histSource = new Mat();

        ArrayList<Mat> histImages=new ArrayList<Mat>();
        histImages.add(hsv_base);
        Imgproc.calcHist(histImages,
                channels,
                new Mat(),
                histRef,
                histSize,
                ranges,
                false);
        Core.normalize(histRef,
                histRef,
                0,
                1,
                Core.NORM_MINMAX,
                -1,
                new Mat());

        histImages=new ArrayList<Mat>();
        histImages.add(hsv_test1);
        Imgproc.calcHist(histImages,
                channels,
                new Mat(),
                histSource,
                histSize,
                ranges,
                false);
        Core.normalize(histSource,
                histSource,
                0,
                1,
                Core.NORM_MINMAX,
                -1,
                new Mat());
        double resp1 = Imgproc.compareHist(histRef, histSource, 0);

        return resp1;
    }
}
