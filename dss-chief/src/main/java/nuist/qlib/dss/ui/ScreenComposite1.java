package nuist.qlib.dss.ui;

import java.util.HashMap;

import nuist.qlib.dss.component.CompositeText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ScreenComposite1 extends Composite {
	public static ScreenComposite1 composite;
	private Composite parent;
	protected static Label matchName;
	protected static CompositeText team; // 队伍名称
	protected static CompositeText category; // 项目
	protected static CompositeText totalScore; // 最后得分
	private int size = 35; // 设置字号
	private int originalSize = 45;
	private Rectangle originalRec;
	protected static HashMap<String, Object> data;
	protected static boolean isDisplay = false; // 显示下一屏是否在显示

	private ScreenComposite1(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;

		GridLayout layout = new GridLayout(6, false);
		this.setLayout(layout);
		this.setBackground(SWTResourceManager.getColor(0, 0, 0));
		this.originalRec = Ds.rec;

		this.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void controlResized(ControlEvent evt) {
				// TODO Auto-generated method stub
				Rectangle rect = ((Composite) evt.widget).getBounds(); // 当前composite的宽和高
				double min = Math.max(
						Math.abs((double) (rect.width - originalRec.width))
								/ originalRec.width,
						Math.abs((double) (rect.height - originalRec.height))
								/ originalRec.height);
				size = (int) ((1 - Math.abs(min)) * originalSize);
				changeFontSize();
			}
		});
		GridData gridData = null;

		{
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 6;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			matchName = new Label(this, SWT.SHADOW_NONE);
			matchName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			matchName.setForeground(SWTResourceManager.getColor(255, 215, 0));
			matchName.setFont(SWTResourceManager.getFont("华文楷体", 20, SWT.BOLD));
			matchName.setAlignment(SWT.CENTER);
			matchName.setLayoutData(gridData);
		}
		{
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessHorizontalSpace = true; // 抢占垂直方向额外空间
			Label label = new Label(this, SWT.None);
			label.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label.setLayoutData(gridData);
		}
		{
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 6;
			gridData.verticalSpan = 1;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			team = new CompositeText(this, SWT.None, "单位：");
			team.setLayoutData(gridData);
			team.setValueLabel("南京信息工程大学");
		}
		{
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 6;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			category = new CompositeText(this, SWT.None, "参赛项目：");
			category.setLayoutData(gridData);
			category.setValueLabel("三人两足项目");
		}
		{
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 5;
			gridData.verticalSpan = 2;
			gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			totalScore = new CompositeText(this, SWT.BORDER, "最后得分:");
			totalScore.setTouchEnabled(true);
			totalScore.setLayoutData(gridData);
			totalScore.changeFont(0,
					SWTResourceManager.getFont("华文楷体", 26, SWT.BOLD),
					SWTResourceManager.getColor(138, 31, 33));
			totalScore.setValueLabel("200.0");
		}
		{
			gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			Label label = new Label(this, SWT.None);
			label.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label.setLayoutData(gridData);
		}
		{
			gridData = new GridData();
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			Label label = new Label(this, SWT.None);
			label.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label.setLayoutData(gridData);
		}
		{
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 6;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			Label label = new Label(this, SWT.None);
			label.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label.setLayoutData(gridData);
		}
		this.layout();
		parent.layout();
	}

	public void changeFontSize() {
		int matchNameSize = size + 8;
		matchName.setFont(SWTResourceManager.getFont("华文楷体", matchNameSize,
				SWT.BOLD));

		team.changeFont(0, SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));
		team.changeFont(1, SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));

		category.changeFont(0,
				SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));
		category.changeFont(1,
				SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));

		totalScore.changeFont(0,
				SWTResourceManager.getFont("华文楷体", (size + 20), SWT.BOLD),
				SWTResourceManager.getColor(138, 31, 33));
		totalScore.changeFont(1,
				SWTResourceManager.getFont("华文楷体", (size + 25), SWT.BOLD),
				SWTResourceManager.getColor(138, 31, 33));

		this.layout();
		parent.layout();
	}

	public static void setText() {
		matchName.setText(data.get("matchName").toString());
		team.setValueLabel(data.get("teamName").toString());
		category.setValueLabel(data.get("category").toString());
		totalScore.setValueLabel(data.get("total").toString());
		composite.layout();
	}

	public static ScreenComposite1 getInstance(Composite parent, int style) {
		if (composite == null) {
			synchronized (ScreenComposite1.class) {
				if (composite == null) {
					composite = new ScreenComposite1(parent, style);
				}
			}
		}
		return composite;
	}
}
