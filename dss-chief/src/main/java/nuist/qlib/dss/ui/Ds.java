/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class Ds {

	protected Shell shell;
	protected static StackLayout stackLayout;
	public static Rectangle rec;
	private HashMap<String, Object> data;
	private Composite composite;
	private MyThread thread;

	// /**
	// * Launch the application.
	// * @param args
	// */
	// public static void main(String[] args) {
	// try {
	// Ds window = new Ds();
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
		center(shell);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
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
	protected void createContents() {
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(0, 0, 0));
		shell.setText("");
		shell.setImage(new Image(shell.getDisplay(), Ds.class
				.getResourceAsStream("/img/logo.png")));
		rec = Display.getDefault().getPrimaryMonitor().getBounds();
		shell.setBounds(rec); // 默认为全屏

		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				MatchPanel.isDisplay = false;
				if (ScreenComposite1.composite != null) {
					ScreenComposite1.composite.dispose();
					ScreenComposite1.composite = null;
				}
				if (ScreenComposite2.composite != null) {
					ScreenComposite2.composite.dispose();
					ScreenComposite2.composite = null;
				}
				if (ScreenComposite3.composite != null) {
					ScreenComposite3.composite.dispose();
					ScreenComposite3.composite = null;
				}
				if (ScreenComposite4.composite != null) {
					ScreenComposite4.composite.dispose();
					ScreenComposite4.composite = null;
				}
				shell.close();
			}
		});

		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		shell.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.layout();

		stackLayout = new StackLayout();
		composite.setLayout(stackLayout);
		ScreenComposite1.data = data;
		stackLayout.topControl = ScreenComposite1.getInstance(composite,
				SWT.None);
		ScreenComposite1.setText();
		composite.layout();

		if (data.get("total") != null
				&& data.get("total").toString().length() != 0) {
			thread = new MyThread(false);
			thread.start();
		}
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}

	public void refresh() {
		ScreenComposite1.data = data;
		stackLayout.topControl = ScreenComposite1.getInstance(composite,
				SWT.None);
		ScreenComposite1.setText();
		composite.layout();
		if (thread != null) {
			thread.setStop(true);
		}
		if (data.get("total") != null
				&& data.get("total").toString().length() != 0) {
			thread = new MyThread(false);
			thread.start();
		}
	}

	private class MyThread extends Thread {
		private int num = 1;
		private boolean isStop = false;

		public MyThread(boolean isStop) {
			this.isStop = isStop;
		}

		@Override
		public void run() {
			try {
				while (!isStop) {
					if (!shell.isDisposed()) {
						switch (num) {
						case 1: // 从第一屏切换到第二屏
							Thread.sleep(3000);
							if (!shell.isDisposed() && !isStop) {
								shell.getDisplay().asyncExec(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										ScreenComposite2.data = data;
										stackLayout.topControl = ScreenComposite2
												.getInstance(composite,
														SWT.None);
										ScreenComposite2.setText();
										composite.layout();
										num++;
									}
								});
							}
							break;
						case 2: // 从第二屏切换到第三屏
							Thread.sleep(3000);
							if (!shell.isDisposed() && !isStop) {
								shell.getDisplay().asyncExec(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										if (ScreenComposite3.composite == null) {
											ScreenComposite3.params = data;
											stackLayout.topControl = ScreenComposite3
													.getInstance(composite,
															SWT.None);
											ScreenComposite3.composite
													.setText();
										} else {
											ScreenComposite3.params = data;
											stackLayout.topControl = ScreenComposite3
													.getInstance(composite,
															SWT.None);
											ScreenComposite3.composite
													.setText();
										}
										composite.layout();
										num++;
									}
								});
							}
							break;
						case 3:
							Thread.sleep(10000);
							if (!shell.isDisposed() && !isStop) {
								shell.getDisplay().asyncExec(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										stackLayout.topControl = ScreenComposite4
												.getInstance(composite,
														SWT.None);
										ScreenComposite4
												.setText(
														data.get("matchName")
																.toString(),
														data.get("nextCategory")
																.toString(),
														data.get("nextTeam")
																.toString());
										composite.layout();
										num++;
									}
								});
							}
							break;
						}
						if (num > 3) {
							isStop = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean isStop() {
			return isStop;
		}

		public void setStop(boolean isStop) {
			this.isStop = isStop;
		}
	}

}
