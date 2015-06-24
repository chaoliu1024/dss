package nuist.qlib.dss.ui;

import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.component.CompositeText;
import nuist.qlib.dss.scoreManager.QueryScore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class ScreenComposite3 extends Composite {
	public static ScreenComposite3 composite;
	private Composite parent;
	private Label matchName;
	private CompositeText category;
	private Composite rankTable;
	private int i = 0;
	private List<HashMap<String, Object>> data;
	protected static HashMap<String, Object> params;
	private int size = 35; // 设置字号
	private int originalSize = 45;
	private Rectangle originalRec;

	private ScreenComposite3(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;

		GridLayout layout = new GridLayout(5, false);
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
			gridData.horizontalSpan = 5;
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
			matchName.setText("2013啦啦操比赛");
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
			gridData.horizontalSpan = 5;
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
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			rankTable = new Composite(this, SWT.None);
			rankTable.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			rankTable.setLayoutData(gridData);
			layout = new GridLayout(4, true);
			rankTable.setLayout(layout);

			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			CLabel teamName = new CLabel(rankTable, SWT.None);
			teamName.setFont(SWTResourceManager.getFont("华文楷体", 22, SWT.BOLD));
			teamName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			teamName.setForeground(SWTResourceManager.getColor(255, 215, 0));
			teamName.setAlignment(SWT.CENTER);
			teamName.setText("单位");
			teamName.setLayoutData(gridData);

			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			CLabel totalScoreName = new CLabel(rankTable, SWT.None);
			totalScoreName.setFont(SWTResourceManager.getFont("华文楷体", 22,
					SWT.BOLD));
			totalScoreName.setForeground(SWTResourceManager.getColor(255, 215,
					0));
			totalScoreName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			totalScoreName.setAlignment(SWT.CENTER);
			totalScoreName.setText("总分");
			totalScoreName.setLayoutData(gridData);

			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			CLabel rankName = new CLabel(rankTable, SWT.None);
			rankName.setFont(SWTResourceManager.getFont("华文楷体", 22, SWT.BOLD));
			rankName.setForeground(SWTResourceManager.getColor(255, 215, 0));
			rankName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			rankName.setAlignment(SWT.CENTER);
			rankName.setText("排名");
			rankName.setLayoutData(gridData);

		}
		{
			gridData = new GridData();
			gridData.horizontalSpan = 5;
			gridData.horizontalAlignment = GridData.FILL;
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

		category.changeFont(0,
				SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));
		category.changeFont(1,
				SWTResourceManager.getFont("华文楷体", size, SWT.BOLD),
				SWTResourceManager.getColor(255, 215, 0));

		Control[] controls = rankTable.getChildren();
		for (Control control : controls) {
			if (control instanceof CLabel) {
				((CLabel) control).setFont(SWTResourceManager.getFont("华文楷体",
						size - 6, SWT.BOLD));
			} else if (control instanceof Label) {
				((Label) control).setFont(SWTResourceManager.getFont("华文楷体",
						size - 8, SWT.BOLD));
			}
		}
		this.layout();
		parent.layout();
	}

	public void changeRank(List<HashMap<String, Object>> data) {
		GridData gridData = null;
		HashMap<String, Object> map;

		Control[] controls = rankTable.getChildren();
		for (Control control : controls) {
			if (control instanceof Label) {
				((Label) control).dispose();
			}
		}
		rankTable.layout();
		this.layout();

		int k = i + 8;
		for (; i < k && i < data.size(); i++) {
			map = data.get(i);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			Label teamName = new Label(rankTable, SWT.None);
			teamName.setFont(SWTResourceManager.getFont("华文楷体", size - 13,
					SWT.BOLD));
			teamName.setForeground(SWTResourceManager.getColor(255, 215, 0));
			teamName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			teamName.setAlignment(SWT.CENTER);
			teamName.setText(map.get("teamName").toString());
			teamName.setLayoutData(gridData);

			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			Label totalScoreName = new Label(rankTable, SWT.None);
			totalScoreName.setFont(SWTResourceManager.getFont("华文楷体",
					size - 13, SWT.BOLD));
			totalScoreName.setForeground(SWTResourceManager.getColor(255, 215,
					0));
			totalScoreName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			totalScoreName.setAlignment(SWT.CENTER);
			totalScoreName.setText(map.get("total").toString());
			totalScoreName.setLayoutData(gridData);

			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
			gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
			gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
			Label rankName = new Label(rankTable, SWT.None);
			rankName.setFont(SWTResourceManager.getFont("华文楷体", size - 13,
					SWT.BOLD));
			rankName.setForeground(SWTResourceManager.getColor(255, 215, 0));
			rankName.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			rankName.setAlignment(SWT.CENTER);
			rankName.setText(map.get("rank").toString());
			rankName.setLayoutData(gridData);
		}
		if (i < k) {
			int j = i;
			for (; j < k; j++) {
				{
					gridData = new GridData(GridData.FILL_BOTH);
					gridData.horizontalSpan = 5;
					gridData.horizontalAlignment = GridData.FILL;
					gridData.verticalAlignment = GridData.FILL; // 垂直方向充满
					gridData.grabExcessVerticalSpace = true; // 抢占垂直方向额外空间
					gridData.grabExcessHorizontalSpace = true;// 抢占水平方向额外空间
					Label label = new Label(this, SWT.None);
					label.setBackground(SWTResourceManager
							.getColor(SWT.COLOR_WIDGET_FOREGROUND));
					label.setLayoutData(gridData);
				}
			}
		}
		rankTable.layout();
		this.layout();
		parent.layout();
	}

	public void setText() {
		matchName.setText(params.get("matchName").toString());
		category.setValueLabel(params.get("category").toString());
		QueryScore lalaScore = new QueryScore();
		data = lalaScore.getRank(params.get("matchName").toString(), params
				.get("category").toString(), Integer.valueOf(params.get(
				"matchType").toString()));
		i = 0;
		changeRank(data);
		new changeThead().start();
	}

	public static ScreenComposite3 getInstance(Composite parent, int style) {
		if (composite == null) {
			synchronized (ScreenComposite3.class) {
				if (composite == null) {
					composite = new ScreenComposite3(parent, style);
				}
			}
		}
		return composite;
	}

	private class changeThead extends Thread {
		private int changeNum;

		public changeThead() {
			changeNum = data.size() % 8 == 0 ? data.size() / 8
					: data.size() / 8 + 1;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					synchronized (this) {
						changeRank(data);
					}
				}
			};

			for (int j = 1; j < changeNum; j++) {
				long now = System.currentTimeMillis();
				while (true) {
					if ((System.currentTimeMillis() - now) > 4000) {
						if (!parent.getDisplay().isDisposed()) {
							parent.getDisplay().asyncExec(runnable);
						}
						break;
					}
				}
			}
		}

	}
}
