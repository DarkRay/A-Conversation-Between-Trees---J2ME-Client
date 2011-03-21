package darkforest.mobile;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;

public class Camera 
{
	private final Form form;
	
	private VideoControl camControl;
	
	public Camera(Form form) 
	{
		this.form = form;
	}
	
	public byte[] getSnapshot() 
	{
		try
		{
			Player camPlayer = Manager.createPlayer("capture://devcam0");
			camPlayer.realize();
			camControl = (VideoControl)camPlayer.getControl("VideoControl");
			Item camItem = (Item)camControl.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
			int itemNum = form.append(camItem);
			camControl.setDisplaySize(1, 1);
			camPlayer.start();
			byte[] imageBytes = camControl.getSnapshot("encoding=jpeg&width=640&height=480&quality=75");
			form.delete(itemNum);
			camPlayer.close();
			return imageBytes;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
