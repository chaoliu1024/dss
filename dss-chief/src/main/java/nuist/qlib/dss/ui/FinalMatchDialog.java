/*
 * 文件名：FinalMatchDialog.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：导出决赛数据导航
 */
package nuist.qlib.dss.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.FinalOperDao;
import nuist.qlib.dss.util.ExcelManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FinalMatchDialog extends Dialog {
	private static Text text;
	private Shell shell;
	private String category;
	private MessageBox box;
	private String matchName;
	private FinalOperDao dao;
	private List<HashMap<String,Object>> data;

	FinalMatchDialog(Shell parent,String category,MessageBox box,String matchName) {
		super(parent);// 调用基类的构造方法
		this.category=category;
		this.box=box;
		this.matchName=matchName;
		dao=new FinalOperDao();
	}
  
	public FinalOperDao getDao() {
		return dao;
	}

	// 将对话框打开
	public void open() {
		
		Display display = Display.getDefault();
		final Shell parent = this.getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(250, 154);
		shell.setText("决赛数据导航");
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();  
	    Rectangle shellBounds = shell.getBounds();  
	    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;  
        int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;  
        shell.setLocation(x, y); 

		Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 39, 61, 17);
		label.setText("导出人数：");

		text = new Text(shell, SWT.BORDER);
		text.setBounds(81, 36, 133, 23);	
		
		data=dao.getTeamByCategory(matchName, category);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String textString = text.getText();
				if (textString == null || textString.equals("")) {
					box.setMessage("信息不完整，请检查！");
					box.open();
				} else {
					int textInt = Integer.valueOf(textString);
					if (textInt > data.size()) {
						box.setMessage("选择的人数超过该项目已有人数！！");
						box.open();
					} else if(textInt<=0){
						box.setMessage("选择的人数必须大于0！！");
						box.open();
					}else {
						// 完成导出功能
						data=dao.getTeamByCategory(textInt,matchName, category);
						shell.close();						
							List<HashMap<String, Object>> realData = new ArrayList<HashMap<String, Object>>();
							for (int i = textInt - 1; i >= 0; i--) {
								realData.add(data.get(i));
							}
							FileDialog dialog = new FileDialog(parent, SWT.SAVE);
							dialog.setText("保存文件");// 设置对话框的标题
							dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
							dialog.setFilterNames(new String[] { "Excel文件(*.xls)",
									"Excel文件(*.xlsx)" });
							String fileName = dialog.open(); // 获得保存的文件名
							if(fileName != null && !fileName.equals("")){
		                        ExcelManager manager=new ExcelManager();
								if(manager.ExportExcelFinal(realData, fileName, matchName, "", false)){
									box.setMessage("导出成功");
									box.open();
								}else{
									box.setMessage("导出失败");
									box.open();
								}
							};
					}
				}
			}
		});
		button.setBounds(154, 76, 80, 27);
		button.setText("导出");
		shell.open();
		shell.addDisposeListener(new DisposeListener(){

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				if(dao.isCollected()){
					dao.close();
				}
				shell.dispose();
			}
			
		});
		while (!shell.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
