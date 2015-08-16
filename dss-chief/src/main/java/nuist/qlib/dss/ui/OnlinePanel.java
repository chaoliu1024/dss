/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import nuist.qlib.dss.constant.CommandType;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.dao.AddressManager;
import nuist.qlib.dss.dao.OnlineDao;
import nuist.qlib.dss.net.BroadcastIP;
import nuist.qlib.dss.net.MainClientOutputThread;
import nuist.qlib.dss.net.ReceIP;
import nuist.qlib.dss.net.vo.CommandMessageVO;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 
 * @author czf
 * @since dss 1.0
 */
public class OnlinePanel extends Composite {

	private static OnlinePanel panel;
	private ExpandBar bar;
	private ExpandItem artJudgeExpandItem;
	private ExpandItem execJudgeExpandItem;
	private ExpandItem impJudgeExpandItem;
	// private ExpandItem chiefJudgeExpandItem;
	private Composite composite_1;
	private Composite composite_2;
	private Composite composite_3;
	// private Composite composite_2;
	private Label artJudge01Label;
	private Label artJudge02Label;
	private Label artJudge03Label;
	private Label artJudge04Label;
	private Label execJudge01Label;
	private Label execJudge02Label;
	private Label execJudge03Label;
	private Label execJudge04Label;
	private Label impJudge01Label;
	private Label impJudge02Label;
	// private Label impJudge03Label;
	// private Label chiefJudge01Label;
	private Boolean isStop = true;
	private String[] numChars = { "01", "02", "03", "04" };
	private OnlineDao onlineDao;
	private AddressManager adressManager = new AddressManager();

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param shell
	 * @param parent
	 * @param style
	 */
	public OnlinePanel(final Shell shell, final Composite parent,
			final int style) {
		super(parent, style);
		// this.setBounds(0, 0, 133, 641);
		this.setLayout(new FillLayout());
		bar = new ExpandBar(this, SWT.V_SCROLL);
		bar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		// First item
		artJudgeExpandItem = new ExpandItem(bar, SWT.NONE, 0);
		artJudgeExpandItem.setExpanded(true);
		// second item
		execJudgeExpandItem = new ExpandItem(bar, SWT.NONE, 1);
		execJudgeExpandItem.setExpanded(true);
		// Third item
		impJudgeExpandItem = new ExpandItem(bar, SWT.NONE, 2);
		impJudgeExpandItem.setExpanded(true);
		// Second item
		// chiefJudgeExpandItem = new ExpandItem(bar, SWT.NONE, 1);
		// chiefJudgeExpandItem.setExpanded(true);

		bar.setSpacing(8);

		new Thread() {// 检测时间以便确定上线还是下线
			String artJudgeName = null;
			String execJudgeName = null;
			String impJudgeName = null;
			// String chiefJudgeName = null;
			int artJudgeNum = 0;
			int execJudgeNum = 0;
			int impJudgeNum = 0;
			// int chiefJudgeNum = 0;
			boolean isLink = BroadcastIP.getLink();// 历史网络连接状态
			boolean curLink = BroadcastIP.getLink();// 当前网络连接状态
			MouseListener mouseListener = new MouseListener() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					Label label = (Label) e.getSource();
					AdjustDialog mb = new AdjustDialog(shell, label.getText());
					mb.open();
				}

				@Override
				public void mouseDown(MouseEvent e) {

				}

				@Override
				public void mouseUp(MouseEvent e) {

				}

			};

