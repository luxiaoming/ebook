/**
 *  Author :  hmg25
 *  Description :
 */
package com.lxm.txtapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;

public class BookPageFactory {

	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private String m_strCharsetName = "GBK";
	private Bitmap m_book_bg = null;
	private int mWidth;
	private int mHeight;
	boolean bIsUserBg = false;
	private Vector<String> m_lines = new Vector<String>();

	private int m_fontSize = 29;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffff9e85; // 背景颜色
	private int marginWidth = 15; // 左右与边缘的距离
	private int marginHeight = 10; // 上下与边缘的距离
	private int mlineMargin = 3;
	private int mLineCount; // 每页可以显示的行数
	private float mVisibleHeight; // 绘制内容的宽
	private float mVisibleWidth; // 绘制内容的宽
	private boolean m_isfirstPage, m_islastPage;
	// private TextClock a;
	// private int m_nLineSpaceing = 5;

	private Paint mPaint;

	public BookPageFactory(Context context, int w, int h, int backColor,
			int textColor, int line_jianju, int fontSize, int jindu) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		m_textColor = textColor;
		m_backColor = backColor;
		mlineMargin = line_jianju;
		m_fontSize = fontSize;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor);
		// Typeface face = Typeface.createFromAsset(context.getAssets(),
		// "fonts/yahei.ttf");
		mPaint.setTypeface(null);
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2 - 12;
		mLineCount = (int) (mVisibleHeight / (m_fontSize + mlineMargin)); // 可显示的行数
		// m_mbBufBegin=300;
		m_mbBufBegin = m_mbBufEnd = jindu;
	}

	public void fontsizechange(int fontsize) {
		m_fontSize = fontsize;
		mPaint.setTextSize(fontsize);
		mLineCount = (int) (mVisibleHeight / (m_fontSize + mlineMargin)); // 可显示的行数
		m_mbBufEnd = m_mbBufBegin;
		m_lines.clear();
		m_lines = pageDown();
		Log.i("lxm", "--" + m_fontSize + "--" + mLineCount + "--" + mlineMargin
				+ "--" + m_fontSize + "--" + m_fontSize);
	}

	public void fontline_jianju(int line_jianju) {
		mlineMargin = line_jianju;
		mLineCount = (int) (mVisibleHeight / (m_fontSize + mlineMargin)); // 可显示的行数
		m_mbBufEnd = m_mbBufBegin;
		m_lines.clear();
		m_lines = pageDown();
	}

	public void openbook(String strFilePath) throws IOException {
		book_file = new File(strFilePath);
		// book_file.
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
		m_strCharsetName = codeString(strFilePath);
		// Log.i("lxm", strFilePath);
		// m_mbBuf.

	}

	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName
	 *            :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String codeString(String fileName) {
		BufferedInputStream bin = null;
		int p = 0;
		String code = null;
		try {
			bin = new BufferedInputStream(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bin = null;
		}
		if (bin != null) {
			try {
				p = (bin.read() << 8) + bin.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}

		code = "UTF-8";
		// Log.i("lxm", code);
		// Log.i("lxm", "" + p);
		return code;
	}

	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}

	// 读取上一段落
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	protected void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage = true;
			return;
		} else
			m_isfirstPage = false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage = true;
			return;
		} else
			m_islastPage = false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}

	public void Draw(Canvas c, PageWidget mPageWidget) {
		Log.i("lxmDraw", "onDraw" + m_fontSize);
		;
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			if (bIsUserBg && m_book_bg != null)
				c.drawBitmap(m_book_bg, 0, 0, null);

			else {
				c.drawColor(m_backColor);
			}
			int y = marginHeight + m_fontSize;
			for (String strLine : m_lines) {

				c.drawText(strLine, marginWidth, y, mPaint);
				y += m_fontSize + mlineMargin;
			}
		}
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		mPageWidget.setjindu(strPercent);
	}

	public void Draw(Canvas c, int color, PageWidget mPageWidget) {
		// Log.i("lxm", "Draw");
		// if (m_lines.size() == 0)
		// m_lines = pageDown();
		Log.i("lxmDraw33", "onDraw" + m_fontSize);
		;
		m_backColor = color;
		if (m_lines.size() > 0) {
			if (bIsUserBg && m_book_bg != null)
				c.drawBitmap(m_book_bg, 0, 0, null);

			else {
				c.drawColor(m_backColor);
			}
			int y = marginHeight + m_fontSize;
			for (String strLine : m_lines) {

				c.drawText(strLine, marginWidth, y, mPaint);
				y += m_fontSize + mlineMargin;
			}
		}
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		// int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		// c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
		mPageWidget.setjindu(strPercent);
		// return strPercent;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;

		Matrix matrix = new Matrix();
		int width = m_book_bg.getWidth();// 获取资源位图的宽
		int height = m_book_bg.getHeight();// 获取资源位图的高
		// Log.i("lxm", "" + width);
		// Log.i("lxm", "" + height);
		float w = (float) mWidth / (float) m_book_bg.getWidth();
		float h = (float) mHeight / (float) m_book_bg.getHeight();
		matrix.postScale(w, h);// 获取缩放比例
		m_book_bg = Bitmap.createBitmap(m_book_bg, 0, 0, width, height, matrix,
				true);// 根据缩放比例获取新的位图

	}

	public void setUseBg(boolean is) {
		bIsUserBg = is;
	}

	public Vector<String> getlines() {
		return m_lines;
	}

	public boolean isfirstPage() {
		return m_isfirstPage;
	}

	public boolean islastPage() {
		return m_islastPage;
	}

	public String getData() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd    hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		return date;
	}

	public int get_jindu() {
		Log.i("jindu", "" + m_mbBufBegin + "--" + m_mbBufLen);
		return m_mbBufBegin;
		// Log.i("jindu", ""+m_mbBufBegin+"--"+m_mbBufLen);
	}

	public int get_jindu_baifen() {
		Log.i("jindu", "" + m_mbBufBegin + "--" + m_mbBufLen);
		return (m_mbBufBegin * 100) / m_mbBufLen;
	}

	public String get_work() {
		// Log.i("jindu", "" + m_mbBufBegin + "--" + m_mbBufLen);
		String ret = "";
		if (m_lines.size() > 0) {
			ret = m_lines.get(0);
			;
			if (m_lines.size() > 1) {
				ret += "\n" + m_lines.get(1);
			}
		} else {
			ret = "no work";
		}
		return ret;
	}

	public void set_jindu(int jindu) {
		if (jindu < 0) {
			m_mbBufBegin = m_mbBufEnd = 0;
		} else if (jindu > m_mbBufLen) {
			m_mbBufBegin = m_mbBufEnd = m_mbBufLen;
		} else {
			m_mbBufBegin = m_mbBufEnd = jindu;
		}
		pageUp();
		if (m_mbBufBegin != 0) {
			m_lines.clear();
			m_lines = pageDown();

			m_mbBufBegin = m_mbBufEnd;
		}
		m_lines.clear();
		m_lines = pageDown();
	}

	public void set_jindu_baifen(int jindu) {

		if (jindu < 0) {
			m_mbBufBegin = m_mbBufEnd = 0;
		} else if (jindu > 100) {
			m_mbBufBegin = m_mbBufEnd = m_mbBufLen;
		} else {
			m_mbBufBegin = m_mbBufEnd = (int) (((float) jindu / 100) * m_mbBufLen);
		}

		// if (m_mbBufBegin != m_mbBufLen) {
		pageUp();
		if (m_mbBufBegin != 0) {
			m_lines.clear();
			m_lines = pageDown();

			m_mbBufBegin = m_mbBufEnd;
		}
		m_lines.clear();
		m_lines = pageDown();
		// }
		Log.i("jindu", "" + m_mbBufBegin + "--" + m_mbBufLen);
	}

	public String getTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("kk:mm");
		String getTime = sDateFormat.format(new java.util.Date());
		return getTime;
	}

	public void setTextColor(int color) {
		mPaint.setColor(color);
	}
}
