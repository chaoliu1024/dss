/*
 * 文件名：MatchNameDialog.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：当在比赛界面跳转到成绩界面时，如果没有赛事名称信息，则进行选择
 */
package nuist.qlib.dss.ui;

import java.util.List;

import nuist.qlib.dss.scoreManager.QueryScore;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class MatchNameDialog extends Dialog {
	private Shell dialog;
	private String matchName;
	private int matchType=-1;
	private static Text text;
	private Combo matchType_combo;
	private String[] matchNames;
	private QueryScore score;

	MatchNameDialog(Shell parent) {
		super(parent);//   /调用基类的构造方法		
		score=new QueryScore();
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}
   
	public String[] getMatchNames() {
		return matchNames;
	}

	public void setMatchNames(String[] matchNames) {
		this.matchNames = matchNames;
	}

	public void open() {
		Display display = Display.getDefault();
		final Shell parent = this.getParent();
		dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(292, 187);
		dialog.setText("赛事名称输入");
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();  
	    Rectangle shellBounds = dialog.getBounds();  
	    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;  
        int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;  
        dialog.setLocation(x, y); 
		Label label = new Label(dialog, SWT.NONE);
		label.setBounds(10, 39, 61, 17);
		label.setText("赛事名称：");

		text = new Text(dialog, SWT.BORDER);
		text.setBounds(81, 36, 193, 23);
		if(matchName!=null&&matchName.trim().length()!=0){
			text.setText(matchName);
		}else{
			text.setText(matchNames[0]);
		}
		AutoCompleteField field = new AutoCompleteField(text,
				new TextContentAdapter(), matchNames);

		Button button = new Button(dialog, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(dialog, SWT.OK);
				box.setText("提示");
				matchName= text.getText();
				if (matchName == null || matchName.equals("")) {
					box.setMessage("请输入赛事名称");
					box.open();
				}else if(matchType==-1){
					box.setMessage("请选择赛事模式");
					box.open();
				}else
				dialog.close();
			}
		});
		button.setBounds(194, 108, 80, 27);
		button.setText("确定");
		
		matchType_combo = new Combo(dialog, SWT.NONE);
		matchType_combo.setBounds(81, 79, 88, 25);
		matchType_combo.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				MessageBox box = new MessageBox(dialog, SWT.OK);
				box.setText("提示");
				if(text.getText().trim().length()==0){
					text.setFocus();
					box.setMessage("请先选择赛事名称");
					box.open();					
				}else{
					if(score.isCollected()){
						List<Integer> matchKinds=score.getMatchKindByMatchName(text.getText().trim());
						matchType_combo.removeAll();
						for(int i=0;i<matchKinds.size();i++){
							matchType_combo.add(matchKinds.get(i)==0?"预赛":"决赛");
							matchType_combo.setData(String.valueOf(i),matchKinds.get(i));
						}
					}else{
						box.setText("警告");
						box.setMessage("数据库连接失败");
						box.open();
					}
				}
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			}			
		});
		matchType_combo.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index=matchType_combo.getSelectionIndex();
				if(index!=-1){
					matchType=Integer.valueOf(matchType_combo.getData(String.valueOf(index)).toString());
				}				
			}			
		});
		Label matchType_label = new Label(dialog, SWT.NONE);
		matchType_label.setBounds(10, 79, 61, 17);
		matchType_label.setText("赛事模式:");
		dialog.open();
		while (!dialog.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
