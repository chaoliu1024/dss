/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class CompositeText extends Composite {
	private Label valueLabel;
	private Label label;
	private String labelString;

	public CompositeText(Composite parent, int style, String labelString) {
		super(parent, SWT.NONE);
		this.labelString = labelString;
		initGUI();
	}

	private void initGUI() {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		this.setLayoutData(gridData);
		try {
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 2;
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 0;
			this.setLayout(layout);
			{
				label = new Label(this, SWT.None);
				label.setText(this.labelString);
				label.setBackground(SWTResourceManager
						.getColor(SWT.COLOR_WIDGET_FOREGROUND));
				label.setForeground(SWTResourceManager.getColor(255, 215, 0));
				label.setAlignment(SWT.RIGHT);
				label.setFont(SWTResourceManager.getFont("华文楷体", 22, SWT.BOLD));
				gridData = new GridData(GridData.FILL_BOTH);
				gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
				gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
				gridData.horizontalAlignment = GridData.FILL;// 水平方向充满
				gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
				label.setLayoutData(gridData);
			}
			{
				valueLabel = new Label(this, SWT.None);
				valueLabel.setBackground(SWTResourceManager
						.getColor(SWT.COLOR_WIDGET_FOREGROUND));
				valueLabel.setForeground(SWTResourceManager.getColor(255, 215,
						0));
				valueLabel.setAlignment(SWT.LEFT);
				valueLabel.setFont(SWTResourceManager.getFont("华文楷体", 22,
						SWT.BOLD));
				gridData = new GridData(GridData.FILL_BOTH);
				gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
				gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
				gridData.horizontalAlignment = GridData.FILL;// 水平方向充满
				gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
				valueLabel.setLayoutData(gridData);
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setValueLabel(String value) {
		valueLabel.setText(value);
	}

	public void changeFont(int index, Font font, Color color) {
		if (index == 0) { // 更改第一个label的字体显示
			if (font != null) {
				label.setFont(font);
			}
			if (color != null) {
				label.setForeground(color);
			}
		}
		if (index == 1) { // 更改第二个label的字体显示
			if (font != null) {
				valueLabel.setFont(font);
			}
			if (color != null) {
				valueLabel.setForeground(color);
			}
		}
	}

	@Override
	public void dispose() {
		if (null != label && !label.isDisposed()) {
			label.dispose();
		}
		if (null != valueLabel && !valueLabel.isDisposed()) {
			valueLabel.dispose();
		}
		super.dispose();
	}

	@Override
	public void addControlListener(ControlListener listener) {
		// TODO Auto-generated method stub
		super.addControlListener(listener);
		label.addControlListener(listener);
		valueLabel.addControlListener(listener);
	}
}
