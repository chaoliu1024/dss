/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.util.ArrayList;
import java.util.List;

import nuist.qlib.dss.handlerData.HandlerData;
import nuist.qlib.dss.util.ExcelManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 数据处理界面
 * 
 * @author WangFang
 * @since dss 1.0
 */
public class HandlerDataPanel extends Composite {
	public static HandlerDataPanel handlerDataPanel;
	private HandlerData handler;
	private Button chose_btn;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HandlerDataPanel(final Shell shell, final Composite parent, int style) {
		super(parent, style);
		this.setBounds(0, 0, 970, 663);
		setLayout(null);

		handler = new HandlerData();

		Group grpexcel = new Group(this, SWT.NONE);
		grpexcel.setText("比赛出场顺序excel导入");
		grpexcel.setBounds(55, 61, 862, 84);

		Button btnexcel = new Button(grpexcel, SWT.NONE);
		btnexcel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.doit = true;
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setText("打开文件");// 设置对话框的标题
				// dialog.setFileName("");//设置默认的文件名
				dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
				dialog.setFilterNames(new String[] { "Excel文件(*.xls)",
						"Excel文件(*.xlsx)" });
				String fileName; // 获取选取的文件名。
				fileName = dialog.open();
				if (fileName != null) {
					MessageBox box = new MessageBox(shell, SWT.OK);
					box.setText("提示");
					ExcelManager manager = new ExcelManager();
					int mark = manager.ImportExcel(fileName, 0);
					List<Integer> error;
					String errorString;
					// 如果导入失败，给予提示
					if (mark == 0) {
						box.setMessage("请查阅数据库的配置信息！！");
					} else if (mark == 1) {
						box.setMessage("文件中数据出错或文件名不符合要求，请确认！！");
					} else if (mark == 2) {
						box.setMessage("导入数据库成功！");
					} else if (mark == 3) {
						box.setMessage("导入数据库失败！！");
					} else if (mark == 4) {
						box.setMessage("程序发生错误！！");
					} else if (mark == 5) {
						box.setMessage("该场比赛已经开始或结束，请检查！！");
					} else if (mark == 6) {
						box.setMessage("没有该赛事，不能附加！！");
					} else if (mark == 7) {
						box.setMessage("文件中没有数据！！");
					} else if (mark == 8) {
						errorString = "下列行中的队伍已经导入过:\n";
						error = manager.getError();
						box.setMessage(errorString + error.toString());
					} else if (mark == 9) {
						errorString = "下列行中的出场顺序与数据库中已有场次的最后出场顺序不一致:\n";
						error = manager.getFirstOrderError();
						box.setMessage(errorString + error.toString());
					} else if (mark == 10) {
						errorString = "下列行中的出场顺序错误:\n";
						error = manager.getOrderError();
						box.setMessage(errorString + error.toString());
					}
					box.open();
					manager.close();
				}
			}
		});
		btnexcel.setBounds(161, 36, 100, 27);
		btnexcel.setText("选择导入excel");

		Button add_data_but = new Button(grpexcel, SWT.NONE);
		add_data_but.setBounds(402, 36, 100, 27);
		add_data_but.setText("添加excel");
		add_data_but.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				e.doit = true;
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setText("打开文件");// 设置对话框的标题
				// dialog.setFileName("");//设置默认的文件名
				dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
				dialog.setFilterNames(new String[] { "Excel文件(*.xls)",
						"Excel文件(*.xlsx)" });
				String fileName; // 获取选取的文件名。
				fileName = dialog.open();
				if (fileName != null) {
					ExcelManager manager = new ExcelManager();
					int mark = manager.ImportExcel(fileName, 1);
					MessageBox box = new MessageBox(shell, SWT.OK);
					box.setText("提示");
					List<Integer> error;
					String errorString;
					// 如果导入失败，给予提示
					if (mark == 0) {
						box.setMessage("请查阅数据库的配置信息！！");
					} else if (mark == 1) {
						box.setMessage("文件中数据出错或文件名不符合要求，请确认！！");
					} else if (mark == 2) {
						box.setMessage("导入数据库成功！");
					} else if (mark == 3) {
						box.setMessage("导入数据库失败！！");
					} else if (mark == 4) {
						box.setMessage("程序发生错误！！");
					} else if (mark == 5) {
						box.setMessage("该场比赛已经开始或结束，请检查！！");
					} else if (mark == 6) {
						box.setMessage("没有该赛事，不能附加！！");
					} else if (mark == 7) {
						box.setMessage("文件中没有数据！！");
					} else if (mark == 8) {
						errorString = "下列行中的出场顺序已经导入过:\n";
						error = manager.getError();
						box.setMessage(errorString + error.toString());
					} else if (mark == 9) {
						errorString = "下列行中的出场顺序与数据库中已有场次的最后出场顺序不一致:\n";
						error = manager.getFirstOrderError();
						box.setMessage(errorString + error.toString());
					}
					box.open();
					manager.close();
				}
			}
		});

		Group group_1 = new Group(this, SWT.NONE);
		group_1.setText("历史数据删除");
		group_1.setBounds(55, 185, 862, 389);

		ScrolledComposite scrolledComposite = new ScrolledComposite(group_1,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(30, 59, 804, 282);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		final Composite content_composite = new Composite(scrolledComposite,
				SWT.NONE);
		content_composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
		content_composite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		scrolledComposite.setContent(content_composite);
		scrolledComposite.setMinSize(content_composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 3;
		layout.marginBottom = 2;
		layout.marginTop = 2;
		layout.marginLeft = 2;
		layout.marginRight = 2;
		content_composite.setLayout(layout);

		Button delete_btn = new Button(group_1, SWT.NONE);
		delete_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (handler.isCollected()) {
					deleteMatches(content_composite, shell);
				} else {
					MessageBox box = new MessageBox(shell);
					box.setText("警告");
					box.setMessage("数据库连接失败");
					box.open();
				}
			}
		});
		delete_btn.setBounds(756, 347, 80, 27);
		delete_btn.setText("删除");

		chose_btn = new Button(group_1, SWT.NONE);
		chose_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (handler.isCollected()) {
					String[][] matches = handler.getMatchNames();
					createMathCheck(content_composite, matches);
				} else {
					MessageBox box = new MessageBox(shell);
					box.setText("警告");
					box.setMessage("数据库连接失败");
					box.open();
				}
			}
		});
		chose_btn.setBounds(79, 26, 80, 27);
		chose_btn.setText("选择赛事");

		Button selectAll_btn = new Button(group_1, SWT.NONE);
		selectAll_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Control[] controls = content_composite.getChildren();
				for (Control one : controls) {
					if (one instanceof Button) {
						((Button) one).setSelection(true);
					}
				}
			}
		});
		selectAll_btn.setBounds(40, 347, 52, 27);
		selectAll_btn.setText("全选");

		Button reset_btn = new Button(group_1, SWT.NONE);
		reset_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Control[] controls = content_composite.getChildren();
				for (Control one : controls) {
					if (one instanceof Button) {
						((Button) one).setSelection(false);
					}
				}
			}
		});
		reset_btn.setBounds(102, 347, 57, 27);
		reset_btn.setText("取消");
	}

	/** 删除选中的赛事的相关数据 **/
	public void deleteMatches(Composite content_composite, Shell shell) {
		Control[] controls = content_composite.getChildren();
		List<String> hasScoreMathes = new ArrayList<String>();
		List<String> noScoreMathes = new ArrayList<String>();
		MessageBox box = new MessageBox(shell, SWT.OK | SWT.CANCEL);
		box.setText("提示");
		MessageBox box2 = new MessageBox(shell);
		box2.setText("提示");
		int i = 0;
		boolean mark = false;
		Button button;
		for (Control one : controls) {
			if (one instanceof Label && i == 0) {
				i = 1;
			}
			if (one instanceof Label && i == 1) {
				i = 2;
			}
			if (one instanceof Button && i == 1) { // 该checkBox是已有成绩的赛事
				button = (Button) one;
				if (button.getSelection()) {
					hasScoreMathes.add(button.getText());
				}
			}
			if (one instanceof Button && i == 2) { // 该checkBox是没有成绩的赛事
				button = (Button) one;
				if (button.getSelection()) {
					noScoreMathes.add(button.getText());
				}
			}
		}
		if (noScoreMathes.size() == 0 && hasScoreMathes.size() == 0) {
			box2.setMessage("请选择要删除的赛事");
			box2.open();
			return;
		}
		if (noScoreMathes.size() != 0) {
			box.setMessage("确定要删除没有比赛的赛事?");
			if (box.open() == SWT.OK) {
				mark = true;
			} else {
				return;
			}
		}
		if (mark) { // 删除赛事相关的数据
			int result = handler.deleteMatches(hasScoreMathes, noScoreMathes);
			switch (result) {
			case 0:
				box2.setMessage("删除成功");
				box2.open();
				break;
			case 1:
				box2.setMessage("删除有成绩的赛事失败");
				box2.open();
				break;
			case 2:
				box2.setMessage("删除没有成绩的赛事失败");
				box2.open();
				break;
			}
			String[][] matches = handler.getMatchNames();
			createMathCheck(content_composite, matches);

		}

	}

	/** 创建所有赛事checkBox **/
	public void createMathCheck(Composite content_composite, String[][] matches) {
		boolean hasScoreMatches = false;
		boolean noScoreMatches = false;
		Control[] control = content_composite.getChildren(); // 删除已有控件
		for (Control temp : control) {
			temp.dispose();
		}
		for (int i = 0; i < matches[0].length; i++) { // 判断是否有已经比赛过的比赛
			if (matches[0][i] != null) {
				hasScoreMatches = true;
				break;
			}
		}

		for (int i = 0; i < matches[1].length; i++) { // 判断是否有没有比赛过的比赛
			if (matches[1][i] != null) {
				noScoreMatches = true;
				break;
			}
		}

		// 创建已经比赛过的赛事的checkBox
		if (hasScoreMatches) {
			Label label = new Label(content_composite, SWT.NONE);
			label.setText("已经比赛过的赛事");
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
					3, 1));
			Button btnCheckButton = null;
			GridData data = null;
			for (int i = 0; i < matches[0].length; i++) {
				if (matches[0][i] != null) {
					btnCheckButton = new Button(content_composite, SWT.CHECK);
					btnCheckButton.setText(matches[0][i]);
					data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
					data.widthHint = 255;
					data.heightHint = 15;
					btnCheckButton.setLayoutData(data);
				}
			}
		}
		// 创建没有比赛过的赛事的checkBox
		if (noScoreMatches) {
			Label label = new Label(content_composite, SWT.NONE);
			label.setText("没有比赛过的赛事");
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
					3, 1));
			Button btnCheckButton = null;
			GridData data = null;
			for (int i = 0; i < matches[1].length; i++) {
				if (matches[1][i] != null) {
					btnCheckButton = new Button(content_composite, SWT.CHECK);
					btnCheckButton.setText(matches[1][i]);
					data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
					data.widthHint = 255;
					data.heightHint = 15;
					btnCheckButton.setLayoutData(data);
				}
			}
		}
		content_composite.layout();
	}

	/***
	 * 实例化唯一实例
	 * 
	 * @param shell
	 * @param parent
	 * @param style
	 * @return
	 */
	public static HandlerDataPanel getInstance(Shell shell, Composite parent,
			int style) {
		if (handlerDataPanel == null) {
			handlerDataPanel = new HandlerDataPanel(shell, parent, style);
		}
		return handlerDataPanel;
	}
}