			public void run() {
				while (isStop) {
					curLink = BroadcastIP.getLink();
					if (isLink != curLink) {// 网络连接状态发生变化
						if (!isLink) {// 变为联网状态,线程暂停10秒，等到各裁判发送IP
							try {
								Thread.sleep(10 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						isLink = curLink;
					}
					onlineDao = new OnlineDao();
					long currentTime = System.currentTimeMillis();
					long[] timer = ReceIP.getTimer();
					artJudgeNum = 0;
					execJudgeNum = 0;
					impJudgeNum = 0;
					int curOnlineArtJudgeNum = 0;
					int curOnlineExecJudgeNum = 0;
					int curOnlineImpJudgeNum = 0;
					int curOfflineArtJudgeNum = 0;
					int curOfflineExecJudgeNum = 0;
					int curOfflineImpJudgeNum = 0;
					int lineState[] = new int[timer.length];

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (composite_1 != null) {
								composite_1.dispose();
							}
							composite_1 = new Composite(bar, SWT.NONE);
							composite_1.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							GridLayout layout = new GridLayout();
							layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
							layout.verticalSpacing = 10;
							composite_1.setLayout(layout);
							artJudge01Label = new Label(composite_1, SWT.NONE);
							artJudge01Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							artJudge02Label = new Label(composite_1, SWT.NONE);
							artJudge02Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							artJudge03Label = new Label(composite_1, SWT.NONE);
							artJudge03Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							artJudge04Label = new Label(composite_1, SWT.NONE);
							artJudge04Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							if (composite_2 != null) {
								composite_2.dispose();
							}
							composite_2 = new Composite(bar, SWT.NONE);
							composite_2.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							GridLayout layout2 = new GridLayout();
							layout2.marginLeft = layout2.marginTop = layout2.marginRight = layout2.marginBottom = 10;
							layout2.verticalSpacing = 10;
							composite_2.setLayout(layout2);

							execJudge01Label = new Label(composite_2, SWT.NONE);
							execJudge01Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							execJudge02Label = new Label(composite_2, SWT.NONE);
							execJudge02Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							execJudge03Label = new Label(composite_2, SWT.NONE);
							execJudge03Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							execJudge04Label = new Label(composite_2, SWT.NONE);
							execJudge04Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));

							if (composite_3 != null) {
								composite_3.dispose();
							}
							composite_3 = new Composite(bar, SWT.NONE);
							composite_3.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							GridLayout layout3 = new GridLayout();
							layout3.marginLeft = layout3.marginTop = layout3.marginRight = layout3.marginBottom = 10;
							layout3.verticalSpacing = 10;
							composite_3.setLayout(layout3);

							impJudge01Label = new Label(composite_3, SWT.NONE);
							impJudge01Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							impJudge02Label = new Label(composite_3, SWT.NONE);
							impJudge02Label.setBackground(SWTResourceManager
									.getColor(SWT.COLOR_WHITE));
							// impJudge03Label = new Label(composite_3,
							// SWT.NONE);
							// impJudge03Label.setBackground(SWTResourceManager
							// .getColor(SWT.COLOR_WHITE));
						}
					});

