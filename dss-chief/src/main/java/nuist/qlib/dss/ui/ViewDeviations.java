/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.text.DecimalFormat;
import java.util.HashMap;

import nuist.qlib.dss.scoreManager.QueryScore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewDeviations {

	protected Shell shell;
	protected static String matchName;
	protected static int matchType;
	// 赛事模式
	private QueryScore score;
	private Table table;

	private static final Logger logger = LoggerFactory
			.getLogger(ViewDeviations.class);

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ViewDeviations window = new ViewDeviations();
			window.open();
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

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
		shell = new Shell(display, SWT.CLOSE | SWT.MIN);
		shell.setSize(838, 118);
		shell.setImage(new Image(display, ViewDeviations.class
				.getResourceAsStream("/img/logo.png")));
		shell.setText(matchName + "---" + "裁判打分误差");
		Rectangle displayBounds = shell.getDisplay().getPrimaryMonitor()
				.getBounds();
		Rectangle shellBounds = shell.getBounds();
		int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
		shell.setLocation(x, y);

		score = new QueryScore();

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 830, 84);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 10, 809, 64);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(80);
		tableColumn.setText("艺术1");

		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(80);
		tableColumn_1.setText("艺术2");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(80);
		tableColumn_2.setText("艺术3");

		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(80);
		tableColumn_3.setText("艺术4");

		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setWidth(80);
		tableColumn_4.setText("完成1");

		TableColumn tableColumn_5 = new TableColumn(table, SWT.NONE);
		tableColumn_5.setWidth(80);
		tableColumn_5.setText("完成2");

		TableColumn tableColumn_6 = new TableColumn(table, SWT.NONE);
		tableColumn_6.setWidth(80);
		tableColumn_6.setText("完成3");

		TableColumn tableColumn_7 = new TableColumn(table, SWT.NONE);
		tableColumn_7.setWidth(80);
		tableColumn_7.setText("完成4");

		TableColumn tableColumn_8 = new TableColumn(table, SWT.NONE);
		tableColumn_8.setWidth(80);
		tableColumn_8.setText("舞步1");

		TableColumn tableColumn_9 = new TableColumn(table, SWT.NONE);
		tableColumn_9.setWidth(80);
		tableColumn_9.setText("舞步2");

		TableItem item;
		if (score.isCollected()) {
			HashMap<String, Object> one = score.getDeviations(matchName,
					matchType);
			DecimalFormat format = new DecimalFormat("#0.0000");
			if (one != null) {
				item = new TableItem(table, SWT.NONE);
				item.setText(new String[] {
						format.format(one.get("arterror1")),
						format.format(one.get("arterror2")),
						format.format(one.get("arterror3")),
						format.format(one.get("arterror4")),
						format.format(one.get("execerror1")),
						format.format(one.get("execerror2")),
						format.format(one.get("execerror3")),
						format.format(one.get("execerror4")),
						format.format(one.get("imperror1")),
						format.format(one.get("imperror2")) });
			}
			if (score.isCollected()) {
				score.close();
			}
		} else {
			MessageBox box = new MessageBox(shell);
			box.setText("警告");
			box.setMessage("数据库连接失败");
			box.open();
		}
	}
}
