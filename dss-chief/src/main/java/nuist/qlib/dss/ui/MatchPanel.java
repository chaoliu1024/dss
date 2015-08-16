/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.ui;

import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.net.MainClientOutputThread;
import nuist.qlib.dss.net.vo.MatchInfoMessageVO;
import nuist.qlib.dss.scoreManager.QueryScore;
import nuist.qlib.dss.teamManager.MatchTeamScore;
import nuist.qlib.dss.util.CalcScore;
import nuist.qlib.dss.util.Validator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 打分界面
 * 
 * @author chao liu
 * @since dss 1.0
 */
public class MatchPanel extends Composite {

	private static MatchPanel panel;

	private Composite title_execosite;

	private Label match_num_label;
	private Combo match_num_combo;
	private Label title;

	private Group score_group;
	private Label art_score_label1;
	private static Text art_score1;
	private Label art_score_label2;
	private static Text art_score2;
	private Label art_score_label3;
	private static Text art_score3;
	private Label art_score_label4;
	private static Text art_score4;
	private Label art_total_score_label;
	private static Label art_total_score;

	private Label exec_score_label1;
	private static Text exec_score1;
	private Label exec_score_label2;
	private static Text exec_score2;
	private Label exec_score_label3;
	private static Text exec_score3;
	private static Label exec_score_label4;
	private static Text exec_score4;
	private Label exec_total_score_label;
	private Label exec_total_score;

	private Label imp_score_label1;
	private static Text imp_score1;
	private Label imp_score_label2;
	private static Text imp_score2;
	private Label imp_total_score_label;
	private Label imp_total_score;

	// 裁判长减分
	private Label deduction_label;
	private static Text deduction_score;
	// 总得分
	private Label total_label;
	private Label total_score;

	private String totalScore;
	private int i = 0;

	Label warningLabel;

	private Composite button_execosite;
	private Button calc_btn;
	private Button next_btn;
	private Button rank_btn;
	private Button resend_btn;
	private Button display_btn;
	private Button abstain_btn;

	@SuppressWarnings("unused")
	private Image icon;

	// team_name 参赛单位
	protected static String team;

	// matchCategory 比赛项目
	protected static String category;

	// 区分预赛和决赛, 预赛为0, 决赛为1
	protected static int matchType = -1;

	// 赛事名称
	protected static String matchName;

	protected static boolean isDisplay = false;

	// 场次
	protected static int match_num;

	// 队伍的出场顺序
	protected static int matchOrder = -1;

	// 队伍id
	protected static int id = -1;

	private Ds sd = null;

	private Shell ref_edit_shell;

	// 记录员发送信息接口
	private MainClientOutputThread mainClientOutputThread = new MainClientOutputThread();

