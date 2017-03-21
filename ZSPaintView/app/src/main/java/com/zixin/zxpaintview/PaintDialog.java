package com.zixin.zxpaintview;

import com.zixin.zxpaintview.MainActivity.BackgroundColorListener;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class PaintDialog extends AlertDialog{

	private SeekBar 	mSeekBar;
	private TextView 	mTextSeek;
	private Spinner 	mSpinner;
	private Paint 		mPaint;
	private Paint		mPaintTemp;
	private MaskFilter 	mBlur;
	private MaskFilter 	mEmboss;
	private Button 		mButtonColor;
	private DrawLine	mDrawLine;
	
	private int mColor;
	private int mPenWidth = 1;
	
	private OnPaintChangedListener mListener;
	
	public interface OnPaintChangedListener
	{
		public void onPaintChanged(Paint paint);
	}
	
	public void setOnPaintChangedListener(OnPaintChangedListener listener)
	{
		mListener = listener;
	}
	
	public PaintDialog(Context context)
	{
		super(context);
	}
	
	public PaintDialog(Context context,int theme)
	{
		super(context,theme);
	}
	
	public void initDialog(Context context,Paint paint)
	{
		Builder builder = new Builder(context);
		builder.setTitle(null);
		View paintView = getLayoutInflater().inflate(R.layout.set_paint,null);
		builder.setView(paintView );
		
		mPaintTemp = new Paint(paint);
		mPaint	 = paint;
		mBlur  = new BlurMaskFilter(13,BlurMaskFilter.Blur.NORMAL);
		mEmboss= new EmbossMaskFilter(new float[]{1.0f,1.0f,1.0f},0.4f,6,3.5f);
		mSeekBar = (SeekBar)paintView.findViewById(R.id.seekbarPenWidth);
//		mSpinner = (Spinner)paintView.findViewById(R.id.spinnerPaint);
		mTextSeek= (TextView)paintView.findViewById(R.id.textPenWidth);
		mButtonColor = (Button)paintView.findViewById(R.id.btnColor);

		mDrawLine	 = (DrawLine)paintView.findViewById(R.id.lineShow);
		mPenWidth	 = (int)mPaint.getStrokeWidth();
		mColor		 = mPaint.getColor();
		updateUIColor(mColor);
		
//		initSpinner(context,mSpinner);
		initSeekBar(mSeekBar,mPenWidth);
		initButton(mButtonColor);
		initDrawLine(mDrawLine);
		
		builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mPaint = mPaintTemp;
				if (mPaint != null)
				{
					mListener.onPaintChanged(mPaint);
				}
			}
		});
		
		builder.create().show();
	}
	
	private void initSeekBar(SeekBar seekBar,int width)
	{
		seekBar.setOnSeekBarChangeListener(new SeekBarListener());
		seekBar.setProgress(width);
	}
	
	private void initSpinner(Context context,Spinner spinner)
	{
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, 
				R.array.paint_spinner,android.R.layout.simple_spinner_item);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		spinner.setAdapter(adapter);
		MaskFilter mask = mPaint.getMaskFilter();
		int nSelected = 0;
		if (mask instanceof BlurMaskFilter)
		{
			nSelected = 0;
		}
		else 
		{
			if(mask instanceof EmbossMaskFilter)
				nSelected = 1;
			else
				nSelected = 2;
		}
		
		spinner.setSelection(nSelected);
		spinner.setOnItemSelectedListener(new SpinnerItemSelected());
	}
	
	private void initButton(Button button)
	{
		button.setBackgroundColor(mPaint.getColor());
		button.setOnClickListener(new ColorClickListener());
	}
	
	private void initDrawLine(DrawLine line)
	{
		line.setPaint(mPaint);
	}
	public class SpinnerItemSelected implements OnItemSelectedListener 
	{
		public void onItemSelected(AdapterView<?> parent, View view,int pos,long id)
		{
			switch(pos)
			{
			case 0:
				mPaint.setMaskFilter(mBlur);
				if(mListener != null)
				{
					mListener.onPaintChanged(mPaint);
					mDrawLine.onPaintChanged(mPaint);
				}
				break;
			case 1:
				mPaint.setMaskFilter(mEmboss);
				if(mListener != null)
				{
					mListener.onPaintChanged(mPaint);
					mDrawLine.onPaintChanged(mPaint);
				}
				break;
			case 2:
				mPaint.setMaskFilter(null);
				if(mListener != null)
				{
					mListener.onPaintChanged(mPaint);
					mDrawLine.onPaintChanged(mPaint);
				}
				break;
			}
		}
		public void onNothingSelected(AdapterView<?> parent)
		{
			
		}
	}
	
	public class SeekBarListener implements OnSeekBarChangeListener
	{
		@Override
		public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser)
		{
			mSeekBar.setProgress(progress);
			mPaint.setStrokeWidth(progress);
			mPenWidth = progress;
			mTextSeek.setText("画笔宽度:" + progress);
			
			if(mListener != null)
			{
				mListener.onPaintChanged(mPaint);
				mDrawLine.onPaintChanged(mPaint);
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			
		}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			
		}
	}
	
	public class ColorClickListener implements View.OnClickListener, 
							ColorPickerDialog.OnColorChangedListener
	{
		public void onClick(View view)
		{
			boolean bAlphaSliderEnabled = false;
			boolean bHexValueEnabled = false;
			Bundle state = null;
			ColorPickerDialog dialog = new ColorPickerDialog(getContext(), mPaint.getColor());
			dialog.setOnColorChangedListener(this);
			if (bAlphaSliderEnabled) {
				dialog.setAlphaSliderVisible(true);
			}
			if (bHexValueEnabled) {
				dialog.setHexValueEnabled(true);
			}
			dialog.show();
		}
		
		public void onColorChanged(int color)
		{
			updateUIColor(color);
		}
	}

	private void updateUIColor(int color){
		mPaint.setColor(color);
		mButtonColor.setBackgroundColor(color);
		mColor = color;
		if(mListener != null)
		{
			mListener.onPaintChanged(mPaint);
			mDrawLine.onPaintChanged(mPaint);
		}
	}

	public void setPaint(Paint paint)
	{
		mPaint = paint;
	}
	
	public Paint getPaint()
	{
		return mPaint;
	}
	
}
