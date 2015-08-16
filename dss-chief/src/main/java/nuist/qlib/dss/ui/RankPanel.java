/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.scoreManager.QueryScore;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 成绩排名界面
 * 
 * @author WangFang
 * @since dss 1.0
 */
public class RankPanel {

	protected Shell rank_shell;
	private Table table;
	private Text search;
	// 导出成绩按钮
	private Button export_result_but;
	// 导出决赛按钮
	private Button export_final_but;
	// 查询成绩按钮
	private Button search_but;
	// 一键式
	private Button export_all_but;
	private Composite rank_composite;

	protected int matchType;
	protected int id;
	protected String matchName;
	protected String category;
	private List<HashMap<String, Object>> data;

	private QueryScore lalaScore;

	// public static void main(String[] args) {
	// try {
	// RankPanel window = new RankPanel();
	// window.open();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents(display);
		rank_shell.open();
		rank_shell.layout();
		while (!rank_shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		lalaScore = new QueryScore();
		data = new ArrayList<HashMap<String, Object>>();

		rank_shell = new Shell(display, SWT.CLOSE | SWT.MIN);
		rank_shell.setSize(1176, 653);
		rank_shell.setImage(new Image(display, RankPanel.class
				.getResourceAsStream("/img/logo.png")));
		rank_shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (lalaScore.isCollected()) {
					lalaScore.close();
				}
				rank_shell.dispose();
			}
		});
		rank_shell.setText("成绩排名");

		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		Rectangle shellBounds = rank_shell.getBounds();
		int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
		rank_shell.setLocation(x, y);

		rank_composite = new Composite(rank_shell, SWT.NONE);
		rank_composite.setBounds(0, 0, 1172, 627);

		export_result_but = new Button(rank_composite, SWT.NONE);
		export_result_but.setBounds(572, 590, 80, 27);
		export_result_but.setText("导出成绩单");

		// 决赛数据导出
		export_final_but = new Button(rank_composite, SWT.NONE);
		export_final_but.setBounds(1037, 590, 80, 27);
		export_final_but.setText("导出决赛数据");

		search = new Text(rank_composite, SWT.BORDER);
		search.setBounds(10, 10, 204, 23);

		search_but = new Button(rank_composite, SWT.NONE);
		search_but.setBounds(234, 8, 80, 27);
		search_but.setText("查询");

		// 决赛数据一键式导出
		export_all_but = new Button(rank_composite, SWT.NONE);
		export_all_but.setBounds(857, 590, 145, 27);
		export_all_but.setText("决赛数据一键式导出");

		Button deviations_btn = new Button(rank_composite, SWT.NONE);
		deviations_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				ViewDeviations window = new ViewDeviations();
				ViewDeviations.matchName = matchName;
				ViewDeviations.matchType = matchType;
				window.open();

			}
		});
		deviations_btn.setBounds(961, 8, 80, 27);
		deviations_btn.setText("查看误差");

		// 初始化导出数据

		if (!lalaScore.isCollected()) {
			MessageBox box = new MessageBox(rank_shell, SWT.OK);
			box.setMessage("提示");
			box.setMessage("连接数据库出错!");
			export_result_but.setEnabled(false);
			box.open();
			createTable(rank_composite, data);
		} else {
			data = lalaScore.getRank(id, matchType);
			// 设置项目类别
			category = lalaScore.getCategory(id);
			table = createTable(rank_composite, data);
			if (matchType == 0) {
				rank_shell.setText(matchName + "预赛--->项目排名情况");
			} else if (matchType == 1)
				rank_shell.setText(matchName + "决赛--->项目排名情况");
			search.setText(category);
			AutoCompleteField field = new AutoCompleteField(search,
					new TextContentAdapter(), lalaScore.getCategories(
							matchType, matchName));
		}
		addEvent();
	}

	public void addEvent() {

		search.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				AutoCompleteField field = new AutoCompleteField(search,
						new TextContentAdapter(), lalaScore.getCategories(
								matchType, matchName));
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}

		});

		/* 导出当前项目决赛数据* */
		export_final_but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(rank_shell, SWT.OK);
				box.setText("提示");
				FinalMatchDialog dialog = new FinalMatchDialog(rank_shell,
						category, box, matchName);
				if (!dialog.getDao().isCollected()) {
					box.setText("警告");
					box.setMessage("数据库连接失败");
				} else
					dialog.open();
			}
		});

		/* 跳转到挑选项目人数界面 */
		export_all_but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(rank_shell, SWT.OK);
				box.setText("提示");
				if (lalaScore.isCollected()) {
					ExportFinalAllPanel window = new ExportFinalAllPanel(
							matchName);
					window.open();
				} else {
					box.setMessage("未与数据库连接");
					box.open();
				}
			}
		});

		/* 查询成绩事件 */
		search_but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (search.getText() != null && !search.getText().equals("")) {
					String selectedValue = search.getText();
					category = selectedValue;
					data = lalaScore.getRank(matchName, selectedValue,
							matchType);
					table.dispose();
					table = createTable(rank_composite, data);
				}
			}
		});

		/* 导出成绩事件 */
		export_result_but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(rank_shell, SWT.OK);
				box.setText("提示");
				if (data.size() == 0) {
					box.setMessage("暂无数据！不能导出");
					box.open();
					return;
				}

				FileDialog dialog = new FileDialog(rank_shell, SWT.SAVE);
				dialog.setText("保存文件");// 设置对话框的标题
				dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
				dialog.setFilterNames(new String[] { "Excel文件(*.xls)",
						"Excel文件(*.xlsx)" });
				dialog.setFileName(category);
				String fileName = dialog.open(); // 获得保存的文件名
				if (fileName != null && !fileName.equals("")) {
					List<HashMap<String, Object>> temp_data = lalaScore
							.getPrintRank(matchName, category, matchType);
					;
					boolean mark = lalaScore.exportScoreExcel(temp_data,
							fileName, matchName, category, matchType);
					if (mark) {
						box.setMessage("导出成功");
					} else {
						box.setMessage("程序发生错误");
					}
					box.open();
				}
			}
		});
	}

	/** 创建table(舞蹈table) **/
	public Table createTable(Composite rank_composite,
			List<HashMap<String, Object>> data) {
		Table table = new Table(rank_composite, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		table.setBounds(0, 60, 1172, 508);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn team_name = new TableColumn(table, SWT.CENTER);
		TableColumn score01_art = new TableColumn(table, SWT.CENTER);
		TableColumn score02_art = new TableColumn(table, SWT.CENTER);
		TableColumn score03_art = new TableColumn(table, SWT.CENTER);
		TableColumn score04_art = new TableColumn(table, SWT.CENTER);
		TableColumn avg_art = new TableColumn(table, SWT.CENTER);
		TableColumn score01_exection = new TableColumn(table, SWT.CENTER);
		TableColumn score02_exection = new TableColumn(table, SWT.CENTER);
		TableColumn score03_exection = new TableColumn(table, SWT.CENTER);
		TableColumn score04_exection = new TableColumn(table, SWT.CENTER);
		TableColumn avg_exection = new TableColumn(table, SWT.CENTER);
		TableColumn score01_impression = new TableColumn(table, SWT.CENTER);
		TableColumn score02_impression = new TableColumn(table, SWT.CENTER);
		TableColumn avg_impression = new TableColumn(table, SWT.CENTER);
		TableColumn sub_score = new TableColumn(table, SWT.CENTER);
		TableColumn total = new TableColumn(table, SWT.CENTER);
		TableColumn rank = new TableColumn(table, SWT.CENTER);
		team_name.setText("参赛单位");
		score01_art.setText("艺术1");
		score02_art.setText("艺术2");
		score03_art.setText("艺术3");
		score04_art.setText("艺术4");
		avg_art.setText("总艺术分");
		score01_exection.setText("完成1");
		score02_exection.setText("完成2");
		score03_exection.setText("完成3");
		score04_exection.setText("完成4");
		avg_exection.setText("总艺术分");
		score01_impression.setText("舞步1");
		score02_impression.setText("舞步2");
		avg_impression.setText("总舞步分");
		sub_score.setText("裁判长减分");
		total.setText("总分");
		rank.setText("排名");
		team_name.setWidth(200);
		score01_art.setWidth(50);
		score02_art.setWidth(50);
		score03_art.setWidth(50);
		score04_art.setWidth(50);
		avg_art.setWidth(75);

		score01_exection.setWidth(50);
		score02_exection.setWidth(50);
		score03_exection.setWidth(50);
		score04_exection.setWidth(50);
		avg_exection.setWidth(75);

		score01_impression.setWidth(70);
		score02_impression.setWidth(70);
		avg_impression.setWidth(75);

		sub_score.setWidth(75);
		total.setWidth(65);
		rank.setWidth(50);

		TableItem item;
		Font font = new Font(Display.getDefault(), "宋体", 10, SWT.COLOR_BLUE);
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);// 红色
		for (int i = 0; i < data.size(); i++) {
			item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {
					(String) data.get(i).get("teamName"),
					String.valueOf(data.get(i).get("score01_art") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score01_art"))),
					String.valueOf(data.get(i).get("score02_art") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score02_art"))),
					String.valueOf(data.get(i).get("score03_art") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score03_art"))),
					String.valueOf(data.get(i).get("score04_art") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score04_art"))),
					String.valueOf(data.get(i).get("avg_art") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("avg_art"))),
					String.valueOf(data.get(i).get("score01_execution") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score01_execution"))),
					String.valueOf(data.get(i).get("score02_execution") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score02_execution"))),
					String.valueOf(data.get(i).get("score03_execution") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score03_execution"))),
					String.valueOf(data.get(i).get("score04_execution") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score04_execution"))),
					String.valueOf(data.get(i).get("avg_execution") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("avg_execution"))),
					String.valueOf(data.get(i).get("score01_impression") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score01_impression"))),
					String.valueOf(data.get(i).get("score02_impression") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("score02_impression"))),
					String.valueOf(data.get(i).get("avg_impression") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("avg_impression"))),
					String.valueOf(data.get(i).get("sub_score") == null ? ""
							: new DecimalFormat("#0.00").format(data.get(i)
									.get("sub_score"))),
					String.valueOf(new DecimalFormat("#0.00").format(data
							.get(i).get("total"))),
					String.valueOf(data.get(i).get("rank")) });
			item.setFont(15, font);
			item.setBackground(15, color);
		}
		return table;
	}
}