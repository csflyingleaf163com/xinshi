package com.zixin.zxpaintview;


import net.margaritov.preference.colorpicker.*;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;

import android.view.Menu;
import android.view.MenuItem;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity
{

    final int SELECT_IMAGE = 1;
    private MyView touchView = null;

    private boolean mAlphaSliderEnabled = false;
    private boolean mHexValueEnabled = false;
    private String mImagePath = null;
    private Paint mPaint =  null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchView = (MyView)findViewById(R.id.myView);
        initPaint();
        touchView.setPaint(mPaint);

//        Button btn = (Button)findViewById(R.id.savePicture);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = "xinshi"+System.currentTimeMillis() +".jpg";
////                touchView.saveImage(name);
//                saveFile(touchView.mBottomBitmap);
//            }
//        });

        Button btn = (Button)findViewById(R.id.settingBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePaint();
            }
        });

        btn = (Button)findViewById(R.id.clearCanvas);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            touchView.clearImage();
            }
        });
    }

    private void saveFile(Bitmap bitma){
//        String path = "/sdcard/DCIM/tsbhongwai22.jpg";
//        long dataTake = System.currentTimeMillis();
//        String jpegName = path + "/" + dataTake +".jpg";

        String jpegName = "/sdcard/DCIM/tsbhongwai22.jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bitma.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xee181818);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
//        BlurMaskFilter blur  = new BlurMaskFilter(13,BlurMaskFilter.Blur.NORMAL);
//        mPaint.setMaskFilter(blur);
    }

    public class BackgroundColorListener implements ColorPickerDialog.OnColorChangedListener
    {
        public void onColorChanged(int color)
        {
            touchView.setBitmapColor(color);

        }
    }

    public class PaintColorChangedListener implements ColorPickerDialog.OnColorChangedListener
    {
        public void onColorChanged(int color)
        {
            touchView.setBitmapColor(color);
        }
    }


    public boolean onOptionsItemSelected(MenuItem Item)
    {
        int item_id = Item.getItemId();
        switch(item_id)
        {
            case R.id.choose:
                chooseBackground();
                break;
            case R.id.pen:
                choosePaint();
                break;
            case R.id.save:
                touchView.saveImage(mImagePath);
                break;
            case R.id.clear:
                touchView.clearImage();
                break;
            case R.id.menu_undo:
                touchView.undo();
                break;
            case R.id.menu_redo:
                touchView.redo();
        }
        return true;
    }

    private void chooseBackground()
    {
        String[] itemsTo = {"—°‘ÒÕº∆¨","—°‘Ò—’…´"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("—°‘Ò±≥æ∞");
        builder.setItems(itemsTo, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                if(arg1 == 0)
                {
                    pickupLocalImage(SELECT_IMAGE);

                }
                if(arg1 == 1)
                {
                    showColorDialog(null);
                }
            }
        });
        builder.create().show();
    }

    protected void pickupLocalImage(int return_num)
    {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent,return_num);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void showColorDialog(Bundle state) {

        ColorPickerDialog dialog = new ColorPickerDialog(MainActivity.this,Color.BLACK);
        dialog.setOnColorChangedListener(new BackgroundColorListener());
        if (mAlphaSliderEnabled) {
            dialog.setAlphaSliderVisible(true);
        }
        if (mHexValueEnabled) {
            dialog.setHexValueEnabled(true);
        }
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case SELECT_IMAGE:
                    try{
                        Uri imgUri = data.getData();
                        if(imgUri != null){
                            ContentResolver cr = this.getContentResolver();
                            String[] columnStr = new String[]{MediaStore.Images.Media.DATA};
                            Cursor cursor = cr.query(imgUri, columnStr, null, null, null);
                            if(cursor != null){
                                int nID = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                                if(cursor.moveToFirst()){
                                    mImagePath = cursor.getString(nID);
                                    //Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
                                    touchView.setBitmap(mImagePath);
                                }
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            };
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class PaintChangeListener implements PaintDialog.OnPaintChangedListener
    {
        public void onPaintChanged(Paint paint)
        {
            touchView.setPaint(paint);
            mPaint = paint;
        }
    }
    private void choosePaint()
    {
        PaintDialog dialog = new PaintDialog(MainActivity.this);
        dialog.initDialog(dialog.getContext(),mPaint);
        dialog.setOnPaintChangedListener( new PaintChangeListener() );

    }


}
