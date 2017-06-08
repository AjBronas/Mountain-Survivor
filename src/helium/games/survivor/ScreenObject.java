package helium.games.survivor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class ScreenObject {

	public String name = "NULL";
	public int health = 100;
	public int maxHealth = 100;
	public int x = 0;
	public int y = 0;
	public int width = 50;
	public int height = 50;
	public int size = 50;
	public String type = Level.WATER;
	public boolean draw = true;
	public Animation animation;
	public boolean addToInventory = false;
	public Resources res;
	public Level level;
	public String displayName = "null";
	public String optionString = "";
	
	public ScreenObject( Resources res, Level level ){
		
		this.res = res;
		this.animation = new Animation( res );
		this.level = level;
	}
	
	public void setX(int x){

		this.x = x;

	}

	public void setY(int y){

		this.y = y;

	}
	
	public void setWidth(int width){

		this.width = width;

	}

	public void setHeight(int height){

		this.height = height;

	}
	
	public void hit( Weapon weapon, int damage ){
		
		//Override in each class
	}
	public int getX(){

		return this.x;

	}

	public int getY(){

		return this.y;

	}
	

	public InventoryItem getItem(){
		
		this.addToInventory = false;
		Bitmap bl = BitmapFactory.decodeResource(res, R.drawable.blank);
		InventoryItem item = new InventoryItem(name,displayName, type, 1, bl, true, res, level);
		
		return item;
	}
	
	public Rect Collision(){
		
		Rect r = new Rect(this.x,this.y,this.x+this.width,this.y+this.height);
		return r;
		
	}
	
	public boolean isOnScreen(){
		
		boolean onScreen = true;
		if(getY() < (0 - height) || getY() > level.screen_height || getX() - level.cameraX < (level.player.getX() - level.screen_width) || getX() - level.cameraX > (level.player.getX() + level.screen_width)){
			onScreen = false;
		}
		
		return onScreen;
	}
}
