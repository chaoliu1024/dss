/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChooseMatchName extends Dialog {
	private String[] matchNames;
	private String matchName;
	private Shell dialog;
	private Text text;

	public ChooseMatchName(Shell parent) {
		super(parent);
	}

	public void open() {
		Display display = Display.getDefault();
		final Shell parent = this.getParent();
		dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(292, 187);
		dialog.setText("赛事名称输入");
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
		Rectangle shellBounds = dialog.getBounds();
		int x = displayBounds.x + (displayBounds.width - shellBounds.width) >> 1;
		int y = displayBounds.y + (displayBounds.height - shellBounds.height) >> 1;
		dialog.setLocation(x, y);
		Label label = new Label(dialog, SWT.NONE);
		label.setBounds(10, 39, 61, 17);
		label.setText("赛事名称：");

		text = new Text(dialog, SWT.BORDER);
		text.setBounds(81, 36, 193, 23);
		if (matchName != null && matchName.trim().length() != 0) {
			text.setText(matchName);
		} else {
			text.setText(matchNames[0]);
		}
		AutoCompleteField field = new AutoCompleteField(text,
				new TextContentAdapter(), matchNames);

		Button button = new Button(dialog, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(dialog, SWT.OK);
				box.setText("提示");
				matchName = text.getText();
				if (matchName == null || matchName.equals("")) {
					box.setMessage("请输入赛事名称！！");
					box.open();
				} else
					dialog.close();
			}
		});
		button.setBounds(194, 108, 80, 27);
		button.setText("确定");
		dialog.open();
		while (!dialog.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public String[] getMatchNames() {
		return matchNames;
	}

	public void setMatchNames(String[] matchNames) {
		this.matchNames = matchNames;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

}
