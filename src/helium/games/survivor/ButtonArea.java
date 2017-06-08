package helium.games.survivor;

import android.graphics.Rect;

public class ButtonArea {
	
	public static final int P_HEIGHT = 20; //%
	public static final int P_WIDTH = 20; //%


	public String name;
	public int x;
	public int y;
	public int width;
	public int height;
	public int pWidth = P_WIDTH;;
	public int pHeight = P_HEIGHT;
	public int id;
	public boolean visable = true;
	public boolean touchable = true;

	public ButtonArea(int id, int x, int y, int width, int height, String name) {

		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;

	}

	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public int getWidth(){
		return this.width;
	}
	public int getHeight(){
		return this.height;
	}
	public boolean isVisable(){
		return this.visable;
	}
	public boolean isTouchable(){
		return this.touchable;
	}
	public Rect getRect(){
		Rect r = new Rect(x,y,width,height);
		return r;
	}

	public void setX( int x ){
		this.x = x;
	}
	public void setY( int y ){
		this.y = y;
	}
	public void setWidth( int width ){
		this.width = width;
	}
	public void setHeight( int height ){
		this.height = height;
	}
	public void setVisable( boolean visable ){
		this.visable = visable;
	}
	public void setTouchable( boolean touchable){
		this.touchable = touchable;
	}

}
