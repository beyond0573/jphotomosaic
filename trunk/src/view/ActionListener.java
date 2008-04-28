package view;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionListener implements java.awt.event.ActionListener {

	Controller ctrl;
	
	Method method;
	
	public ActionListener(Controller controller, String action) {
		ctrl = controller;
		Class<Controller> c = Controller.class;
		try {
			method = c.getMethod(action, new Class<?>[] {});
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		try {
			method.invoke(ctrl);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
