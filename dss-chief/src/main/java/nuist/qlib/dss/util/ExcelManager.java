/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.util;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.ConnSQL;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelManager {
	// 连接数据库需要的变量
	Connection conn;
	PreparedStatement st;
	boolean mark; // 用来表明是否正确取到配置信息
	private Logger logger;
	private List<Integer> error; // 在excel对应的Integer行中的数据在数据库中已经有了
	private List<Integer> firstOrderError; // 在附加数据时，每个场次的第一个出场顺序错误，与数据库的不匹配
	private List<Integer> orderError; // 出场顺序错误(没有校对数据库，可能是出场顺序不连续)

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

	/**
	 * @param type
	 *            表示导入数据的类型，判断是附加数据，还是直接导入数据,0表示直接导入，1表示附加数据
	 * @return 0 表示获取数据库的配置信息不正确
	 * @return 1 表示excel文件的数据写错或者是文件名错误
	 * @return 2 表示导入数据库成功
	 * @return 3 表示导入数据库失败
	 * @return 4 表示程序出错
	 * @return 5 表示比赛已经开始，不可以重新入库
	 * @return 6 表示附加数据时，该赛事不存在，不可以附加
	 * @return 7 表示没有数据
	 * @return 8 队伍已经导入过
	 * @return 9 与数据库中已有的场次的最后一个出场顺序不匹配
	 * @return 10 顺序错误
	 */
	public int ImportExcel(String path, int type) {

		error = new ArrayList<Integer>();
		firstOrderError = new ArrayList<Integer>();
		orderError = new ArrayList<Integer>();

		if (mark == false) {
			return 0;
		} else
			try {
				POIFSFileSystem fs;
				HSSFWorkbook book;
				fs = new POIFSFileSystem(new FileInputStream(new File(path)));
				book = new HSSFWorkbook(fs);
				// 获得第一个工作表对象
				HSSFSheet sheet = book.getSheetAt(0);
				String matchName = path.substring(path.lastIndexOf("\\") + 1,
						path.indexOf("."));
				// 插入数据的代码
				String sql;
				if (type == 0) {
					// 检验该文件中的比赛名称是否在数据库中已经存在，如果存在，查找团体分数表，判断是否有该赛事名称，如果有，通知用户不可更新，如果没有，删除团体信息表已有重复的数据的，重新入库
					String deleteSql = "delete from match_order where match_name=?";
					String checkScore = "select b.* from match_order as a,score as b where a.id=b.team_id and a.match_name=?";
					String checkSql = "select * from match_order where match_name=?";
					st = conn.prepareStatement(checkSql);
					st.setString(1, matchName);
					ResultSet rs = st.executeQuery();
					// 已存在数据
					if (rs.next()) {
						// 判断打分表中是否已经有了这个数据，如果有的话提示用户，不能覆盖
						st = conn.prepareStatement(checkScore);
						st.setString(1, matchName);
						rs = st.executeQuery();
						if (rs.next()) {
							return 5; // 告知用户不能修改，已经进行了比赛
						} else {
							st = conn.prepareStatement(deleteSql);
							st.setString(1, matchName);
							st.execute();
						}
					}
				} else if (type == 1) { // 当附加数据时，先判断是否有该赛事名称，如果没有提醒用户
					String checkSql = "select * from match_order where match_name=?";
					st = conn.prepareStatement(checkSql);
					st.setString(1, matchName);
					ResultSet rs = st.executeQuery();
					if (!rs.next()) {
						return 6; // 提醒用户信息表中没有该赛事，不能够进行附加
					}
				}
				// 没有存在,直接将数据导入数据库
				sql = "insert into match_order(match_order,match_num,match_units,match_category,match_name,final_preliminary,unit_status,member_name)values(?,?,?,?,?,?,?,?)";
				// 检查一个队伍参加的一个项目是否已经在数据库中
				// String
				// checkSql="select id from match_order where match_units=? and match_category=? and match_name=? and final_preliminary=?";
				// 获取比赛是某场比赛的最后一个order
				String lastOrder = "select max(match_order) from match_order where match_num=? and match_name=?";
				// 将数据直接插入的过程
				conn.setAutoCommit(false);
				st = conn.prepareStatement(sql);
				// 得到总行数
				int rowNum = sheet.getLastRowNum();
				HSSFRow row = sheet.getRow(0);
				// 正文内容应该从第二行开始,第一行为表头的标题
				if (rowNum == 0) {
					return 7;
				}
				int num = 1;
				int order = 0;
				int order_temp;
				int num_temp;
				boolean mark = false;

				// 校验数据格式的正确性
				for (int i = 1; i <= rowNum; i++) {
					row = sheet.getRow(i);
					HSSFCell cell = row.getCell((short) 0); // 出场顺序
					HSSFCell cell2 = row.getCell((short) 1); // 场次
					HSSFCell cell3 = row.getCell((short) 2); // 队伍名称
					HSSFCell cell4 = row.getCell((short) 3); // 参加的项目
					HSSFCell cell5 = row.getCell((short) 4); // 决赛/预赛
					HSSFCell cell6 = row.getCell((short) 5); // 队员名称
					if (cell == null || cell2 == null || cell3 == null
							|| cell4 == null || cell5 == null || cell6 == null) {
						return 1;
					}
					if (i == 1) {
						order = (int) cell.getNumericCellValue();
						num = (int) cell2.getNumericCellValue();
						st = conn.prepareStatement(lastOrder);
						st.setInt(1, num);
						st.setString(2, matchName);
						ResultSet rs = st.executeQuery();
						if (rs.next()) {
							int temp = rs.getInt(1);
							if (order != (temp + 1)) {
								firstOrderError.add(i + 1);
								mark = false;
							} else {
								mark = true;
							}
						} else {
							if (order != 1) {
								orderError.add(i + 1);
							} else
								mark = true;
						}
					} else {
						order_temp = (int) cell.getNumericCellValue();
						num_temp = (int) cell2.getNumericCellValue();
						if (num_temp == num && order_temp == (order + 1)) {
							order = order_temp;
							mark = true;
						} else if (num_temp == num && order_temp != (order + 1)) {
							error.add(i + 1);
							/*** 新添加的 ***/
							return 8;
						} else if (num_temp != num) {
							num = num_temp;
							order = (int) cell.getNumericCellValue();
							st = conn.prepareStatement(lastOrder);
							st.setInt(1, num);
							st.setString(2, matchName);
							ResultSet rs = st.executeQuery();
							if (rs.next()) {
								int temp = rs.getInt(1);
								if (order != (temp + 1)) {
									firstOrderError.add(i + 1);
									mark = false;
								} else {
									mark = true;
								}
							} else {
								if (order != 1) {
									orderError.add(i + 1);
								} else
									mark = true;
							}
						}
					}
					// if(mark){
					// //检查该条记录数据库中是否已经有了
					// st=conn.prepareStatement(checkSql);
					// st.setString(1, cell3.getStringCellValue());
					// st.setString(2, cell4.getStringCellValue());
					// st.setString(3, matchName);
					// st.setInt(4, (int) cell5.getNumericCellValue());
					// if(st.executeQuery().next()){
					// error.add(i+1);
					// }
					// }
				}
				if (orderError.size() != 0) {
					return 10;
				} else if (firstOrderError.size() != 0) {
					return 9;
				} else if (error.size() == 0) {
					st = conn.prepareStatement(sql);
					for (int i = 1; i <= rowNum; i++) {
						row = sheet.getRow(i);
						HSSFCell cell = row.getCell((short) 0); // 出场顺序
						HSSFCell cell2 = row.getCell((short) 1); // 场次
						HSSFCell cell3 = row.getCell((short) 2); // 队伍名称
						HSSFCell cell4 = row.getCell((short) 3); // 参加的项目
						HSSFCell cell5 = row.getCell((short) 4); // 决赛/预赛
						HSSFCell cell6 = row.getCell((short) 5); // 队员名称
						st.setInt(1, (int) cell.getNumericCellValue());
						// 将第二列的数据放进st中
						st.setInt(2, (int) cell2.getNumericCellValue());
						st.setString(3, cell3.getStringCellValue());
						st.setString(4, cell4.getStringCellValue());
						st.setString(5, matchName);
						st.setInt(6, (int) cell5.getNumericCellValue());
						st.setInt(7, 0);
						st.setString(8, cell6.getStringCellValue());
						st.addBatch();
						if (i > 1000) {
							int[] result = st.executeBatch();
							for (int k : result) {
								if (k < 0) {
									return 3;
								}
							}
						}
					}
					int[] result = st.executeBatch();
					for (int k : result) {
						if (k < 0) {
							return 3;
						}
					}
					conn.commit();
					return 2;
				} else {
					return 8;
				}
			} catch (NumberFormatException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return 1;
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return 4;
			}
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
	public boolean ExportScoreExcel(List<HashMap<String, Object>> data,
			String fileName, String matchName, String category, int matchType) {
		String matchTypeString = "";
		if (matchType == 0) {
			matchTypeString = "预赛";
		} else if (matchType == 1) {
			matchTypeString = "决赛";
		}
		// 创建一个HSSFWorkbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// 由HSSFWorkbook创建一个HSSFSheet
		HSSFSheet sheet = wb.createSheet("sheet1");
		sheet.setDefaultColumnWidth((short) 3);
		sheet.setColumnWidth((short) 1, (short) 2000);
		sheet.setColumnWidth((short) 2, (short) 2000);
		sheet.setColumnWidth((short) 3, (short) 2000);
		sheet.setColumnWidth((short) 4, (short) 2000);
		sheet.setColumnWidth((short) 5, (short) 1600);
		sheet.setColumnWidth((short) 7, (short) 2000);
		sheet.setColumnWidth((short) 8, (short) 2000);
		sheet.setColumnWidth((short) 9, (short) 2000);
		sheet.setColumnWidth((short) 10, (short) 2000);
		sheet.setColumnWidth((short) 11, (short) 1600);
		sheet.setColumnWidth((short) 13, (short) 1600);
		sheet.setColumnWidth((short) 14, (short) 1600);
		sheet.setColumnWidth((short) 15, (short) 1600);
		sheet.setColumnWidth((short) 17, (short) 1600);
		sheet.setColumnWidth((short) 18, (short) 1600);
		sheet.setMargin(HSSFSheet.TopMargin, (short) 1);
		sheet.setMargin(HSSFSheet.BottomMargin, (short) 0.8);
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setPaperSize((HSSFPrintSetup.A4_PAPERSIZE));
		printSetup.setLandscape(true);
		// 由HSSFSheet创建HSSFRow
		// 创建显示赛事的标题
		HSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeight((short) 400);
		HSSFCell cell0 = row0.createCell((short) 0);
		cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell0.setCellValue(matchName);
		HSSFCellStyle matchNamestyle = wb.createCellStyle();
		matchNamestyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont matchNameFont = wb.createFont(); // 设置字体样式
		matchNameFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		matchNameFont.setFontHeight((short) (15 * 20));
		matchNamestyle.setFont(matchNameFont);
		cell0.setCellStyle(matchNamestyle);
		for (int i = 1; i < 20; i++) {
			cell0 = row0.createCell((short) i);
			cell0.setCellStyle(matchNamestyle);
		}
		String[] params = getParams();
		// 将赛事标题所在行的第一列到第20列合并
		sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 19));
		// 创建子标题(占第二行和第三行，15和16列)
		HSSFRow subRow = sheet.createRow(2);
		HSSFCellStyle subStyle = wb.createCellStyle();
		subStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont subFont = wb.createFont();
		subFont.setFontHeight((short) (12 * 15));
		subStyle.setFont(subFont);
		HSSFCellStyle subValueStyle = wb.createCellStyle();
		subValueStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		HSSFFont subValueFont = wb.createFont();
		subValueFont.setFontHeight((short) (12 * 15));
		subValueStyle.setFont(subValueFont);
		HSSFCell subCell = subRow.createCell((short) 1);
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue("比赛地点:");
		subCell = subRow.createCell((short) 2);
		sheet.addMergedRegion(new Region(2, (short) 1, 2, (short) 2));
		subCell = subRow.createCell((short) 3);
		subCell.setCellStyle(subValueStyle);
		subCell.setCellValue(params[3]);
		subCell = subRow.createCell((short) 4);
		sheet.addMergedRegion(new Region(2, (short) 3, 2, (short) 4));
		subCell = subRow.createCell((short) 13);
		subCell.setCellStyle(subStyle);
		subCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		subCell.setCellValue("比赛时间:");
		subCell = subRow.createCell((short) 14);
		sheet.addMergedRegion(new Region(2, (short) 13, 2, (short) 14));
		subCell = subRow.createCell((short) 15);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		String time = format.format(new Date(System.currentTimeMillis()));
		subCell.setCellStyle(subStyle);
		subCell.setCellValue(time);
		subCell = subRow.createCell((short) 16);
		subCell = subRow.createCell((short) 17);
		sheet.addMergedRegion(new Region(2, (short) 15, 2, (short) 17));

		// 第3行的bottom上添加线条
		HSSFRow lineRow = sheet.createRow(3);
		HSSFCellStyle lineStyle = wb.createCellStyle();
		lineStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		HSSFCell lineCell;
		for (int i = 0; i < 20; i++) {
			lineCell = lineRow.createCell((short) i);
			lineCell.setCellStyle(lineStyle);
		}

		// 第4行设置参赛项目名称
		HSSFRow categoryRow = sheet.createRow(4);
		categoryRow.setHeight((short) 400);
		HSSFCellStyle categoryStyle = wb.createCellStyle();
		categoryStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont categoryFont = wb.createFont();
		categoryFont.setFontHeight((short) (14 * 15));
		categoryFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		categoryStyle.setFont(categoryFont);
		HSSFCell categoryCell = categoryRow.createCell((short) 5);
		categoryCell.setCellStyle(categoryStyle);
		categoryCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		categoryCell.setCellValue(category + " " + matchTypeString);
		for (int i = 10; i < 14; i++) {
			categoryCell = categoryRow.createCell((short) i);
			categoryCell.setCellStyle(categoryStyle);
		}
		sheet.addMergedRegion(new Region(4, (short) 5, 4, (short) 14));

		// 设置第7行
		HSSFRow NineRow = sheet.createRow(7);
		NineRow.setHeight((short) 50);

		// 总表头行，占第8行
		HSSFRow totalRow = sheet.createRow(8);
		totalRow.setHeight((short) 400);
		HSSFCellStyle totalStyle = wb.createCellStyle();
		HSSFCell totalCell;
		HSSFFont totalFont = wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		totalStyle.setFont(totalFont);
		totalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		totalStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		for (int i = 0; i < 20; i++) {
			totalCell = totalRow.createCell((short) i);
			totalCell.setCellStyle(totalStyle);
		}
		totalCell = totalRow.getCell((short) 0);
		totalCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		totalCell.setCellValue("名次");
		totalCell = totalRow.getCell((short) 1);
		totalCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		totalCell.setCellValue("姓名/单位");
		sheet.addMergedRegion(new Region(8, (short) 1, 8, (short) 2));

		// 创建excel的表头占第9行和第10行
		HSSFCellStyle style = wb.createCellStyle();
		totalFont = wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(totalFont);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		HSSFRow row = sheet.createRow((short) 9);
		HSSFCell cell = null;
		style = wb.createCellStyle();
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
		style = wb.createCellStyle();
		totalFont = wb.createFont();
		totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(totalFont);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		cell.setCellStyle(style);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("最后得分");
		// sheet.addMergedRegion(new Region(9,(short)17,10,(short)17));

		// 添加数据时的样式
		HSSFCellStyle datastyle = wb.createCellStyle();
		datastyle.setBorderBottom((short) 1);

		// /下面的是根据list 进行遍历循环
		int i = 11;
		int k = 1;
		int count = data.size();
		int left; // 总共的行数
		int pageSize; // 总共的页数
		int currentPage = 1; // 当前的页数
		if (count <= 8) {
			left = 39;
			pageSize = 1;
		} else {
			pageSize = (count - 8) % 12 == 0 ? (count - 8) / 12
					: (count - 8) / 12 + 1; // 页数
			left = 39 + pageSize * 41;
			pageSize = pageSize + 1;
		}
		HSSFCellStyle footerStyle = wb.createCellStyle(); // 脚页面的文字
		footerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// System.out.println("pageSize:"+pageSize+","+left);
		for (; i < left; i++) {

			// System.out.println("导出成绩单："+i+":"+k);
			HashMap<String, Object> o = (HashMap<String, Object>) data
					.get(k - 1);

			row = sheet.getRow(i);
			if (row == null)
				row = sheet.createRow((short) i);
			for (int j = 0; j < 20; j++) {
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
			sheet.addMergedRegion(new Region(i, (short) 1, i, (short) 18));
			i++;
			row = sheet.createRow((short) i);
			for (int j = 0; j < 20; j++) {
				cell = row.getCell((short) j);
				if (cell == null)
					cell = row.createCell((short) j);
				cell.setCellStyle(datastyle);
			}
			// 设置分数的单元格
			cell = row.getCell((short) 1);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score01_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score01_art")));
			}
			cell = row.getCell((short) 2);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score02_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score02_art")));
			}
			cell = row.getCell((short) 3);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score03_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score03_art")));
			}
			cell = row.getCell((short) 4);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("score04_art") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(String.valueOf(o.get("score04_art")));
			}
			cell = row.getCell((short) 5);
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
			/* 创建最后得分单元格 */
			cell = row.getCell((short) 18);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("total") == null) {
				cell.setCellValue(0);
			} else {
				cell.setCellValue(new DecimalFormat("#.00").format(o
						.get("total")));
			}

			i++;
			if (k <= 8) {
				currentPage = 1;
			} else {
				currentPage = (k - 8) % 12 == 0 ? (k - 8) / 12 + 1
						: (k - 8) / 12 + 2;
			}
			if (k == data.size() && currentPage == 1 && data.size() <= 8) {
				i = i + (8 - data.size()) * 3;
				// 创造签名的行
				i = i + 1;
				HSSFRow rowLast = sheet.getRow(i);
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				HSSFCell cellLast = rowLast.getCell((short) 15);
				if (cellLast == null)
					cellLast = rowLast.createCell((short) 15);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("裁判长签名：");
				HSSFCellStyle style0 = wb.createCellStyle();
				HSSFFont font = wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("Times New Roman");
				font.setFontHeight((short) (15 * 15));
				style0.setFont(font);
				cellLast.setCellStyle(style0);
				i = i + 2;
				row = sheet.createRow((short) i);
				for (int j = 0; j < 20; j++) {
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category + "  " + time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i, (short) 0, i, (short) 19));
				i++;
			} else if (k == data.size() && currentPage == pageSize) {
				i = i + (8 + 12 * (currentPage - 1) - k) * 3;
				// 创造签名的行
				i = i + 2;
				HSSFRow rowLast = sheet.getRow(i);
				if (rowLast == null)
					rowLast = sheet.createRow((short) i);
				HSSFCell cellLast = rowLast.getCell((short) 15);
				if (cellLast == null)
					cellLast = rowLast.createCell((short) 15);
				cellLast.setEncoding(HSSFCell.ENCODING_UTF_16);
				cellLast.setCellValue("裁判长签名：");
				HSSFCellStyle style0 = wb.createCellStyle();
				HSSFFont font = wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				font.setFontName("Times New Roman");
				font.setFontHeight((short) (15 * 15));
				style0.setFont(font);
				cellLast.setCellStyle(style0);
				i = i + 2;
				row = sheet.createRow((short) i);
				for (int j = 0; j < 20; j++) {
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category + "  " + time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i, (short) 0, i, (short) 19));
				i++;
			} else if ((k - 8) % 12 == 0 && currentPage < pageSize) { // 中间页
				i = i + 4;
				row = sheet.createRow((short) i);
				for (int j = 0; j < 20; j++) {
					cell = row.getCell((short) j);
					if (cell == null)
						cell = row.createCell((short) j);
				}
				cell = row.getCell((short) 0);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(category + "  " + time);
				cell.setCellStyle(footerStyle);
				sheet.addMergedRegion(new Region(i, (short) 0, i, (short) 19));
				i++;
			} // 第一页的脚底
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

	// 将决赛队伍导出
	/***
	 * 将数据导出 ,参数分别为数据，文件名，比赛事件，比赛项目
	 * 
	 * @param data
	 *            要导出的数据
	 * @param fileName
	 *            文件名
	 * @param matchName
	 *            比赛名称
	 * @param category
	 *            比赛项目
	 * @param mark
	 *            true表示一键式决赛队伍导出,false表示按照项目导出决赛队伍
	 * @return
	 */
	public boolean ExportExcelFinal(List<HashMap<String, Object>> data,
			String fileName, String matchName, String category, boolean mark) {
		// 创建一个HSSFWorkbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// 由HSSFWorkbook创建一个HSSFSheet
		HSSFSheet sheet = wb.createSheet("sheet1");
		sheet.setDefaultColumnWidth((short) 6);
		sheet.setColumnWidth((short) 1, (short) 8000);
		sheet.setColumnWidth((short) 2, (short) 8000);
		// 由HSSFSheet创建HSSFRow
		// 创建excel的标题
		HSSFRow row0 = sheet.createRow((short) 0);
		HSSFCell cell0 = row0.createCell((short) 0);
		cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
		if (mark) {
			cell0.setCellValue(matchName + "决赛");
		} else {
			cell0.setCellValue(matchName + category);
		}
		HSSFCellStyle style = cell0.getCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cell0.setCellStyle(style);
		for (int i = 1; i < 8; i++) {
			cell0 = row0.createCell((short) i);
		}
		// 将标题所在行的第一列到第19列合并
		sheet.addMergedRegion(new Region(0, (short) 0, 0, (short) 8));
		// 创建excel的表头
		HSSFRow row = sheet.createRow((short) 1);
		HSSFCell cell = row.createCell((short) 0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("编号");
		cell = row.createCell((short) 1);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("参赛项目");
		cell = row.createCell((short) 2);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("比赛单位");
		cell = row.createCell((short) 3);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue("队员");
		cell = row.createCell((short) 4);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		if (mark) {
			cell.setCellValue("预赛排名");
		} else {
			cell.setCellValue("决赛排名");
		}
		int i = 2;
		// /下面的是根据list 进行遍历循环
		for (; i < data.size() + 2; i++) {
			HashMap<String, Object> o = (HashMap<String, Object>) data
					.get(i - 2);
			row = sheet.getRow(i);
			if (row == null)
				row = sheet.createRow((short) i);
			row.setHeight((short) 500);
			/* 创建编号的单元格 */
			cell = row.getCell((short) 0);
			if (cell == null)
				cell = row.createCell((short) 0);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(i - 1);

			/* 创建参赛单位的单元格 */
			cell = row.getCell((short) 1);
			if (cell == null)
				cell = row.createCell((short) 1);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("category") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("category")));
			}
			cell.setCellStyle(cellStyle);
			/* 创建参赛项目的单元格 */
			cell = row.getCell((short) 2);
			if (cell == null)
				cell = row.createCell((short) 2);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("teamName") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("teamName")));
			}
			cell.setCellStyle(cellStyle);
			/* 创建参赛成员的单元格 */
			cell = row.getCell((short) 3);
			if (cell == null)
				cell = row.createCell((short) 3);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);

			if (o.get("memberName") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("memberName")));
			}
			cell.setCellStyle(cellStyle);
			/* 创建预赛排名的单元格 */
			cell = row.getCell((short) 4);
			if (cell == null)
				cell = row.createCell((short) 4);
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			if (o.get("rank") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(String.valueOf(o.get("rank")));
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