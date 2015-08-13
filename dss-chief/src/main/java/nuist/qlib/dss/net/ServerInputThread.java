package nuist.qlib.dss.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.ScoreMessageVO;
import nuist.qlib.dss.ui.MatchPanel;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 类名：ServerInputThread 功能：建立主服务器的输入线程类，接收客户端的信息
 */

public class ServerInputThread implements Runnable {
	// tcp/ip协议
	private Socket socket;
	private Logger logger;
	private static final String regex = "\\d+";

	public ServerInputThread(Socket socket) {
		logger = Logger.getLogger(ServerInputThread.class.getName());
		this.socket = socket;
	}

	@Override
	public void run() {

		try {

			// 建立输入流
			InputStream is = socket.getInputStream();
			byte[] by = new byte[1024];
			// 将输入流里的字节读到字节数组里，并返回读的字节数
			int length = is.read(by);
			// 将字节数组里的length个字节转换为字符串
			if (length == -1) {
				return;
			}

			// 解析接收的信息
			String message = new String(by, 0, length, "utf-8");
			JSONObject jsonObject = JSONObject.fromObject(message);
			ScoreMessageVO scoreMessageVO = (ScoreMessageVO) JSONObject.toBean(
					jsonObject, ScoreMessageVO.class);

			// 角色类型
			RoleType roleType = scoreMessageVO.getRoleType();
			if (roleType == null) {
				return;
			}
			// 角色编号
			int roleNum;
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(roleType.getKeyWord());
			if (matcher.find()) {
				roleNum = Integer.parseInt(matcher.group());
			} else {
				return;
			}
			// 根据角色和角色编号显示得分
			if (roleType.isArtJudge()) {// 艺术打分
				switch (roleNum) {
				case 1:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getArt_score1().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 2:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getArt_score2().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 3:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getArt_score3().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 4:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getArt_score4().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				}
			} else if (roleType.isExecJudge()) {// 完成打分
				switch (roleNum) {
				case 1:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getexec_score1().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 2:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getexec_score2().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 3:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getexec_score3().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 4:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getexec_score4().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				}
			} else if (roleType.isImpJudge()) {// 印象打分
				switch (roleNum) {
				case 1:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getImp_score1().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				case 2:
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							MatchPanel.getImp_score2().setText(
									String.valueOf(scoreMessageVO.getScore()));
						}
					});
					break;
				}
			}
			is.close();
			socket.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}

/**
 * @ClassName: AdjustDialog
 * @author czf
 * 
 */
class AdjustDialog extends Dialog {
	private int index;

	public AdjustDialog(Shell shell, int index) {
		super(shell);
		this.index = index;
	}

	protected Point getInitialSize() {
		return new Point(100, 120);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return null;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		switch (index) {
		case 1: // 上调
			CLabel upScore = new CLabel(container, SWT.None);
			upScore.setBounds(30, 5, 80, 80);
			upScore.setImage(SWTResourceManager.getImage("img\\up.png"));
			break;
		case 2: // 下调
			CLabel downScore = new CLabel(container, SWT.None);
			downScore.setBounds(30, 5, 80, 80);
			downScore.setImage(SWTResourceManager.getImage("img\\down.png"));
			break;
		case 3: // 确认
			CLabel ok = new CLabel(container, SWT.None);
			ok.setBounds(30, 5, 80, 80);
			ok.setImage(SWTResourceManager.getImage("img\\ok.png"));
			break;
		}
		return container;
	}
}
