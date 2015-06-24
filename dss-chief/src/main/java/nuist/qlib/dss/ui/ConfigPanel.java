/*
 * 文件名：ConfigPanel.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：配置裁判长名字等配置信息
 */
package nuist.qlib.dss.ui;

import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.config.Config;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConfigPanel extends Composite {

	private static ConfigPanel panel;
	protected Shell shell;
	private Text intercessorText;
	private Text viceRefereeText;
	private Text refreeText;
	private Text locationText;
	private Config config;
	private List<HashMap<String, Object>> data;

	public ConfigPanel(final Shell shell, final Composite parent,
			final int style) {
		super(parent, style);
		this.setBounds(0, 0, 970, 663);
		setLayout(null);
		config = new Config();
		this.shell = shell;

		Group group_2 = new Group(this, SWT.NONE);
		group_2.setText("地点配置");
		group_2.setBounds(10, 79, 937, 320);

		// Group group = new Group(group_2, SWT.NONE);
		// group.setText("裁判长");
		// group.setBounds(59, 34, 861, 93);
		//
		// Label label = new Label(group, SWT.NONE);
		// label.setBounds(25, 48, 55, 15);
		// label.setText("仲裁主任");
		//
		// intercessorText = new Text(group, SWT.BORDER);
		// intercessorText.setBounds(86, 48, 73, 21);
		//
		// Label label_1 = new Label(group, SWT.NONE);
		// label_1.setBounds(191, 48, 55, 15);
		// label_1.setText("副裁判长");
		//
		// viceRefereeText = new Text(group, SWT.BORDER);
		// viceRefereeText.setBounds(252, 48, 73, 21);
		//
		// Label label_2 = new Label(group, SWT.NONE);
		// label_2.setBounds(377, 48, 55, 15);
		// label_2.setText("总裁判长");
		//
		// refreeText = new Text(group, SWT.BORDER);
		// refreeText.setBounds(438, 48, 73, 21);

		Group group_1 = new Group(group_2, SWT.NONE);
		group_1.setText("比赛地点配置");
		group_1.setBounds(36, 69, 861, 93);

		Label label_3 = new Label(group_1, SWT.NONE);
		label_3.setBounds(40, 52, 55, 15);
		label_3.setText("参赛地点");

		locationText = new Text(group_1, SWT.BORDER);
		locationText.setBounds(113, 46, 73, 21);

		Button submit = new Button(group_2, SWT.NONE);
		submit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String mention = "";
				// String intercessor=intercessorText.getText();
				boolean mark = false;
				// if(intercessor==null||intercessor.equals("")){
				// mention+="未填写仲裁主任的名字\n";
				// }
				// String viceReferee=viceRefereeText.getText();
				// if(viceReferee==null||viceReferee.equals("")){
				// mention+="未填写副裁判长的名字\n";
				// }
				// String refree=refreeText.getText();
				// if(refree==null||refree.equals("")){
				// mention+="未填写总裁判长的名字\n";
				// }
				String location = locationText.getText();
				if (location == null || location.equals("")) {
					mention += "未填写比赛地点的名字\n";
				}
				if (mention.equals("")) {
					mark = true;
				} else {
					MessageBox box = new MessageBox(shell, SWT.OK | SWT.CANCEL);
					box.setText("提示");
					box.setMessage("存在以下问题是否继续??\n" + mention);
					if (box.open() == SWT.OK) {
						mark = true;
					}
				}
				if (mark) {
					// if(intercessor==null||intercessor.equals("")){
					// data.get(0).put("name", "");
					// }else{
					// data.get(0).put("name", intercessor);
					// }
					// if(viceReferee==null||viceReferee.equals("")){
					// data.get(1).put("name", "");
					// }else{
					// data.get(1).put("name", viceReferee);
					// }
					// if(refree==null||refree.equals("")){
					// data.get(2).put("name", "");
					// }else{
					// data.get(2).put("name", refree);
					// }
					if (location == null || location.equals("")) {
						data.get(0).put("location", "");
						data.get(1).put("location", "");
						data.get(2).put("location", "");
					} else {
						data.get(0).put("location", location);
						data.get(1).put("location", location);
						data.get(2).put("location", location);
					}
					MessageBox box = new MessageBox(shell, SWT.OK);
					box.setText("提示");
					if (config.updateParams(data) > -1) {
						box.setMessage("修改成功");
					} else {
						box.setMessage("修改失败");
					}
					box.open();
				}
			}
		});
		submit.setBounds(823, 275, 75, 25);
		submit.setText("确定");

		// 初始化数据
		if (config.isCollected()) {
			data = config.getParams();
			// if(data.get(0).get("name")!=null){
			// intercessorText.setText(data.get(0).get("name").toString());
			// }
			// if(data.get(1).get("name")!=null){
			// viceRefereeText.setText(data.get(1).get("name").toString());
			// }
			// if(data.get(2).get("name")!=null){
			// refreeText.setText(data.get(2).get("name").toString());
			// }
			if (data.get(2).get("location") != null) {
				locationText.setText(data.get(2).get("location").toString());
			}
		} else {
			MessageBox box = new MessageBox(this.shell, SWT.OK);
			box.setText("提示");
			box.setMessage("数据库连接失败");
			box.open();
		}
	}

	public void initParams() {

	}

	public static ConfigPanel getInstance(Shell shell, Composite parent,
			int style) {
		if (panel == null) {
			panel = new ConfigPanel(shell, parent, style);
		}
		return panel;
	}
}