					for (int i = 0; i < timer.length; i++) {

						if (currentTime - timer[i] <= 10 * 1000) {// 若相隔小于等于10s(IP发送时间的两倍)或不相等,则表示该角色在线
							if (i < 4) {
								artJudgeNum++; // 在线艺术裁判统计
							} else if (i < 8) {
								execJudgeNum++; // 在线完成裁判统计
							} else if (i < 10) {
								impJudgeNum++; // 在线印象裁判统计
							}
							lineState[i] = 1;
						} else {// 若相隔大于10s(IP发送时间的两倍)或相等,则表示该角色不在线
							lineState[i] = 0;
						}
					}
					// 根据统计情况设置label
					for (int j = 0; j < lineState.length; j++) {
						if (lineState[j] == 0) {// 不在线
							if (j < 4) {// 不在线的艺术裁判
								curOfflineArtJudgeNum++;
								switch (curOfflineArtJudgeNum + artJudgeNum) {
								case 1:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge01Label
															.setText(artJudgeName);
													artJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												artJudgeName, false);
									}
									break;
								case 2:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge02Label
															.setText(artJudgeName);
													artJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												artJudgeName, false);
									}
									break;
								case 3:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge03Label
															.setText(artJudgeName);
													artJudge03Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												artJudgeName, false);
									}
									break;
								case 4:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge04Label
															.setText(artJudgeName);
													artJudge04Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												artJudgeName, false);
									}
									break;
								default:
									break;
								}
							} else if (j < 8) {
								curOfflineExecJudgeNum++;
								switch (curOfflineExecJudgeNum + execJudgeNum) {
								case 1:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge01Label
															.setText(execJudgeName);
													execJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												execJudgeName, false);
									}
									break;
								case 2:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge02Label
															.setText(execJudgeName);
													execJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												execJudgeName, false);
									}
									break;
								case 3:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge03Label
															.setText(execJudgeName);
													execJudge03Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												execJudgeName, false);
									}
									break;
								case 4:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge04Label
															.setText(execJudgeName);
													execJudge04Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												execJudgeName, false);
									}
									break;
								default:
									break;
								}
							} else if (j < 10) {
								curOfflineImpJudgeNum++;
								switch (curOfflineImpJudgeNum + impJudgeNum) {
								case 1:
									impJudgeName = "总体评价裁判" + numChars[j - 8];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													impJudge01Label
															.setText(impJudgeName);
													impJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												impJudgeName, false);
									}
									break;
								case 2:
									impJudgeName = "总体评价裁判" + numChars[j - 8];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													impJudge02Label
															.setText(impJudgeName);
													impJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_GRAY));
												}
											});
									if (curLink) {
										onlineDao.updateLoginStatus(
												impJudgeName, false);
									}
									break;
								// case 3:
								// impJudgeName = "印象裁判" + numChars[j-6];
								// Display.getDefault().syncExec(
								// new Runnable() {
								// public void run() {
								// impJudge03Label
								// .setText(impJudgeName);
								// impJudge03Label
								// .setForeground(SWTResourceManager
								// .getColor(SWT.COLOR_GRAY));
								// }
								// });
								// if(curLink){
								// onlineDao.updateLoginStatus(impJudgeName,
								// false);
								// }
								// break;
								default:
									break;
								}
							}
						} else if (lineState[j] == 1) {// 在线
							// 在线
							if (j < 4) {// 在线的艺术裁判
								curOnlineArtJudgeNum++;
								switch (curOnlineArtJudgeNum) {
								case 1:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge01Label
															.setText(artJudgeName);
													artJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													artJudge01Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 2:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge02Label
															.setText(artJudgeName);
													artJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													artJudge02Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 3:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge03Label
															.setText(artJudgeName);
													artJudge03Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													artJudge03Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 4:
									artJudgeName = "艺术裁判" + numChars[j];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													artJudge04Label
															.setText(artJudgeName);
													artJudge04Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													artJudge04Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								default:
									break;
								}
							} else if (j < 8) {
								curOnlineExecJudgeNum++;
								switch (curOnlineExecJudgeNum) {
								case 1:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge01Label
															.setText(execJudgeName);
													execJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													execJudge01Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 2:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge02Label
															.setText(execJudgeName);
													execJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													execJudge02Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 3:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge03Label
															.setText(execJudgeName);
													execJudge03Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													execJudge03Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 4:
									execJudgeName = "完成裁判" + numChars[j - 4];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													execJudge04Label
															.setText(execJudgeName);
													execJudge04Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													execJudge04Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								default:
									break;
								}
							} else if (j < 10) {
								curOnlineImpJudgeNum++;
								switch (curOnlineImpJudgeNum) {
								case 1:
									impJudgeName = "总体评价裁判" + numChars[j - 8];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													impJudge01Label
															.setText(impJudgeName);
													impJudge01Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													impJudge01Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								case 2:
									impJudgeName = "总体评价裁判" + numChars[j - 8];
									Display.getDefault().syncExec(
											new Runnable() {
												public void run() {
													impJudge02Label
															.setText(impJudgeName);
													impJudge02Label
															.setForeground(SWTResourceManager
																	.getColor(SWT.COLOR_BLUE));
													impJudge02Label
															.addMouseListener(mouseListener);
												}
											});
									break;
								// case 3:
								// impJudgeName = "印象裁判" + numChars[j-6];
								// Display.getDefault().syncExec(
								// new Runnable() {
								// public void run() {
								// impJudge03Label
								// .setText(impJudgeName);
								// impJudge03Label
								// .setForeground(SWTResourceManager
								// .getColor(SWT.COLOR_BLUE));
								// impJudge03Label.addMouseListener(mouseListener);
								// }
								// });
								// break;
								default:
									break;
								}
							}
						}
					}
					onlineDao.close();// 关闭数据库连接
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							artJudgeExpandItem.setText("艺术裁判 【" + artJudgeNum
									+ "/4】");
							artJudgeExpandItem.setHeight(composite_1
									.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
							artJudgeExpandItem.setControl(composite_1);
							artJudgeExpandItem.setExpanded(artJudgeExpandItem
									.getExpanded());
							execJudgeExpandItem.setText("完成裁判 【" + execJudgeNum
									+ "/4】");
							execJudgeExpandItem.setHeight(composite_2
									.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
							execJudgeExpandItem.setControl(composite_2);
							execJudgeExpandItem.setExpanded(execJudgeExpandItem
									.getExpanded());
							impJudgeExpandItem.setText("舞步裁判 【" + impJudgeNum
									+ "/2】");
							impJudgeExpandItem.setHeight(composite_3
									.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
							impJudgeExpandItem.setControl(composite_3);
							impJudgeExpandItem.setExpanded(impJudgeExpandItem
									.getExpanded());
						}
					});

					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						isStop = false;
					}
				}
			}
		}.start();
	}

	/**
	 * @ClassName: AdjustDialog
	 * @Description: 调整分数
	 * @author czf
	 * @date 2014年5月2日 上午11:14:20
	 * 
	 */
	class AdjustDialog extends Dialog {
		private String role;
		private Shell shell;

		public AdjustDialog(Shell shell, String role) {
			super(shell);
			this.role = role;
			this.shell = shell;
		}

		protected Point getInitialSize() {
			return new Point(245, 145);
		}

		@Override
		protected Point getInitialLocation(Point initialSize) {

			Point location = new Point(760, 380);
			return location;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = new Composite(parent, SWT.NONE);
			Label label = new Label(container, SWT.NONE);
			label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
			label.setForeground(SWTResourceManager
					.getColor(SWT.COLOR_LIST_SELECTION));
			label.setBounds(86, 20, 82, 27);
			label.setText(role);
			Button upScore = new Button(container, SWT.ARROW | SWT.UP
					| SWT.BORDER);
			upScore.setBounds(10, 75, 52, 27);
			upScore.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
			upScore.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MainClientOutputThread mainClientOutputThread = new MainClientOutputThread();

					CommandMessageVO commandMessageVO = new CommandMessageVO();
					commandMessageVO.setRoleType(RoleType.praseName(role));
					commandMessageVO.setCommandType(CommandType.UP);

					int sum = mainClientOutputThread
							.sendCommand(commandMessageVO);
					if (sum == -1) {// 发送失败
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								adressManager.clearIP();
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("发送失败，请重发！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					} else if (sum == 0) {// 无ip
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("未获得接收地址，请稍等片刻！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					}
				}
			});
			Button downScore = new Button(container, SWT.ARROW | SWT.DOWN
					| SWT.BORDER);
			downScore.setBounds(96, 75, 52, 27);
			downScore.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MainClientOutputThread mainClientOutputThread = new MainClientOutputThread();

					CommandMessageVO commandMessageVO = new CommandMessageVO();
					commandMessageVO.setRoleType(RoleType.praseName(role));
					commandMessageVO.setCommandType(CommandType.DOWN);

					int sum = mainClientOutputThread
							.sendCommand(commandMessageVO);
					if (sum == -1) {// 发送失败
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								adressManager.clearIP();
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("发送失败，请重发！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					} else if (sum == 0) {// 无ip
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("未获得接收地址，请稍等片刻！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					}
				}
			});
			Button ok = new Button(container, SWT.BORDER);
			ok.setBounds(179, 75, 52, 27);
			ok.setText("确认");
			ok.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					MainClientOutputThread mainClientOutputThread = new MainClientOutputThread();

					CommandMessageVO commandMessageVO = new CommandMessageVO();
					commandMessageVO.setRoleType(RoleType.praseName(role));
					commandMessageVO.setCommandType(CommandType.OK);

					int sum = mainClientOutputThread
							.sendCommand(commandMessageVO);
					if (sum == -1) {// 发送失败
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								adressManager.clearIP();
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("发送失败，请重发！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					} else if (sum == 0) {// 无ip
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								MessageBox box = new MessageBox(shell, SWT.OK);
								box.setText("提示");
								box.setMessage("未获得接收地址，请稍等片刻！");
								int val = box.open();
								if (val == SWT.OK)
									return;
							}
						});
					}
					close();
				}
			});

			return container;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * @Title: getInstance
	 * @Description: 获取唯一实例
	 * @param shell
	 * @param parent
	 * @param style
	 * @return
	 */
	public static OnlinePanel getInstance(Shell shell, Composite parent,
			int style) {
		if (panel == null) {
			panel = new OnlinePanel(shell, parent, style);
		}
		return panel;
	}
}
