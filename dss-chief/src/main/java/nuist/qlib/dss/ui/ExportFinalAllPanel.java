/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.teamManager.FinalTeamExport;
import nuist.qlib.dss.util.ExcelManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 一键式导出决赛队伍
 * 
 * @author WangFang
 * @since dss 1.0
 */
public class ExportFinalAllPanel {

	protected Shell shell;
	private Table table;
	private FinalTeamExport finalTeamExport;
	private String matchName;
	private List<HashMap<String, Object>> teams;
	private List<Text> counts;

	public ExportFinalAllPanel(String matchName) {
		this.matchName = matchName;
	}

	// public static void main(String[] args) {
	// try {
	// ExportFinalAllPanel window = new ExportFinalAllPanel(null);
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
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		finalTeamExport = new FinalTeamExport();
		counts = new ArrayList<Text>();

		shell = new Shell();
		shell.setImage(new Image(display, ExportFinalAllPanel.class
				.getResourceAsStream("/img/logo.png")));
		shell.setSize(748, 311);
		shell.setText("项目队伍一键式导出");
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (finalTeamExport.isCollected()) {
					finalTeamExport.close();
				}
				shell.dispose();
			}
		});

		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		Rectangle shellBounds = shell.getBounds();
		int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
		shell.setLocation(x, y);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		table.setBounds(0, 10, 732, 224);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(200);
		tblclmnNewColumn.setText("项目名称");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(76);
		tblclmnNewColumn_1.setText("队伍总数量");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(97);
		tblclmnNewColumn_2.setText("未比赛队伍数量");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(97);
		tblclmnNewColumn_3.setText("已比赛队伍数量");

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(79);
		tblclmnNewColumn_4.setText("拟导出人数");

		Button button = new Button(shell, SWT.NONE);
		button.setBounds(456, 240, 75, 25);
		button.setText("确认");

		// 初始化数据
		MessageBox box = new MessageBox(shell, SWT.OK);
		box.setText("提示");
		if (finalTeamExport.isCollected()) {
			teams = finalTeamExport.getAllCategory(matchName);
			TableItem item;
			for (int i = 0; i < teams.size(); i++) {
				item = new TableItem(table, SWT.NONE);
				item.setText(new String[] {
						teams.get(i).get("category").toString(),
						teams.get(i).get("totalCount").toString(),
						teams.get(i).get("leftCount").toString(),
						teams.get(i).get("hasMatchCount").toString() });
			}
			createTableText(table, teams);
		} else {
			box.setMessage("数据库连接失败");
			box.open();
		}

		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				String biggermention = "";
				String negativemention = ""; // 填写导出队伍数量不合法(负数或者不是整数)的队伍
				String blackmention = ""; // 导出队伍数目大于已经比赛完的队伍数目
				String leftCountmention = ""; // 有没有比赛完的且有成绩的队伍
				String text;
				boolean mark = false;
				for (int i = 0; i < teams.size(); i++) {
					text = counts.get(i).getText();
					if (text == null || text.equals("")) {
						blackmention += teams.get(i).get("category").toString()
								+ "\n"; // 没有填写要导出队伍人数的项目
					} else if (!teams.get(i).get("leftCount").toString().trim()
							.equals("0")
							&& !teams.get(i).get("exportCount").toString()
									.trim().equals("0")) {
						leftCountmention += teams.get(i).get("category")
								.toString()
								+ "\n"; // 没有比赛完的队伍
					} else {
						int count;
						try {
							count = Integer.valueOf(text);
							if (count < 0) {
								negativemention += teams.get(i).get("category")
										.toString()
										+ "\n"; // 填写要导出队伍人数为负值的项目
							} else if (count > Integer.valueOf(teams.get(i)
									.get("hasMatchCount").toString())) {
								biggermention += teams.get(i).get("category")
										.toString()
										+ "\n";
							}
						} catch (Exception e) {
							negativemention += teams.get(i).get("category")
									.toString()
									+ "\n";
						}
					}
				}
				MessageBox box = new MessageBox(shell, SWT.OK);
				box.setText("提示");
				if (!negativemention.equals("")) {
					box.setMessage("以下队伍选取人数不合法:\n" + negativemention);
					box.open();
				} else if (!biggermention.equals("")) {
					box.setMessage("以下队伍选取人数超过已比赛队伍人数:\n" + biggermention);
					box.open();
				} else if (!blackmention.equals("")) {
					box = new MessageBox(shell, SWT.OK | SWT.CANCEL);
					box.setText("提示");
					box.setMessage("有队伍没有选择人数，是否继续??");
					if (box.open() == SWT.OK) {
						mark = true;
					}
				} else if (!leftCountmention.equals("")) {
					box = new MessageBox(shell, SWT.OK | SWT.CANCEL);
					box.setText("提示");
					box.setMessage("有队伍没有比赛完但有导出数据，如下：" + leftCountmention
							+ "\n是否继续导出数据??");
					if (box.open() == SWT.OK) {
						mark = true;
					}
				} else {
					mark = true;
				}
				if (mark) {
					if (finalTeamExport.isCollected()) {
						List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
						HashMap<String, Object> one = null;
						for (int i = 0; i < teams.size(); i++) {
							if (counts.get(i).getText() != null
									&& !counts.get(i).getText().equals("0")) {
								one = teams.get(i);
								one.put("selectCount", counts.get(i).getText()
										.trim());
								data.add(one);
							}
						}
						MessageBox mention = new MessageBox(shell, SWT.OK);
						mention.setText("提示");
						if (data.size() == 0) {
							mention.setMessage("没有要导出的队伍");
							mention.open();
							return;
						}
						FileDialog dialog = new FileDialog(shell, SWT.SAVE);
						dialog.setText("保存文件");// 设置对话框的标题
						dialog.setFilterExtensions(new String[] { "*.xls",
								"*.xlsx" });
						dialog.setFilterNames(new String[] { "Excel文件(*.xls)",
								"Excel文件(*.xlsx)" });
						String fileName = dialog.open(); // 获得保存的文件名
						if (fileName != null && !fileName.equals("")) {
							ExcelManager manager = new ExcelManager();
							if (manager.ExportExcelFinal(finalTeamExport
									.getFinalTeamAll(data, matchName),
									fileName, matchName, "", true)) {
								mention.setMessage("导出成功");
								mention.open();
								shell.close();
							} else {
								mention.setMessage("导出失败");
								mention.open();
							}
						}
					} else {
						MessageBox mention = new MessageBox(shell, SWT.OK);
						mention.setText("提示");
						mention.setMessage("数据库连接失败");
						mention.open();
					}
				}
			}
		});
	}

	private void createTableText(Table table,
			List<HashMap<String, Object>> items) {
		TableEditor editor = new TableEditor(table);
		for (int i = 0; i < table.getItemCount(); i++) {
			editor = new TableEditor(table);
			Text text = new Text(table, SWT.BORDER);
			text.setText(items.get(i).get("exportCount").toString());
			counts.add(text);
			text.pack();
			editor.minimumWidth = text.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(text, table.getItem(i), 4);
		}
	}
}