	/**
	 * Create the execosite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MatchPanel(final Shell shell, final Composite parent, final int style) {
		super(parent, style);
		this.setBounds(0, 0, 1151, 669);
		ref_edit_shell = shell;

		title_execosite = new Composite(this, SWT.NONE);
		title_execosite.setBounds(0, 10, 986, 114);

		title = new Label(title_execosite, SWT.CENTER);
		title.setFont(SWTResourceManager.getFont("微软雅黑", 20, SWT.NORMAL));
		title.setBackground(SWTResourceManager.getColor(255, 192, 203));
		title.setBounds(0, 10, 986, 45);
		title.setText("参赛队伍");

		match_num_label = new Label(title_execosite, SWT.NONE);
		match_num_label.setText("场次：");
		match_num_label.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.NORMAL));
		match_num_label.setBounds(33, 70, 57, 29);

		match_num_combo = new Combo(title_execosite, SWT.NONE);
		match_num_combo.setFont(SWTResourceManager.getFont("微软雅黑", 12,
				SWT.NORMAL));
		match_num_combo.setBounds(112, 70, 64, 25);

		warningLabel = new Label(title_execosite, SWT.HORIZONTAL);
		warningLabel.setBounds(652, 80, 288, 27);
		warningLabel.setAlignment(SWT.CENTER);
		warningLabel.setFont(SWTResourceManager.getFont("仿宋", 9, SWT.BOLD));
		warningLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));

		score_group = new Group(this, SWT.NONE);
		score_group.setBounds(0, 130, 986, 270);

		art_score_label1 = new Label(score_group, SWT.NONE);
		art_score_label1.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		art_score_label1.setBounds(10, 38, 83, 28);
		art_score_label1.setText("艺术分一：");

		art_score1 = new Text(score_group, SWT.BORDER);
		art_score1.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		art_score1.setBounds(99, 38, 73, 28);

		art_score_label2 = new Label(score_group, SWT.NONE);
		art_score_label2.setText("艺术分二：");
		art_score_label2.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		art_score_label2.setBounds(209, 38, 83, 28);

		art_score2 = new Text(score_group, SWT.BORDER);
		art_score2.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		art_score2.setBounds(298, 38, 73, 28);

		art_score_label3 = new Label(score_group, SWT.NONE);
		art_score_label3.setText("艺术分三：");
		art_score_label3.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		art_score_label3.setBounds(410, 38, 83, 28);

		art_score3 = new Text(score_group, SWT.BORDER);
		art_score3.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		art_score3.setBounds(499, 38, 73, 28);

		art_score_label4 = new Label(score_group, SWT.NONE);
		art_score_label4.setText("艺术分四：");
		art_score_label4.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		art_score_label4.setBounds(610, 38, 83, 28);

		art_score4 = new Text(score_group, SWT.BORDER);
		art_score4.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		art_score4.setBounds(699, 38, 73, 28);

		art_total_score_label = new Label(score_group, SWT.NONE);
		art_total_score_label.setText("艺术分：");
		art_total_score_label.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.NORMAL));
		art_total_score_label.setBounds(809, 38, 72, 28);

		art_total_score = new Label(score_group, SWT.NONE);
		art_total_score.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_BLUE));
		art_total_score.setFont(SWTResourceManager
				.getFont("微软雅黑", 14, SWT.BOLD));
		art_total_score.setBounds(887, 38, 68, 28);

		exec_score_label1 = new Label(score_group, SWT.NONE);
		exec_score_label1.setText("完成分一：");
		exec_score_label1.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		exec_score_label1.setBounds(10, 104, 83, 28);

		exec_score1 = new Text(score_group, SWT.BORDER);
		exec_score1.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		exec_score1.setBounds(99, 104, 73, 28);

		exec_score_label2 = new Label(score_group, SWT.NONE);
		exec_score_label2.setText("完成分二：");
		exec_score_label2.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		exec_score_label2.setBounds(209, 104, 83, 28);

		exec_score2 = new Text(score_group, SWT.BORDER);
		exec_score2.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		exec_score2.setBounds(298, 104, 73, 28);

		exec_score_label3 = new Label(score_group, SWT.NONE);
		exec_score_label3.setText("完成分三：");
		exec_score_label3.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		exec_score_label3.setBounds(410, 104, 83, 28);

		exec_score3 = new Text(score_group, SWT.BORDER);
		exec_score3.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		exec_score3.setBounds(499, 104, 73, 28);

		exec_score_label4 = new Label(score_group, SWT.NONE);
		exec_score_label4.setText("完成分四：");
		exec_score_label4.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		exec_score_label4.setBounds(610, 104, 83, 28);

		exec_score4 = new Text(score_group, SWT.BORDER);
		exec_score4.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		exec_score4.setBounds(699, 104, 73, 28);

		exec_total_score_label = new Label(score_group, SWT.NONE);
		exec_total_score_label.setText("完成分：");
		exec_total_score_label.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.NORMAL));
		exec_total_score_label.setBounds(809, 104, 72, 28);

		exec_total_score = new Label(score_group, SWT.NONE);
		exec_total_score.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_BLUE));
		exec_total_score.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.BOLD));
		exec_total_score.setBounds(887, 104, 68, 28);

		imp_score_label1 = new Label(score_group, SWT.NONE);
		imp_score_label1.setText("舞步分一：");
		imp_score_label1.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		imp_score_label1.setBounds(10, 167, 83, 28);

		imp_score1 = new Text(score_group, SWT.BORDER);
		imp_score1.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		imp_score1.setBounds(99, 167, 73, 28);

		imp_score_label2 = new Label(score_group, SWT.NONE);
		imp_score_label2.setText("舞步分二：");
		imp_score_label2.setFont(SWTResourceManager.getFont("微软雅黑", 13,
				SWT.NORMAL));
		imp_score_label2.setBounds(212, 167, 83, 28);

		imp_score2 = new Text(score_group, SWT.BORDER);
		imp_score2.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.NORMAL));
		imp_score2.setBounds(298, 167, 73, 28);

		imp_total_score_label = new Label(score_group, SWT.NONE);
		imp_total_score_label.setText("舞步分：");
		imp_total_score_label.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.NORMAL));
		imp_total_score_label.setBounds(410, 165, 83, 28);

		imp_total_score = new Label(score_group, SWT.NONE);
		imp_total_score.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_BLUE));
		imp_total_score.setFont(SWTResourceManager
				.getFont("微软雅黑", 14, SWT.BOLD));
		imp_total_score.setBounds(499, 167, 73, 28);

		deduction_label = new Label(score_group, SWT.NONE);
		deduction_label.setText("裁判长减分：");
		deduction_label.setFont(SWTResourceManager.getFont("微软雅黑", 15,
				SWT.NORMAL));
		deduction_label.setBounds(10, 222, 120, 38);

		deduction_score = new Text(score_group, SWT.BORDER);
		deduction_score.setFont(SWTResourceManager.getFont("微软雅黑", 14,
				SWT.NORMAL));
		deduction_score.setBounds(139, 222, 73, 28);

		total_label = new Label(score_group, SWT.HORIZONTAL);
		total_label.setText("最后得分：");
		total_label.setFont(SWTResourceManager.getFont("微软雅黑", 16, SWT.NORMAL));
		total_label.setAlignment(SWT.CENTER);
		total_label.setBounds(281, 222, 110, 38);
		total_label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));

		total_score = new Label(score_group, SWT.HORIZONTAL);
		total_score.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		total_score.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.BOLD));
		total_score.setAlignment(SWT.CENTER);
		total_score.setBounds(397, 223, 68, 28);

		button_execosite = new Composite(this, SWT.NONE);
		button_execosite.setBounds(23, 430, 963, 126);

		calc_btn = new Button(button_execosite, SWT.NONE);
		calc_btn.setEnabled(false);
		calc_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		calc_btn.setBounds(167, 18, 134, 37);
		calc_btn.setText("计算总得分");

		next_btn = new Button(button_execosite, SWT.NONE);
		next_btn.setEnabled(false);
		next_btn.setText("下一只队伍");
		next_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		next_btn.setBounds(395, 18, 134, 37);

		rank_btn = new Button(button_execosite, SWT.NONE);
		rank_btn.setText("排名");
		rank_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		rank_btn.setBounds(638, 18, 134, 37);

		resend_btn = new Button(button_execosite, SWT.NONE);
		resend_btn.setText("重发");
		resend_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		resend_btn.setBounds(512, 83, 134, 37);

		display_btn = new Button(button_execosite, SWT.NONE);
		display_btn.setText("投影");
		display_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		display_btn.setBounds(296, 83, 134, 37);

		Button choise = new Button(button_execosite, SWT.NONE);
		choise.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		choise.setBounds(54, 82, 132, 38);
		choise.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MatchTeamScore query = new MatchTeamScore();
				// 赛事名称
				String[] matchNames = query.getMatchName();
				if (matchNames != null) {
					ChooseMatchName dialog = new ChooseMatchName(ref_edit_shell);
					dialog.setMatchNames(matchNames);
					if (matchName != null && matchName.trim().length() != 0) {
						dialog.setMatchName(matchName);
					}
					dialog.open();
					matchName = dialog.getMatchName();
					if (matchName == null) {
						MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
						box.setText("提示");
						box.setMessage("请选择赛事！");
						box.open();
					} else
						// 场次
						match_num = query.getInitMatchNum(matchName);
				} else {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("无比赛进行！");
					box.open();
				}
				query.close();
			}
		});
		choise.setText("选择赛事");

		abstain_btn = new Button(button_execosite, SWT.NONE);
		abstain_btn.setEnabled(false);
		abstain_btn.setText("弃权");
		abstain_btn.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		abstain_btn.setBounds(728, 83, 134, 37);

		icon = SWTResourceManager.getImage("img/icon.png");

		display_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String nextCategory = "";
				String nextTeam = "";
				if (matchName == null || matchName.trim().length() == 0) {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("无比赛进行！");
					box.open();
					return;
				} else if (match_num_combo.getText() == null
						|| match_num_combo.getText().equals("")) {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("请选择比赛场次！");
					box.open();
					return;
				} else {
					MatchTeamScore query = new MatchTeamScore();
					List<HashMap<String, Object>> data = query.getNextTeam(
							(matchOrder + 1), match_num, matchName);
					if (data.size() != 0) {
						// 比赛项目
						nextCategory = data.get(0).get("category").toString();
						// 参赛单位
						nextTeam = data.get(0).get("teamName").toString();
					} else {
						data = query.getNextTeam(1, (match_num + 1), matchName);
						if (data.size() != 0) {
							// 比赛项目
							nextCategory = data.get(0).get("category")
									.toString();
							// 参赛单位
							nextTeam = data.get(0).get("teamName").toString();
						}
					}

				}
				if (isDisplay == false) {
					sd = openScreenDisplay(nextCategory, nextTeam);
					isDisplay = true;
					sd.open();
				} else {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("artScore", art_total_score.getText());
					data.put("execScore", exec_total_score.getText());
					data.put("impScore", imp_total_score.getText());
					data.put("chifSubScore", deduction_score.getText());
					data.put("total", total_score.getText());
					data.put("teamName", team);
					data.put("category", category);
					data.put("matchType", matchType);
					data.put("matchName", matchName);
					data.put("nextCategory", nextCategory);
					data.put("nextTeam", nextTeam);
					sd.setData(data);
					sd.refresh();
				}
			}
		});

		InitData();
		matchNumComboEvent();

		calcButton();
		rankButton();
		nextTeamButton();
		resendButton();
		abstainButton();
	}

	/**
	 * 打开大屏显示窗口
	 * 
	 * @author liuchao
	 * @version 2014-6-14 下午8:44:58
	 */
	public Ds openScreenDisplay(String nextCategory, String nextTeam) {
		Ds sd = new Ds();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("artScore", art_total_score.getText());
		data.put("execScore", exec_total_score.getText());
		data.put("impScore", imp_total_score.getText());
		data.put("chifSubScore", deduction_score.getText());
		data.put("total", total_score.getText());
		data.put("teamName", team);
		data.put("category", category);
		data.put("matchType", matchType);
		data.put("nextCategory", nextCategory);
		data.put("nextTeam", nextTeam);
		if (matchName == null || matchName.trim().length() == 0) {
			data.put("matchName", "比赛即将开始");
		} else {
			data.put("matchName", matchName);
		}
		sd.setData(data);
		return sd;
	}

