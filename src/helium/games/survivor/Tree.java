/**
 * Trees are used for harvesting wood.
 * 
 * Trees are week to AXEs
 * 
 * Trees give about 15 - 20 wood per tree.
 */
package helium.games.survivor;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Tree extends ScreenObject{

	public static final int HEIGHT = 300;
	public static final int WIDTH = 150;
	public static final int P_HEIGHT = 65; //%
	public static final int P_WIDTH = 23; //%
	public static final int GIVEWAY = 20; 		//Adjustment to tree location

	public String type = Level.GRASS;
	public int pWidth = P_WIDTH;;
	public int pHeight = P_HEIGHT;
	public boolean isDead = false;
	public int value = 15;
	public boolean flip = false;
	public Bitmap tree;

	public Tree(int health, String type,int x, int y, int width, int height, int woodValue, boolean flip, Resources res, Level level ){

		super(res, level);
		this.health = health;
		this.maxHealth = health;
		this.type = type;
		setX(x);
		setY(y);
		setHeight(height);
		setWidth(width);
		this.value = woodValue;
		this.flip = flip;
		this.tree = BitmapFactory.decodeResource(res, R.drawable.treenew1);
		this.tree = Bitmap.createScaledBitmap(tree, width, height, true);

		this.animation = new Animation( res );
		if(this.type == Level.GRASS){
			setupAniGrass( res, width, height );
		}else if(this.type == Level.SNOW){
			setupAniSnow( res, width, height );
		}
	}

	public void setupAniGrass( Resources res, int width, int height ){

		this.animation = new Animation(tree, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.treenew2);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.treenew3);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {tree,image1,image2,image1};
		Random random = new Random();
		int wind = 120;
		wind += random.nextInt(180);
		int durations[] = {wind,wind,wind,wind};

		this.animation.createAnimation(images, durations, true, 0);

		this.animation.start(0, 0);
	}

	public void setupAniSnow( Resources res, int width, int height ){
		
		this.tree = BitmapFactory.decodeResource(res, R.drawable.treesnow);
		this.tree = Bitmap.createScaledBitmap(tree, width, height, true);
		this.animation = new Animation(tree, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.treesnow);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.treesnow);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {tree,image1,image2,image1};
		Random random = new Random();
		int wind = 120;
		wind += random.nextInt(180);
		int durations[] = {wind,wind,wind,wind};

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
				ScreenObject debris = level.effects.woodDebris();

				debris.x = x + (width/2) - GIVEWAY;
				debris.y = y + height - (level.player.size/2);
				debris.animation.start(0, 0);
				level.screenEffects.add(debris);
				
				float dmg = weapon.damage;

				if(weapon.name == Weapon.AXE){
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

		this.draw = false;
		this.health = 0;
		this.isDead = true;
		this.animation = null;		//TODO: Can cause it to crash from LevelView, because it tries to draw from animaiton
		level.tree.remove(this);
	}
	
	public void addToInventory(){

		level.player.inventory.add( new InventoryItem("Wood","Wood"+" ", type, value, tree, true , super.res, super.level));

	}
	
	@Override
	public InventoryItem getItem(){

		this.addToInventory = false;

		InventoryItem item = new InventoryItem("Wood","Wood"+" ", type, value, tree , true, super.res, super.level);

		return item;
	}

	@Override
	public Rect Collision(){

		Rect r = new Rect(this.x + (this.width/3),this.y,(this.x+this.width) - (this.width/3),this.y+this.height);
		return r;

	}

}
