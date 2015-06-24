package nuist.lib.dss.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nuist.lib.dss.dao.ConnSQL;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

public class ExcelManager {
	// 连接数据库需要的变量
	Connection conn;
	PreparedStatement st;
	boolean mark; // 用来表明是否正确取到配置信息
	private Logger logger;
	private List<Integer> error;     //在excel对应的Integer行中的数据在数据库中已经有了
	private List<Integer> firstOrderError;   //在附加数据时，每个场次的第一个出场顺序错误，与数据库的不匹配
	private List<Integer> orderError;          //出场顺序错误(没有校对数据库，可能是出场顺序不连续)

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public ExcelManager() {
		logger = Logger.getLogger(ExcelManager.class.getName());
		ConnSQL connSql = new ConnSQL();
		conn = connSql.connectDataBase();
		mark = connSql.isConnected();
	}

	/** 判断是否连接到了数据库 */
	public boolean isCollected() {
		return mark;
	}

	


	public void close() {
		try {
			if (!conn.isClosed()) {
				conn.close();
			}
			if (st != null && !st.isClosed()) {
				st.close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/** 将成绩导出 ,参数分别为数据，文件名，比赛事件，比赛项目 */
	public boolean ExportExcel(List<HashMap<String, Object>> data,
			String fileName, String matchName, String category, int matchType) {
		String matchTypeString = "";
		if (matchType == 0) {
			matchTypeString = "预赛";
		} else if (matchType == 1) {
			matchTypeString = "决赛";
		}
		String[] params = this.getParams();
		// 创建一个HSSFWorkbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// 由HSSFWorkbook创建一个HSSFSheet
		HSSFSheet sheet = wb.createSheet("sheet1");
		sheet.setDefaultColumnWidth((short)6);
		sheet.setColumnWidth((short)1, (short)4000);
		sheet.setColumnWidth((short)14, (short)2000);
		sheet.setColumnWidth((short)2, (short)3350);   //92像素
		sheet.setColumnWidth((short)16, (short)2000);
		sheet.setColumnWidth((short)17, (short)2000);
		sheet.setMargin(HSSFSheet.LeftMargin, (short) 0.5);
		sheet.setMargin(HSSFSheet.RightMargin, (short) 0.5);
		sheet.setMargin(HSSFSheet.TopMargin, (short) 0.5);
		sheet.setMargin(HSSFSheet.BottomMargin, (short) 0.5);
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setPaperSize((HSSFPrintSetup.A4_PAPERSIZE));
		printSetup.setLandscape(true);
		// 由HSSFSheet创建HSSFRow
		// 创建显示赛事的标题
	    HSSFRow row_temp=sheet.createRow((short)0);
	    row_temp.setHeightInPoints((float)14.5);   //19像素
		HSSFRow row0 = sheet.createRow((short) 1);
		row0.setHeightInPoints((float)25.5);      //34像素
		HSSFCell cell0 = row0.createCell((short) 0);
		cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell0.setCellValue(matchName);
		HSSFCellStyle matchNamestyle = wb.createCellStyle();
		matchNamestyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont matchNameFont = wb.createFont(); // 设置字体样式
		matchNameFont.setFontName("方正小标宋简体");
		matchNameFont.setFontHeightInPoints((short)20);  //设置赛事名称的高度
		matchNamestyle.setFont(matchNameFont);
		cell0.setCellStyle(matchNamestyle);
		for (int i = 1; i < 18; i++) {
			cell0 = row0.createCell((short) i);
			cell0.setCellStyle(matchNamestyle);
		}
		// 将赛事标题所在行的第一列到第18列合并
		sheet.addMergedRegion(new Region(1, (short) 0, 1, (short) 17));
		// 创建项目名称(占据第三行)
		HSSFRow subRow = sheet.createRow(2);
		HSSFCellStyle subStyle = wb.createCellStyle();
		subStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFFont subFont = wb.createFont();
		subFont.setFontName("黑体");
		subFont.setFontHeightInPoints((short)11);   //设置项目的高度
		subStyle.setFont(subFont);
		HSSFCell subCell = subRow.createCell((short) 0);
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue(category+matchTypeString);		
		for (int i = 1; i < 18; i++) {
			subCell = subRow.createCell((short) i);
		}	
		sheet.addMergedRegion(new Region(2, (short) 0, 2, (short) 13));
		subStyle = wb.createCellStyle();
		subStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		subFont = wb.createFont();
		subFont.setFontName("黑体");
		subFont.setFontHeightInPoints((short)11);   //设置项目的高度
		subStyle.setFont(subFont);
		subCell = subRow.createCell((short) 14);		
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		sheet.addMergedRegion(new Region(2, (short) 14,2, (short) 17));
		createHeader(sheet,wb,3,4,matchType);	
		
		// 添加数据时的样式
		HSSFCellStyle datastyle = wb.createCellStyle();
		datastyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		datastyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		datastyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		datastyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		datastyle.setWrapText(true);
		datastyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		datastyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font=wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short)10);
		datastyle.setFont(font);
		
		HSSFRow row=null;

		// /下面的是根据list 进行遍历循环
		int i = 5;     //第6行才是队伍成绩等
		int k = 1;     //数据的下标
		int count = data.size();
		int left; // 总共的行数
		int pageSize; // 总共的页数
		int currentPage = 1; // 当前的页数
		if (count <= 12) {    //第一页显示10个队伍
			left = 20;
			pageSize = 1;
		} else {
			pageSize = (count - 12) % 13 == 0 ? (count - 12) / 13
					: (count - 12) / 13 + 1; // 页数
			left = 20 + pageSize * 19;
			pageSize = pageSize + 1;
		}
		HSSFCellStyle footerStyle = wb.createCellStyle(); // 脚页面的文字
		footerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell=null;
		 for(;i<left;i++){
			 row = sheet.createRow((short) i);
			 row.setHeightInPoints((float)14.5);  //19像素
		 }
		 i=5;
		for (; i <left; i++) {
             if(((i+2)<left)&&(i-1)%19==0){   //18为除了第一页,后面每一页的行数-1
            	 i++;
            	 createHeader(sheet,wb,i,i+1,matchType);
            	 i=i+1;
             }else{
			HashMap<String, Object> o = (HashMap<String, Object>) data
					.get(k - 1);

			row = sheet.getRow(i);
			if (row == null)
				row = sheet.createRow((short) i);
			row.setHeightInPoints((float)38.5);  //51像素
			for (int j = 0; j < 18; j++) {
				cell = row.getCell((short) j);
				if (cell == null)
					cell = row.createCell((short) j);
				cell.setCellStyle(datastyle);
			}
			/* 创建参赛排名的单元格 */
			cell = row.getCell((short) 0);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("rank") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("rank")));
			}
			cell.setCellStyle(datastyle);
			/* 创建参赛单位的单元格 */
			cell = row.getCell((short) 1);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("teamName") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("teamName")));
			}
			cell.setCellStyle(datastyle);
			// 设置姓名的单元格
			cell = row.getCell((short) 2);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("memberName") == null) {
				cell.setCellValue("");
			} else {
				String[] names=String.valueOf(o.get("memberName")).split(",");
				String temp_name="";
				int g=0;
				for(;g<names.length;g++){
					if((g+1)%2==0){
						temp_name+=names[g]+"\n";
					}else{
						temp_name+=names[g]+" ";
					}
				}
				if(g==names.length&&(g%2)==0){
					temp_name=temp_name.substring(0,temp_name.lastIndexOf("\n"));
				}else{
					temp_name=temp_name.substring(0,temp_name.lastIndexOf(" "));
				}
				cell.setCellValue(temp_name);
			}
			cell.setCellStyle(datastyle);
			//设置套次的单元格
			cell = row.getCell((short) 3);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if(category.endsWith("I")){
				cell.setCellValue(String.valueOf(1));
			}else if(category.endsWith("II")){
				cell.setCellValue(String.valueOf(2));
			}else{
				cell.setCellValue("\\");
			}
			cell.setCellStyle(datastyle);
			//设置完成的单元格
			cell = row.getCell((short) 4);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("completion1") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("completion1"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 5);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("completion2") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("completion2"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 6);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("completion3") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("completion3"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 7);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("completion4") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("completion4"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 8);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("completionTotal") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.000").format(o.get("completionTotal"))));
			}
			cell.setCellStyle(datastyle);
			//设置艺术裁判打分单元格
			cell = row.getCell((short) 9);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("art1") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("art1"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 10);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("art2") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("art2"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 11);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("art3") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("art3"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 12);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("art4") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.0").format(o.get("art4"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 13);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("artTotal") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.000").format(o.get("artTotal"))));
			}
			cell.setCellStyle(datastyle);
			//设置难度的单元格
			cell = row.getCell((short) 14);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("difficult") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.000").format(o.get("difficult"))));
			}
			cell.setCellStyle(datastyle);
			cell = row.getCell((short) 15);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("difficult_sub") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.000").format(o.get("difficult_sub"))));
			}
			cell.setCellStyle(datastyle);
			/* 创建裁判长减分的单元格 */
			cell = row.getCell((short) 16);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("sub_score") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(new DecimalFormat("#0.000").format(o.get("sub_score"))));
			}
			cell.setCellStyle(datastyle);
			/* 创建最后得分单元格 */
			cell = row.getCell((short) 17);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("total") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(new DecimalFormat("#0.000").format(o
						.get("total")));
			}
			cell.setCellStyle(datastyle);
			if (k <= 12) {
				currentPage = 1;
			} else {
				currentPage = (k - 12) % 13 == 0 ? (k - 12) / 13 + 1
						: (k - 12) / 13 + 2;
			}
			if (k == data.size() && currentPage == 1 && data.size() <= 12) {
				for(int m=(i+1);m<=(i+12 - data.size());m++){	
					sheet.getRow(m).setHeightInPoints((float)38.5);
				}
				i = i + (12 - data.size());
				// 创造签名的行
				i = i + 1;
				HSSFRow rowLast = sheet.getRow(i);
				HSSFCell cellLast=null;
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				for(int j=0;j<18;j++){
					cellLast=rowLast.getCell((short)j);
					if (cellLast == null)
						cellLast = rowLast.createCell((short) j);
				}
				
				cellLast=rowLast.getCell((short)1);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("总裁判长：");
				sheet.addMergedRegion(new Region(i, (short) 1, i, (short) 5));
				cellLast = rowLast.getCell((short) 6);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("总记录长:");
				sheet.addMergedRegion(new Region(i, (short) 6, i, (short) 17));
				i=i+2;
			} else if (k == data.size() && currentPage == pageSize) {
				for(int m=(i+1);m<=(i+12 + 13 * (currentPage - 1) - k);m++){	
					sheet.getRow(m).setHeightInPoints((float)38.5);
				}
				i = i + (12 + 13 * (currentPage - 1) - k);
				// 创造签名的行
				i = i + 2;
				HSSFRow rowLast = sheet.getRow(i);
				HSSFCell cellLast=null;
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				for(int j=0;j<18;j++){
					cellLast=rowLast.getCell((short)j);
					if (cellLast == null)
						cellLast = rowLast.createCell((short) j);
				}
				
				cellLast=rowLast.getCell((short)1);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("总裁判长：");
				sheet.addMergedRegion(new Region(i, (short) 1, i, (short) 5));
				cellLast = rowLast.getCell((short) 6);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("总记录长:");
				sheet.addMergedRegion(new Region(i, (short) 6, i, (short) 17));
				i++;
			} else if ((k - 12) % 13 == 0 && currentPage < pageSize) { // 中间页
				i = i + 2;
				row = sheet.createRow((short) i);
				for (int j = 0; j < 18; j++) {
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i, (short) 0, i, (short) 17));
				i=i+1;
			} // 第一页的脚底
			k++;
          }
		}

		final File file = new File(fileName);
		try {

			OutputStream os = new FileOutputStream(file);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				wb.write(os);
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void createHeader(HSSFSheet sheet,HSSFWorkbook wb,int row1,int row2,int matchType){
		// 第4行和第5行设置小标题
		HSSFRow row3 = sheet.createRow(row1);
		row3.setHeightInPoints((float)19);    //25像素
		HSSFRow row4 = sheet.createRow(row2);
		row4.setHeightInPoints((float)19);
		HSSFCellStyle lineStyle = wb.createCellStyle();
		lineStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		lineStyle.setWrapText(true);
		lineStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		lineStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font=wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short)11);
		lineStyle.setFont(font);
		
		//设置名次
		HSSFCell columnCell=row3.createCell((short)0);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		if(matchType==0){
			columnCell.setCellValue("位次");	
		}else
			columnCell.setCellValue("名次");
		columnCell=row4.createCell((short)0);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)0,row2,(short)0));
		//队伍名称
		columnCell=row3.createCell((short)1);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("队伍名称");
		columnCell=row4.createCell((short)1);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)1,row2,(short)1));
		//设置姓名
		columnCell=row3.createCell((short)2);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("姓名");
		columnCell=row4.createCell((short)2);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)2,row2,(short)2));
		//设置套次
		columnCell=row3.createCell((short)3);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("套次");
		columnCell=row4.createCell((short)3);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)3,row2,(short)3));
		//设置完成
		columnCell=row3.createCell((short)4);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("完成裁判");
		for(int i=5;i<8;i++){
			columnCell=row3.createCell((short)i);
			columnCell.setCellStyle(lineStyle);
		}
		sheet.addMergedRegion(new Region(row1,(short)4,row1,(short)7));
		for(int i=4;i<8;i++){
			columnCell=row4.createCell((short)i);
			columnCell.setCellStyle(lineStyle);
			columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			columnCell.setCellValue((i-3)+"");
			columnCell.setCellStyle(lineStyle);
		}
		
		//设置完成总分
		columnCell=row3.createCell((short)8);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("小计");
		columnCell=row4.createCell((short)8);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)8,row2,(short)8));
        
		//设置艺术
		columnCell=row3.createCell((short)9);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("艺术裁判");
		for(int i=10;i<13;i++){
			columnCell=row3.createCell((short)i);
			columnCell.setCellStyle(lineStyle);
		}
		sheet.addMergedRegion(new Region(row1,(short)9,row1,(short)12));
		for(int i=9;i<13;i++){
			columnCell=row4.createCell((short)i);
			columnCell.setCellStyle(lineStyle);
			columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			columnCell.setCellValue((i-8)+"");
			columnCell.setCellStyle(lineStyle);
		}
		
		//设置艺术总分
		columnCell=row3.createCell((short)13);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("小计");
		columnCell=row4.createCell((short)13);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)13,row2,(short)13));
		
        //设置难度
		columnCell=row3.createCell((short)14);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("难度裁判");
		columnCell=row3.createCell((short)15);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)14,row1,(short)15));
		columnCell=row4.createCell((short)14);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("难度分");
		columnCell=row4.createCell((short)15);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("减分");
		
		
		//设置裁判长减分
		columnCell=row3.createCell((short)16);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("裁判长减分");
		columnCell=row4.createCell((short)16);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)16,row2,(short)16));
		
		//设置最后得分
		columnCell=row3.createCell((short)17);
		columnCell.setCellStyle(lineStyle);
		columnCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		columnCell.setCellValue("最后得分");
		columnCell=row4.createCell((short)17);
		columnCell.setCellStyle(lineStyle);
		sheet.addMergedRegion(new Region(row1,(short)17,row2,(short)17));
	}
	
	   /** 将成绩导出 ,参数分别为数据，文件名，比赛事件，比赛项目*/
	public boolean ExportScoreExcel(List<HashMap<String, Object>> data,
			String fileName,String matchName,String category,int matchType) {
		String matchTypeString="";
		if(matchType==0){
			matchTypeString="预赛";
		}else if(matchType==1){
			matchTypeString="决赛";
		}
		// 创建一个HSSFWorkbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// 由HSSFWorkbook创建一个HSSFSheet
		HSSFSheet sheet = wb.createSheet("sheet1");
		sheet.setDefaultColumnWidth((short)3);
		sheet.setColumnWidth((short)1, (short)2000);
		sheet.setColumnWidth((short)2, (short)2000);
		sheet.setColumnWidth((short)3, (short)2000);
		sheet.setColumnWidth((short)4, (short)2000);
		sheet.setColumnWidth((short)5, (short)1600);
		sheet.setColumnWidth((short)7, (short)2000);
		sheet.setColumnWidth((short)8, (short)2000);
		sheet.setColumnWidth((short)9, (short)2000);
		sheet.setColumnWidth((short)10, (short)2000);
		sheet.setColumnWidth((short)11, (short)1600);
		sheet.setColumnWidth((short)13, (short)1600);
		sheet.setColumnWidth((short)14, (short)1600);
		sheet.setColumnWidth((short)15, (short)1600);
		sheet.setColumnWidth((short)17, (short)1600);
		sheet.setColumnWidth((short)18, (short)1600);
		sheet.setMargin(HSSFSheet.TopMargin, (short)1);
		sheet.setMargin(HSSFSheet.BottomMargin, (short)0.8);
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setPaperSize((HSSFPrintSetup.A4_PAPERSIZE));
		printSetup.setLandscape(true);
		// 由HSSFSheet创建HSSFRow
		//创建显示赛事的标题
		HSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeight((short)400);
		HSSFCell cell0 = row0.createCell((short) 0);
		cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell0.setCellValue(matchName);
		HSSFCellStyle matchNamestyle=wb.createCellStyle();
		matchNamestyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont matchNameFont=wb.createFont();    //设置字体样式
		matchNameFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		matchNameFont.setFontHeight((short)(15*20));
		matchNamestyle.setFont(matchNameFont);
		cell0.setCellStyle(matchNamestyle);
		for(int i=1;i<20;i++){
			cell0 = row0.createCell((short) i);
			cell0.setCellStyle(matchNamestyle);
		}
		String[] params=getParams();
		//将赛事标题所在行的第一列到第20列合并
		sheet.addMergedRegion(new Region(0,(short)0,0,(short)19));  
		//创建子标题(占第二行和第三行，15和16列)
		HSSFRow subRow=sheet.createRow(2);
		HSSFCellStyle subStyle=wb.createCellStyle();
		subStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont subFont=wb.createFont();
		subFont.setFontHeight((short)(12*15));
		subStyle.setFont(subFont);
		HSSFCellStyle subValueStyle=wb.createCellStyle();
		subValueStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFFont subValueFont=wb.createFont();
		subValueFont.setFontHeight((short)(12*15));
		subValueStyle.setFont(subValueFont);
		HSSFCell subCell=subRow.createCell((short)1);
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue("比赛地点:");
		subCell=subRow.createCell((short)2);
		sheet.addMergedRegion(new Region(2,(short)1,2,(short)2));
		subCell=subRow.createCell((short)3);
		subCell.setCellStyle(subValueStyle);
		subCell.setCellValue(params[3]);
		subCell=subRow.createCell((short)4);
		sheet.addMergedRegion(new Region(2,(short)3,2,(short)4));
		subCell=subRow.createCell((short)13);
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue("比赛时间:");
		subCell=subRow.createCell((short)14);
		sheet.addMergedRegion(new Region(2,(short)13,2,(short)14));
		subCell=subRow.createCell((short)15);
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
		String time=format.format(new Date(System.currentTimeMillis()));
		subCell.setCellStyle(subStyle);
		subCell.setCellValue(time);
		subCell=subRow.createCell((short)16);
		subCell=subRow.createCell((short)17);
		sheet.addMergedRegion(new Region(2,(short)15,2,(short)17));
       
		//第3行的bottom上添加线条
		HSSFRow lineRow=sheet.createRow(3);
		HSSFCellStyle lineStyle=wb.createCellStyle();
		lineStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		HSSFCell lineCell;
		for(int i=0;i<20;i++){
			lineCell=lineRow.createCell((short)i);
			lineCell.setCellStyle(lineStyle);
		}
		
		//第4行设置参赛项目名称
		HSSFRow categoryRow=sheet.createRow(4);
	    categoryRow.setHeight((short)400);
		HSSFCellStyle categoryStyle=wb.createCellStyle();
		categoryStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);	
		HSSFFont categoryFont=wb.createFont();
		categoryFont.setFontHeight((short)(14*15));
		categoryFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		categoryStyle.setFont(categoryFont);
		HSSFCell categoryCell=categoryRow.createCell((short)5);
		categoryCell.setCellStyle(categoryStyle);
		categoryCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		categoryCell.setCellValue(category+" "+matchTypeString);
		for(int i=10;i<14;i++){
			categoryCell=categoryRow.createCell((short)i);
			categoryCell.setCellStyle(categoryStyle);
		}
		sheet.addMergedRegion(new Region(4,(short)5,4,(short)14));  
		
		//设置第7行
		HSSFRow NineRow=sheet.createRow(7);
		NineRow.setHeight((short)50);
		
		//总表头行，占第8行
		HSSFRow totalRow=sheet.createRow(8);
		totalRow.setHeight((short)400);
		HSSFCellStyle totalStyle=wb.createCellStyle();
		HSSFCell totalCell;
		HSSFFont totalFont=wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		totalStyle.setFont(totalFont);
		totalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		totalStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		for(int i=0;i<20;i++){
			totalCell=totalRow.createCell((short)i);
			totalCell.setCellStyle(totalStyle);
		}
		totalCell=totalRow.getCell((short)0);
		totalCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		totalCell.setCellValue("名次");
		totalCell=totalRow.getCell((short)1);
		totalCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		totalCell.setCellValue("姓名/单位");
		sheet.addMergedRegion(new Region(8,(short)1,8,(short)2)); 
		
		//创建excel的表头占第9行和第10行
		HSSFCellStyle style=wb.createCellStyle();
		 totalFont=wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(totalFont);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		HSSFRow row = sheet.createRow((short) 9);
		HSSFCell cell=null;
		style=wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setWrapText(true);
		row = sheet.createRow((short) 10);
		cell = row.createCell((short) 1);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("艺术分1");
		cell = row.createCell((short) 2);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("艺术分2");
		cell = row.createCell((short) 3);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("艺术分3");
		cell = row.createCell((short) 4);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("艺术分4");
		cell = row.createCell((short) 5);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("艺术平均分");
		cell = row.createCell((short) 7);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("完成分1");
		cell = row.createCell((short) 8);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("完成分2");
		cell = row.createCell((short) 9);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("完成分3");
		cell = row.createCell((short) 10);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("完成分4");
		cell = row.createCell((short) 11);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("完成平均分");
		cell = row.createCell((short) 13);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("总体评价1");
		cell = row.createCell((short) 14);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("总体评价2");
		cell = row.createCell((short) 15);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("总体评价分");
		

		cell = row.createCell((short) 17);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("裁判长减分");
		cell = row.createCell((short) 18);
		style=wb.createCellStyle();
		totalFont=wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(totalFont);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("最后得分");
		//sheet.addMergedRegion(new Region(9,(short)17,10,(short)17)); 
		
		//添加数据时的样式
		HSSFCellStyle datastyle=wb.createCellStyle();
		datastyle.setBorderBottom((short)1);
		
		// /下面的是根据list 进行遍历循环
		int i=11;
		int k=1;
		int count=data.size();
		int left;   //总共的行数
		int pageSize;   //总共的页数
		int currentPage=1;   //当前的页数
		if(count<=8){
			left=39;
			pageSize=1;
		}else{
			pageSize=(count-8)%12==0?(count-8)/12:(count-8)/12+1; //页数
			left=39+pageSize*41;
			pageSize=pageSize+1;
		}
		HSSFCellStyle footerStyle=wb.createCellStyle();    //脚页面的文字
		footerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	//	System.out.println("pageSize:"+pageSize+","+left);
		for (; i < left; i++) {
					
	//		System.out.println("导出成绩单："+i+":"+k);
			HashMap<String, Object> o = (HashMap<String, Object>) data
					.get(k-1);			
			
			row = sheet.getRow(i);
			if (row == null)
				row = sheet.createRow((short) i);
			for(int j=0;j<20;j++){
				cell = row.getCell((short) j);
				if (cell == null)
					cell = row.createCell((short) j);
			}
			/* 创建参赛排名的单元格 */
			cell = row.getCell((short) 0);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("rank") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("rank")));
			}
			/* 创建参赛单位的单元格 */
			cell = row.getCell((short) 1);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("teamName") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("teamName")));
			}
			sheet.addMergedRegion(new Region(i,(short)1,i,(short)18));
			i++;
			row = sheet.createRow((short) i);
			for(int j=0;j<20;j++){
				cell = row.getCell((short) j);
				if (cell == null)
					cell = row.createCell((short) j);
				cell.setCellStyle(datastyle);
			}
			//设置分数的单元格
			cell=row.getCell((short)1);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score01_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score01_art")));
			}
			cell=row.getCell((short)2);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score02_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score02_art")));
			}
			cell=row.getCell((short)3);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score03_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score03_art")));
			}
			cell=row.getCell((short)4);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score04_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score04_art")));
			}
			cell=row.getCell((short)5);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("avg_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("avg_art")));
			}
			
			cell = row.getCell((short) 7);			
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score01_execution") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score01_execution")));
			}
			cell = row.getCell((short) 8);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score02_execution") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score02_execution")));
			}
			cell = row.getCell((short) 9);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score03_execution") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score03_execution")));
			}
			cell = row.getCell((short) 10);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score04_execution") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score04_execution")));
			}
			cell = row.getCell((short) 11);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("avg_execution") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("avg_execution")));
			}
			
			cell = row.getCell((short) 13);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score01_impression") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score01_impression")));
			}
			cell = row.getCell((short) 14);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score02_impression") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score02_impression")));
			}
			cell = row.getCell((short) 15);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("avg_impression") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("avg_impression")));
			}
	   		
			
			/* 创建裁判长减分的单元格 */
			cell = row.getCell((short) 17);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("sub_score") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("sub_score")));
			}
         /*创建最后得分单元格*/
			cell = row.getCell((short) 18);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("total") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(new DecimalFormat("#.00").format(o.get("total")));
			}

			i++;
			if(k<=8){
				currentPage=1;
			}else 
			{
				currentPage=(k-8)%12==0?(k-8)/12+1:(k-8)/12+2;
			}
			if(k==data.size()&&currentPage==1&&data.size()<=8){
				i=i+(8-data.size())*3;	
				//创造签名的行
				i=i+1;
				HSSFRow rowLast = sheet.getRow(i);
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				HSSFCell cellLast = rowLast.getCell((short) 15);
				if (cellLast == null)
					cellLast = rowLast.createCell((short) 15);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("裁判长签名：");
				HSSFCellStyle style0=wb.createCellStyle();
				HSSFFont font=wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("Times New Roman");
				font.setFontHeight((short) (15 * 15));  
				style0.setFont(font);
				cellLast.setCellStyle(style0);
				i=i+2;
				row = sheet.createRow((short) i);
				for(int j=0;j<20;j++){
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category+"  "+time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i,(short)0,i,(short)19));
				i++;
			}else if(k==data.size()&&currentPage==pageSize){
				i=i+(8+12*(currentPage-1)-k)*3;
				//创造签名的行
				i=i+2;
				HSSFRow rowLast = sheet.getRow(i);
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				HSSFCell cellLast = rowLast.getCell((short) 15);
				if (cellLast == null)
					cellLast = rowLast.createCell((short) 15);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("裁判长签名：");
				HSSFCellStyle style0=wb.createCellStyle();
				HSSFFont font=wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("Times New Roman");
				font.setFontHeight((short) (15 * 15));  
				style0.setFont(font);
				cellLast.setCellStyle(style0);
				i=i+2;
				row = sheet.createRow((short) i);
				for(int j=0;j<20;j++){
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category+"  "+time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i,(short)0,i,(short)19));
				i++;
			}else if((k-8)%12==0&&currentPage<pageSize){   //中间页
				i=i+4;
				row = sheet.createRow((short) i);
				for(int j=0;j<20;j++){
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category+"  "+time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i,(short)0,i,(short)19));
				i++;
			}     //第一页的脚底	
			k++;	
		}
				
		final File file = new File(fileName);
		try {

			OutputStream os = new FileOutputStream(file);
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				wb.write(os);
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}  		
	
	/***
	 * 返回裁判等参数
	 * 
	 * @return params[0]表示仲裁主任，params[1]表示副裁判长，params[2]表示总裁判长，params[3]表示比赛地点
	 */
	public String[] getParams() {
		String[] params = new String[4];
		String sql = "select id,name,role,location from config order by id";
		try {
			st = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet res = st.executeQuery();
			int i = 0;
			while (res.next()) {
				params[i] = res.getString(2);
				i++;
			}
			res.first();
			params[3] = res.getString(4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	public List<Integer> getError() {
		return error;
	}


	public List<Integer> getFirstOrderError() {
		return firstOrderError;
	}

	public List<Integer> getOrderError() {
		return orderError;
	}
}