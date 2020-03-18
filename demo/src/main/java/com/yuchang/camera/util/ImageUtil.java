/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuchang.camera.util;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.yuchang.camera.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import sun.misc.BASE64Decoder;

/**
 *
 *
 * 类名:ImageUtil.java 作者： 邓
 * 创建时间：2019-11-1 说明:图片处理工具类.
 *
 * 修改时间： 修改者： 修改说明：
 *
 */
@SuppressLint("SimpleDateFormat")
public class ImageUtil {
	private static Paint mPaint;
	private static Paint titilePaint;
	private static Paint titileTextPaint;
    private static int sADD_line_contents;

    private static int CANVAS_WIDTH = 5;
    private static int CANVAS_HEIGHT = 7;
    /**
	 * 图片压缩，按比例大小压缩方法
	 */
	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inPreferredConfig = Config.RGB_565;// 降低图片从ARGB888到RGB565
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 质量压缩.
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

		/*
		 * int options = 100; while ( baos.toByteArray().length / 1024>500) {
		 * //循环判断如果压缩后图片是否大于100kb,大于继续压缩 baos.reset();//重置baos即清空baos options -=
		 * 30;//每次都减少10 image.compress(Bitmap.CompressFormat.JPEG, options,
		 * baos);//这里压缩options%，把压缩后的数据存放到baos中 }
		 */
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}


	@SuppressWarnings("unused")
	private String getPhotoFileName(int type) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		return type + "" + ".png";
	}

	public static Bitmap getMap(String src, int width, int height)
			throws Exception {
		InputStream inputStream = new FileInputStream(src);
		// 通过一个InputStream创建一个BitmapDrawable对象
		BitmapDrawable drawable = new BitmapDrawable(inputStream);
		// 通过BitmapDrawable对象获得Bitmap对象
		Bitmap bm = drawable.getBitmap();
		// 利用Bitmap对象创建缩略图
		bm = ThumbnailUtils.extractThumbnail(bm, width, height);
		return bm;
	}

	/**
	 * 绘制文字到左下方
	 */
	public Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap,
            List<String> list_keywords,boolean b_titileShow_switch,String str_titileShow,
            Paint paint, float paddingLeft, float paddingBottom,int background_color_depth,int background_color,int CANVAS_WIDTH) {
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//		Log.d(TAG, "drawTextToLeftBottom: "+width+"----"+height);
		this.CANVAS_WIDTH = CANVAS_WIDTH;
		return drawTextToBitmap(context,bitmap, list_keywords,b_titileShow_switch,str_titileShow, paint,paddingLeft,bitmap.getHeight() - paddingBottom,background_color_depth,background_color);
	}

	static boolean havAddress = false;
	//图片上绘制文字
	public  Bitmap drawTextToBitmap(Context context, Bitmap bitmap,
        List<String> list_keywords,boolean b_titileShow_switch,String str_titileShow,
        Paint paint,float paddingLeft, float paddingBottom,int background_color_depth,int background_color) {
		Config bitmapConfig = bitmap.getConfig();
		paint.setDither(true); // 获取清晰的图像采样
		paint.setFilterBitmap(true);// 过滤一些
		if (bitmapConfig == null) {
			bitmapConfig = Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bitmap);
        Paint paint_title = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_title.setColor(Color.rgb(252, 100, 8));
        paint_title.setTextSize(100* getPxRatio(bitmap.getWidth(),bitmap.getHeight()));
        //canvas.drawText("电企通相机", paddingLeft,175*MainActivity.getPxRatio(bitmap.getWidth(),bitmap.getHeight()), paint_title);
		Rect rect = new Rect();
		if (list_keywords.size()>0){
            paint.getTextBounds(list_keywords.get(0), 0, list_keywords.get(0).length(), rect);//获取字体的外边框
            int h = rect.height()+15;//行与行之间的间距
            mPaint = new Paint();
			titilePaint = new Paint();
			titileTextPaint = new Paint();
			titilePaint.setColor(context.getResources().getColor(R.color.them_color_green));
            titileTextPaint.setColor(context.getResources().getColor(R.color.white));
            switch (background_color_depth) {//先做颜色的深度选择，再进行颜色的选择
                case 0:
                    mPaint.setColor(
                            context.getResources().getColor(R.color.titi_background_color_white_transparent));
                    break;
                case 1:
                    switch (background_color) {
						case -1:
                        case 0:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_white_1));
                            break;
                        case 1:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_blue_1));
                            break;
                        case 2:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_green_1));
                            break;
                        case 3:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_yellow_1));
                            break;
                        case 4:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_red_1));
                            break;
                        case 5:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_black_1));
                            break;
                        case 6:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_pruple_1));
                            break;
                    }
                    break;
                case 2:
                    switch (background_color) {
                        case 0:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_white_2));
                            break;
                        case 1:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_blue_2));
                            break;
                        case 2:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_green_2));
                            break;
                        case 3:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_yellow_2));
                            break;
                        case 4:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_red_2));
                            break;
                        case 5:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_black_2));
                            break;
                        case 6:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_pruple_2));
                            break;
                    }
                    break;
                case 3:
                    switch (background_color) {
                        case 0:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_white_3));
                            break;
                        case 1:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_blue_3));
                            break;
                        case 2:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_green_3));
                            break;
                        case 3:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_yellow_3));
                            break;
                        case 4:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_red_3));
                            break;
                        case 5:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_black_3));
                            break;
                        case 6:
                            mPaint.setColor(
                                    context.getResources().getColor(R.color.titi_background_color_pruple_3));
                            break;
                    }
                    break;
            }
            int j = 0;//变量j是多添加的行数，可以让
			int singleLine_textSize = 12;//单行默认显示的字数
            for (int i=0;i<list_keywords.size();i++){
				String str = list_keywords.get(i);
                if (str.startsWith("^_^")){// 以^_^开头的字符串说明是长字符串地址
					havAddress = true;
                	str = str.substring(3);
                	if (str.length()>singleLine_textSize){//地址大于12个字小于24个字的，显示为两行
                        sADD_line_contents = 2;
						String substring_front= str.substring(0,singleLine_textSize);
						String substring_end = str.substring(singleLine_textSize);
						canvas.drawText("位置信息："+substring_front, paddingLeft, paddingBottom-(list_keywords.size()-i-j-1)*h, paint);j++;
						canvas.drawText("\u3000\u3000\u3000\u3000\u3000"+substring_end, paddingLeft, paddingBottom-(list_keywords.size()-i-j-1)*h, paint);
					}else {//其他情况一行显示
                        sADD_line_contents = 1;
						canvas.drawText("位置信息："+str, paddingLeft, paddingBottom-(list_keywords.size()-i-j-1)*h, paint);
					}
				}else {//其他信息情况
                    canvas.drawText(str, paddingLeft, paddingBottom-(list_keywords.size()-i-(j+1))*h, paint);
                }
            }// for,,,end
			// 背景画布
			if (havAddress){//有地址的时候画布最上方会多两行，进行修复
				j = j - 1;
			}
			if (sADD_line_contents ==2){
                canvas.drawRect(new RectF(paddingLeft-10*getPxRatio(bitmap.getWidth(),bitmap.getHeight()),
						paddingBottom-(list_keywords.size()+j)*h-0*getPxRatio(bitmap.getWidth(),bitmap.getHeight()),
						CANVAS_WIDTH*(bitmap.getWidth()/CANVAS_HEIGHT),
						paddingBottom+(5*h) ), mPaint);
            }else{
                canvas.drawRect(new RectF(
                		paddingLeft-10*getPxRatio(bitmap.getWidth(),bitmap.getHeight()),
						paddingBottom-(list_keywords.size()+(j+1))*h-0*getPxRatio(bitmap.getWidth(),bitmap.getHeight()),
						CANVAS_WIDTH*(bitmap.getWidth()/CANVAS_HEIGHT),
						paddingBottom + (5*h)), mPaint);
            }
			Rect rect1;
			if (b_titileShow_switch){
				switch (sADD_line_contents){
					case 3:
						rect1 = new Rect((int) (paddingLeft-10*getPxRatio(bitmap.getWidth(),bitmap.getHeight())),
								(int) paddingBottom-(list_keywords.size()+j-1)*h-(h/2),
								(int) CANVAS_WIDTH*(bitmap.getWidth()/CANVAS_HEIGHT),
								(int) paddingBottom-(list_keywords.size()+(j-2))*h);
						break;
					case 2:
						rect1 = new Rect((int) (paddingLeft-10*getPxRatio(bitmap.getWidth(),bitmap.getHeight())),
								(int)paddingBottom-(list_keywords.size()+(j+1))*h-(h/2),
								(int) CANVAS_WIDTH*(bitmap.getWidth()/CANVAS_HEIGHT),
								(int) paddingBottom-(list_keywords.size()+(j))*h);
						break;
					default:
						rect1 = new Rect((int) (paddingLeft-10*getPxRatio(bitmap.getWidth(),bitmap.getHeight())),
								(int) paddingBottom-(list_keywords.size()+j+2)*h-(h/2),
								(int) CANVAS_WIDTH*(bitmap.getWidth()/CANVAS_HEIGHT),
								(int) paddingBottom-(list_keywords.size()+(j+1))*h);
						break;
				}
				Paint rectPaint = new Paint();
				rectPaint.setColor(context.getResources().getColor(R.color.them_color_green));
				rectPaint.setStyle(Paint.Style.FILL);
				canvas.drawRect(rect1, rectPaint);
//				titileTextPaint.setStyle(Paint.Style.FILL); //标题居中显示，现在需求靠左了
				//该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
//				titileTextPaint.setTextAlign(Paint.Align.CENTER); //标题居中显示，现在取消了
				Paint.FontMetrics fontMetrics = titileTextPaint.getFontMetrics();
				float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
				float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
				//计算baseline
				titileTextPaint.setTextSize(paint.getTextSize());
				float distance=(bottom - top)/2 + bottom*6;
				float baseline=rect1.centerY()+distance;
//				canvas.drawText(str_titileShow, rect1.centerX(), baseline, titileTextPaint); //标题居中显示，现在需求靠左了
				canvas.drawText(str_titileShow, paddingLeft, baseline, titileTextPaint);
			}
        }// if,,,end
		havAddress = false;
		return bitmap;
	}

	//图片上绘制文字
	private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
										   Paint paint, Rect bounds, int paddingLeft, int paddingTop,int width,int high) {
		Config bitmapConfig = bitmap.getConfig();
		paint.setDither(true); // 获取跟清晰的图像采样
		paint.setFilterBitmap(true);// 过滤一些
		if (bitmapConfig == null) {
			bitmapConfig = Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, paddingLeft, paddingTop, paint);
		return bitmap;
	}


	/**
	 * dip转pix
	 */
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
        Log.d(TAG, "dp2px: "+scale);
		return (int) (dp * scale + 0.5f);
	}



	public static Bitmap createDegree(Bitmap bitmap,int orientationDegree){
		if(bitmap!=null){
			Matrix m=new Matrix();
			try{
				m.setRotate(orientationDegree, bitmap.getWidth()/2, bitmap.getHeight()/2);//90就是我们需要选择的90度
				Bitmap bmp2=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				bitmap.recycle();
				bitmap=bmp2;
			}catch(Exception ex){
				System.out.print("创建图片失败！"+ex);
			}
		}
		return bitmap;
	}


	private static int baseBitmapWidth = 1080;//在拍出照片的bitmap为1080*1920的真机下测试
	private static int baseBitmapHeight = 1920;
	public static float getPxRatio(float realBitmapWidth, float realBitmapHeight) {
		float ratioWidth = realBitmapWidth / baseBitmapWidth;
		float ratioHeight = realBitmapHeight / baseBitmapHeight;
		return Math.min(ratioWidth, ratioHeight);
	}
}
