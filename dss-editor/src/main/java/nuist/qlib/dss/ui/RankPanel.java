/*
 * 文件名：RankPanel.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：成绩排名界面
 */
package nuist.qlib.dss.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import nuist.qlib.dss.net.BroadcastIP;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class RankPanel {

	protected Shell rank_shell;
	private Table table;
	private Text search;
	private Button export_result_but; // 导出成绩按钮
	private Button search_but; // 查询成绩按钮
	private Composite rank_composite;
	private Button choice; // 选择赛事按钮
	private Label matchNameLabel; // 显示赛事名称

	protected int matchType = -1; // 赛事模式 0表示预赛；1表示决赛
	protected String matchName; // 赛事名称
	protected String category; // 赛事项目
	private List<HashMap<String, Object>> data; // 查询的成绩

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
		createContents();
		center(rank_shell);
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
	protected void createContents() {

		Display display = Display.getDefault();
		rank_shell = new Shell(display, SWT.CLOSE | SWT.MIN);
		rank_shell.setSize(1127, 680);
		rank_shell.setText("舞蹈竞赛评分系统----成绩排名");
		rank_shell.setImage(new Image(display, RankPanel.class
				.getResourceAsStream("/img/logo.png")));
		rank_shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				MessageBox messagebox = new MessageBox(rank_shell,
						SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messagebox.setText("提示");
				messagebox.setMessage("您确定关闭系统吗?");
				int message = messagebox.open();
				if (message == SWT.YES) {
					e.doit = true;
					Properties pro = new Properties();
					try {
						FileInputStream in = new FileInputStream(
								"dataBase.properties");
						pro.load(in);
						in.close();
						pro.setProperty("ip", "null");
						FileOutputStream out = new FileOutputStream(
								"dataBase.properties");
						pro.store(out, "update");
						out.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					System.exit(0);
				} else {
					e.doit = false;
				}
			}
		});
		new Thread(new BroadcastIP(rank_shell)).start();
		this.defaultInit();
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-4 上午11:11:15
	 * @Description: 窗口居中
	 * @param @param shell
	 * @return void
	 * @throws
	 */
	private void center(Shell shell) {
		Monitor monitor = shell.getMonitor();
		Rectangle bounds = monitor.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	/**
	 * Create contents of the window.
	 */
	protected void defaultInit() {
		try {
			// 打开时清空address.txt
			File f = new File("Address.txt");
			FileWriter fw = new FileWriter(f);
			fw.write("");
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		lalaScore = new QueryScore();
		data = new ArrayList<HashMap<String, Object>>();
		rank_shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (lalaScore.isCollected()) {
					lalaScore.close();
				}
				rank_shell.dispose();
			}
		});
		rank_shell.setText("成绩排名");

		rank_composite = new Composite(rank_shell, SWT.NONE);
		rank_composite.setBounds(0, 0, 1122, 652);

		export_result_but = new Button(rank_composite, SWT.NONE);
		export_result_but.setBounds(889, 615, 80, 27);
		export_result_but.setText("导出成绩单");

		search = new Text(rank_composite, SWT.BORDER);
		search.setBounds(77, 53, 204, 23);

		search_but = new Button(rank_composite, SWT.NONE);
		search_but.setBounds(302, 51, 80, 27);
		search_but.setText("查询");

		matchNameLabel = new Label(rank_composite, SWT.NONE);
		matchNameLabel.setAlignment(SWT.CENTER);
		matchNameLabel.setFont(SWTResourceManager.getFont("微软雅黑", 15,
				SWT.NORMAL));
		matchNameLabel.setBounds(0, 10, 1112, 35);
		matchNameLabel.setText("赛事名称");

		Label categoryLabel = new Label(rank_composite, SWT.NONE);
		categoryLabel.setBounds(10, 56, 61, 17);
		categoryLabel.setText("项目名称:");

		choice = new Button(rank_composite, SWT.NONE);

		choice.setBounds(984, 51, 80, 27);
		choice.setText("选择赛事");

		table = new Table(rank_composite, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		table.setBounds(0, 82, 1112, 527);
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
		avg_art.setText("平均艺术分");
		score01_exection.setText("完成1");
		score02_exection.setText("完成2");
		score03_exection.setText("完成3");
		score04_exection.setText("完成4");
		avg_exection.setText("平均完成分");
		score01_impression.setText("总体评价1");
		score02_impression.setText("总体评价2");
		avg_impression.setText("总体评价分");
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

		addEvent();
	}

	public void addEvent() {

		search.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				AutoCompleteField field = new AutoCompleteField(search,
						new TextContentAdapter(), lalaScore.getCategories(
								matchType, matchName));
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(rank_shell, SWT.OK);
				box.setText("提示");
				if (!lalaScore.isCollected()) {
					lalaScore = new QueryScore();
				}
				if (lalaScore.isCollected()) {
					// 当matchName不为空时，跳出选择界面，让用户选择赛事模式
					if (lalaScore.getAllMatchNames().length == 0) {
						box.setMessage("无有成绩赛事");
						box.open();
						return;
					}
					MatchNameDialog dialog = new MatchNameDialog(rank_shell);
					dialog.setMatchNames(lalaScore.getAllMatchNames());
					dialog.open();
					matchName = dialog.getMatchName();
					matchType = dialog.getMatchType();
					if (matchName != null && !matchName.equals("")
							&& matchType != -1) {
						int temp = lalaScore.getTeamMinId(matchType, matchName);
						switch (temp) {
						case -1: {
							box.setMessage("暂无成绩");
							box.open();
							break;
						}
						default: // 获取数据填充表格
							data = lalaScore.getRank(temp, matchType);
							// 设置项目类别
							category = lalaScore.getCategory(temp);
							createTable(rank_composite, data);
							if (matchType == 0) {
								matchNameLabel.setText(matchName + "--预赛排名");
							} else
								matchNameLabel.setText(matchName + "--决赛排名");
							search.setText(category);
							AutoCompleteField field = new AutoCompleteField(
									search, new TextContentAdapter(),
									lalaScore.getCategories(matchType,
											matchName));
						}
					}
				} else {
					box.setMessage("连接数据库失败");
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
					if (!lalaScore.isCollected()) {
						lalaScore = new QueryScore();
					}
					data = lalaScore.getRank(matchName, selectedValue,
							matchType);
					createTable(rank_composite, data);
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
					box.setMessage("暂时没有比赛成绩,不能导出");
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
					if (!lalaScore.isCollected()) {
						lalaScore = new QueryScore();
					}
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

	/** 创建技巧table **/
	public void createTable(Composite rank_composite,
			List<HashMap<String, Object>> data) {
		table.dispose();
		table = new Table(rank_composite, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);
		table.setBounds(0, 82, 1112, 527);
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
		avg_art.setText("平均艺术分");
		score01_exection.setText("完成1");
		score02_exection.setText("完成2");
		score03_exection.setText("完成3");
		score04_exection.setText("完成4");
		avg_exection.setText("平均完成分");
		score01_impression.setText("总体评价1");
		score02_impression.setText("总体评价2");
		avg_impression.setText("总体评价分");
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
	}
}
