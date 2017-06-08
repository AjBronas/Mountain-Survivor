package helium.games.survivor;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;

public class InventoryItem extends ScreenObject{

	public static final int DEFAULT_WIDTH = 125;
	public static final int DEFAULT_HEIGHT = 125;
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 50;
	public static final int P_BUTTON_WIDTH = 62;
	public static final int P_BUTTON_HEIGHT = 10;

	private int amount = 0;
	public boolean isInInventory = true;
	public Bitmap image;
	private String holding = "None";
	private String ammo;
	public int size = 50;
	public int value = 1;
	public boolean isOnGround = false;
	public String displayName = "null";
	public boolean isSolid = false;
	public int otherInt = 0;
	public String otherString;
	public boolean halfCol = false;
	public Handler handler = new Handler();

	public InventoryItem( Resources res, Level level ){

		super(res, level);
		image = BitmapFactory.decodeResource(res, R.drawable.blank);
		this.image  = Bitmap.createScaledBitmap(this.image,InventoryItem.DEFAULT_WIDTH, InventoryItem.DEFAULT_HEIGHT,true);	

	}


	public InventoryItem(String name, String displayName, String type, int amount, Bitmap image, boolean isSolid,  Resources res , Level level){

		super(res, level);
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.amount = amount;
		this.isSolid = isSolid;
		setImage(image);

	}

	public InventoryItem( Weapon weapon, Resources res, Level level ){

		super(res,  level);
		this.name = weapon.name;
		this.displayName = weapon.displayName;
		this.type = weapon.type;
		this.amount = 1;
		setImage(weapon.animation.main);
	}

	public void setName( String name ){
		this.name = name;
	}

	public void addAmount(int amount){
		this.amount += amount;
	}

	public void setAmount(int amount){
		this.amount = amount;
	}

	public void attach(String holding){
		this.holding = holding;
	}

	public void setImage(Bitmap image){
		this.image = image;
		this.image  = Bitmap.createScaledBitmap(this.image,InventoryItem.DEFAULT_WIDTH, InventoryItem.DEFAULT_HEIGHT,true);	

	}

	public void setAmmo( String ammo ){		//If this a weapon for example, tell what kind of ammo it uses
		this.ammo = ammo;
	}

	public void consume(){

		this.amount--;
		if( this.amount <= 0 ){
			destroy();
		}

	}

	public void consume( int amount ){

		this.amount-= amount;
		if( this.amount <= 0 ){
			destroy();
		}

	}

	public void destroy(){

		this.image = null;
		this.holding = null;
		this.draw = false;
		if( level.equippedItem.equals(this) ){
			if(level.player.inventory.indexOf(level.equippedItem) > 0){
				level.selectedItem = level.player.inventory.get(level.player.inventory.indexOf( level.selectedItem ) -1);
				level.equippedItem = level.selectedItem;
				level.player.weapon.setWeapon(level.equippedItem);
				level.player.inventory.remove(this);

			}else{
				if( level.player.inventory.size() > 1 ){
					level.selectedItem = level.player.inventory.get(level.player.inventory.indexOf( level.selectedItem ) +1);
					level.equippedItem = level.selectedItem;
				}else{
					level.selectedItem = null;
					level.equippedItem = level.selectedItem;

				}
				level.player.setWeapon( Weapon.FIST );
				level.player.inventory.remove(this);
			}
		}

	}

	private Runnable checkCol = new Runnable() {  

		public void run() {  

			if( isInInventory == false){

				Rect npcCol = Collision();
				npcCol = new Rect(npcCol.left - level.cameraX,npcCol.top,npcCol.right - level.cameraX,npcCol.bottom);

				if(Rect.intersects(level.player.feetCollision(), npcCol)){
					
					level.blstring+= name + " , " ;
				}

				List<Blocks> blocksarea = level.findBlocksInArea(npcCol);
				int solidblocks = 0;
				for(Blocks bls : blocksarea){

					Rect Col =  bls.Collision();
					//Left				Top		Right			Bottom
					Col = new Rect(Col.left -level.cameraX,Col.top,Col.right -level.cameraX,Col.bottom);
					if(Rect.intersects(Col, npcCol)){



						if( bls.isSolid == true ){
							solidblocks++;

						}


					}


				}

				if(blocksarea.size()>0){
					if(solidblocks>0){

						isOnGround = true;

					}else{
						isOnGround = false;
						if((getY() + height) >= level.screen_height){
							isOnGround = true;
						}
					}

				}

				handler.post(checkCol);
			}
		}     

	};

	public void equip(){

		level.player.weapon.setWeapon(this);
	}

	public void toss(){

		if(level.equippedItem == level.selectedItem){
			level.player.weapon.setWeapon(Weapon.FIST);
		}
		this.isOnGround = false;
		this.isInInventory = false;
		this.x = level.player.getX() + level.cameraX;
		this.y = level.player.getY() - level.player.size;
		handler.post(checkCol);
	}


	public void setX(int x){

		this.x = x;

	}

	public void setY(int y){

		this.y = y;

	}

	public String getName(){
		return this.name;
	}

	public int getAmount(){
		return this.amount;
	}

	public void moveX(int amount){

		this.x+=amount;

	}

	public void moveY(int amount){

		this.y+=amount;

	}

	public boolean isOnScreen(int playerx, int playery,int canvasw, int canvash){

		boolean isonscreen = false;
		if(this.getX()>=(0-this.size)&this.getY()>=(0-this.size)){

			if(this.getX()<=(Level.screen_width+this.size)&this.getY()>=(Level.screen_height+this.size)){

				isonscreen = true;

			}

		}


		return isonscreen;

	}

	public Rect Colision(){

		Rect r = new Rect(this.x,this.y,this.size,this.size);
		if(halfCol == true){
			r = new Rect(this.x + this.size/2,this.y + this.size/2,this.x + this.size - this.size/2,this.y + this.size - this.size/2);
		}
		return r;

	}

	@Override
	public String toString(){

		String string = this.name + "," + this.displayName + "," + this.type + "," + this.amount;
		return string;
	}
}
