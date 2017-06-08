package helium.games.survivor;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Blocks extends ScreenObject{

	public static final String WOOD_WALL = "Wood Wall";
	
	private int number = 0;
	private int height = 0;
	private int size = 50;
	private int x = 0;
	private int y = 0;
	public boolean draw = true;
	public boolean isSolid = false;
	public Bitmap water;
	public Animation animation;
	public boolean isDead = false;
	public Resources res;
	public Level level;

	public Blocks(String type, int number, int height, int size, int x, int y, boolean solid, Resources res, Level level){

		super(res,level);
		this.type = type;
		this.number = number;
		this.height = height;
		this.size = size;
		this.x = x;
		this.y = y;
		this.isSolid = solid;
		this.res = res;
		this.level = level;
		this.animation = new Animation( res );

		if( type == Level.WATER )
			setupAni( res , size );

	}

	public Blocks(String type, int number, int height, int size, int x, int y, boolean solid, Resources res, Level level, InventoryItem item){

		super(res,level);
		this.name = item.getName();
		this.type = type;
		this.number = number;
		this.height = height;
		this.size = size;
		this.x = x;
		this.y = y;
		this.isSolid = solid;
		this.res = res;
		this.level = level;
		if(item.image != null){
			Bitmap iimage = Bitmap.createScaledBitmap(item.image, size, size, true);
			this.animation = new Animation(iimage, x, y, size, size);

		}else{
			this.animation = new Animation(res);

		}

	}

	public void setupAni( Resources res, int size ){

		this.water = BitmapFactory.decodeResource(res, R.drawable.water);
		this.water = Bitmap.createScaledBitmap(water, size, size, true);
		this.animation = new Animation(water, x, y, size, size);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.water1);
		image1 = Bitmap.createScaledBitmap(image1, size, size, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.water2);
		image2 = Bitmap.createScaledBitmap(image2, size, size, true);

		Bitmap images[] = {water,image1,image2,image1};
		int durations[] = {300,300,300,300};

		this.animation.createAnimation(images, durations, true, 0);

		this.animation.start(0, 0);
	}

	public void setX(int x){

		this.x = x;
	}

	public void setY(int y){

		this.y = y;
	}

	public void setLocation(int x, int y){

		this.x = x;
		this.y = y;
	}

	public void setNumber(int number){

		this.number = number;
	}

	public int getX(){

		return this.x;

	}

	public int getY(){

		return this.y;

	}

	public void moveX(int amount){

		this.x+=amount;

	}

	public void moveY(int amount){

		this.y+=amount;

	}

	public int getSize(){

		return this.size;

	}

	public int getNumber(){

		return this.number;

	}
	public int getHeight(){

		return this.height;

	}


	public String getType(){

		return this.type;

	}

	@Override
	public void hit( Weapon weapon, int damage ){

		
			if(weapon != null){
				float dmg = weapon.damage;

				level.effects.width = size;
				level.effects.height = size;
				ScreenObject debri = level.effects.woodDebris();

				debri.x = x;
				debri.y = y + size - (size/2);
				debri.animation.start(0, 0);
				level.screenEffects.add(debri);
				if(weapon.name == Weapon.AXE)
					dmg *= 2;
				this.health -= dmg;

			}else{

				this.health -= damage;
			}

			if(this.health <= 0){

				addToInventory();
				die();
			}


		

	}

	public void die(){

		this.health = 0;
		this.isDead = true;
		animation = null;
		level.block.remove(this);
	}

	public void addToInventory(){

		level.player.inventory.add( new InventoryItem("Wood","Wood"+" ", type, 1, animation.main, isSolid , super.res, super.level));

	}

	public Rect Collision(){

		Rect r = new Rect(this.x,this.y,this.x+this.size,this.y+this.size);
		return r;

	}

	@Override
	public String toString(){

		String string = this.name + "," + this.type + "," + this.x + "," + this.y + "," + this.size +"," + this.isSolid + ",";
		return string;
	}
	
}