	/**
	 * 弃权比赛
	 */
	public void abstainButton() {

		abstain_btn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// 清空列表内容
				cleanScore();
				// hasScore = false;
				// reSelect = false;
				// rePause = false;
				// reGiveUp = true;
				// combo = false;
				MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
				box.setText("提示");
				if (match_num_combo.getText() == null
						|| match_num_combo.getText().equals("")) {
					box.setMessage("请选择场次");
					box.open();
				} else {
					MatchTeamScore query = new MatchTeamScore();
					if (query.isCollected()) {
						if (id != 0) {
							if (query.updateTeamStatu(id, 3) == 0) {
								box.setMessage("更改队伍状态失败！");
								box.open();
							} else {
								title.setText("该参赛队伍弃权比赛......");
							}
						} else if (query.updateTeamStatu(id, 3) == 0) {
							box.setMessage("更改队伍状态失败！");
							box.open();
						} else {
							title.setText("该参赛队伍弃权比赛......");
						}
					} else {
						box.setMessage("数据库连接失败！！");
						box.open();
					}
					query.close();
				}
				next_btn.setEnabled(true);
				calc_btn.setEnabled(false);
				abstain_btn.setEnabled(false);
			}
		});
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 上午10:04:33
	 * @Description 初始化数据及第一个队伍信息
	 */
	public void InitData() {

		MatchTeamScore query = new MatchTeamScore();
		String[] matchNames = query.getMatchName(); // 赛事名称
		if (matchNames == null) {
			MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
			box.setText("提示");
			box.setMessage("无比赛进行！");
			box.open();
		}
		query.close();
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午4:26:09
	 * @Description 设置未进行的比赛场次
	 */
	public void matchNumComboEvent() {
		match_num_combo.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				match_num_combo.removeAll();
				if (matchName != null && matchName.trim().length() != 0) {
					MatchTeamScore query = new MatchTeamScore();
					// 获得未进行比赛的场次
					List<String> matchNum = query.getMatchNum(matchName);
					for (int i = 0; i < matchNum.size(); i++) {
						match_num_combo.add(matchNum.get(i));
					}
					query.close();
				} else {
					if (i == 0) {
						art_score1.setFocus();
						art_score1.forceFocus();
						match_num_combo.clearSelection();
						i++;
						MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
						box.setText("提示");
						box.setMessage("无比赛进行！");
						box.open();
					}
					return;
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (i != 0) {
					i = 0;
				}
			}
		});

		match_num_combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				if (matchName == null || matchName.trim().length() == 0) {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("无比赛进行！");
					box.open();
					return;
				}

				MatchTeamScore query = new MatchTeamScore();
				if (match_num_combo.getSelectionIndex() == -1) {
					return;
				}
				// 清除预赛、决赛信息
				if (matchOrder > 0) {
					matchOrder = -1;
				}

				match_num = Integer.parseInt(match_num_combo
						.getItem(match_num_combo.getSelectionIndex()));

				cleanScore();

				calc_btn.setEnabled(true);
				next_btn.setEnabled(false);

				if (query.isCollected()) {
					List<HashMap<String, Object>> data = query.getInitTeam(
							match_num, matchName);
					if (data.size() != 0) {
						category = data.get(0).get("category").toString(); // 比赛项目
						team = data.get(0).get("teamName").toString(); // 参赛单位
						matchName = data.get(0).get("matchName").toString(); // 赛事名称
						matchOrder = Integer.valueOf(data.get(0)
								.get("matchOrder").toString()); // 出场顺序
						// 决赛或预赛
						if (data.get(0).get("final").toString().equals("false"))
							matchType = 0;
						if (data.get(0).get("final").toString().equals("true"))
							matchType = 1;
						id = Integer.valueOf(data.get(0).get("id").toString()); // 队伍id
						title.setText(team + "--" + category);

						/********************************** 发送队伍信息 ***********************************/
						new Thread() {
							public void run() {
								MatchInfoMessageVO matchInfoMessageVO = new MatchInfoMessageVO();
								matchInfoMessageVO.setMatchCategory(category);
								matchInfoMessageVO.setMatchName(matchName);
								matchInfoMessageVO.setMatchUnit(team);

								int sum = mainClientOutputThread
										.sendTeam(matchInfoMessageVO);
								if (sum == -1) {// 发送失败
									sendFailWaing();
								} else if (sum == 0) {// 无ip
									noIPWaring();
								}
							}
						}.start();
					} else {
						cleanScore(); // 清楚界面分数
						title.setText("参赛队伍");
						MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
						box.setText("提示");
						box.setMessage("无参赛队伍信息");
						box.open();
					}
				} else {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("数据库连接失败");
					box.open();
				}
				query.close();
				art_score1.setFocus();
			}
		});
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午4:28:28
	 * @Description 计算总成绩
	 */
	public void calcButton() {

		calc_btn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Validator validator = new Validator();

				HashMap<String, String> scores = getHashJudgeScore(
						art_score1.getText(), art_score2.getText(),
						art_score3.getText(), art_score4.getText(),
						exec_score1.getText(), exec_score2.getText(),
						exec_score3.getText(), exec_score4.getText(),
						imp_score1.getText(), imp_score2.getText(),
						deduction_score.getText());

				// 成绩验证合法
				if (validator.scoreValidator(ref_edit_shell, scores)) {
					MatchTeamScore query = new MatchTeamScore();
					CalcScore cs = new CalcScore();

					// 裁判长减分
					String sub_score = "0";
					if (scores.get("dedu") != null
							&& !scores.get("dedu").equals("")) {
						sub_score = scores.get("dedu");
					}

					HashMap<String, Double> eachFinalScore = cs
							.getHashJudgeScore(scores);

					// 艺术总分
					art_total_score.setText(eachFinalScore.get("artFinal")
							.toString());

					// 完成总分
					exec_total_score.setText(eachFinalScore.get("execFinal")
							.toString());
					// 舞步总分
					imp_total_score.setText(eachFinalScore.get("impFinal")
							.toString());

					// 最后总得分
					total_score.setText(eachFinalScore.get("totalScore")
							.toString());

					// 计算误差
					List<Double> deviation = cs.calcDeviations(
							art_score1.getText(), art_score2.getText(),
							art_score3.getText(), art_score4.getText(),
							exec_score1.getText(), exec_score2.getText(),
							exec_score3.getText(), exec_score4.getText(),
							imp_score1.getText(), imp_score2.getText());
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");

					// 数据库连接正常
					if (query.isCollected()) {
						// 总分大于0
						if (Float.valueOf(total_score.getText()) > 0) {
							if (match_num_combo.getText() != null
									&& !match_num_combo.getText().equals("")
									&& matchOrder > 0) {
								if (query.insertScore(id, art_score1.getText(),
										art_score2.getText(),
										art_score3.getText(),
										art_score4.getText(),
										art_total_score.getText(),
										exec_score1.getText(),
										exec_score2.getText(),
										exec_score3.getText(),
										exec_score4.getText(),
										exec_total_score.getText(),
										imp_score1.getText(),
										imp_score2.getText(),
										imp_total_score.getText(), sub_score,
										total_score.getText(),
										deviation.get(0), deviation.get(1),
										deviation.get(2), deviation.get(3),
										deviation.get(4), deviation.get(5),
										deviation.get(6), deviation.get(7),
										deviation.get(8), deviation.get(9)) == 1) {
									next_btn.setEnabled(true);
								} else {
									box.setMessage("录入比赛成绩失败");
									box.open();
								}
								if (query.updateTeamStatu(id, 1) == -1) {
									box.setMessage("更改队伍状态失败");
									box.open();
								}
							} else if (match_num_combo.getText() != null
									&& !match_num_combo.getText().equals("")
									&& matchOrder == -1) {
								box.setMessage("无参赛队伍信息");
								box.open();
							} else {
								box.setMessage("请选择比赛场次");
								box.open();
							}

						} else if (Float.valueOf(total_score.getText()) < 0) {
							box.setMessage("该队伍总成绩小于零分，有误！");
							box.open();
						} else {
							// 总得分等于0
							if (query.updateTeamStatu(id, 1) == -1) {
								box.setMessage("更改队伍状态失败");
								box.open();
							}
							next_btn.setEnabled(false);
							abstain_btn.setEnabled(true);
						}
					} else {
						box.setMessage("数据库连接失败");
						box.open();
					}
				}
			}
		});
	}

	/**
	 * Package 11 judge's scores to HashMap
	 * 
	 * @param art_score1
	 *            score of art judge1
	 * @param art_score2
	 *            score of art judge2
	 * @param art_score3
	 *            score of art judge3
	 * @param art_score4
	 *            score of art judge4
	 * @param exec_score1
	 *            score of execution judge1
	 * @param exec_score2
	 *            score of execution judge2
	 * @param exec_score3
	 *            score of execution judge3
	 * @param exec_score4
	 *            score of execution judge4
	 * @param imp_score1
	 *            score of implementation judge1
	 * @param imp_score2
	 *            score of implementation judge2
	 * @param deduction_score
	 *            sub score of chief
	 * @return HashMap score of every judge
	 * @since DSS 1.0
	 */
	public HashMap<String, String> getHashJudgeScore(String art_score1,
			String art_score2, String art_score3, String art_score4,
			String exec_score1, String exec_score2, String exec_score3,
			String exec_score4, String imp_score1, String imp_score2,
			String deduction_score) {

		HashMap<String, String> scores = new HashMap<String, String>();
		scores.put("art1", art_score1);
		scores.put("art2", art_score2);
		scores.put("art3", art_score3);
		scores.put("art4", art_score4);

		scores.put("exec1", exec_score1);
		scores.put("exec2", exec_score2);
		scores.put("exec3", exec_score3);
		scores.put("exec4", exec_score4);

		scores.put("imp1", imp_score1);
		scores.put("imp2", imp_score2);

		scores.put("dedu", deduction_score);
		return scores;
	}

	/**
	 * 下一只参赛队伍
	 * 
	 * @author liuchao
	 * @version 2014-4-1 上午12:17:01
	 * 
	 * @throws
	 */
	public void nextTeamButton() {
		next_btn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (matchName == null || matchName.trim().length() == 0) {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("无比赛进行！");
					box.open();
					return;
				}

				cleanScore();
				calc_btn.setEnabled(true);

				if (match_num_combo.getText() == null
						|| match_num_combo.getText().equals("")) {
					MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
					box.setText("提示");
					box.setMessage("请选择比赛场次!");
					box.open();
				} else {
					matchOrder++;
					MatchTeamScore query = new MatchTeamScore();
					if (query.isCollected()) {
						List<HashMap<String, Object>> data = query.getNextTeam(
								matchOrder, match_num, matchName);
						if (data.size() != 0) {
							id = Integer.valueOf(data.get(0).get("id")
									.toString());
							matchOrder = Integer.parseInt(data.get(0)
									.get("matchOrder").toString());
							// 比赛项目
							category = data.get(0).get("category").toString();
							// 参赛单位
							team = data.get(0).get("teamName").toString();
							// 赛事名称
							matchName = data.get(0).get("matchName").toString();
							// 决赛或预赛
							if (data.get(0).get("final").toString()
									.equals("false"))
								matchType = 0;
							if (data.get(0).get("final").toString()
									.equals("true"))
								matchType = 1;
							title.setText(team + "--" + category);
							/*********************** 发送队伍信息给裁判长和打分裁判 ******************************/
							new Thread() {
								public void run() {
									MatchInfoMessageVO matchInfoMessageVO = new MatchInfoMessageVO();
									matchInfoMessageVO
											.setMatchCategory(category);
									matchInfoMessageVO.setMatchName(matchName);
									matchInfoMessageVO.setMatchUnit(team);

									int sum = mainClientOutputThread
											.sendTeam(matchInfoMessageVO);
									if (sum == -1) {
										sendFailWaing();
									} else if (sum == 0) {// 无ip
										noIPWaring();
									}
								}
							}.start();
						} else {
							matchOrder--; // 最后一只队伍,id减1
							MessageBox box = new MessageBox(ref_edit_shell,
									SWT.OK);
							box.setText("提示");
							box.setMessage("第" + match_num + "场比赛已结束");
							box.open();
						}
					} else {
						MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
						box.setText("提示");
						box.setMessage("数据库连接失败");
						box.open();
					}
					query.close();
				}
				next_btn.setEnabled(false);
			}
		});
	}

	/**
	 * 发送失败时重新发送按钮
	 * 
	 * @author liuchao
	 */
	public void resendButton() {
		resend_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					public void run() {
						totalScore = null;
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								totalScore = total_score.getText();
							}
						});
						if (totalScore == null || totalScore.equals("")) {
							MatchInfoMessageVO matchInfoMessageVO = new MatchInfoMessageVO();
							matchInfoMessageVO.setMatchCategory(category);
							matchInfoMessageVO.setMatchName(matchName);
							matchInfoMessageVO.setMatchUnit(team);

							int sum = mainClientOutputThread
									.sendTeam(matchInfoMessageVO);
							if (sum == -1) {
								sendFailWaing();
							} else if (sum == 0) {// 无ip
								noIPWaring();
							}
						}
						// else {
						// artScore1 = null;
						// artScore2 = null;
						// artScore3 = null;
						// totalArtScore = null;
						// execScore1 = null;
						// execScore2 = null;
						// execScore3 = null;
						// totalExecScore = null;
						// impScore1 = null;
						// impScore1 = null;
						// impScore1 = null;
						// totalImpScore = null;
						// deduction = null;
						// totalScore = null;
						// Display.getDefault().syncExec(
						// new Runnable() {
						// public void run() {
						// artScore1 = art_score1.getText();
						// artScore2 = art_score2.getText();
						// artScore3 = art_score3.getText();
						// totalArtScore = art_total_score.getText();
						// execScore1 = exec_score1.getText();
						// execScore2 = exec_score2.getText();
						// execScore3 = exec_score3.getText();
						// totalExecScore = exec_total_score.getText();
						// impScore1 = imp_score1.getText();
						// impScore2 = imp_score2.getText();
						// impScore3 = imp_score3.getText();
						// totalImpScore = imp_total_score.getText();
						// deduction = deduction_score.getText();
						// totalScore = total_score.getText();
						// }
						// });
						// int sum = mainClientOutputThread.sendScore(
						// scoreReceiver,
						// artScore1,
						// artScore2,
						// artScore3,
						// totalArtScore,
						// execScore1,
						// execScore2,
						// execScore3,
						// totalExecScore,
						// impScore1,
						// impScore2,
						// impScore3,
						// totalImpScore,
						// deduction,
						// totalScore);
						// if (sum == -1) {// 发送失败
						// addressManager.clearIP();
						// Display.getDefault().syncExec(new Runnable() {
						// public void run() {
						// MessageBox box1 = new MessageBox(
						// ref_edit_shell, SWT.OK);
						// box1.setText("提示");
						// box1.setMessage("发送失败，请重发");
						// int val = box1.open();
						// if (val == SWT.OK)
						// return;
						// }
						// });
						// } else if (sum == 0) {// 无ip
						// Display.getDefault().syncExec(
						// new Runnable() {
						// public void run() {
						// MessageBox box = new MessageBox(
						// ref_edit_shell,
						// SWT.OK);
						// box.setText("提示");
						// box.setMessage("未获得接收地址，请稍等片刻！");
						// int val = box.open();
						// if (val == SWT.OK)
						// return;
						// }
						// });
						// }
						// }
					}
				}.start();
			}
		});
	}

	@Override
	protected void checkSubclass() {
	}

	/**
	 * 
	 * 实例化对象的唯一接口
	 * 
	 * @author liuchao
	 * @param shell
	 * @param parent
	 * @param style
	 * @return MatchPanel
	 * @throws
	 */
	public static MatchPanel getInstance(Shell shell, Composite parent,
			int style) {
		if (panel == null) {
			panel = new MatchPanel(shell, parent, style);
		}
		return panel;
	}

	/**
	 * 清楚界面上的分数
	 * 
	 * @author liuchao
	 */
	public void cleanScore() {
		title.setText("");
		art_score1.setText("");
		art_score2.setText("");
		art_score3.setText("");
		art_score4.setText("");
		art_total_score.setText("");
		exec_score1.setText("");
		exec_score2.setText("");
		exec_score3.setText("");
		exec_score4.setText("");
		exec_total_score.setText("");
		imp_score1.setText("");
		imp_score2.setText("");
		imp_total_score.setText("");
		deduction_score.setText("");
		total_score.setText("");
	}

	/**
	 * 排名按钮触发事件
	 * 
	 * @author wangfang
	 */
	public void rankButton() {

		rank_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				QueryScore query = new QueryScore();

				MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
				box.setText("提示");
				// if (matchOrder < 1||matchName.equals("noMatch")) {
				if (query.isCollected()) {
					int matchType = -1;
					String matchName_temp;
					// 当matchName不为空时，跳出选择界面，让用户选择赛事模式
					if (query.getAllMatchNames().length == 0) {
						box.setMessage("无有成绩赛事");
						box.open();
						return;
					}
					MatchNameDialog dialog = new MatchNameDialog(ref_edit_shell);
					if (matchName != null && !matchName.equals("")
							&& !matchName.equals("noMatch")) {
						dialog.setMatchName(matchName);
					}
					dialog.setMatchNames(query.getAllMatchNames());
					dialog.open();
					matchName_temp = dialog.getMatchName();
					matchType = dialog.getMatchType();
					if (matchName_temp != null && !matchName_temp.equals("")
							&& matchType != -1) {
						int temp = query
								.getTeamMinId(matchType, matchName_temp);
						switch (temp) {
						case -1: {
							box.setMessage("暂无成绩");
							box.open();
							break;
						}
						default:
							RankPanel window = new RankPanel();
							window.id = temp;
							window.matchType = matchType;
							window.matchName = matchName_temp;
							window.open();
						}
					}
					if (query.isCollected()) {
						query.close();
					}
				} else {
					box.setMessage("连接数据库失败");
					box.open();
				}
				// } else {
				// int temp;
				// if (match_num == -1) {
				// box.setMessage("请选择场次");
				// box.open();
				// } else if (matchName == null || matchName.equals("")) {
				// box.setMessage("赛事名称不能为空，程序错误");
				// box.open();
				// } else if (matchType == -1) {
				// box.setMessage("赛事模式不能为空，程序错误");
				// box.open();
				// } else {
				// temp = query.getTeamMinId(matchType, matchName, match_num,
				// matchOrder);
				// RankPanel window = new RankPanel();
				// window.id = temp;
				// window.matchType = matchType;
				// window.matchName = matchName;
				// window.open();
				// }
				// }
			}
		});
	}

	private void noIPWaring() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				warningLabel.setText("未获得接收地址,请稍等");
			}
		});
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				warningLabel.setText("");
			}
		});
	}

	private void sendFailWaing() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
				box.setText("提示");
				box.setMessage("发送失败，请重发");
				int val = box.open();
				if (val == SWT.OK)
					return;
			}
		});
	}

	public static void setDeduction_score(Text deduction_score) {
		MatchPanel.deduction_score = deduction_score;
	}

	public static Text getArt_score1() {
		return art_score1;
	}

	public static void setArt_score1(Text art_score1) {
		MatchPanel.art_score1 = art_score1;
	}

	public static Text getArt_score2() {
		return art_score2;
	}

	public static void setArt_score2(Text art_score2) {
		MatchPanel.art_score2 = art_score2;
	}

	public static Text getArt_score3() {
		return art_score3;
	}

	public static void setArt_score3(Text art_score3) {
		MatchPanel.art_score3 = art_score3;
	}

	public static Text getArt_score4() {
		return art_score4;
	}

	public static void setArt_score4(Text art_score4) {
		MatchPanel.art_score4 = art_score4;
	}

	public Label getArt_total_score() {
		return art_total_score;
	}

	public static Text getexec_score1() {
		return exec_score1;
	}

	public static void setexec_score1(Text exec_score1) {
		MatchPanel.exec_score1 = exec_score1;
	}

	public static Text getexec_score2() {
		return exec_score2;
	}

	public static void setexec_score2(Text exec_score2) {
		MatchPanel.exec_score2 = exec_score2;
	}

	public static Text getexec_score3() {
		return exec_score3;
	}

	public static void setexec_score3(Text exec_score3) {
		MatchPanel.exec_score3 = exec_score3;
	}

	public static Text getexec_score4() {
		return exec_score4;
	}

	public static void setexec_score4(Text exec_score4) {
		MatchPanel.exec_score4 = exec_score4;
	}

	public Label getexec_total_score() {
		return exec_total_score;
	}

	public static Text getImp_score1() {
		return imp_score1;
	}

	public static void setImp_score1(Text imp_score1) {
		MatchPanel.imp_score1 = imp_score1;
	}

	public static Text getImp_score2() {
		return imp_score2;
	}

	public static void setImp_score2(Text imp_score2) {
		MatchPanel.imp_score2 = imp_score2;
	}

	public Label getImp_total_score() {
		return imp_total_score;
	}

	public Label getTotal_score() {
		return total_score;
	}
}
