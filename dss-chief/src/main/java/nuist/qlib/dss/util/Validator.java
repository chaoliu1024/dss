/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.util;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Validate some value
 * 
 * @author Chao Liu
 * @since dss 1.0
 */
public class Validator {

	/**
	 * regular expression to verify the input score
	 * 
	 * @author LiuChao
	 */
	public boolean scoreValidator(Shell ref_edit_shell,
			HashMap<String, String> scores) {
		MessageBox box = new MessageBox(ref_edit_shell, SWT.OK);
		box.setText("提示");
		Pattern p = Pattern
				.compile("([0,1,2,3,4,5,6,7,8,9])(\\.\\d{0,2})?|10|10.0|10.00");
		Pattern dedu_p = Pattern.compile("(\\d{0,2})(\\.\\d{0,2})?");

		boolean b = true;

		if (scores.get("art1") != null && !scores.get("art1").equals("")) {
			if (!p.matcher(scores.get("art1")).matches()) {
				box.setMessage("'艺术分一'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'艺术分一'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("art2") != null && !scores.get("art2").equals("")) {
			if (!p.matcher(scores.get("art2")).matches()) {
				box.setMessage("'艺术分二'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'艺术分二'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("art3") != null && !scores.get("art3").equals("")) {
			if (!p.matcher(scores.get("art3")).matches()) {
				box.setMessage("'艺术分三'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'艺术分三'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("art4") != null && !scores.get("art4").equals("")) {
			if (!p.matcher(scores.get("art4")).matches()) {
				box.setMessage("'艺术分四'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'艺术分四'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("exec1") != null && !scores.get("exec1").equals("")) {
			if (!p.matcher(scores.get("exec1")).matches()) {
				box.setMessage("'完成分一'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'完成分一'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("exec2") != null && !scores.get("exec2").equals("")) {
			if (!p.matcher(scores.get("exec2")).matches()) {
				box.setMessage("'完成分二'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'完成分二'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("exec3") != null && !scores.get("exec3").equals("")) {
			if (!p.matcher(scores.get("exec3")).matches()) {
				box.setMessage("'完成分三'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'完成分三'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("exec4") != null && !scores.get("exec4").equals("")) {
			if (!p.matcher(scores.get("exec4")).matches()) {
				box.setMessage("'完成分四'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'完成分四'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("imp1") != null && !scores.get("imp1").equals("")) {
			if (!p.matcher(scores.get("imp1")).matches()) {
				box.setMessage("'舞步分一'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'舞步分一'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("imp2") != null && !scores.get("imp2").equals("")) {
			if (!p.matcher(scores.get("imp2")).matches()) {
				box.setMessage("'舞步分二'输入范围0~10分,保留二位小数");
				box.open();
				b = false;
			}
		} else {
			box.setMessage("'舞步分二'不能为空");
			box.open();
			b = false;
		}

		if (scores.get("dedu") != null && !scores.get("dedu").equals("")) {
			if (!dedu_p.matcher(scores.get("dedu")).matches()) {
				box.setMessage("'裁判长减分'输入不合法,保留两位小数");
				box.open();
				b = false;
			}
		}
		return b;
	}
}
