/**
 * Stones include rocks(decor), Silver, Gold, ETC.
 * 
 * Stones are week to PICKAXEs
 * 
 * Stones give about 5 - 10 ore per stone.
 */

package helium.games.survivor;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Stone extends ScreenObject{

	public static final int HEIGHT = 75;
	public static final int WIDTH = 75;
	public static final int P_HEIGHT = 12; //%
	public static final int P_WIDTH = 12; //%
	public static final int GIVEWAY = 10; 		//Adjustment to stone location

	public int variety = 0;
	public int pWidth = P_WIDTH;;
	public int pHeight = P_HEIGHT;
	public String type = Level.STONE;
	public boolean isDead = false;
	public int value = 15;
	public boolean flip = false;

	public Stone(int health, String type,int x, int y, int width, int height, int stoneValue, boolean flip, Resources res, Level level ){

		super(res, level);
		this.health = health;
		this.maxHealth = health;
		this.type = type;
		setX(x);
		setY(y);
		setHeight(height);
		setWidth(width);
		this.value = stoneValue;
		this.flip = flip;
		Random random = new Random();
		this.variety = random.nextInt(2);

		this.animation = new Animation( res );
		if(this.type.equals(Level.STONE)){
			setupStone( res, width, height );
		}
		if(this.type.equals(Level.SILVER)){
			setupSilver( res, width, height );
		}
	}

	public void setupStone( Resources res, int width, int height ){

		Bitmap var[] = {BitmapFactory.decodeResource(res, R.drawable.rock),BitmapFactory.decodeResource(res, R.drawable.rock2)};

		Bitmap stone = var[this.variety];
		stone =  Bitmap.createScaledBitmap(stone, width, height, true);

		this.animation = new Animation(stone, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.rock);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.rock);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {stone,image1,image2};

		int durations[] = {0,0,0};

		this.animation.createAnimation(images, durations, true, 0);

		//this.animation.start(0, 0);
	}

	public void setupSilver( Resources res, int width, int height ){

		Bitmap stone = BitmapFactory.decodeResource(res, R.drawable.silver);
		stone =  Bitmap.createScaledBitmap(stone, width, height, true);

		this.animation = new Animation(stone, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.silver);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.silver);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {stone,image1,image2};

		int durations[] = {0,0,0};

		this.animation.createAnimation(images, durations, true, 0);

		//this.animation.start(0, 0);
	}

	public String getType(){

		return this.type;

	}

	@Override
	public void hit( Weapon weapon, int damage ){
		
		if(isDead == false){
			if(weapon != null){
				weapon.isHitting = true;

				ScreenObject debris = level.effects.stoneDebris();

				debris.x = x - GIVEWAY;
				debris.y = y + height - (level.player.size/2);
				debris.animation.start(0, 0);
				level.screenEffects.add(debris);

				float dmg = weapon.damage;

				if(weapon.name.equals(Weapon.PICKAXE)){
					dmg *= 2.5;
				}else{
					weapon.health -=3;
				}
				this.health -= dmg;

			}else{

				this.health -= damage;
			}

			if(this.health <= 0){
				addToInventory();
				die();
			}


		}

	}

	public void die(){


		this.animation = null;		//if nulled getItem will fail.
		this.draw = false;
		this.health = 0;
		this.isDead = true;
		level.stone.remove(this);
	}
	
	public void addToInventory(){
		
		String upper = type.substring(0, 1);
		String lower = type.substring(1);
		upper = upper.toUpperCase();
		lower = lower.toLowerCase();
		InventoryItem item = new InventoryItem("Stone",upper+lower+" ", type, value, animation.main, true, super.res, super.level);
		level.player.inventory.add( item );

	}
	
	@Override
	public InventoryItem getItem(){

		this.addToInventory = false;
		String upper = type.substring(0, 1);
		String lower = type.substring(1);
		upper = upper.toUpperCase();
		lower = lower.toLowerCase();
		InventoryItem item = new InventoryItem("Stone",upper+lower+" ", type, value, animation.main, true, super.res, super.level);

		return item;
	}

	@Override
	public Rect Collision(){

		Rect r = new Rect( this.x, this.y, this.x + this.width, this.y + this.height);
		return r;

	}

}
