package nuist.qlib.dss.ui;

import java.io.IOException;
import java.net.MulticastSocket;

import nuist.qlib.dss.net.ReceIP;
import nuist.qlib.dss.thread.SplashThread;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class Splash {

	protected Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Splash window = new Splash();
			window.open();
		} catch (Exception e) {
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
		shell = new Shell();
		shell.setSize(341, 112);
		shell.setText("技巧竞赛评分系统");
		shell.setImage(new Image(display, Splash.class
				.getResourceAsStream("/img/logo.png")));
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				MessageBox messagebox = new MessageBox(shell, SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				messagebox.setText("提示");
				messagebox.setMessage("您确定关闭系统吗?");
				int message = messagebox.open();
				if (message == SWT.YES) {
					e.doit = true;
					System.exit(0);
				} else {
					e.doit = false;
				}
			}
		});
		center(shell);
		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.NORMAL));
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(100, -21);
		fd_label.right = new FormAttachment(100, -21);
		fd_label.top = new FormAttachment(0, 22);
		fd_label.left = new FormAttachment(0, 33);
		label.setLayoutData(fd_label);
		label.setBounds(50, 28, 244, 23);
		label.setText("正在与裁判长取得联系，请稍后...");
		shell.setLayout(new FormLayout());
		// 启动接收IP的线程
		MulticastSocket receSocket;
		try {			
			receSocket = new MulticastSocket(9999);
			new Thread(new ReceIP(receSocket)).start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		startThread();
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

	public void startThread() {
		SplashThread thread = new SplashThread(shell);
		new Thread(thread).start();
	}
}
